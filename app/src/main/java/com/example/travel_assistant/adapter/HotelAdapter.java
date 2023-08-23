package com.example.travel_assistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_assistant.R;
import com.example.travel_assistant.model.FlightModel;
import com.example.travel_assistant.model.HotelModel;

import java.util.ArrayList;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<HotelModel> hotelModelArrayList;

    // Constructor
    public HotelAdapter(Context context, ArrayList<HotelModel> hotelModelArrayList) {
        this.context = context;
        this.hotelModelArrayList = hotelModelArrayList;
    }

    @NonNull
    @Override
    public HotelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_card_layout, parent, false);
        return new HotelAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        HotelModel model = hotelModelArrayList.get(position);
        holder.hotelNameTV.setText(model.getHotelName());
        holder.hotelRatingTV.setText("" + model.getHotelRating());
        holder.hotelIV.setImageResource(model.getHotelImage());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return hotelModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView hotelIV;
        private final TextView hotelNameTV;
        private final TextView hotelRatingTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelIV = itemView.findViewById(R.id.hotelIV);
            hotelNameTV = itemView.findViewById(R.id.hotelNameTV);
            hotelRatingTV = itemView.findViewById(R.id.hotelRatingTV);
        }
    }

}
