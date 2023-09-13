//package com.example.travel_assistant.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//
//import com.amadeus.Amadeus;
//import com.amadeus.Params;
//import com.amadeus.referencedata.Locations;
//import com.amadeus.resources.FlightOfferSearch;
//import com.amadeus.resources.Location;
//import com.example.travel_assistant.R;
//import com.google.android.material.navigation.NavigationView;
//import com.google.android.material.tabs.TabLayout;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.gson.JsonObject;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//public class test {
//
//    DrawerLayout dLayout;
//    TabLayout flightTL;
//    FirebaseAuth auth;
//    RelativeLayout fromRl, toRL, departureArrivalRL, passengersRL, mainMenuRL;
//    private final String TAG = "MainMenu";
//    private Executor executor = Executors.newSingleThreadExecutor();
//    private Handler handler = new Handler(Looper.getMainLooper());
//    LocationListData locationListData;
//    ArrayList<LocationListData> locationArrayList = new ArrayList<>();
//    LayoutInflater layoutInflater;
//    int width = ViewGroup.LayoutParams.MATCH_PARENT;
//    int height = ViewGroup.LayoutParams.MATCH_PARENT;
//    boolean focusable = true;
//    PopupWindow popupWindow;
//
//    Amadeus amadeus = Amadeus
//            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
//            .build();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_menu);
//
//        mainMenuRL = findViewById(R.id.mainMenuRL);
//        flightTL = findViewById(R.id.flightTL);
//        fromRl = findViewById(R.id.fromRL);
//        toRL = findViewById(R.id.toRL);
//        departureArrivalRL = findViewById(R.id.departureArrivalRL);
//        passengersRL = findViewById(R.id.passengersRL);
//        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//
//        auth = FirebaseAuth.getInstance();
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        dLayout = findViewById(R.id.drawerLayout);
//
//        NavigationView navView = findViewById(R.id.navigationView);
//        navView.setNavigationItemSelectedListener(this);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.open, R.string.close);
//        dLayout.addDrawerListener(toggle);
//
//        toggle.syncState();
//
//        getFlights();
//
//        flightTL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()){
//                    case 0:
//                        //round trip
//
//                        break;
//                    case 1:
//                        //one way
//
//                        break;
//                    default:
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//        fromRl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: got into from click listener");
//                createPopUpWindow("from");
//            }
//        });
//
//        toRL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createPopUpWindow("to");
//            }
//        });
//
//        departureArrivalRL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        passengersRL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createPopUpWindow("passengers");
//            }
//        });
//
//
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        if(item.getItemId() == R.id.navItinerary){
//            startActivity(new Intent(MainMenu.this, ItineraryList.class));
//        }
//        else if (item.getItemId() == R.id.navHistory) {
//            //startActivity(new Intent(MainMenu.this, ));
//        }
//        else if (item.getItemId() == R.id.navReviews) {
//
//        }
//        else if (item.getItemId() == R.id.signOut) {
//            signOut();
//        }
//
//        dLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(dLayout.isDrawerOpen(GravityCompat.START)){
//            dLayout.closeDrawer(GravityCompat.START);
//        }
//        else {
//            super.onBackPressed();
//        }
//    }
//
//    private void signOut(){
//        auth.signOut();
//        Toast.makeText(this, "Successfully signed out!", Toast.LENGTH_SHORT).show();
//        startActivity(new Intent(MainMenu.this, PreLoginRegPage.class));
//    }
//
//    public void createPopUpWindow(String popup){
//
//        switch(popup){
//            case "from":
//
//                View fromPopupView = layoutInflater.inflate(R.layout.flight_from_popup, null);
//
//                popupWindow = new PopupWindow(fromPopupView,width,height,focusable);
//
//                mainMenuRL.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        popupWindow.showAtLocation(mainMenuRL, Gravity.CENTER,0,0);
//
//                    }
//                });
//
//                EditText flightFromET = fromPopupView.findViewById(R.id.flightFromET);
//                ImageButton flightFromIB = fromPopupView.findViewById(R.id.flightFromIB);
//
//                flightFromIB.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        getLocation(flightFromET.getText().toString());
//
//                    }
//                });
//
//                //need to change
//                RelativeLayout fromPopupRL = fromPopupView.findViewById(R.id.fromPopupRL);
//                fromPopupRL.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        popupWindow.dismiss();
//                        return true;
//                    }
//                });
//
//                break;
//
//            case "to":
//                View toPopupView = layoutInflater.inflate(R.layout.flight_to_popup, null);
//
//                popupWindow = new PopupWindow(toPopupView,width,height,focusable);
//                mainMenuRL.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        popupWindow.showAtLocation(mainMenuRL, Gravity.CENTER,0,0);
//
//                    }
//                });
//
//                break;
//
//            case "passengers":
//                View passengersPopupView = layoutInflater.inflate(R.layout.flight_passengers_popup, null);
//
//                popupWindow = new PopupWindow(passengersPopupView,width,height,focusable);
//                mainMenuRL.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        popupWindow.showAtLocation(mainMenuRL, Gravity.CENTER,0,0);
//
//                    }
//                });
//
//                break;
//
//            default:
//                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//    }
//
//    public void getFlights(){
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//
//
//                try {
//
//                    Log.d(TAG, "run: got into before setting flight offers");
//
//
//
//                    FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(
//                            Params.with("originLocationCode","SYD")
//                                    .and("destinationLocationCode", "BKK")
//                                    .and("departureDate", "2023-11-02")
//                                    .and("adults", 1));
//
//                    Location[] locations = amadeus.referenceData.locations.get(Params
//                            .with("keyword", "LON")
//                            .and("subType", Locations.ANY));
//
//                    Log.d(TAG, "run: got into before checking for flight call result");
//
//                    if (flightOffers[0].getResponse().getStatusCode() != 200) {
//                        Log.d(TAG, "run: got in error in calling flight");
//                        Log.d(TAG, "onCreate: Wrong status code for Flight Offers Search: " + flightOffers[0]
//                                .getResponse().getStatusCode());
//                    }
//                    else{
//                        Log.d(TAG, "run: got in success in calling flight");
//                        Log.d(TAG, "onCreate(flight results): " + flightOffers[0].getResponse().getResult());
//                        Log.d(TAG, "run: flight offer count: " + Arrays.stream(flightOffers).count());
//                        Log.d(TAG, "run: flight offer class: " + flightOffers.getClass());
//
//                        JsonObject jsonObject = flightOffers[0].getResponse().getResult();
//                        Log.d(TAG, "run: json meta: " + jsonObject.get("meta").getAsJsonObject().get("count"));
//                        Log.d(TAG, "run: json meta: " + jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("duration"));
//                        Log.d(TAG, "run: json meta: " + jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("itineraries").getAsJsonArray().get(0).getAsJsonObject().get("segments").getAsJsonArray().get(0).getAsJsonObject().get("departure").getAsJsonObject().get("iataCode"));
//
//                        String flightDuration = jsonObject.get("data").
//                                getAsJsonArray().get(0).getAsJsonObject().get("itineraries").
//                                getAsJsonArray().get(0).getAsJsonObject().get("duration").toString();
//
//                        String departureIata = jsonObject.get("data").
//                                getAsJsonArray().get(0).getAsJsonObject().get("itineraries").
//                                getAsJsonArray().get(0).getAsJsonObject().get("segments").
//                                getAsJsonArray().get(0).getAsJsonObject().get("departure").
//                                getAsJsonObject().get("iataCode").toString();
//
//                        Log.d(TAG, "run: duration: " + flightDuration);
//                        Log.d(TAG, "run: iata: " + departureIata);
//
//                        for(int i = 0; i < Arrays.stream(flightOffers).count(); i++){
//
//                        }
//
//                    }
//
//                }
//                catch (Exception e){
//                    Log.d(TAG, "onCreate: Exception(in flight call): " + e.getMessage());
//                }
//
//
//
//            }
//        });
//    }
//
//    public void getLocation(String location){
//
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//
//                    //location = "london";
//
//                    Location[] locations = amadeus.referenceData.locations.get(Params
//                            .with("keyword", location)
//                            .and("subType", Locations.ANY));
//
//                    Log.d(TAG, "getLocation: location: " + locations[0]);
//                    Log.d(TAG, "getLocation: location: " + locations[0].getResponse().getResult());
//
//                    String iata = "";
//                    String location = "";
//
//                    for (int i = 0; i < Arrays.stream(locations).count(); i++){
//
//                        locationListData = new LocationListData(iata, location);
//                        locationArrayList.add(locationListData);
//
//                    }
//
//                    View fromPopupView = layoutInflater.inflate(R.layout.flight_from_popup, null);
//
//                    android.widget.ListView flightFromLV = (android.widget.ListView)fromPopupView.findViewById(R.id.flightFromLV);
//
//                    FlightFromAdapter customAdapter = new FlightFromAdapter(getApplicationContext(), locationArrayList);
//                    flightFromLV.setAdapter(customAdapter);
//
//                }
//                catch (Exception e){
//                    Log.d(TAG, "getLocation: error: " + e);
//                }
//
//
//            }
//        });
//
//    }
//
//}
