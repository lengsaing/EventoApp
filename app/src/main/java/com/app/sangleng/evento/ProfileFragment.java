package com.app.sangleng.evento;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    CircularImageView profile_pic;
    TextView profile_name;
    Button edit_profile;
    TextView emailText;
    TextView addressText;
    TextView phoneText;
    Button btn_logout;

    private DatabaseReference mDatabase;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        profile_pic = view.findViewById(R.id.profile_pic);
        profile_name = view.findViewById(R.id.profile_name);
        edit_profile = view.findViewById(R.id.edit_profile);
        emailText = view.findViewById(R.id.emailText);
        addressText = view.findViewById(R.id.addressText);
        phoneText = view.findViewById(R.id.phoneText);
        btn_logout = view.findViewById(R.id.btn_logout);

        getUserInfo();

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                if(task.isSuccessful()) {
                                    startActivity(new Intent(getContext(), AuthenticateActivity.class));
                                }
                                else{
                                    Toast.makeText(getContext(), "Log out unsuccessfully! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }

    private void getUserInfo() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Query query = mDatabase.child(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profile_name.setText(dataSnapshot.child("username").getValue().toString());
                emailText.setText(dataSnapshot.child("user_email").getValue().toString());
                addressText.setText(dataSnapshot.child("user_address").getValue().toString());
                phoneText.setText(dataSnapshot.child("user_phone").getValue().toString());

                String imageUrl = dataSnapshot.child("user_image").getValue().toString();
                if(!imageUrl.isEmpty()) {
                    Glide.with(getContext())
                            .load(imageUrl)
                            .centerCrop()
                            .into(profile_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
