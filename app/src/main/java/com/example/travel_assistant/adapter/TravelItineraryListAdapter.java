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
import com.example.travel_assistant.model.ItineraryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TravelItineraryListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ItineraryModel> itineraryArrayList;
    LayoutInflater inflater;

    public TravelItineraryListAdapter(Context context, ArrayList<ItineraryModel> itineraryArrayList) {
        this.context = context;
        this.itineraryArrayList = itineraryArrayList;
        //Log.d("TAG", "getView: got in the adapter constructor");
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
        view = inflater.inflate(R.layout.travel_itinerary_list_listview, null);
        TextView itineraryNameTV = view.findViewById(R.id.itineraryNameTV);
        TextView itineraryDateTV = view.findViewById(R.id.itineraryDateTV);
        itineraryNameTV.setText(itineraryArrayList.get(i).itineraryName);

        try{

            Date itineraryFromDate = new SimpleDateFormat("dd/MM/yyyy").parse(itineraryArrayList.get(i).itineraryDateFrom);
            Date itineraryToDate = new SimpleDateFormat("dd/MM/yyyy").parse(itineraryArrayList.get(i).itineraryDateTo);

            Calendar fromCal = Calendar.getInstance();
            Calendar toCal = Calendar.getInstance();

            fromCal.setTime(itineraryFromDate);
            toCal.setTime(itineraryToDate);


            SimpleDateFormat day_month = new SimpleDateFormat("dd MMM");
            String fromDayMonth = day_month.format(fromCal.getTime());
            String toDayMonth = day_month.format(toCal.getTime());

            itineraryDateTV.setText(fromDayMonth + " - " + toDayMonth);

        }catch(Exception e){
            throw new RuntimeException(e);
        }



        //Log.d("TAG", "getView: got in the end of getView");
        return view;
    }

}
