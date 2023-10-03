package com.example.travel_assistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_assistant.model.FlightListModel;
import com.example.travel_assistant.model.ItineraryModel;
import com.example.travel_assistant.R;

import java.util.ArrayList;

public class ItineraryAdapter extends BaseAdapter {

    Context context;
    ArrayList<ItineraryModel> itineraryArrayList;
    LayoutInflater inflater;

    public ItineraryAdapter(Context context, ArrayList<ItineraryModel> itineraryArrayList) {
        this.context = context;
        this.itineraryArrayList = itineraryArrayList;
        //inflater = (LayoutInflater.from(context));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itineraryArrayList.size();
    }

    @Override
    public ItineraryModel getItem(int i) {
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
        view = inflater.inflate(R.layout.flight_list_listview, null);
        TextView departureIATA = view.findViewById(R.id.departureIATATV);
        TextView arrivalIATA = view.findViewById(R.id.arrivalIATATV);
        TextView price = view.findViewById(R.id.priceTV);
        TextView airline = view.findViewById(R.id.airlineTV);
        return view;
    }

}

