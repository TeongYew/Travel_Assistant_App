package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Hotel;
import com.example.travel_assistant.R;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HotelList extends AppCompatActivity {

    final String TAG = String.valueOf(HotelList.this);
    android.widget.ListView hotelListLV;
    String location = "NYC";
    HotelListModel hotelListModel;
    ArrayList<HotelListModel> hotelArrayList = new ArrayList<>();

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    Amadeus amadeus = Amadeus
            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        hotelListLV = findViewById(R.id.hotelListLV);

//        Intent fromFlightPage = getIntent();
//        location = fromFlightPage.getStringExtra("location");

        Log.d(TAG, "onCreate: location: " + location);

        getHotel();

    }

    public void getHotel(){

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    Hotel[] hotelList = amadeus.referenceData.locations.hotels.byCity.get(Params.with("cityCode", location));

                    Log.d(TAG, "run: hotel list data: " + hotelList[0].getResponse().getResult().toString());

                    JsonArray hotelData = hotelList[0].getResponse().getResult().get("data").getAsJsonArray();

                    for (int i = 0; i < hotelData.size(); i++){

                        String hotelName = hotelData.get(i).getAsJsonObject().get("name").toString().replaceAll("\"", "");
                        String hotelId = hotelData.get(i).getAsJsonObject().get("hotelId").toString().replaceAll("\"", "");

                        Log.d(TAG, "run: name and id: " + hotelName + ", " + hotelId);

                        hotelListModel = new HotelListModel(hotelName, hotelId);

                        hotelArrayList.add(hotelListModel);

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            HotelListAdapter customAdapter = new HotelListAdapter(getApplicationContext(), hotelArrayList);
                            hotelListLV.setAdapter(customAdapter);

                            hotelListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    //intent to go to hotel page
                                    Intent toHotelPage = new Intent(getApplicationContext(), HotelPage.class);
                                    toHotelPage.putExtra("hotelName", hotelArrayList.get(i).hotelName);
                                    toHotelPage.putExtra("hotelId", hotelArrayList.get(i).hotelId);
                                    startActivity(toHotelPage);

                                }
                            });

                        }
                    });

                }
                catch (Exception e){

                }



            }
        });

    }

}