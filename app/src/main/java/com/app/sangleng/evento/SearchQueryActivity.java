package com.app.sangleng.evento;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchQueryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private RecyclerView queryRecyclerView;
    private FirebaseRecyclerAdapter searchEventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(currentUser == null){
                    startActivity(new Intent(SearchQueryActivity.this, AuthenticateActivity.class));
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");

        queryRecyclerView = findViewById(R.id.queryRecyclerView);

        firebaseSearchItems();

    }

    private void firebaseSearchItems() {
        String eventName = getIntent().getStringExtra("eventName");
        String eventLoc = getIntent().getStringExtra("eventLoc");
        String eventDate = getIntent().getStringExtra("eventDate");
        String eventCat = getIntent().getStringExtra("eventCat");

        Query searchQuery = null;

        if(!eventName.isEmpty()) {
            searchQuery = mDatabase.orderByChild("name").startAt(eventName).endAt(eventName + "\uf8ff");
        }

        else if (!eventLoc.isEmpty()){
            searchQuery = mDatabase.orderByChild("loc").startAt(eventLoc).endAt(eventLoc + "\uf8ff");
        }

        else if (!eventDate.isEmpty()){
            searchQuery = mDatabase.orderByChild("date").startAt(eventDate).endAt(eventDate + "\uf8ff");
        }

        else if (!eventCat.isEmpty()){
            searchQuery = mDatabase.orderByChild("cat").startAt(eventCat).endAt(eventCat + "\uf8ff");
        }

        FirebaseRecyclerOptions<EventItem> searchOptions =
                new FirebaseRecyclerOptions.Builder<EventItem>()
                        .setQuery(searchQuery, new SnapshotParser<EventItem>() {
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

        searchEventsAdapter = new FirebaseRecyclerAdapter<EventItem, SearchEventViewHolder>(searchOptions) {
            @NonNull
            @Override
            public SearchEventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                final View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.event_item, viewGroup, false);

                return new SearchEventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SearchEventViewHolder holder, final int position, @NonNull EventItem model) {
                holder.setTitle(model.getTitle());
                holder.setPrice(model.getPrice());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventID = getRef(position).getKey();
                        Log.i("OMG", eventID);

                        Intent intent = new Intent(SearchQueryActivity.this, DescriptionActivity.class);
                        intent.putExtra("eventID", eventID);
                        startActivity(intent);
                    }
                });

                Glide.with(SearchQueryActivity.this)
                        .load(model.getImage())
                        .centerCrop()
                        .into(holder.event_image);
            }
        };

        queryRecyclerView.setAdapter(searchEventsAdapter);
        queryRecyclerView.setLayoutManager(new LinearLayoutManager(SearchQueryActivity.this));
    }

    public class SearchEventViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView event_title;
        public TextView event_price;
        public ImageView event_image;
        public ImageView squareImage;

        public SearchEventViewHolder(@NonNull final View itemView) {
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
        searchEventsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchEventsAdapter.stopListening();
    }
}