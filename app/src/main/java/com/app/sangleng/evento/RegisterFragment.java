package com.app.sangleng.evento;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    EditText reg_name;
    EditText reg_email;
    EditText reg_pwd;
    EditText reg_confirmpwd;
    Button btn_register;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        reg_name = view.findViewById(R.id.reg_name);
        reg_email = view.findViewById(R.id.reg_email);
        reg_pwd = view.findViewById(R.id.reg_pwd);
        reg_confirmpwd = view.findViewById(R.id.reg_confirmpwd);
        btn_register = view.findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });


        return view;
    }

    public void startRegister(){
        String name = reg_name.getText().toString();
        String email = reg_email.getText().toString();
        final String password = reg_pwd.getText().toString();
        final String confirm_password = reg_confirmpwd.getText().toString();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm_password)){
            Toast.makeText(getActivity(), "Please fill in all the fields.", Toast.LENGTH_LONG).show();

        }
        else if (password.equals(confirm_password)){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                currentUser = mAuth.getCurrentUser();
                                saveUserData();
                                Toast.makeText(getActivity(), "Registered successfully!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                            } else {
                                Log.i("AUTHENTICATE", "pwd: "+password);
                                Log.i("AUTHENTICATE", "confirmed pwd: "+confirm_password);

                                // If sign in fails, display a message to the user.
                                Toast.makeText(getActivity(), "Password & Confirmed Password are different. Try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void saveUserData(){
        String name = reg_name.getText().toString();
        String email = reg_email.getText().toString();

        User userObj = new User(name, email);
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child(user.getUid()).setValue(userObj);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
