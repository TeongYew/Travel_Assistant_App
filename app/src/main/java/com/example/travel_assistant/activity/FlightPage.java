package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.referencedata.Locations;
import com.amadeus.resources.Location;
import com.example.travel_assistant.R;
import com.example.travel_assistant.model.FlightItineraryListModel;
import com.example.travel_assistant.model.LocationModel;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FlightPage extends AppCompatActivity {

    TextView departureTimeTV, departureIATATV, departureLocationTV,departureAirportTV;
    TextView arrivalTimeTV, arrivalIATATV, arrivalLocationTV, arrivalAirportTV;
    TextView flightAirlineTV, flightClassTV, flightCodeTV, flightDateTV, flightTerminalTV, flightPriceTV;
    Button bookFlightBtn, lookForHotelBtn;

    String flightLocation = "";
    String flightDestination = "";
    String fromDate = "";
    String toDate = "";
    String adultCount = "";
    String kidCount = "";
    String roundOrOneWayTrip = "";
    boolean directFlightsOnly = false;
    String flightClass = "";
    String airline = "";
    String flight = "";
    String depTerminal = "";
    String arrTerminal = "";
    String price = "";
    final String TAG = String.valueOf(FlightPage.this);


    ArrayList<FlightItineraryListModel> flightItinerary = new ArrayList<>();
    LocationModel depLocation;
    LocationModel arrLocation;
    Amadeus amadeus = Amadeus
            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
            .build();

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

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
        flightTerminalTV = findViewById(R.id.flightTerminalTV);
        flightPriceTV = findViewById(R.id.flightPriceTV);
        bookFlightBtn = findViewById(R.id.bookFlightBtn);
        lookForHotelBtn = findViewById(R.id.lookForHotelBtn);

        Intent intent = getIntent();
        flightLocation = intent.getStringExtra("location");
        flightDestination = intent.getStringExtra("destination");
        fromDate = intent.getStringExtra("fromDate");
        toDate = intent.getStringExtra("toDate");
        depTerminal = intent.getStringExtra("depTerminal");
        arrTerminal = intent.getStringExtra("arrTerminal");
        adultCount = intent.getStringExtra("adultCount");
        kidCount = intent.getStringExtra("kidCount");
        directFlightsOnly = intent.getBooleanExtra("directFlightsOnly",false);
        roundOrOneWayTrip = intent.getStringExtra("roundOrOneWayTrip");
        flightClass = intent.getStringExtra("class");
        airline = intent.getStringExtra("airline");
        price = intent.getStringExtra("price");
        flight = intent.getStringExtra("flight");
        flightItinerary = (ArrayList<FlightItineraryListModel>) intent.getSerializableExtra("flightItinerary");

        departureIATATV.setText(flightLocation);
        arrivalIATATV.setText(flightDestination);
        departureTimeTV.setText(fromDate);
        arrivalTimeTV.setText(toDate);
        flightClassTV.setText(flightClass);
        flightAirlineTV.setText(airline + " Airlines");
        flightCodeTV.setText(flight);
        flightTerminalTV.setText(depTerminal);
        flightPriceTV.setText(price);

        getLocation(flightLocation, flightDestination);

        bookFlightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toPayment = new Intent(getApplicationContext(), PaymentPage.class);
                startActivity(toPayment);
            }
        });

        lookForHotelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toHotelList = new Intent(getApplicationContext(), HotelList.class);
                toHotelList.putExtra("location", flightDestination);
                toHotelList.putExtra("fromDate", fromDate);
                toHotelList.putExtra("toDate", toDate);
                toHotelList.putExtra("adultCount", adultCount);
                toHotelList.putExtra("kidCount", kidCount);
                toHotelList.putExtra("flightPrice", price);
                startActivity(toHotelList);
            }
        });

    }

    public void getLocation(String dep, String arr){

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    //location = "london";

                    Location[] departure = amadeus.referenceData.locations.get(Params
                            .with("keyword", dep)
                            .and("subType", Locations.ANY));

                    Location[] arrival = amadeus.referenceData.locations.get(Params
                            .with("keyword", arr)
                            .and("subType", Locations.ANY));

                    String depName = departure[0].getResponse().getResult()
                            .getAsJsonObject().get("data")
                            .getAsJsonArray().get(0)
                            .getAsJsonObject().get("name")
                            .toString()
                            .replaceAll("\"", "");
                    String depIata = departure[0].getResponse().getResult()
                            .getAsJsonObject().get("data")
                            .getAsJsonArray().get(0)
                            .getAsJsonObject().get("iataCode")
                            .toString()
                            .replaceAll("\"", "");

                    String depCityName = departure[0].getResponse().getResult()
                            .getAsJsonObject().get("data")
                            .getAsJsonArray().get(0)
                            .getAsJsonObject().get("address")
                            .getAsJsonObject().get("cityName")
                            .toString()
                            .replaceAll("\"", "");

                    String arrName = arrival[0].getResponse().getResult()
                            .getAsJsonObject().get("data")
                            .getAsJsonArray().get(0)
                            .getAsJsonObject().get("name")
                            .toString()
                            .replaceAll("\"", "");
                    String arrIata = arrival[0].getResponse().getResult()
                            .getAsJsonObject().get("data")
                            .getAsJsonArray().get(0)
                            .getAsJsonObject().get("iataCode")
                            .toString()
                            .replaceAll("\"", "");

                    String arrCityName = arrival[0].getResponse().getResult()
                            .getAsJsonObject().get("data")
                            .getAsJsonArray().get(0)
                            .getAsJsonObject().get("address")
                            .getAsJsonObject().get("cityName")
                            .toString()
                            .replaceAll("\"", "");


                    depLocation = new LocationModel(depIata, depName, depCityName);
                    arrLocation = new LocationModel(arrIata, arrName, arrCityName);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            departureAirportTV.setText(depLocation.location);
                            departureLocationTV.setText(depLocation.cityName);
                            arrivalAirportTV.setText(arrLocation.location);
                            arrivalLocationTV.setText(arrLocation.cityName);

                        }
                    });

                }
                catch (Exception e){
                    Log.d(TAG, "getLocation: error: " + e);
                }


            }
        });



    }

}