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
    String offerId = "";
    String checkIn = "";
    String checkOut = "";
    String numBeds = "";
    String bedType = "";
    String description = "";
    String price = "";
    String flightPrice = "";
    TextView hotelNameTV, priceTV, checkInTV, checkOutTV, numBedsTV, bedTypeTV, descriptionTV;
    Button toPaymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_page);

        hotelNameTV = findViewById(R.id.hotelNameTV);
        priceTV = findViewById(R.id.priceTV);
        checkInTV = findViewById(R.id.checkInTV);
        checkOutTV = findViewById(R.id.checkOutTV);
        //numBedsTV = findViewById(R.id.numBedsTV);
        //bedTypeTV = findViewById(R.id.bedTypeTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        toPaymentBtn = findViewById(R.id.toPaymentBtn);

        Intent fromHotelList = getIntent();
        hotelName = fromHotelList.getStringExtra("hotelName");
        hotelId = fromHotelList.getStringExtra("hotelId");
        offerId = fromHotelList.getStringExtra("offerId");
        checkIn = fromHotelList.getStringExtra("checkIn");
        checkOut = fromHotelList.getStringExtra("checkOut");
        numBeds = fromHotelList.getStringExtra("numBeds");
        bedType = fromHotelList.getStringExtra("bedType");
        description = fromHotelList.getStringExtra("description");
        //description = "Marriott Senior Discount, includes\\n1 King, 285sqft/26sqm, Wireless internet, for\\na fee, Coffee/tea maker";
        price = fromHotelList.getStringExtra("price");
        flightPrice = fromHotelList.getStringExtra("flightPrice");

        Log.d(TAG, "onCreate: description: " + description);
        Log.d(TAG, "onCreate: description replace: " + description.replace("\\n", " "));

        Log.d(TAG, "onCreate: hotel name and id: " + hotelName + ", " + hotelId);

        hotelNameTV.setText(hotelName);
        priceTV.setText(price);
        checkInTV.setText(checkIn);
        checkOutTV.setText(checkOut);
        //bedTypeTV.setText(bedType + " Bed");
        //numBedsTV.setText(numBeds + " Bed(s)");
        descriptionTV.setText(description);

        toPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toPaymentPage = new Intent(getApplicationContext(), PaymentPage.class);
                startActivity(toPaymentPage);

            }
        });

    }
}