package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.referencedata.Locations;
import com.amadeus.resources.Location;
import com.example.travel_assistant.R;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {

    Amadeus amadeus = Amadeus
            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
            .build();

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private final String TAG = "TestActivity";
    LocationListData locationListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        firstMethod();


    }
    public void firstMethod(){
        secondMethod();
    }

    public void secondMethod(){
        getLocation();
    }

    public void getLocation(){

        String location = "london";

        ArrayList<LocationListData> locationArrayList = new ArrayList<>();

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    //location = "london";

                    Location[] locations = amadeus.referenceData.locations.get(Params
                            .with("keyword", location)
                            .and("subType", Locations.ANY));



                    Log.d(TAG, "getLocation: location: " + locations[0]);
                    Log.d(TAG, "getLocation: location: " + locations[0].getResponse().getResult());
                    Log.d(TAG, "getLocation: location data: " + locations[0].getResponse().getResult().getAsJsonObject().get("data"));
                    Log.d(TAG, "getLocation: location data count: " + locations[0].getResponse().getResult().getAsJsonObject().get("data").getAsJsonArray().size());
                    Log.d(TAG, "getLocation: location first data name: " + locations[0].getResponse().getResult().getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("name"));
                    Log.d(TAG, "getLocation: location first data name: " + locations[0].getResponse().getResult().getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("iataCode"));



                    for (int i = 0; i < locations[0].getResponse().getResult().getAsJsonObject().get("data").getAsJsonArray().size(); i++){

                        String location = locations[0].getResponse().getResult()
                                .getAsJsonObject().get("data")
                                .getAsJsonArray().get(i)
                                .getAsJsonObject().get("name")
                                .toString();
                        String iata = locations[0].getResponse().getResult()
                                .getAsJsonObject().get("data")
                                .getAsJsonArray().get(i)
                                .getAsJsonObject().get("iataCode")
                                .toString();

                        Log.d(TAG, "run: dis is the " + i + " location data: " + location);
                        Log.d(TAG, "run: dis is the " + i + " iata data: " + iata);

                        locationListData = new LocationListData(iata, location);
                        locationArrayList.add(locationListData);

                    }

                    for (int i = 0; i < locationArrayList.size(); i++){

                        Log.d(TAG, "run: dis is the " + i + " location data from arraylist: " + locationArrayList.get(i).location);
                        Log.d(TAG, "run: dis is the " + i + " iata data from arraylist: " + locationArrayList.get(i).iata);

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            android.widget.ListView flightFromLV = findViewById(R.id.flightFromLVTest);

                            Log.d(TAG, "run: getapplicationcontext: " + getApplicationContext().toString());
                            Log.d(TAG, "run: getapplicationcontext: " + getBaseContext().toString());

                            LocationSearchAdapter customAdapter = new LocationSearchAdapter(getBaseContext(), locationArrayList);
                            flightFromLV.setAdapter(customAdapter);

                        }
                    });

                    Log.d(TAG, "run: reached end of execute");

                }
                catch (Exception e){
                    Log.d(TAG, "getLocation: error: " + e);
                }


            }
        });

    }

}