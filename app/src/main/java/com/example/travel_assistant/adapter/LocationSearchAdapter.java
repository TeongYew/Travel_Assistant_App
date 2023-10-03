package com.example.travel_assistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.model.LocationModel;

import java.util.ArrayList;

public class LocationSearchAdapter extends BaseAdapter {

    Context context;
    ArrayList<LocationModel> locationArrayList;
    LayoutInflater inflater;

    public LocationSearchAdapter(Context context, ArrayList<LocationModel> locationArrayList) {
        this.context = context;
        this.locationArrayList = locationArrayList;
        Log.d("TAG", "getView: got in the adapter constructor");
        //inflater = (LayoutInflater.from(context));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return locationArrayList.size();
    }

    @Override
    public LocationModel getItem(int i) {
        return locationArrayList.get(i);
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
        view = inflater.inflate(R.layout.location_search_listview, null);
        TextView iata = view.findViewById(R.id.iataTV);
        TextView location = view.findViewById(R.id.locationTV);
        iata.setText(locationArrayList.get(i).iata);
        location.setText(locationArrayList.get(i).location);
        Log.d("TAG", "getView: got in the end of getView");
        return view;
    }
}
