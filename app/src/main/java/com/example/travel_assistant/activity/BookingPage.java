package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travel_assistant.R;

public class BookingPage extends AppCompatActivity {

    EditText bookingNameET, bookingEmailET, bookingPhoneET;
    Button bookingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);

        bookingNameET = findViewById(R.id.bookingNameET);
        bookingEmailET = findViewById(R.id.bookingEmailET);
        bookingPhoneET = findViewById(R.id.bookingPhoneET);
        bookingBtn = findViewById(R.id.bookingBtn);

        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(bookingNameET.getText()) || TextUtils.isEmpty(bookingEmailET.getText()) || TextUtils.isEmpty(bookingPhoneET.getText())){
                    Toast.makeText(BookingPage.this, "Please make sure all the necessary fields are filled", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent toPayment = new Intent(BookingPage.this, PaymentPage.class);
                    toPayment.putExtra("name", bookingNameET.getText());
                    toPayment.putExtra("email", bookingEmailET.getText());
                    toPayment.putExtra("phone", bookingPhoneET.getText());

                    startActivity(toPayment);

                }

            }
        });

    }
}