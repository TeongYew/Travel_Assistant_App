package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.referencedata.Locations;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Location;
import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.FlightItineraryListAdapter;
import com.example.travel_assistant.adapter.FlightListAdapter;
import com.example.travel_assistant.model.FlightItineraryListModel;
import com.example.travel_assistant.model.FlightListModel;
import com.example.travel_assistant.others.LoadingDialog;
import com.example.travel_assistant.others.LoadingView;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FlightList extends AppCompatActivity {

    String flightLocation = "";
    String flightDestination = "";
    String fromDate = "";
    String toDate = "";
    String adultCount = "";
    String kidCount = "";
    String roundOrOneWayTrip = "";
    boolean directFlightsOnly = false;
    final String TAG = String.valueOf(FlightList.this);
    android.widget.ListView flightListLV;
    ArrayList<FlightListModel> flightArrayList = new ArrayList<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    Amadeus amadeus = Amadeus
            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
            .build();

    FlightListModel flightListData;
    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View itineraryPopupView;
    RelativeLayout flightListRL;
    ArrayList<FlightItineraryListModel> itineraryArrayList;
    LoadingDialog loadingDialog;

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

        loadingDialog = new LoadingDialog(this);

        flightListLV = findViewById(R.id.flightListLV);
        flightListRL = findViewById(R.id.flightListRL);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        itineraryArrayList = new ArrayList<>();


        Log.d(TAG, "flightlist, info before flight search: " + flightLocation + ", " + flightDestination + ", " + fromDate + ", " + toDate + ", " + adultCount + ", " + kidCount + ", " + directFlightsOnly + ", " + roundOrOneWayTrip);

        //start the loading animation and getFlights
        loadingDialog.show();
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
                                        .and("max", 20));
                    }
                    else if(roundOrOneWayTrip.equals("oneway")){
                        flightOffers = amadeus.shopping.flightOffersSearch.get(
                                Params.with("originLocationCode",flightLocation)
                                        .and("destinationLocationCode", flightDestination)
                                        .and("departureDate", fromDate)
                                        .and("adults", adultCount)
                                        .and("children", kidCount)
                                        .and("nonstop", String.valueOf(directFlightsOnly))
                                        .and("max", 20));
                    }
                    else{
                        flightOffers = amadeus.shopping.flightOffersSearch.get(
                                Params.with("originLocationCode",flightLocation)
                                        .and("destinationLocationCode", flightDestination)
                                        .and("departureDate", fromDate)
                                        .and("adults", adultCount)
                                        .and("children", kidCount)
                                        .and("nonstop", String.valueOf(directFlightsOnly))
                                        .and("max", 20));
                    }



                    Location[] locations = amadeus.referenceData.locations.get(Params
                            .with("keyword", "LON")
                            .and("subType", Locations.ANY));

                    JsonArray flightsData = flightOffers[0].getResponse().getResult().get("data").getAsJsonArray();

                    Log.d(TAG, "run: flightsData: " + flightOffers[0].getResponse().getResult().toString());

                    //each data is a flight
                    for(int i = 0; i < flightsData.size(); i++){

                        String duration = flightsData.get(i).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("duration").toString();
                        //each flight has its own itinerary
                        //but itinerary only has one
                        //different stops can get from the segment in the itinerary
                        JsonArray itinerarySegments = flightsData.get(i).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("segments").getAsJsonArray();

                        Log.d(TAG, "run: this is itinerary duration: " + duration);

                        String firstdepartureIata = itinerarySegments.get(0).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode").toString().replaceAll("\"", "");
                        String firstdepartureAt = itinerarySegments.get(0).getAsJsonObject().get("departure").getAsJsonObject().get("at").toString().replaceAll("\"", "");
                        String lastArrivalIata = itinerarySegments.get(itinerarySegments.size() - 1).getAsJsonObject().get("arrival").getAsJsonObject().get("iataCode").toString().replaceAll("\"", "");
                        String lastArrivalAt = itinerarySegments.get(itinerarySegments.size() - 1).getAsJsonObject().get("arrival").getAsJsonObject().get("at").toString().replaceAll("\"", "");
                        String priceCurrency = flightsData.get(i).getAsJsonObject().get("travelerPricings").getAsJsonArray().get(0).getAsJsonObject().get("price").getAsJsonObject().get("currency").toString().replaceAll("\"", "");
                        String priceTotal = flightsData.get(i).getAsJsonObject().get("travelerPricings").getAsJsonArray().get(0).getAsJsonObject().get("price").getAsJsonObject().get("total").toString().replaceAll("\"", "");
                        String cabin = flightsData.get(i).getAsJsonObject().get("travelerPricings").getAsJsonArray().get(0).getAsJsonObject().get("fareDetailsBySegment").getAsJsonArray().get(0).getAsJsonObject().get("cabin").toString().replaceAll("\"", "");
                        String airline = flightsData.get(i).getAsJsonObject().get("validatingAirlineCodes").getAsJsonArray().get(0).toString().replaceAll("\"", "");
                        JsonArray flightItinerary = flightsData.get(i).getAsJsonObject().get("itineraries").getAsJsonArray();



                        Log.d(TAG, "run: this is itinerary first departure and final arrival: " + firstdepartureIata + ", " + lastArrivalIata);

                        FlightListModel flight = new FlightListModel(firstdepartureIata, "", lastArrivalIata, "", firstdepartureAt, lastArrivalAt, priceCurrency, priceTotal, cabin, airline, flightItinerary);

                        flightArrayList.add(flight);

                        for(int x = 0; x < itinerarySegments.size(); x++){

                            String departureIata = itinerarySegments.get(x).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode").toString();
                            //String departureTerminal = itinerarySegments.get(x).getAsJsonObject().get("departure").getAsJsonObject().get("terminal").toString();
                            String departureTime = itinerarySegments.get(x).getAsJsonObject().get("departure").getAsJsonObject().get("at").toString();
                            String arrivalIata = itinerarySegments.get(x).getAsJsonObject().get("arrival").getAsJsonObject().get("iataCode").toString();
                            String arrivalTime = itinerarySegments.get(x).getAsJsonObject().get("arrival").getAsJsonObject().get("at").toString();

//                            String carrierCode = itinerarySegments.get(x).getAsJsonObject().get("carrierCode").toString();
//                            String number = itinerarySegments.get(x).getAsJsonObject().get("number").toString();
//                            String aircraftCode = itinerarySegments.get(x).getAsJsonObject().get("aircraft").getAsJsonObject().get("code").toString();



//                            Location[] departure = amadeus.referenceData.locations.get(Params
//                                    .with("keyword", departureIata)
//                                    .and("subType", Locations.ANY));
//
//                            Location[] arrival = amadeus.referenceData.locations.get(Params
//                                    .with("keyword", arrivalIata)
//                                    .and("subType", Locations.ANY));
//
//                            String departureName = departure[0].getResponse().getResult().get("data").getAsJsonArray().get(0).getAsJsonObject().get("address").getAsJsonObject().get("cityName").toString();
//                            String arrivalName = arrival[0].getResponse().getResult().get("data").getAsJsonArray().get(0).getAsJsonObject().get("address").getAsJsonObject().get("cityName").toString();

                            Log.d(TAG, "run: this is itinerary departure plan " + x + ": " + departureIata + ", " + departureTime);
                            Log.d(TAG, "run: this is itinerary arrival plan " + x + ": " + arrivalIata + ", " + arrivalTime);
                            //Log.d(TAG, "run: this is itinerary carrier code + number + aircraft code: " + x + ": " + carrierCode + ", " + number + ", " + aircraftCode);
                            //Log.d(TAG, "run: this is itinerary departure and arrival city name " + x + ": " + departureName + ", " + arrivalName);


                        }

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            //stop the loading animation
                            loadingDialog.cancel();

                            //code for initialising the listview
                            FlightListAdapter customAdapter = new FlightListAdapter(getApplicationContext(), flightArrayList);
                            flightListLV.setAdapter(customAdapter);

                            flightListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    itineraryPopupView = layoutInflater.inflate(R.layout.flight_list_popup, null);

                                    popupWindow = new PopupWindow(itineraryPopupView,width,height,focusable);

                                    flightListRL.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            popupWindow.showAtLocation(flightListRL, Gravity.CENTER,0,0);

                                        }
                                    });


                                    JsonArray itinerary = flightArrayList.get(i).getFlightItinerary();

                                    Log.d(TAG, "onItemClick: itinerary size: " + itinerary.size());

                                    for(int x = 0; x < itinerary.size(); x++){

                                        String departureIATA = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode").toString().replaceAll("\"", "");
                                        String arrivalIATA = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("arrival").getAsJsonObject().get("iataCode").toString().replaceAll("\"", "");
                                        String departureAt = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("at").toString().replaceAll("\"", "");
                                        String arrivalAt = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("arrival").getAsJsonObject().get("at").toString().replaceAll("\"", "");
                                        String carrierCode = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("carrierCode").toString().replaceAll("\"", "");
                                        String number = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("number").toString().replaceAll("\"", "");
                                        String aircraftCode = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("aircraft").getAsJsonObject().get("code").toString().replaceAll("\"", "");
                                        String duration = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("duration").toString().replaceAll("\"", "");

                                        String departureTerminal = "-";
                                        String arrivalTerminal = "-";

                                        try {
                                            departureTerminal = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("terminal").toString().replaceAll("\"", "");
                                            Log.d(TAG, "run: departure terminal: " + departureTerminal);
                                        }
                                        catch (NullPointerException e){
                                            Log.d(TAG, "run: departure nullpointerexception terminal: " + e);
                                            departureTerminal = "-";
                                        }

                                        try {
                                            arrivalTerminal = itinerary.get(x).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("arrival").getAsJsonObject().get("terminal").toString().replaceAll("\"", "");
                                            Log.d(TAG, "run: arrival terminal: " + arrivalTerminal);
                                        }
                                        catch (NullPointerException e){
                                            Log.d(TAG, "run: arrival nullpointerexception terminal: " + e);
                                            arrivalTerminal = "-";
                                        }

                                        FlightItineraryListModel itineraryListModel = new FlightItineraryListModel(departureIATA, arrivalIATA, departureAt, arrivalAt, departureTerminal, arrivalTerminal, carrierCode,number, aircraftCode, duration);

                                        itineraryArrayList.add(itineraryListModel);

                                    }

                                    android.widget.ListView itineraryLV = itineraryPopupView.findViewById(R.id.itineraryLV);
                                    RelativeLayout itineraryListRL = itineraryPopupView.findViewById(R.id.flightPopupRL);
                                    Button toFlightPageBtn = itineraryPopupView.findViewById(R.id.toFlightPageBtn);
                                    TextView departureTV = itineraryPopupView.findViewById(R.id.departureTV);
                                    TextView arrivalTV = itineraryPopupView.findViewById(R.id.arrivalTV);

                                    departureTV.setText(flightArrayList.get(i).departureIATA);
                                    arrivalTV.setText(flightArrayList.get(i).arrivalIATA);
                                    FlightItineraryListAdapter itineraryAdapter = new FlightItineraryListAdapter(getApplicationContext(), itineraryArrayList);
                                    itineraryLV.setAdapter(itineraryAdapter);

                                    toFlightPageBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent toFlightPage = new Intent(getApplicationContext(), FlightPage.class);
                                            //toFlightPage.putExtra("location", flightLocation);
                                            //toFlightPage.putExtra("destination", flightDestination);
                                            toFlightPage.putExtra("location", itineraryArrayList.get(0).departureIATA);
                                            toFlightPage.putExtra("destination", itineraryArrayList.get(0).arrivalIATA);
                                            toFlightPage.putExtra("fromDate", fromDate);
                                            toFlightPage.putExtra("toDate", toDate);
                                            toFlightPage.putExtra("depTerminal", itineraryArrayList.get(0).departureTerminal);
                                            toFlightPage.putExtra("arrTerminal", itineraryArrayList.get(0).arrivalTerminal);
                                            toFlightPage.putExtra("adultCount", adultCount);
                                            toFlightPage.putExtra("kidCount", kidCount);
                                            toFlightPage.putExtra("directFlightsOnly", directFlightsOnly);
                                            toFlightPage.putExtra("roundOrOneWayTrip", roundOrOneWayTrip);
                                            toFlightPage.putExtra("class", flightArrayList.get(i).cabin);
                                            toFlightPage.putExtra("airline", flightArrayList.get(i).airline);
                                            toFlightPage.putExtra("flightCurrency", flightArrayList.get(i).priceCurrency);
                                            toFlightPage.putExtra("flightPrice", flightArrayList.get(i).priceTotal);
                                            toFlightPage.putExtra("flight", itineraryArrayList.get(0).aircraftCode + itineraryArrayList.get(0).number);
                                            //toFlightPage.putExtra("flightData", flightArrayList.get(i));
                                            toFlightPage.putExtra("flightItinerary", itineraryArrayList);
                                            startActivity(toFlightPage);

                                        }
                                    });

                                    itineraryListRL.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View view, MotionEvent motionEvent) {
                                            itineraryArrayList.clear();
                                            itineraryAdapter.notifyDataSetChanged();
                                            popupWindow.dismiss();
                                            return true;
                                        }
                                    });

                                }
                            });

                        }
                    });


                }
                catch (Exception e){
                    //stop loading animation
                    loadingDialog.cancel();
                    Log.d(TAG, "onCreate: Exception(in flight call): " + e.getMessage());
                }

            }
        });

    }

}