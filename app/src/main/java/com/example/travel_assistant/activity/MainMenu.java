package com.example.travel_assistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amadeus.Params;
import com.amadeus.referencedata.Locations;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Location;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.LocationSearchAdapter;
import com.example.travel_assistant.fragments.PreLoginFragment;
import com.example.travel_assistant.model.LocationModel;
import com.example.travel_assistant.others.LoadingDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import com.amadeus.Amadeus;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainMenu";

    //layouts
    DrawerLayout dLayout;
    TabLayout flightTL;
    FirebaseAuth auth;
    RelativeLayout fromRl, toRL, departureArrivalRL, passengersRL, mainMenuRL;
    TextView departureNameTV, arrivalNameTV, dateTV, passengersTV;
    Button searchFlightBtn;
    CheckBox directFlightCB;
    LoadingDialog loadingDialog;

    //for async methods
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    //popup window
    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View fromPopupView;
    View toPopupView;

    //data for flight
    LocationModel locationListData;
    //ArrayList<LocationListData> locationArrayList = new ArrayList<>();
    String flightLocation = "";
    String flightDestination = "";
    String fromDate = "";
    String toDate = "";
    String adultCount = "0";
    String kidCount = "0";
    String roundOrOneWayTrip = "round";
    boolean directFlightsOnly = false;
    int year,month,day;

    //amadeus api
    Amadeus amadeus = Amadeus
            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
            .build();
    String aviationAK = "2dc38c7582b03a9963f2fe39eeac574a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //intialize all the layouts
        mainMenuRL = findViewById(R.id.mainMenuRL);
        flightTL = findViewById(R.id.flightTL);
        fromRl = findViewById(R.id.fromRL);
        toRL = findViewById(R.id.toRL);
        departureArrivalRL = findViewById(R.id.departureArrivalRL);
        passengersRL = findViewById(R.id.passengersRL);
        departureNameTV = findViewById(R.id.departureNameTV);
        arrivalNameTV = findViewById(R.id.arrivalNameTV);
        dateTV = findViewById(R.id.dateTV);
        passengersTV = findViewById(R.id.passengersTV);
        searchFlightBtn = findViewById(R.id.searchFlightBtn);
        directFlightCB = findViewById(R.id.directFlightCB);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        loadingDialog = new LoadingDialog(this);

        //initialise firebase auth
        auth = FirebaseAuth.getInstance();

        //initialise toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dLayout = findViewById(R.id.drawerLayout);

        NavigationView navView = findViewById(R.id.navigationView);
        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.open, R.string.close);
        dLayout.addDrawerListener(toggle);

        toggle.syncState();

        //getFlights();

        flightTL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        //round trip
                        roundOrOneWayTrip = "round";
                        break;
                    case 1:
                        //one way
                        roundOrOneWayTrip = "oneway";
                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fromRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUpWindow("from");
            }
        });

        toRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUpWindow("to");
            }
        });

        departureArrivalRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get the instance of calendar.
                final Calendar c = Calendar.getInstance();

                //get current day, month and year.
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                //create date picker dialog.
                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        MainMenu.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //check if the month and day string has only 1 number
                                // if only 1 number, add a 0 in front to avoid date string formatting errors
                                String yearStr = String.valueOf(year);
                                String monthStr = String.valueOf(monthOfYear + 1);
                                String dayStr = String.valueOf(dayOfMonth);

                                if(monthStr.length() < 2){
                                    monthStr = "0" + monthStr;
                                }

                                if (dayStr.length() < 2){
                                    dayStr = "0" + dayStr;
                                }

                                //set the fromDate to the selected date
                                fromDate = yearStr + "-" + monthStr + "-" + dayStr;

                                //create another date picker for toDate
                                DatePickerDialog toDatePickerDialog = new DatePickerDialog(
                                        // on below line we are passing context.
                                        MainMenu.this,
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year,
                                                                  int monthOfYear, int dayOfMonth) {

                                                //check if the month and day string has only 1 number
                                                // if only 1 number, add a 0 in front to avoid formatting errors
                                                String yearStr = String.valueOf(year);
                                                String monthStr = String.valueOf(monthOfYear + 1);
                                                String dayStr = String.valueOf(dayOfMonth);

                                                if(monthStr.length() < 2){
                                                    monthStr = "0" + monthStr;
                                                }

                                                if (dayStr.length() < 2){
                                                    dayStr = "0" + dayStr;
                                                }

                                                //set toDate to the selected date
                                                toDate = yearStr + "-" + monthStr + "-" + dayStr;

                                                //set the dateTV with fromDate and toDate
                                                dateTV.setText(fromDate + " - " + toDate);

                                            }
                                        },
                                        //pass year, month, and day for selected date in our date picker.
                                        year, monthOfYear, dayOfMonth);

                                toDatePickerDialog.show();

                            }
                        },
                        //pass year, month, and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                fromDatePickerDialog.show();

            }
        });

        passengersRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUpWindow("passengers");
            }
        });

        directFlightCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if direct flight checkbox is selected, then set directFlightsOnly to true
                // if not then set it to false
                if (directFlightCB.isChecked()){
                    directFlightsOnly = true;
                }
                else if (!directFlightCB.isChecked()) {
                    directFlightsOnly = false;
                }

            }
        });

        searchFlightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: info before flight search: " + flightLocation + ", " + flightDestination + ", " + fromDate + ", " + toDate + ", " + adultCount + ", " + kidCount + ", " + directFlightsOnly + ", " + roundOrOneWayTrip);

                //check if any of the required fields are empty
                //if empty the display a toast message
                if(TextUtils.isEmpty(flightLocation) || TextUtils.isEmpty(flightDestination) || TextUtils.isEmpty(fromDate) || TextUtils.isEmpty(toDate) || adultCount.equals("0")){
                    Toast.makeText(MainMenu.this, "Please make sure all the necessary details for your flight have been provided.", Toast.LENGTH_SHORT).show();
                }
                else {

                    //if all the required fields are filled, send the user to FlightList activity
                    Intent toFlightList = new Intent(getApplicationContext(), FlightList.class);
                    //pass the necessary variables to FlightList activity
                    toFlightList.putExtra("location", flightLocation);
                    toFlightList.putExtra("destination", flightDestination);
                    toFlightList.putExtra("fromDate", fromDate);
                    toFlightList.putExtra("toDate", toDate);
                    toFlightList.putExtra("adultCount", adultCount);
                    toFlightList.putExtra("kidCount", kidCount);
                    toFlightList.putExtra("directFlightsOnly", directFlightsOnly);
                    toFlightList.putExtra("roundOrOneWayTrip", roundOrOneWayTrip);

                    startActivity(toFlightList);

                }

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //send the user to the selected activity
        if(item.getItemId() == R.id.navItinerary){
            startActivity(new Intent(MainMenu.this, ItineraryList.class));
        }
        else if (item.getItemId() == R.id.navPhrases) {
            startActivity(new Intent(MainMenu.this, CommonPhrases.class));
        }
        else if (item.getItemId() == R.id.navHistory) {
            startActivity(new Intent(MainMenu.this, BookingHistory.class));
        }
        else if (item.getItemId() == R.id.navReviews) {

        }
        else if (item.getItemId() == R.id.signOut) {
            signOut();
        }

        dLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(dLayout.isDrawerOpen(GravityCompat.START)){
            dLayout.closeDrawer(GravityCompat.START);
        }
        else {
            //super.onBackPressed();
            //if user back presses sign the user out
            signOut();
        }
    }

    private void signOut(){
        //sign out from firebase, finish the activity and send the user back to the pre login page
        auth.signOut();
        Toast.makeText(this, "Successfully signed out!", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(MainMenu.this, AuthFragmentContainer.class));
    }

    public void createPopUpWindow(String popup){

        switch(popup){
            case "from":

                //set a new popupview and popupwindow
                fromPopupView = layoutInflater.inflate(R.layout.flight_from_popup, null);

                popupWindow = new PopupWindow(fromPopupView,width,height,focusable);

                //display the popup window
                mainMenuRL.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.showAtLocation(mainMenuRL, Gravity.CENTER,0,0);

                    }
                });

                //initialise the variables from fromPopupView
                EditText flightFromET = fromPopupView.findViewById(R.id.flightFromET);
                ImageButton flightFromIB = fromPopupView.findViewById(R.id.flightFromIB);

                flightFromIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //display the loading animation and get the location
                        loadingDialog.show();
                        getLocation(flightFromET.getText().toString(), popup);
                        getLocation2(flightFromET.getText().toString(), popup);

                    }
                });

                //if user clicks on the side of the popup, dismiss the popup
                RelativeLayout fromPopupRL = fromPopupView.findViewById(R.id.fromPopupRL);
                fromPopupRL.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                break;

            case "to":

                //set the toPopupView
                toPopupView = layoutInflater.inflate(R.layout.flight_to_popup, null);

                popupWindow = new PopupWindow(toPopupView,width,height,focusable);

                //display the popup window
                mainMenuRL.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.showAtLocation(mainMenuRL, Gravity.CENTER,0,0);

                    }
                });

                //initialise the variables from toPopupView
                EditText flightToET = toPopupView.findViewById(R.id.flightToET);
                ImageButton flightToIB = toPopupView.findViewById(R.id.flightToIB);

                flightToIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //display the loading animation and get the location
                        loadingDialog.show();
                        getLocation(flightToET.getText().toString(), popup);
                        getLocation2(flightToET.getText().toString(), popup);

                    }
                });

                //if user clicks on the side of the popup, dismiss the popup window
                RelativeLayout toPopupRL = toPopupView.findViewById(R.id.toPopupRL);
                toPopupRL.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                break;

            case "passengers":

                //set the passengersPopupView
                View passengersPopupView = layoutInflater.inflate(R.layout.flight_passengers_popup, null);

                popupWindow = new PopupWindow(passengersPopupView,width,height,focusable);
                mainMenuRL.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.showAtLocation(mainMenuRL, Gravity.CENTER,0,0);

                    }
                });

                //initialise the variables from passengersPopupView
                EditText adultCountET, kidCountET;
                ImageButton addAdultIB, removeAdultIB, addKidIB, removeKidIB;

                adultCountET = passengersPopupView.findViewById(R.id.adultCountET);
                kidCountET = passengersPopupView.findViewById(R.id.kidCountET);
                addAdultIB = passengersPopupView.findViewById(R.id.addAdultIB);
                removeAdultIB = passengersPopupView.findViewById(R.id.removeAdultIB);
                addKidIB = passengersPopupView.findViewById(R.id.addKidIB);
                removeKidIB = passengersPopupView.findViewById(R.id.removeKidIB);

                adultCountET.setText(adultCount);
                kidCountET.setText(kidCount);

                addAdultIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //increase the adult count
                        String adultCountStr = adultCountET.getText().toString();
                        int updateCount = Integer.parseInt(adultCountStr) + 1;
                        adultCountET.setText(String.valueOf(updateCount));
                    }
                });

                removeAdultIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //minus the adult count
                        String adultCountStr = adultCountET.getText().toString();
                        int updateCount = Integer.parseInt(adultCountStr) - 1;
                        adultCountET.setText(String.valueOf(updateCount));
                    }
                });

                addKidIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //increase the kid count
                        String kidCountStr = kidCountET.getText().toString();
                        int updateCount = Integer.parseInt(kidCountStr) + 1;
                        kidCountET.setText(String.valueOf(updateCount));
                    }
                });

                removeKidIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //minus the kid count
                        String kidCountStr = kidCountET.getText().toString();
                        int updateCount = Integer.parseInt(kidCountStr) - 1;
                        kidCountET.setText(String.valueOf(updateCount));
                    }
                });

                RelativeLayout passengersPopupRL = passengersPopupView.findViewById(R.id.passengersPopupRL);
                passengersPopupRL.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        //set the adult and kid count and dismiss the passengers popup
                        adultCount = adultCountET.getText().toString();
                        kidCount = kidCountET.getText().toString();
                        passengersTV.setText(adultCount + " Adult(s), " + kidCount + " Kid(s)");
                        popupWindow.dismiss();
                        return true;
                    }
                });

                break;
            default:
                //display an error message
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public void getFlights(){
        executor.execute(new Runnable() {
            @Override
            public void run() {


                try {

                    FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(
                            Params.with("originLocationCode","LON")
                                    .and("destinationLocationCode", "NYC")
                                    .and("departureDate", "2023-11-02")
                                    .and("adults", 1)
                                    .and("max", 5));

                    Location[] locations = amadeus.referenceData.locations.get(Params
                            .with("keyword", "LON")
                            .and("subType", Locations.ANY));


                    if (flightOffers[0].getResponse().getStatusCode() != 200) {
                        Log.d(TAG, "run: got in error in calling flight");
                        Log.d(TAG, "onCreate: Wrong status code for Flight Offers Search: " + flightOffers[0]
                                .getResponse().getStatusCode());
                    }
                    else{
                        Log.d(TAG, "run: got in success in calling flight");
                        Log.d(TAG, "onCreate(flight results): " + flightOffers[0].getResponse().getResult());
//                        Log.d(TAG, "run: flight offer count: " + Arrays.stream(flightOffers).count());
//                        Log.d(TAG, "run: flight offer class: " + flightOffers.getClass());

                        JsonObject jsonObject = flightOffers[0].getResponse().getResult();
//                        Log.d(TAG, "run: json meta: " + jsonObject.get("meta").getAsJsonObject().get("count"));
//                        Log.d(TAG, "run: json meta: " + jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("duration"));
//                        Log.d(TAG, "run: json meta: " + jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode"));

                        JsonArray flightsData = flightOffers[0].getResponse().getResult().get("data").getAsJsonArray();

                        //each data is a flight
                        for(int i = 0; i < flightsData.size(); i++){

                            String duration = flightsData.get(i).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("duration").toString();
                            //each flight has its own itinerary
                            //but itinerary only has one
                            //different stops can get from the segment in the itinerary
                            JsonArray itinerarySegments = flightsData.get(i).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("segments").getAsJsonArray();

                            Log.d(TAG, "run: this is itinerary duration: " + duration);

                            String firstdepartureIata = itinerarySegments.get(0).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode").toString();
                            String lastArrivalIata = itinerarySegments.get(itinerarySegments.size() - 1).getAsJsonObject().get("arrival").getAsJsonObject().get("iataCode").toString();



                            Log.d(TAG, "run: this is itinerary first departure and final arrival: " + firstdepartureIata + ", " + lastArrivalIata);

                            for(int x = 0; x < itinerarySegments.size(); x++){

                                String departureIata = itinerarySegments.get(x).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode").toString();
                                //String departureTerminal = itinerarySegments.get(x).getAsJsonObject().get("departure").getAsJsonObject().get("terminal").toString();
                                String departureTime = itinerarySegments.get(x).getAsJsonObject().get("departure").getAsJsonObject().get("at").toString();
                                String arrivalIata = itinerarySegments.get(x).getAsJsonObject().get("arrival").getAsJsonObject().get("iataCode").toString();
                                String arrivalTime = itinerarySegments.get(x).getAsJsonObject().get("arrival").getAsJsonObject().get("at").toString();

                                String carrierCode = itinerarySegments.get(x).getAsJsonObject().get("carrierCode").toString();
                                String number = itinerarySegments.get(x).getAsJsonObject().get("number").toString();
                                String aircraftCode = itinerarySegments.get(x).getAsJsonObject().get("aircraft").getAsJsonObject().get("code").toString();

                                Location[] departure = amadeus.referenceData.locations.get(Params
                                        .with("keyword", departureIata)
                                        .and("subType", Locations.ANY));

                                Location[] arrival = amadeus.referenceData.locations.get(Params
                                        .with("keyword", arrivalIata)
                                        .and("subType", Locations.ANY));

                                String departureName = departure[0].getResponse().getResult().get("data").getAsJsonArray().get(0).getAsJsonObject().get("address").getAsJsonObject().get("cityName").toString();
                                String arrivalName = arrival[0].getResponse().getResult().get("data").getAsJsonArray().get(0).getAsJsonObject().get("address").getAsJsonObject().get("cityName").toString();

                                Log.d(TAG, "run: this is itinerary departure plan " + x + ": " + departureIata + ", " + departureTime);
                                Log.d(TAG, "run: this is itinerary arrival plan " + x + ": " + arrivalIata + ", " + arrivalTime);
                                Log.d(TAG, "run: this is itinerary carrier code + number + aircraft code: " + x + ": " + carrierCode + ", " + number + ", " + aircraftCode);
                                Log.d(TAG, "run: this is itinerary departure and arrival city name " + x + ": " + departureName + ", " + arrivalName);


                            }

                        }

                        //itinerary data
                        JsonArray itineraries = flightsData.get(0).getAsJsonObject().get("itineraries").getAsJsonArray();

                        String duration = itineraries.get(0).getAsJsonObject().get("duration").toString();
                        String departureIata = itineraries.get(0).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode").toString();
                        String departureTerminal = itineraries.get(0).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("terminal").toString();
                        String departureTime = itineraries.get(0).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("at").toString();
                        String arrivalIata = itineraries.get(0).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("arrival").getAsJsonObject().get("iataCode").toString();
                        String arrivalTime = itineraries.get(0).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("arrival").getAsJsonObject().get("at").toString();

                        //departure to final arrival data
                        JsonArray itinerarySegments = itineraries.get(0).getAsJsonObject().get("segments").getAsJsonArray();
                        String firstdepartureIata = itinerarySegments.get(0).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode").toString();
                        String lastArrivalIata = itinerarySegments.get(itinerarySegments.size() - 1).getAsJsonObject().get("arrival").getAsJsonObject().get("iataCode").toString();


                        //Log.d(TAG, "run: this is itinerary departure plan" + duration + ", " + departureIata + ", " + departureTerminal + ", " + departureTime);
                        //Log.d(TAG, "run: this is itinerary arrival plan" + duration + ", " + arrivalIata + ", " + arrivalTime);

//                        Log.d(TAG, "run: this is itinerary first departure and final arrival: " + firstdepartureIata + ", " + lastArrivalIata);

                        String flightDuration = jsonObject.get("data").
                                getAsJsonArray().get(0).getAsJsonObject().get("itineraries").
                                getAsJsonArray().get(0).getAsJsonObject().get("duration").toString();

                        String departureIata1 = jsonObject.get("data").
                                getAsJsonArray().get(0).getAsJsonObject().get("itineraries").
                                getAsJsonArray().get(0).getAsJsonObject().get("segments").
                                getAsJsonArray().get(0).getAsJsonObject().get("departure").
                                getAsJsonObject().get("iataCode").toString();


                    }

                }
                catch (Exception e){
                    Log.d(TAG, "onCreate: Exception(in flight call): " + e.getMessage());
                }



            }
        });
    }

    public void getLocation2(String location, String popup){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://api.aviationstack.com/v1/cities";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(MainMenu.this, "got a response!", Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "onResponse: aviation: " + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("access_key", aviationAK);
                paramV.put("search", location);
                return paramV;
            }
        };
        queue.add(stringRequest);

    }

    public void getLocation(String location, String popup){

        //create a new location model to hold the location data
        ArrayList<LocationModel> locationArrayList = new ArrayList<>();

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    //location = "london";

                    //call the amadeus location search api
                    Location[] locations = amadeus.referenceData.locations.get(Params
                            .with("keyword", location)
                            .and("subType", Locations.ANY));



                    Log.d(TAG, "getLocation: location: " + locations[0]);
                    Log.d(TAG, "getLocation: location: " + locations[0].getResponse().getResult());
                    Log.d(TAG, "getLocation: location data: " + locations[0].getResponse().getResult().getAsJsonObject().get("data"));
                    Log.d(TAG, "getLocation: location data count: " + locations[0].getResponse().getResult().getAsJsonObject().get("data").getAsJsonArray().size());
                    Log.d(TAG, "getLocation: location first data name: " + locations[0].getResponse().getResult().getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("name"));
                    Log.d(TAG, "getLocation: location first data name: " + locations[0].getResponse().getResult().getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("iataCode"));


                    //get all the location results and add it into locationArrayList
                    for (int i = 0; i < locations[0].getResponse().getResult().getAsJsonObject().get("data").getAsJsonArray().size(); i++){

                        String location = locations[0].getResponse().getResult()
                                .getAsJsonObject().get("data")
                                .getAsJsonArray().get(i)
                                .getAsJsonObject().get("name")
                                .toString()
                                .replaceAll("\"", "");
                        String iata = locations[0].getResponse().getResult()
                                .getAsJsonObject().get("data")
                                .getAsJsonArray().get(i)
                                .getAsJsonObject().get("iataCode")
                                .toString()
                                .replaceAll("\"", "");

                        String cityName = locations[0].getResponse().getResult()
                                .getAsJsonObject().get("data")
                                .getAsJsonArray().get(i)
                                .getAsJsonObject().get("address")
                                .getAsJsonObject().get("cityName")
                                .toString()
                                .replaceAll("\"", "");

                        Log.d(TAG, "run: dis is the " + i + " location data: " + location);
                        Log.d(TAG, "run: dis is the " + i + " iata data: " + iata);

                        locationListData = new LocationModel(iata, location, cityName);
                        locationArrayList.add(locationListData);

                    }

//                    for (int i = 0; i < locationArrayList.size(); i++){
//
//                        Log.d(TAG, "run: dis is the " + i + " location data from arraylist: " + locationArrayList.get(i).location);
//                        Log.d(TAG, "run: dis is the " + i + " iata data from arraylist: " + locationArrayList.get(i).iata);
//
//                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            //stop loading animation
                            loadingDialog.cancel();

                            //create an adapter for the listview and use the locationArrayList data
                            LocationSearchAdapter customAdapter = new LocationSearchAdapter(getApplicationContext(), locationArrayList);

                            //depending on which popup is currently being displayed,
                            //populate the listview and set the layout variable accordingly
                            if (popup.equals("from")){

                                //populate the listview
                                android.widget.ListView flightFromLV = fromPopupView.findViewById(R.id.flightFromLV);

                                flightFromLV.setAdapter(customAdapter);

                                flightFromLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        //initialise the variables from the selected listview item
                                        TextView location = view.findViewById(R.id.locationTV);
                                        TextView iata = view.findViewById(R.id.iataTV);
                                        //get the data from the selected listview item
                                        //set the data to the global flightLocation variable
                                        String loc = location.getText().toString();
                                        flightLocation = iata.getText().toString();
                                        //Toast.makeText(MainMenu.this, "dis is loc: " + loc, Toast.LENGTH_SHORT).show();
                                        //set the departureNameTV text using the data from the selected listview item
                                        departureNameTV.setText(loc);
                                        //close the popup
                                        popupWindow.dismiss();

                                    }
                                });

                            }
                            else if (popup.equals("to")) {

                                //populate the listview
                                android.widget.ListView flightToLV = toPopupView.findViewById(R.id.flightToLV);
                                flightToLV.setAdapter(customAdapter);

                                flightToLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        //initialise the layout variables from the selected listview item
                                        TextView location = view.findViewById(R.id.locationTV);
                                        TextView iata = view.findViewById(R.id.iataTV);
                                        //get the data from the selected listview item
                                        //set the flightDestination variable with the data from the selected listview item
                                        String loc = location.getText().toString();
                                        flightDestination = iata.getText().toString();
                                        //Toast.makeText(MainMenu.this, "dis is loc: " + loc, Toast.LENGTH_SHORT).show();
                                        //set the arrivalNameTV text using the data from the selected listview item
                                        arrivalNameTV.setText(loc);
                                        //close the popup
                                        popupWindow.dismiss();

                                    }
                                });

                            }


                        }
                    });

                }
                catch (Exception e){
                    //stop loading animation
                    loadingDialog.cancel();
                    Log.d(TAG, "getLocation: error: " + e);
                }


            }
        });



    }
}