package com.example.travel_assistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_assistant.model.ItineraryModel;
import com.example.travel_assistant.R;

import java.util.ArrayList;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ItineraryModel> itineraryModelArrayList;

    // Constructor
    public ItineraryAdapter(Context context, ArrayList<ItineraryModel> itineraryModelArrayList) {
        this.context = context;
        this.itineraryModelArrayList = itineraryModelArrayList;
    }

    @NonNull
    @Override
    public ItineraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        ItineraryModel model = itineraryModelArrayList.get(position);
        holder.itineraryNameTV.setText(model.getItineraryName());
        holder.itineraryRatingTV.setText("" + model.getItineraryRating());
        holder.itineraryIV.setImageResource(model.getItineraryImage());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return itineraryModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itineraryIV;
        private final TextView itineraryNameTV;
        private final TextView itineraryRatingTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itineraryIV = itemView.findViewById(R.id.itineraryIV);
            itineraryNameTV = itemView.findViewById(R.id.itineraryNameTV);
            itineraryRatingTV = itemView.findViewById(R.id.itineraryRatingTV);
        }
    }
}

