package com.app.sangleng.evento;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

public class EditProfileActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST = 1;

    CircularImageView profile_pic_New;
    Button profilePicEdit;
    EditText nameEditText;
    EditText emailEditText;
    EditText pwdEditText;
    EditText addressEditText;
    EditText phoneEditText;
    Button btnSave;

    private Uri imageUri = null;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        profile_pic_New = findViewById(R.id.profile_pic_New);
        profilePicEdit = findViewById(R.id.profilePicEdit);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        pwdEditText = findViewById(R.id.pwdEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        btnSave = findViewById(R.id.btnSave);

        getUserInfo();

        profilePicEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saving();
            }
        });

    }

    private void saving() {
        String username = nameEditText.getText().toString().trim();
        final String useremail = emailEditText.getText().toString().trim();
        String userpwd = pwdEditText.getText().toString().trim();
        String useraddress = addressEditText.getText().toString().trim();
        String userphone = phoneEditText.getText().toString().trim();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference userDB = mDatabase.child(mAuth.getCurrentUser().getUid());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(imageUri != null) {
            final StorageReference filepath = mStorage.child("User_Images").child(imageUri.getLastPathSegment());
            UploadTask uploadTask = filepath.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        userDB.child("user_image").setValue(downloadUri.toString());
                        Toast.makeText(EditProfileActivity.this, "Upload success", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        if(!useremail.isEmpty()) {
            user.updateEmail(useremail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                userDB.child("user_email").setValue(useremail);
                                //Log.d(TAG, "User email address updated.");
                            }
                        }
                    });
        }

        if(!userpwd.isEmpty()) {
            user.updatePassword(userpwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                    }
                }
            });
        }

        if(!username.isEmpty()) {
            userDB.child("username").setValue(username);
        }

        if(!useraddress.isEmpty()) {
            userDB.child("user_address").setValue(useraddress);
        }

        if(!userphone.isEmpty()) {
            userDB.child("user_phone").setValue(userphone);
        }

        startActivity(new Intent(this, MainActivity.class));
    }

    private void getUserInfo() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Query query = mDatabase.child(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameEditText.setHint(dataSnapshot.child("username").getValue().toString());
                emailEditText.setHint(dataSnapshot.child("user_email").getValue().toString());
                addressEditText.setHint(dataSnapshot.child("user_address").getValue().toString());
                phoneEditText.setHint(dataSnapshot.child("user_phone").getValue().toString());

                String imageUrl = dataSnapshot.child("user_image").getValue().toString();

                if(!imageUrl.isEmpty()) {
                    Glide.with(EditProfileActivity.this)
                            .load(imageUrl)
                            .centerCrop()
                            .into(profile_pic_New);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();

            Glide.with(EditProfileActivity.this)
                    .load(imageUri)
                    .centerCrop()
                    .into(profile_pic_New);
            }

    }

}
