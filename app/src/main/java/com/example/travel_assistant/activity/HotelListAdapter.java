package com.example.travel_assistant.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amadeus.resources.TransferOffersPost;
import com.example.travel_assistant.R;

import java.util.ArrayList;

public class HotelListAdapter extends BaseAdapter {

    Context context;
    ArrayList<HotelListModel> hotelArrayList;
    LayoutInflater inflater;

    public HotelListAdapter(Context context, ArrayList<HotelListModel> hotelArrayList) {
        this.context = context;
        this.hotelArrayList = hotelArrayList;
        //inflater = (LayoutInflater.from(context));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return hotelArrayList.size();
    }

    @Override
    public HotelListModel getItem(int i) {
        return hotelArrayList.get(i);
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
        view = inflater.inflate(R.layout.hotel_list_listview, null);
        TextView hotelName = view.findViewById(R.id.hotelNameTV);
        hotelName.setText(hotelArrayList.get(i).hotelName.replaceAll("\"", ""));
        return view;
    }

}
