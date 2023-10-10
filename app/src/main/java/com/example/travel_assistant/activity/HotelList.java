package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Hotel;
import com.amadeus.resources.HotelOfferSearch;
import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.HotelListAdapter;
import com.example.travel_assistant.model.FlightItineraryListModel;
import com.example.travel_assistant.model.HotelListModel;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HotelList extends AppCompatActivity {

    final String TAG = String.valueOf(HotelList.this);
    android.widget.ListView hotelListLV;

    //data for hotel search
    String location = "";
    String fromDate = "";
    String toDate = "";
    //String adultCount = "";
    //String kidCount = "";
    //String flightPrice = "";
    HotelListModel hotelListModel;
    ArrayList<HotelListModel> hotelArrayList = new ArrayList<>();

    //data from flight
    String flightDepartureIATA = "";
    String flightArrivalIATA = "";
    String flightDepartureLocation = "";
    String flightArrivalLocation = "";
    String flightDepartureDateTime = "";
    String flightArrivalDateTime = "";
    String adultCount = "";
    String kidCount = "";
    String roundOrOneWayTrip = "";
    boolean roundTrip = false;
    String flightClass = "";
    String airline = "";
    String flightCode = "";
    String flightPrice = "";
    ArrayList<FlightItineraryListModel> flightItinerary = new ArrayList<>();

    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View hotelPopupView;
    RelativeLayout hotelListRL;

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    Amadeus amadeus = Amadeus
            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        hotelListLV = findViewById(R.id.hotelListLV);
        hotelListRL = findViewById(R.id.hotelListRL);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        Intent fromFlightPage = getIntent();

        //hotel data
        location = fromFlightPage.getStringExtra("hotelLocation");
        fromDate = fromFlightPage.getStringExtra("hotelFromDate");
        toDate = fromFlightPage.getStringExtra("hotelToDate");
        //adultCount = fromFlightPage.getStringExtra("adultCount");
        //kidCount = fromFlightPage.getStringExtra("kidCount");
        //flightPrice = fromFlightPage.getStringExtra("flightPrice");

        //flight data
        flightDepartureIATA = fromFlightPage.getStringExtra("flightLocation");
        flightArrivalIATA = fromFlightPage.getStringExtra("flightDestination");
        flightDepartureDateTime = fromFlightPage.getStringExtra("flightFromDate");
        flightArrivalDateTime = fromFlightPage.getStringExtra("flightToDate");
        flightDepartureLocation = fromFlightPage.getStringExtra("flightLocationName");
        flightArrivalLocation = fromFlightPage.getStringExtra("flightDestinationName");
        adultCount = fromFlightPage.getStringExtra("adultCount");
        kidCount = fromFlightPage.getStringExtra("kidCount");
        roundOrOneWayTrip = fromFlightPage.getStringExtra("roundOrOneWayTrip");
        flightClass = fromFlightPage.getStringExtra("class");
        airline = fromFlightPage.getStringExtra("airline");
        flightPrice = fromFlightPage.getStringExtra("flightPrice");
        flightCode = fromFlightPage.getStringExtra("flightCode");
        flightItinerary = (ArrayList<FlightItineraryListModel>) fromFlightPage.getSerializableExtra("flightItinerary");


        Log.d(TAG, "onCreate: location: " + location);

        getHotel();

    }

    public void getHotel(){

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    Hotel[] hotelList = amadeus.referenceData.locations.hotels.byCity.get(Params.with("cityCode", location));

                    Log.d(TAG, "run: hotel list data: " + hotelList[0].getResponse().getResult().toString());

                    JsonArray hotelData = hotelList[0].getResponse().getResult().get("data").getAsJsonArray();

                    for (int i = 0; i < hotelData.size(); i++){

                        String hotelName = hotelData.get(i).getAsJsonObject().get("name").toString().replaceAll("\"", "");
                        String hotelId = hotelData.get(i).getAsJsonObject().get("hotelId").toString().replaceAll("\"", "");

                        Log.d(TAG, "run: name and id: " + hotelName + ", " + hotelId);

                        hotelListModel = new HotelListModel(hotelName, hotelId);

                        hotelArrayList.add(hotelListModel);

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            HotelListAdapter customAdapter = new HotelListAdapter(getApplicationContext(), hotelArrayList);
                            hotelListLV.setAdapter(customAdapter);

                            hotelListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {

                                            try{

                                                Log.d(TAG, "from hotel list: fromDate and toDate: " + fromDate + ", " + toDate);

                                                HotelOfferSearch[] hotelOffers = amadeus.shopping.hotelOffersSearch.get(Params
                                                        .with("hotelIds", hotelArrayList.get(i).hotelId)
                                                        .and("checkInDate", fromDate)
                                                        .and("checkOutDate", toDate)
                                                        .and("adult", Integer.valueOf(adultCount)));

                                                Log.d(TAG, "run: hotel offer data: " + hotelOffers[0].getResponse().getResult().toString());

                                                JsonArray hotelDataOffer = hotelOffers[0].getResponse().getResult().get("data").getAsJsonArray().get(0).getAsJsonObject().get("offers").getAsJsonArray();

                                                String offerId = hotelDataOffer.get(0).getAsJsonObject().get("id").toString().replaceAll("\"", "");
                                                String checkIn = hotelDataOffer.get(0).getAsJsonObject().get("checkInDate").toString().replaceAll("\"", "");
                                                String checkOut = hotelDataOffer.get(0).getAsJsonObject().get("checkOutDate").toString().replaceAll("\"", "");
                                                //String boardType = hotelDataOffer.get(0).getAsJsonObject().get("boardType").toString().replaceAll("\"", "");
                                                //String roomType = hotelDataOffer.get(0).getAsJsonObject().get("room").getAsJsonObject().get("typeEstimated").getAsJsonObject().get("category").toString().replaceAll("\"", "");
                                                String numBeds = hotelDataOffer.get(0).getAsJsonObject().get("room").getAsJsonObject().get("typeEstimated").getAsJsonObject().get("beds").toString().replaceAll("\"", "");
                                                String bedType = hotelDataOffer.get(0).getAsJsonObject().get("room").getAsJsonObject().get("typeEstimated").getAsJsonObject().get("bedType").toString().replaceAll("\"", "");
                                                String description = hotelDataOffer.get(0).getAsJsonObject().get("room").getAsJsonObject().get("description").getAsJsonObject().get("text").toString().replaceAll("\"", "").replace("\\n", " ");
                                                String priceCurrency = hotelDataOffer.get(0).getAsJsonObject().get("price").getAsJsonObject().get("currency").toString().replaceAll("\"", "");
                                                String priceTotal = hotelDataOffer.get(0).getAsJsonObject().get("price").getAsJsonObject().get("total").toString().replaceAll("\"", "");

                                                //intent to go to hotel page
                                                Intent toHotelPage = new Intent(getApplicationContext(), HotelPage.class);

                                                //hotel data
                                                toHotelPage.putExtra("hotelName", hotelArrayList.get(i).hotelName);
                                                toHotelPage.putExtra("hotelId", hotelArrayList.get(i).hotelId);
                                                toHotelPage.putExtra("hotelOfferId", offerId);
                                                toHotelPage.putExtra("hotelCheckIn", checkIn);
                                                toHotelPage.putExtra("hotelCheckOut", checkOut);
//                                                toHotelPage.putExtra("numBeds", numBeds);
//                                                toHotelPage.putExtra("bedType", bedType);
                                                toHotelPage.putExtra("hoteldescription", description);
                                                toHotelPage.putExtra("hotelPrice", priceCurrency + " " + priceTotal);
                                                //toHotelPage.putExtra("flightPrice", flightPrice);

                                                //flight data
                                                toHotelPage.putExtra("flightLocation", flightDepartureIATA);
                                                toHotelPage.putExtra("flightDestination", flightArrivalIATA);
                                                toHotelPage.putExtra("flightLocationName", flightDepartureLocation);
                                                toHotelPage.putExtra("flightDestinationName", flightArrivalLocation);
                                                toHotelPage.putExtra("flightFromDate", flightDepartureDateTime);
                                                toHotelPage.putExtra("flightToDate", flightArrivalDateTime);
                                                toHotelPage.putExtra("adultCount", adultCount);
                                                toHotelPage.putExtra("kidCount", kidCount);
                                                toHotelPage.putExtra("roundOrOneWayTrip", roundOrOneWayTrip);
                                                toHotelPage.putExtra("class", flightClass);
                                                toHotelPage.putExtra("airline", airline);
                                                toHotelPage.putExtra("flightPrice", flightPrice);
                                                toHotelPage.putExtra("flightCode", flightCode);
                                                toHotelPage.putExtra("flightItinerary", flightItinerary);

                                                startActivity(toHotelPage);

//                                                handler.post(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//
//                                                        hotelPopupView = layoutInflater.inflate(R.layout.hotel_offer_popup, null);
//
//                                                        popupWindow = new PopupWindow(hotelPopupView,width,height,focusable);
//
//                                                        hotelListRL.post(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                popupWindow.showAtLocation(hotelListRL, Gravity.CENTER,0,0);
//
//                                                            }
//                                                        });
//
//                                                    }
//                                                });

                                            }
                                            catch (Exception e){
                                                Log.d(TAG, "run: hotel offer exception: " + e);

                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(HotelList.this, "Hotel currently does not have any offers, please select other hotels.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                            }

                                        }
                                    });



                                }
                            });

                        }
                    });

                }
                catch (Exception e){

                }



            }
        });

    }

}