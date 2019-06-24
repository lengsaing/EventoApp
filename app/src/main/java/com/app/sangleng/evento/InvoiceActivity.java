package com.app.sangleng.evento;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class InvoiceActivity extends AppCompatActivity {

    TextView orderNumber;
    TextView orderDate;
    TextView ticketPrice;
    TextView discountPrice;
    TextView totalPrice;
    Button btnConfirm;

    private DatabaseReference mDatabase;

    int eventCount = 0;
    String eventID;
    String userID;
    String userName;
    String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        orderNumber = findViewById(R.id.orderNumber);
        orderDate = findViewById(R.id.orderDate);
        ticketPrice = findViewById(R.id.ticketPrice);
        discountPrice = findViewById(R.id.discountPrice);
        totalPrice = findViewById(R.id.totalPrice);
        btnConfirm = findViewById(R.id.btnConfirm);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tickets");

        final DatabaseReference newInvoiceID = mDatabase.push();

        eventID = getIntent().getStringExtra("eventID");
        userID = getIntent().getStringExtra("userID");
        userName = getIntent().getStringExtra("userName");
        price = getIntent().getStringExtra("price");

        orderNumber.setText(""+ newInvoiceID.getKey().toString());

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);
        orderDate.setText(formattedDate);

        ticketPrice.setText(price);
        discountPrice.setText("- $0");
        totalPrice.setText(price);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newInvoiceID.child("userID").setValue(userID);
                newInvoiceID.child("userName").setValue(userName);
                newInvoiceID.child("purchase_date").setValue(formattedDate);
                newInvoiceID.child("price").setValue(price);
                newInvoiceID.child("discount").setValue(discountPrice.getText().toString());
                newInvoiceID.child("totalPrice").setValue(totalPrice.getText().toString());

                final DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventID);

                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("count").exists()){
                            int pastCount = ((Long) dataSnapshot.child("count").getValue()).intValue();
                            eventCount = pastCount + 1;

                            eventRef.child("count").setValue(eventCount);
                            Log.i("EventCount", eventCount+"");
                        }
                        else{
                            eventCount = 1;
                            eventRef.child("count").setValue(eventCount);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                setResult(RESULT_OK);
                finish();
            }
        });

    }
}
