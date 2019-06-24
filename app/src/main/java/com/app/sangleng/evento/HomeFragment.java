package com.app.sangleng.evento;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView allEventsRecyclerView;
    private RecyclerView popularEventRecyclerView;
    private FirebaseRecyclerAdapter allEventsAdapter;
    private FirebaseRecyclerAdapter popularEventsAdapter;

    private ObservableSnapshotArray<EventItem> popularEvents;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        allEventsRecyclerView = view.findViewById(R.id.allEventsRecyclerView);

        final Query query = FirebaseDatabase.getInstance().getReference().child("Events");

        FirebaseRecyclerOptions<EventItem> options =
                new FirebaseRecyclerOptions.Builder<EventItem>()
                        .setQuery(query, new SnapshotParser<EventItem>() {
                            @NonNull
                            @Override
                            public EventItem parseSnapshot(@NonNull DataSnapshot snapshot) {

                                EventItem mEvent = new EventItem(snapshot.child("name").getValue().toString(),
                                        snapshot.child("price").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        ((Long)snapshot.child("count").getValue()).intValue());

                                return mEvent;
                            }
                        })
                        .build();
        popularEvents = options.getSnapshots();

        allEventsAdapter = new FirebaseRecyclerAdapter<EventItem, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                final View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.event_item, viewGroup, false);

                return new EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final EventViewHolder holder, final int position, @NonNull EventItem model) {
                holder.setTitle(model.getTitle());
                holder.setPrice(model.getPrice());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventID = getRef(position).getKey();
                        Log.i("OMG", eventID);

                        Intent intent = new Intent(getActivity(), DescriptionActivity.class);
                        intent.putExtra("eventID", eventID);
                        startActivity(intent);
                    }
                });

                Glide.with(getContext())
                        .load(model.getImage())
                        .centerCrop()
                        .into(holder.event_image);
            }
        };

        allEventsRecyclerView.setAdapter(allEventsAdapter);
        Resources resources = getResources();
        allEventsRecyclerView.addItemDecoration(new SpacesItemDecoration((int)resources.getDimension(R.dimen.default_padding)));
        allEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        popularEventRecyclerView = view.findViewById(R.id.popularEventRecyclerView);
        getTopEvents();

        FirebaseRecyclerOptions<EventItem> popularEventsOptions = new FirebaseRecyclerOptions.Builder<EventItem>()
                .setSnapshotArray(popularEvents)
                .build();

        popularEventsAdapter = new FirebaseRecyclerAdapter<EventItem, EventViewHolder>(popularEventsOptions){

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.popular_event_item, viewGroup, false);

                Log.i("OMG2", "Inflated Success");
                return new EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, final int pos, @NonNull EventItem model) {
                Glide.with(getContext())
                        .load(model.getImage())
                        .centerCrop()
                        .into(holder.squareImage);

                holder.squareImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventID = getRef(pos).getKey();
                        Log.i("OMG2", eventID);

                        Intent intent = new Intent(getActivity(), DescriptionActivity.class);
                        intent.putExtra("eventID", eventID);
                        startActivity(intent);
                    }
                });
            }
        };

        popularEventRecyclerView.setAdapter(popularEventsAdapter);
        popularEventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        return view;
    }

    private void getTopEvents() {

        for(int i=1; i<popularEvents.size()-1; i++){
            EventItem item1 = popularEvents.get(i-1);
            EventItem item2 = popularEvents.get(i);

            if(item1.getRegisterCount() < item2.getRegisterCount()){
                popularEvents.set(i-1, item2);
                popularEvents.set(i, item1);
            }

            if(i==4){
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        allEventsAdapter.startListening();
        popularEventsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        allEventsAdapter.stopListening();
        popularEventsAdapter.stopListening();
    }


    public class EventViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView event_title;
        public TextView event_price;
        public ImageView event_image;
        public ImageView squareImage;

        public EventViewHolder(@NonNull final View itemView) {
            super(itemView);
            mView = itemView;

            event_title = mView.findViewById(R.id.title);
            event_price = mView.findViewById(R.id.price);
            event_image = mView.findViewById(R.id.thumbnail);
            squareImage = mView.findViewById(R.id.squareImage);
        }

        public void setTitle(String title){
            event_title.setText(title);
        }
        public void setPrice(String price){
            event_price.setText(price);
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }
}
