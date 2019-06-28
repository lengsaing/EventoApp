package com.app.sangleng.evento;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;

    ImageButton btnAddImage;
    EditText edit_title;
    EditText edit_price;
    EditText edit_desc;
    EditText edit_loc;
    EditText edit_date;
    EditText edit_cat;
    DatePickerDialog datePickerDialog;
    Button btnPost;

    private final int GALLERY_REQUEST = 1;
    private Uri imageUri = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = mAuth.getCurrentUser();
                if(currentUser == null){
                    startActivity(new Intent(PostActivity.this, AuthenticateActivity.class));
                }
            }
        };

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");

        btnAddImage = findViewById(R.id.btnAddImage);
        edit_title = findViewById(R.id.edit_title);
        edit_price = findViewById(R.id.edit_price);
        edit_desc = findViewById(R.id.edit_desc);
        edit_loc = findViewById(R.id.edit_loc);
        edit_date = findViewById(R.id.edit_date);
        edit_cat = findViewById(R.id.edit_cat);
        btnPost = findViewById(R.id.btnPost);

        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(PostActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edit_date.setText(dayOfMonth + "/"
                                        + (month + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posting();
            }
        });
    }

    public void posting(){

        final String title = edit_title.getText().toString().trim();
        final String price = edit_price.getText().toString().trim();
        final String loc = edit_loc.getText().toString().trim();
        final String desc = edit_desc.getText().toString().trim();
        final String date = edit_date.getText().toString().trim();
        final String cat = edit_cat.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(price) && imageUri!=null ){
            final StorageReference filepath = mStorage.child("Event_Images").child(imageUri.getLastPathSegment());

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
                        DatabaseReference newPost = mDatabase.push();
                        newPost.child("name").setValue(title);
                        newPost.child("price").setValue(price);
                        newPost.child("desc").setValue(desc);
                        newPost.child("cat").setValue(cat);
                        newPost.child("loc").setValue(loc);
                        newPost.child("date").setValue(date);
                        newPost.child("image").setValue(downloadUri.toString());
                        newPost.child("creator").setValue(currentUser.getUid());
                        Toast.makeText(PostActivity.this, "Upload success", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                    }
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            btnAddImage.setImageURI(imageUri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
