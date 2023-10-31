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
import com.example.travel_assistant.adapter.HotelHistoryListAdapter;
import com.example.travel_assistant.model.HotelHistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HotelHistoryFragment extends Fragment {

    final String TAG = HotelHistoryFragment.this.toString();
    View view;
    android.widget.ListView hotelHistoryLV;
    String uid = "";
    ArrayList<HotelHistoryModel> hotelHistoryArrayList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_hotel_history, container, false);

        hotelHistoryLV = view.findViewById(R.id.hotelHistoryLV);

        //get the current user's uid
        try {
            uid = auth.getCurrentUser().getUid();
            //uid = "000001";
            Log.d(TAG, "uid:" + uid);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        getHotelHistory();

        return view;
    }

    private void getHotelHistory(){

        db.collection("hotel_booking")
                .whereEqualTo("user_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String hotelBookingId = document.getData().get("hotel_booking_id").toString();
                                String hotelId = document.getData().get("hotel_id").toString();
                                String hotelName = document.getData().get("hotel_name").toString();
                                String offerId = document.getData().get("offer_id").toString();
                                String hotelCheckIn = document.getData().get("check_in").toString();
                                String hotelCheckOut = document.getData().get("check_out").toString();
                                String hotelPrice = document.getData().get("hotel_price").toString();

                                String description = "";

                                try{
                                    description = document.getData().get("description").toString();

                                }
                                catch (Exception e){
                                    Log.d(TAG, "onComplete: exception getting hotel description: " + e);
                                }



                                HotelHistoryModel hotelHistoryModel = new HotelHistoryModel(hotelBookingId, hotelId, hotelName, offerId, hotelCheckIn, hotelCheckOut, hotelPrice, description);

                                hotelHistoryArrayList.add(hotelHistoryModel);

                            }

                            HotelHistoryListAdapter customAdapter = new HotelHistoryListAdapter(view.getContext(), hotelHistoryArrayList);
                            hotelHistoryLV.setAdapter(customAdapter);

                            hotelHistoryLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

}