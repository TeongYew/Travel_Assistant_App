package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.travel_assistant.R;

public class HotelPage extends AppCompatActivity {

    final String TAG = String.valueOf(HotelPage.this);
    String hotelName = "";
    String hotelId = "";
    TextView hotelNameTV;
    Button toPaymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_page);

        hotelNameTV = findViewById(R.id.hotelNameTV);
        toPaymentBtn = findViewById(R.id.toPaymentBtn);

        Intent fromHotelList = getIntent();
        hotelName = fromHotelList.getStringExtra("hotelName");
        hotelId = fromHotelList.getStringExtra("hotelId");

        Log.d(TAG, "onCreate: hotel name and id: " + hotelName + ", " + hotelId);

        hotelNameTV.setText(hotelName);

        toPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toPaymentPage = new Intent(getApplicationContext(), PaymentPage.class);
                startActivity(toPaymentPage);

            }
        });

    }
}