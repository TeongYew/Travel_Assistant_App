package com.example.travel_assistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.model.HotelHistoryModel;
import com.example.travel_assistant.model.HotelListModel;

import java.util.ArrayList;

public class HotelHistoryListAdapter extends BaseAdapter {

    Context context;
    ArrayList<HotelHistoryModel> hotelHistoryArrayList;
    LayoutInflater inflater;

    public HotelHistoryListAdapter(Context context, ArrayList<HotelHistoryModel> hotelHistoryArrayList) {
        this.context = context;
        this.hotelHistoryArrayList = hotelHistoryArrayList;
        //inflater = (LayoutInflater.from(context));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return hotelHistoryArrayList.size();
    }

    @Override
    public HotelHistoryModel getItem(int i) {
        return hotelHistoryArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.hotel_history_listview, null);
        TextView hotelName = view.findViewById(R.id.hotelNameTV);
        TextView hotelCheckInOut = view.findViewById(R.id.hotelCheckInOutTV);
        //TextView hotelCheckOut = view.findViewById(R.id.hotelCheckOutTV);
        TextView hotelPrice = view.findViewById(R.id.hotelPriceTV);

        hotelName.setText(hotelHistoryArrayList.get(i).hotelName);
        hotelCheckInOut.setText(hotelHistoryArrayList.get(i).hotelCheckIn + " - " + hotelHistoryArrayList.get(i).hotelCheckOut);
        //hotelCheckOut.setText(hotelHistoryArrayList.get(i).hotelcheckOut);
        hotelPrice.setText(hotelHistoryArrayList.get(i).hotelPrice);

        return view;
    }
}
