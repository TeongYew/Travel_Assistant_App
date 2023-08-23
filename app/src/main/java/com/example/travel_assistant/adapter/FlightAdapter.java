package com.example.travel_assistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_assistant.model.FlightModel;
import com.example.travel_assistant.R;

import java.util.ArrayList;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<FlightModel> flightModelArrayList;

    // Constructor
    public FlightAdapter(Context context, ArrayList<FlightModel> flightModelArrayList) {
        this.context = context;
        this.flightModelArrayList = flightModelArrayList;
    }

    @NonNull
    @Override
    public FlightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        FlightModel model = flightModelArrayList.get(position);
        holder.flightNameTV.setText(model.getFlightName());
        holder.flightRatingTV.setText("" + model.getFlightRating());
        holder.flightIV.setImageResource(model.getFlightImage());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return flightModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView flightIV;
        private final TextView flightNameTV;
        private final TextView flightRatingTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flightIV = itemView.findViewById(R.id.flightIV);
            flightNameTV = itemView.findViewById(R.id.flightNameTV);
            flightRatingTV = itemView.findViewById(R.id.flightRatingTV);
        }
    }
}