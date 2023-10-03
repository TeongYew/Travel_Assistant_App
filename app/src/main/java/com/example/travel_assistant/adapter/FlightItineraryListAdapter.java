package com.example.travel_assistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.model.FlightItineraryListModel;

import java.util.ArrayList;

public class FlightItineraryListAdapter extends BaseAdapter {

    Context context;
    ArrayList<FlightItineraryListModel> itineraryArrayList;
    LayoutInflater inflater;

    public FlightItineraryListAdapter(Context context, ArrayList<FlightItineraryListModel> itineraryArrayList) {
        this.context = context;
        this.itineraryArrayList = itineraryArrayList;
        Log.d("TAG", "getView: got in the adapter constructor");
        //inflater = (LayoutInflater.from(context));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itineraryArrayList.size();
    }

    @Override
    public FlightItineraryListModel getItem(int i) {
        return itineraryArrayList.get(i);
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
        view = inflater.inflate(R.layout.flight_itinerary_list_listview, null);
        TextView departureIATA = view.findViewById(R.id.departureIATATV);
        TextView arrivalIATA = view.findViewById(R.id.arrivalIATATV);
        TextView departureAt = view.findViewById(R.id.departureAtTV);
        TextView arrivalAt = view.findViewById(R.id.arrivalAtTV);
        TextView duration = view.findViewById(R.id.durationTV);
        departureIATA.setText(itineraryArrayList.get(i).departureIATA);
        arrivalIATA.setText(itineraryArrayList.get(i).arrivalIATA);
        departureAt.setText(itineraryArrayList.get(i).departureAt);
        arrivalAt.setText(itineraryArrayList.get(i).arrivalAt);
        duration.setText(itineraryArrayList.get(i).duration);
        Log.d("TAG", "getView: got in the end of getView");
        return view;
    }

}
