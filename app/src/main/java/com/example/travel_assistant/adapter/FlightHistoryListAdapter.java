package com.example.travel_assistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.model.FlightHistoryModel;
import com.example.travel_assistant.model.HotelHistoryModel;

import java.util.ArrayList;

public class FlightHistoryListAdapter extends BaseAdapter {

    Context context;
    ArrayList<FlightHistoryModel> flightHistoryArrayList;
    LayoutInflater inflater;

    public FlightHistoryListAdapter(Context context, ArrayList<FlightHistoryModel> flightHistoryArrayList) {
        this.context = context;
        this.flightHistoryArrayList = flightHistoryArrayList;
        //inflater = (LayoutInflater.from(context));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return flightHistoryArrayList.size();
    }

    @Override
    public FlightHistoryModel getItem(int i) {
        return flightHistoryArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.flight_history_listview, null);
        TextView flightAirline = view.findViewById(R.id.flightAirlineTV);
        //TextView flightCode = view.findViewById(R.id.flightCodeTV);
        TextView departureIata = view.findViewById(R.id.departureIataTV);
        TextView departureAt = view.findViewById(R.id.departureAtTV);
        TextView arrivalIata = view.findViewById(R.id.arrivalIataTV);
        TextView arrivalAt = view.findViewById(R.id.arrivalAtTV);
        TextView flightPrice = view.findViewById(R.id.flightPriceTV);

        flightAirline.setText(flightHistoryArrayList.get(i).flightAirline + " " + flightHistoryArrayList.get(i).flightCode);
        //flightCode.setText(flightHistoryArrayList.get(i).flightCode);
        departureIata.setText(flightHistoryArrayList.get(i).departureIATA);
        departureAt.setText(flightHistoryArrayList.get(i).departureAt);
        arrivalIata.setText(flightHistoryArrayList.get(i).arrivalIATA);
        arrivalAt.setText(flightHistoryArrayList.get(i).arrivalAt);
        flightPrice.setText(flightHistoryArrayList.get(i).flightPrice);

        return view;
    }
}
