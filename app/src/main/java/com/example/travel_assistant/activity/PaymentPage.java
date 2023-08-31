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

public class PaymentPage extends AppCompatActivity {

    EditText cardNameET, cardNumET, cardMonthET, cardYearEt, cardCVCET;
    Button paymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        cardNameET = findViewById(R.id.cardNameET);
        cardNumET = findViewById(R.id.cardNumET);
        cardMonthET = findViewById(R.id.cardMonthET);
        cardYearEt = findViewById(R.id.cardYearET);
        cardCVCET = findViewById(R.id.cardCVCET);
        paymentBtn = findViewById(R.id.paymentBtn);

        Intent fromBooking = getIntent();
        String bookingName = fromBooking.getStringExtra("name");
        String bookingEmail = fromBooking.getStringExtra("email");
        String bookingPhone = fromBooking.getStringExtra("phone");

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(cardNameET.getText()) || TextUtils.isEmpty(cardNumET.getText()) || TextUtils.isEmpty(cardMonthET.getText()) || TextUtils.isEmpty(cardYearEt.getText()) || TextUtils.isEmpty(cardCVCET.getText())){
                    Toast.makeText(PaymentPage.this, "Please make sure all the necessary fields are filled", Toast.LENGTH_SHORT).show();
                }
                else{


                }

            }
        });


    }
}