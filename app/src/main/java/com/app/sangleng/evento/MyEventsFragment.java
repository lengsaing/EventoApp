package com.app.sangleng.evento;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {
    public MyEventsFragment(){

    }

    RecyclerView myEventsRecyclerView;
    FloatingActionButton fab;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;
    DatabaseReference mDatabase;

    FirebaseRecyclerAdapter myEventsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(currentUser == null){
                    startActivity(new Intent(getContext(), AuthenticateActivity.class));
                }
            }
        };

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PostActivity.class));
            }
        });

        myEventsRecyclerView = view.findViewById(R.id.myEventsRecyclerView);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");

        getMyEvents();


        return view;
    }

    private void getMyEvents() {
        String userID = mAuth.getCurrentUser().getUid();

        Query myQuery = mDatabase.orderByChild("creator").startAt(userID).endAt(userID + "\uf8ff");

        FirebaseRecyclerOptions<EventItem> searchOptions =
                new FirebaseRecyclerOptions.Builder<EventItem>()
                        .setQuery(myQuery, new SnapshotParser<EventItem>() {
                            @NonNull
                            @Override
                            public EventItem parseSnapshot(@NonNull DataSnapshot snapshot) {

                                return new EventItem(snapshot.child("name").getValue().toString(),
                                        snapshot.child("price").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        ((Long)snapshot.child("count").getValue()).intValue());
                            }
                        })
                        .build();

        myEventsAdapter = new FirebaseRecyclerAdapter <EventItem, MyEventsViewHolder>(searchOptions) {

            @NonNull
            @Override
            public MyEventsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                final View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.event_item, viewGroup, false);

                return new MyEventsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyEventsViewHolder holder, final int position, @NonNull EventItem model) {
                holder.setTitle(model.getTitle());
                holder.setPrice(model.getPrice());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventID = getRef(position).getKey();
                        Log.i("OMG", eventID);

                        Intent intent = new Intent(getContext(), DescriptionActivity.class);
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

        myEventsRecyclerView.setAdapter(myEventsAdapter);
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    public class MyEventsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView event_title;
        public TextView event_price;
        public ImageView event_image;
        public ImageView squareImage;

        public MyEventsViewHolder(@NonNull final View itemView) {
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


    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
        myEventsAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        myEventsAdapter.stopListening();
    }

}
