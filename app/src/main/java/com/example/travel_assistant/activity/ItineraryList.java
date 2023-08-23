package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.ItineraryAdapter;
import com.example.travel_assistant.model.ItineraryModel;

import java.util.ArrayList;

public class ItineraryList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_list);

        RecyclerView courseRV = findViewById(R.id.itineraryRV);

        // Here, we have created new array list and added data to it
        ArrayList<ItineraryModel> courseModelArrayList = new ArrayList<ItineraryModel>();
//        courseModelArrayList.add(new ItineraryModel("DSA in Java", 4, R.drawable.gfgimage));
//        courseModelArrayList.add(new ItineraryModel("Java Course", 3, R.drawable.gfgimage));
//        courseModelArrayList.add(new ItineraryModel("C++ Course", 4, R.drawable.gfgimage));

        // we are initializing our adapter class and passing our arraylist to it.
        ItineraryAdapter itineraryAdapter = new ItineraryAdapter(this, courseModelArrayList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        courseRV.setLayoutManager(linearLayoutManager);
        courseRV.setAdapter(itineraryAdapter);

    }
}