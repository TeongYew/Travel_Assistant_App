package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.model.FlightItineraryListModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HotelPage extends AppCompatActivity {

    final String TAG = String.valueOf(HotelPage.this);

    //hotel data
    String hotelName = "";
    String hotelId = "";
    String hotelOfferId = "";
    String hotelCheckIn = "";
    String hotelCheckOut = "";
    String numBeds = "";
    String bedType = "";
    String hotelDescription = "";
    String hotelImageURL = "";
    String hotelCurrency = "";
    String hotelPrice = "";

    //flight data
    String flightDepartureIATA = "";
    String flightArrivalIATA = "";
    String flightDepartureLocation = "";
    String flightArrivalLocation = "";
    String flightDepartureDateTime = "";
    String flightArrivalDateTime = "";
    String adultCount = "";
    String kidCount = "";
    String roundOrOneWayTrip = "";
    boolean roundTrip = false;
    String flightClass = "";
    String airline = "";
    String flightCode = "";
    String flightCurrency = "";
    String flightPrice = "";
    ArrayList<FlightItineraryListModel> flightItinerary = new ArrayList<>();

    //layouts
    TextView hotelNameTV, priceTV, checkInTV, checkOutTV, numBedsTV, bedTypeTV, descriptionTV;
    Button toPaymentBtn;
    ImageView hotelIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_page);

        //initialise the layouts
        hotelNameTV = findViewById(R.id.hotelNameTV);
        priceTV = findViewById(R.id.priceTV);
        checkInTV = findViewById(R.id.checkInTV);
        checkOutTV = findViewById(R.id.checkOutTV);
        //numBedsTV = findViewById(R.id.numBedsTV);
        //bedTypeTV = findViewById(R.id.bedTypeTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        toPaymentBtn = findViewById(R.id.toPaymentBtn);
        hotelIV = findViewById(R.id.hotelIV);

        //get the hotel and flight data from HotelList activity
        Intent fromHotelList = getIntent();

        //hotel data
        hotelName = fromHotelList.getStringExtra("hotelName");
        hotelId = fromHotelList.getStringExtra("hotelId");
        hotelOfferId = fromHotelList.getStringExtra("hotelOfferId");
        hotelCheckIn = fromHotelList.getStringExtra("hotelCheckIn");
        hotelCheckOut = fromHotelList.getStringExtra("hotelCheckOut");
        hotelImageURL = fromHotelList.getStringExtra("hotelImageURL");
        hotelDescription = fromHotelList.getStringExtra("hotelDescription");
        //description = "Marriott Senior Discount, includes\\n1 King, 285sqft/26sqm, Wireless internet, for\\na fee, Coffee/tea maker";
        hotelCurrency = fromHotelList.getStringExtra("hotelCurrency");
        hotelPrice = fromHotelList.getStringExtra("hotelPrice");
        //flightPrice = fromHotelList.getStringExtra("flightPrice");

        //flight data
        flightDepartureIATA = fromHotelList.getStringExtra("flightLocation");
        flightArrivalIATA = fromHotelList.getStringExtra("flightDestination");
        flightDepartureDateTime = fromHotelList.getStringExtra("flightFromDate");
        flightArrivalDateTime = fromHotelList.getStringExtra("flightToDate");
        flightDepartureLocation = fromHotelList.getStringExtra("flightLocationName");
        flightArrivalLocation = fromHotelList.getStringExtra("flightDestinationName");
        adultCount = fromHotelList.getStringExtra("adultCount");
        kidCount = fromHotelList.getStringExtra("kidCount");
        roundOrOneWayTrip = fromHotelList.getStringExtra("roundOrOneWayTrip");
        flightClass = fromHotelList.getStringExtra("class");
        airline = fromHotelList.getStringExtra("airline");
        flightCurrency = fromHotelList.getStringExtra("flightCurrency");
        flightPrice = fromHotelList.getStringExtra("flightPrice");
        flightCode = fromHotelList.getStringExtra("flightCode");
        flightItinerary = (ArrayList<FlightItineraryListModel>) fromHotelList.getSerializableExtra("flightItinerary");

        //Log.d(TAG, "onCreate: description: " + hotelDescription);
        //Log.d(TAG, "onCreate: description replace: " + hotelDescription.replace("\\n", " "));

        //Log.d(TAG, "onCreate: hotel name and id: " + hotelName + ", " + hotelId);

        //set the layouts with the hotel data
        hotelNameTV.setText(hotelName);
        Picasso.get().load(hotelImageURL).into(hotelIV);
        priceTV.setText(hotelCurrency + hotelPrice);
        checkInTV.setText(hotelCheckIn);
        checkOutTV.setText(hotelCheckOut);
        //bedTypeTV.setText(bedType + " Bed");
        //numBedsTV.setText(numBeds + " Bed(s)");
        descriptionTV.setText(hotelDescription);
        descriptionTV.setMovementMethod(new ScrollingMovementMethod());

        toPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //create new intent to send user to the PaymentPage activity
                //send the hotel and flight data to the PaymentPage activity
                Intent toPaymentPage = new Intent(getApplicationContext(), PaymentPage.class);
                toPaymentPage.putExtra("paymentFor", "flightAndHotel");

                //hotel data
                toPaymentPage.putExtra("hotelId", hotelId);
                toPaymentPage.putExtra("hotelName", hotelName);
                toPaymentPage.putExtra("hotelOfferId", hotelOfferId);
                toPaymentPage.putExtra("hotelCheckIn", hotelCheckIn);
                toPaymentPage.putExtra("hotelCheckOut", hotelCheckOut);
                toPaymentPage.putExtra("hotelDescription", hotelDescription);
                toPaymentPage.putExtra("hotelCurrency", hotelCurrency);
                toPaymentPage.putExtra("hotelPrice", hotelPrice);

                //flight data
                toPaymentPage.putExtra("flightLocation", flightDepartureIATA);
                toPaymentPage.putExtra("flightDestination", flightArrivalIATA);
                toPaymentPage.putExtra("flightLocationName", flightDepartureLocation);
                toPaymentPage.putExtra("flightDestinationName", flightArrivalLocation);
                toPaymentPage.putExtra("flightFromDate", flightDepartureDateTime);
                toPaymentPage.putExtra("flightToDate", flightArrivalDateTime);
                toPaymentPage.putExtra("adultCount", adultCount);
                toPaymentPage.putExtra("kidCount", kidCount);
                toPaymentPage.putExtra("roundOrOneWayTrip", roundOrOneWayTrip);
                toPaymentPage.putExtra("class", flightClass);
                toPaymentPage.putExtra("airline", airline);
                toPaymentPage.putExtra("flightCurrency", flightCurrency);
                toPaymentPage.putExtra("flightPrice", flightPrice);
                toPaymentPage.putExtra("flightCode", flightCode);
                toPaymentPage.putExtra("flightItinerary", flightItinerary);

                startActivity(toPaymentPage);

            }
        });

    }
}