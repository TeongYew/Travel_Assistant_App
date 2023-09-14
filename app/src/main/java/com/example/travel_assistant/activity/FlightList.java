package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.referencedata.Locations;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Location;
import com.example.travel_assistant.R;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FlightList extends AppCompatActivity {

    String flightLocation = "";
    String flightDestination = "";
    String fromDate = "";
    String toDate = "";
    String adultCount = "0";
    String kidCount = "0";
    String roundOrOneWayTrip = "round";
    boolean directFlightsOnly = false;
    final String TAG = String.valueOf(FlightList.this);
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    Amadeus amadeus = Amadeus
            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);

        Intent intent = getIntent();
        flightLocation = intent.getStringExtra("location");
        flightDestination = intent.getStringExtra("destination");
        fromDate = intent.getStringExtra("fromDate");
        toDate = intent.getStringExtra("toDate");
        adultCount = intent.getStringExtra("adultCount");
        kidCount = intent.getStringExtra("kidCount");
        directFlightsOnly = intent.getBooleanExtra("directFlightsOnly",false);
        roundOrOneWayTrip = intent.getStringExtra("roundOrOneWayTrip");

        Log.d(TAG, "flightlist, info before flight search: " + flightLocation + ", " + flightDestination + ", " + fromDate + ", " + toDate + ", " + adultCount + ", " + kidCount + ", " + directFlightsOnly + ", " + roundOrOneWayTrip);

        getFlights();

    }

    public void getFlights(){

        executor.execute(new Runnable() {
            @Override
            public void run() {


                try {

                    Log.d(TAG, "run: got into before setting flight offers");

                    FlightOfferSearch[] flightOffers;

                    if(roundOrOneWayTrip.equals("round")){
                        flightOffers = amadeus.shopping.flightOffersSearch.get(
                                Params.with("originLocationCode",flightLocation)
                                        .and("destinationLocationCode", flightDestination)
                                        .and("departureDate", fromDate)
                                        .and("returnDate", toDate)
                                        .and("adults", adultCount)
                                        .and("children", kidCount)
                                        .and("nonstop", String.valueOf(directFlightsOnly)));
                    }
                    else if(roundOrOneWayTrip.equals("oneway")){
                        flightOffers = amadeus.shopping.flightOffersSearch.get(
                                Params.with("originLocationCode",flightLocation)
                                        .and("destinationLocationCode", flightDestination)
                                        .and("departureDate", fromDate)
                                        .and("adults", adultCount)
                                        .and("children", kidCount)
                                        .and("nonstop", String.valueOf(directFlightsOnly)));
                    }
                    else{
                        flightOffers = amadeus.shopping.flightOffersSearch.get(
                                Params.with("originLocationCode",flightLocation)
                                        .and("destinationLocationCode", flightDestination)
                                        .and("departureDate", fromDate)
                                        .and("adults", adultCount)
                                        .and("children", kidCount)
                                        .and("nonstop", String.valueOf(directFlightsOnly)));
                    }



                    Location[] locations = amadeus.referenceData.locations.get(Params
                            .with("keyword", "LON")
                            .and("subType", Locations.ANY));

                    Log.d(TAG, "run: got into before checking for flight call result");


                    Log.d(TAG, "run: got in success in calling flight");
                    Log.d(TAG, "onCreate(flight results): " + flightOffers[0].getResponse().getResult());
                    Log.d(TAG, "run: flight offer count: " + Arrays.stream(flightOffers).count());
                    Log.d(TAG, "run: flight offer class: " + flightOffers.getClass());

                    JsonObject jsonObject = flightOffers[0].getResponse().getResult();
                    Log.d(TAG, "run: json meta: " + jsonObject.get("meta").getAsJsonObject().get("count"));
                    Log.d(TAG, "run: json meta: " + jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("duration"));
                    Log.d(TAG, "run: json meta: " + jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode"));

                    String flightDuration = jsonObject.get("data").
                            getAsJsonArray().get(0).getAsJsonObject().get("itineraries").
                            getAsJsonArray().get(0).getAsJsonObject().get("duration").toString();

                    String departureIata = jsonObject.get("data").
                            getAsJsonArray().get(0).getAsJsonObject().get("itineraries").
                            getAsJsonArray().get(0).getAsJsonObject().get("segments").
                            getAsJsonArray().get(0).getAsJsonObject().get("departure").
                            getAsJsonObject().get("iataCode").toString();

                    Log.d(TAG, "run: duration: " + flightDuration);
                    Log.d(TAG, "run: iata: " + departureIata);

                    for(int i = 0; i < Arrays.stream(flightOffers).count(); i++){

                    }

                }
                catch (Exception e){
                    Log.d(TAG, "onCreate: Exception(in flight call): " + e.getMessage());
                }



            }
        });

    }

}