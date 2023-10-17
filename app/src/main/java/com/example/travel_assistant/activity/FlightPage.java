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
import com.google.firebase.firestore.FirebaseFirestore;

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
        //departureLocationTV = findViewById(R.id.departureLocationTV);
        departureAirportTV = findViewById(R.id.departureAirportTV);
        arrivalTimeTV = findViewById(R.id.arrivalTimeTV);
        arrivalIATATV = findViewById(R.id.arrivalIATATV);
        //arrivalLocationTV = findViewById(R.id.arrivalLocationTV);
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

        //getLocation(flightLocation, flightDestination);
        getAirport(flightLocation,flightDestination);

        bookFlightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //flight data
                Intent toPayment = new Intent(getApplicationContext(), PaymentPage.class);
                toPayment.putExtra("paymentFor", "flightOnly");
                toPayment.putExtra("flightLocation", flightLocation);
                toPayment.putExtra("flightDestination", flightDestination);
                toPayment.putExtra("flightLocationName", depLocation.cityName);
                toPayment.putExtra("flightDestinationName", arrLocation.cityName);
                toPayment.putExtra("flightFromDate", fromDate);
                toPayment.putExtra("flightToDate", toDate);
                //toPayment.putExtra("depTerminal", depTerminal);
                //toPayment.putExtra("arrTerminal", arrTerminal);
                toPayment.putExtra("adultCount", adultCount);
                toPayment.putExtra("kidCount", kidCount);
                toPayment.putExtra("roundOrOneWayTrip", roundOrOneWayTrip);
                toPayment.putExtra("class", flightClass);
                toPayment.putExtra("airline", airline);
                toPayment.putExtra("flightPrice", price);
                toPayment.putExtra("flightCode", flight);
                toPayment.putExtra("flightItinerary", flightItinerary);

                startActivity(toPayment);
            }
        });

        lookForHotelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toHotelList = new Intent(getApplicationContext(), HotelList.class);

                //hotel data
                toHotelList.putExtra("hotelLocation", flightDestination);
                toHotelList.putExtra("hotelFromDate", fromDate);
                toHotelList.putExtra("hotelToDate", toDate);
                //toHotelList.putExtra("adultCount", adultCount);
                //toHotelList.putExtra("kidCount", kidCount);
                //toHotelList.putExtra("flightPrice", price);
                Log.d(TAG, "onClick: fromDate and toDate: " + fromDate + ", " + toDate);

                //flight data
                toHotelList.putExtra("flightLocation", flightLocation);
                toHotelList.putExtra("flightDestination", flightDestination);
                toHotelList.putExtra("flightLocationName", depLocation.cityName);
                toHotelList.putExtra("flightDestinationName", arrLocation.cityName);
                toHotelList.putExtra("flightFromDate", fromDate);
                toHotelList.putExtra("flightToDate", toDate);
                toHotelList.putExtra("adultCount", adultCount);
                toHotelList.putExtra("kidCount", kidCount);
                toHotelList.putExtra("roundOrOneWayTrip", roundOrOneWayTrip);
                toHotelList.putExtra("class", flightClass);
                toHotelList.putExtra("airline", airline);
                toHotelList.putExtra("flightPrice", price);
                toHotelList.putExtra("flightCode", flight);
                toHotelList.putExtra("flightItinerary", flightItinerary);

                startActivity(toHotelList);
            }
        });

    }

    public void getAirport(String depIata, String arrIata){

        String depIataCode = depIata;
        String arrIataCode = arrIata;
        int depIataResourceId = getResources().getIdentifier(depIataCode, "string", getPackageName());
        int arrIataResourceId = getResources().getIdentifier(arrIataCode, "string", getPackageName());

        if (depIataResourceId != 0) {
            String airportName = getResources().getString(depIataResourceId);
            // Use the resourceValue as needed
            Log.d("getString", "dep airportName: " + airportName);
            departureAirportTV.setText(airportName);

        } else {
            // Handle the case where the resource is not found
            Log.d("getString", "can't get dep string ");
            departureAirportTV.setText("-");
        }

        if (arrIataResourceId != 0) {
            String airportName = getResources().getString(arrIataResourceId);
            // Use the resourceValue as needed
            Log.d("getString", "arr airportName: " + airportName);
            arrivalAirportTV.setText(airportName);

        } else {
            // Handle the case where the resource is not found
            Log.d("getString", "can't get arr string ");
            arrivalAirportTV.setText("-");
        }

    }

    public void getLocation(String dep, String arr){

        executor.execute(new Runnable() {
            @Override
            public void run() {

                //try to get departure location name
                try {

                    //location = "london";

                    Location[] departure = amadeus.referenceData.locations.get(Params
                            .with("keyword", dep)
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

                    depLocation = new LocationModel(depIata, depName, depCityName);

                }
                catch (Exception e){
                    Log.d(TAG, "getLocation: error: " + e);
                    depLocation = new LocationModel(dep, "-", "-");
                }

                //try to get arrival location name
                try {

                    //location = "london";

                    Location[] arrival = amadeus.referenceData.locations.get(Params
                            .with("keyword", arr)
                            .and("subType", Locations.ANY));


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

                    arrLocation = new LocationModel(arrIata, arrName, arrCityName);

                }
                catch (Exception e){
                    Log.d(TAG, "getLocation: error: " + e);
                    arrLocation = new LocationModel(arr, "-", "-");
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        departureAirportTV.setText(depLocation.location);
                        //departureLocationTV.setText(depLocation.cityName);
                        arrivalAirportTV.setText(arrLocation.location);
                        //arrivalLocationTV.setText(arrLocation.cityName);

                    }
                });

            }
        });



    }

}