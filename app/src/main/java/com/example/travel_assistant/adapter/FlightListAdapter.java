package com.example.travel_assistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.model.FlightListModel;

import java.util.ArrayList;

public class FlightListAdapter extends BaseAdapter {

    Context context;
    ArrayList<FlightListModel> flightArrayList;
    LayoutInflater inflater;

    public FlightListAdapter(Context context, ArrayList<FlightListModel> flightArrayList) {
        this.context = context;
        this.flightArrayList = flightArrayList;
        Log.d("TAG", "getView: got in the adapter constructor");
        //inflater = (LayoutInflater.from(context));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return flightArrayList.size();
    }

    @Override
    public FlightListModel getItem(int i) {
        return flightArrayList.get(i);
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
        view = inflater.inflate(R.layout.flight_list_listview, null);
        TextView departureIATA = view.findViewById(R.id.departureIATATV);
        TextView arrivalIATA = view.findViewById(R.id.arrivalIATATV);
        TextView price = view.findViewById(R.id.priceTV);
        TextView airline = view.findViewById(R.id.airlineTV);
        departureIATA.setText(flightArrayList.get(i).departureIATA.replaceAll("\"", ""));
        arrivalIATA.setText(flightArrayList.get(i).arrivalIATA.replaceAll("\"", ""));
        price.append(flightArrayList.get(i).priceCurrency.replaceAll("\"", "") + " " + flightArrayList.get(i).priceTotal.replaceAll("\"", ""));
        airline.setText(flightArrayList.get(i).airline + " Airline");
        Log.d("TAG", "getView: got in the end of getView");
        return view;
    }

}
