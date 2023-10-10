package com.example.travel_assistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.model.ItineraryDayModel;
import com.example.travel_assistant.model.ItineraryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TravelItineraryDayAdapter extends BaseAdapter {

    Context context;
    ArrayList<ItineraryDayModel> itineraryDayArrayList;
    LayoutInflater inflater;

    public TravelItineraryDayAdapter(Context context, ArrayList<ItineraryDayModel> itineraryDayArrayList) {
        this.context = context;
        this.itineraryDayArrayList = itineraryDayArrayList;
        //Log.d("TAG", "getView: got in the adapter constructor");
        //inflater = (LayoutInflater.from(context));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itineraryDayArrayList.size();
    }

    @Override
    public ItineraryDayModel getItem(int i) {
        return itineraryDayArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("TAG", "getView: got in getView");
//        View vi = view;
//        if(vi == null){
//            vi = inflater.inflate(R.layout.location_search_listview, null);
//        }
        view = inflater.inflate(R.layout.travel_itinerary_day_listview, null);
        TextView itineraryLocationTV = view.findViewById(R.id.itineraryLocationTV);
        TextView itineraryTimeTV = view.findViewById(R.id.itineraryTimeTV);

        itineraryLocationTV.setText(itineraryDayArrayList.get(i).locationName);
        itineraryTimeTV.setText(itineraryDayArrayList.get(i).locationTimeFrom);

        //Log.d("TAG", "getView: got in the end of getView");
        return view;
    }

}
