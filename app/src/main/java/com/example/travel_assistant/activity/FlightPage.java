package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.travel_assistant.R;

public class FlightPage extends AppCompatActivity {

    TextView departureTimeTV, departureIATATV, departureLocationTV,departureAirportTV;
    TextView arrivalTimeTV, arrivalIATATV, arrivalLocationTV, arrivalAirportTV;
    TextView flightAirlineTV, flightClassTV, flightCodeTV, flightDateTV, flightGateTV, flightSeatTV;
    Button bookFlightBtn, lookForHotelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_page);

        departureTimeTV = findViewById(R.id.departureTimeTV);
        departureIATATV = findViewById(R.id.departureIATATV);
        departureLocationTV = findViewById(R.id.departureLocationTV);
        departureAirportTV = findViewById(R.id.departureAirportTV);
        arrivalTimeTV = findViewById(R.id.arrivalTimeTV);
        arrivalIATATV = findViewById(R.id.arrivalIATATV);
        arrivalLocationTV = findViewById(R.id.arrivalLocationTV);
        arrivalAirportTV = findViewById(R.id.arrivalAirportTV);
        flightAirlineTV = findViewById(R.id.flightAirlineTV);
        flightClassTV = findViewById(R.id.flightClassTV);
        flightCodeTV = findViewById(R.id.flightCodeTV);
        flightDateTV = findViewById(R.id.flightDateTV);
        flightGateTV = findViewById(R.id.flightGateTV);
        flightSeatTV = findViewById(R.id.flightSeatTV);
        bookFlightBtn = findViewById(R.id.bookFlightBtn);
        lookForHotelBtn = findViewById(R.id.lookForHotelBtn);


    }
}