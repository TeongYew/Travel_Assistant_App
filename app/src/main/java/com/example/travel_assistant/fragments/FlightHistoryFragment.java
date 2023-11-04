package com.example.travel_assistant.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.FlightHistoryListAdapter;
import com.example.travel_assistant.model.FlightHistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FlightHistoryFragment extends Fragment {

    final String TAG = FlightHistoryFragment.this.toString();
    View view;
    android.widget.ListView flightHistoryLV;
    String uid = "";
    ArrayList<FlightHistoryModel> flightHistoryArrayList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_flight_history, container, false);

        flightHistoryLV = view.findViewById(R.id.flightHistoryLV);

        //get the current user's uid
        try {
            uid = auth.getCurrentUser().getUid();
            //uid = "000001";
            Log.d(TAG, "uid:" + uid);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        getFlightHistory();

        return view;
    }

    private void getFlightHistory(){

        db.collection("flight_booking")
                .whereEqualTo("user_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                String flightBookingId = document.getData().get("flight_booking_id").toString();
                                String flightDepartureIata = document.getData().get("departure_iata").toString();
                                String flightDepartureName = document.getData().get("departure_location").toString();
                                //String flightDepartureAt = document.getData().get("departure_date_time").toString();
                                String flightArrivalIata = document.getData().get("arrival_iata").toString();
                                String flightArrivalName = document.getData().get("arrival_location").toString();
                                //String flightArrivalAt = document.getData().get("arrival_date_time").toString();
                                String flightAirline = document.getData().get("airline").toString();
                                String flightClass = document.getData().get("class").toString();
                                String flightCode = document.getData().get("flight_code").toString();
                                String flightCurrency = document.getData().get("flight_currency").toString();
                                String flightPrice = document.getData().get("flight_price").toString();
                                String totalPrice = flightCurrency + " " + flightPrice;
                                String adultCount = document.getData().get("adult_count").toString();
                                String kidCount = document.getData().get("kid_count").toString();
                                boolean roundTrip = (boolean) document.getData().get("roundTrip");


                                //get the date time data from the first flight itinerary of the flight
                                db.collection("flight_itinerary")
                                        .whereEqualTo("user_uid", uid)
                                        .whereEqualTo("flight_booking_id", flightBookingId)
                                        .whereEqualTo("order", 1)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                                        String flightDepartureAt = document.getData().get("departure_date_time").toString().replaceAll("T", " ");
                                                        String flightArrivalAt = document.getData().get("arrival_date_time").toString().replaceAll("T", " ");


                                                        FlightHistoryModel flightHistoryModel = new FlightHistoryModel(flightBookingId, flightDepartureIata, flightDepartureName, flightDepartureAt, flightArrivalIata, flightArrivalName, flightArrivalAt, flightAirline, flightClass, flightCode, totalPrice, adultCount, kidCount, roundTrip);

                                                        flightHistoryArrayList.add(flightHistoryModel);

                                                    }
                                                    Log.d(TAG, "onComplete: flightArrayList size: " + flightHistoryArrayList.size());

                                                    FlightHistoryListAdapter customAdapter = new FlightHistoryListAdapter(getContext(), flightHistoryArrayList);
                                                    flightHistoryLV.setAdapter(customAdapter);

                                                    Log.d(TAG, "onComplete: done setting adapter");
                                                    flightHistoryLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                                                        }
                                                    });

                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}