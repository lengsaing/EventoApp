package com.app.sangleng.evento;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DescriptionActivity extends AppCompatActivity {

    ImageView eventImage;
    TextView dateText;
    TextView locationText;
    TextView detailText;
    TextView priceText;
    Button btnApply;

    String eventID;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;

    private final int REQUEST_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(currentUser == null){
                    startActivity(new Intent(DescriptionActivity.this, AuthenticateActivity.class));
                }
            }
        };

        eventImage = findViewById(R.id.eventImage);
        dateText = findViewById(R.id.dateText);
        locationText = findViewById(R.id.locationText);
        detailText = findViewById(R.id.detailText);
        priceText = findViewById(R.id.priceText);
        btnApply = findViewById(R.id.btnApply);

        eventID = getIntent().getStringExtra("eventID");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events").child(eventID);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("OMG", "Received: "+eventID);

                dateText.setText(dataSnapshot.child("date").getValue().toString());
                locationText.setText(dataSnapshot.child("loc").getValue().toString());
                priceText.setText(dataSnapshot.child("price").getValue().toString());
                detailText.setText(dataSnapshot.child("desc").getValue().toString());


                Glide.with(DescriptionActivity.this)
                        .load(dataSnapshot.child("image").getValue().toString())
                        .centerCrop()
                        .into(eventImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DescriptionActivity.this, InvoiceActivity.class);
                intent.putExtra("eventID", eventID);
                intent.putExtra("userID", currentUser.getUid());
                intent.putExtra("userName", currentUser.getDisplayName());
                intent.putExtra("price", priceText.getText().toString());

                startActivityForResult(intent, REQUEST_OK);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_OK && resultCode == RESULT_OK){
            Toast.makeText(this, "Your purchase has been confirmed.", Toast.LENGTH_LONG).show();
        }
    }
}
