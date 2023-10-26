package com.example.travel_assistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.travel_assistant.others.LoadingDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HotelList extends AppCompatActivity {

    final String TAG = String.valueOf(HotelList.this);


    //data for hotel search
    String location = "";
    String fromDate = "";
    String toDate = "";
    //String adultCount = "";
    //String kidCount = "";
    //String flightPrice = "";

    //layout and variable for listview
    android.widget.ListView hotelListLV;
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
    String flightCurrency = "";
    String flightPrice = "";
    ArrayList<FlightItineraryListModel> flightItinerary = new ArrayList<>();

    //layout variables for popup window
    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View setCityPopupView;
    RelativeLayout hotelListRL;
    String city = "";
    LoadingDialog loadingDialog;

    //for async methods
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    //initialise the amadeus api
    Amadeus amadeus = Amadeus
            .builder("htHGvYM2OB3wmAqVykNHAbGPuTlSBV1m","0hiGWqr3KQSGXION")
            .build();

    //initialise the okhttpclient
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        //initialise the layout
        hotelListLV = findViewById(R.id.hotelListLV);
        hotelListRL = findViewById(R.id.hotelListRL);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        loadingDialog = new LoadingDialog(this);

        //get the hotel and flight data from the FlightPage activity
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
        flightCurrency = fromFlightPage.getStringExtra("flightCurrency");
        flightPrice = fromFlightPage.getStringExtra("flightPrice");
        flightCode = fromFlightPage.getStringExtra("flightCode");
        flightItinerary = (ArrayList<FlightItineraryListModel>) fromFlightPage.getSerializableExtra("flightItinerary");


        Log.d(TAG, "onCreate: location: " + location);

//        //start loading animation and getHotel
//        loadingDialog.show();
//        //getHotel();
//        getHotel2();
        //display a popup window that will prompt the user to set the city of their accommodation
        setCityPopup();

    }

    public void getHotel2(){

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try{

                    //get dest_id of the city using the apidojo booking autocomplete api
                    Request request = new Request.Builder()
                            .url("https://apidojo-booking-v1.p.rapidapi.com/locations/auto-complete?text=" + city + "&languagecode=en-us")
                            .get()
                            .addHeader("X-RapidAPI-Key", "b34ecf7a0fmsh5cbb7c353f899abp1c8c15jsn43d3583a9734")
                            .addHeader("X-RapidAPI-Host", "apidojo-booking-v1.p.rapidapi.com")
                            .build();

                    Response response = client.newCall(request).execute();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.d(TAG, "onFailure: Failed to load response due to: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {

                            if(response.isSuccessful()){

                                //get the json response in string
                                String responseStr = response.body().string();

                                Log.d(TAG, "run: response: " + response);
                                Log.d(TAG, "onResponse: response body: " + responseStr);

                                try {
                                    //create a json array to hold the json array from the response string
                                    JSONArray jsonArray = new JSONArray(responseStr);

                                    //get first dest_id
                                    JSONObject locationJS = jsonArray.getJSONObject(0);
                                    String destID = locationJS.get("dest_id").toString();

                                    //using the dest_id from the previous api, call the apidojo booking properties list api
                                    Request request1 = new Request.Builder()
                                            .url("https://apidojo-booking-v1.p.rapidapi.com/properties/list?offset=0&arrival_date=" + fromDate +"&departure_date=" + toDate + "&guest_qty=" + adultCount +"&dest_ids=" + destID +"&room_qty=1&search_type=city&children_qty=" + kidCount + "&search_id=111&price_filter_currencycode=USD&order_by=popularity&languagecode=en-us&travel_purpose=leisure")
                                            .get()
                                            .addHeader("X-RapidAPI-Key", "b34ecf7a0fmsh5cbb7c353f899abp1c8c15jsn43d3583a9734")
                                            .addHeader("X-RapidAPI-Host", "apidojo-booking-v1.p.rapidapi.com")
                                            .build();

                                    Response response1 = client.newCall(request1).execute();

                                    client.newCall(request1).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                            Log.d(TAG, "onFailure: Failed to load response due to: " + e.getMessage());
                                        }

                                        @Override
                                        public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {

                                            if(response.isSuccessful()){

                                                //get the json response in string
                                                String responseStr = response.body().string();

                                                Log.d(TAG, "run: response2: " + response);
                                                Log.d(TAG, "onResponse: response body2: " + responseStr);

                                                try{

                                                    //create a json array to hold the json array from the response string
                                                    JSONObject jsonObject = new JSONObject(responseStr);
                                                    JSONArray jsonArray = new JSONArray(jsonObject.get("result").toString());
//
                                                    for (int i=0; i<jsonArray.length(); i++){

                                                        //create a json object to hold the current json object of the json array
                                                        JSONObject curJSON = jsonArray.getJSONObject(i);

                                                        //create the variables to hold the required data from the api
                                                        //String hotelName = "";
                                                        String hotelName = curJSON.get("hotel_name").toString();
                                                        String hotelId = curJSON.get("hotel_id").toString();
                                                        JSONObject priceBreakdown = new JSONObject(curJSON.get("price_breakdown").toString());
                                                        String hotelCurrency = priceBreakdown.getString("currency").toString();
                                                        String hotelPrice = priceBreakdown.getString("gross_price").toString();
                                                        String address = curJSON.get("address_trans").toString();
                                                        String accomType = curJSON.get("accommodation_type_name").toString();

                                                        //create a hotel list model to hold the hotel data received from the api
                                                        //add the hotel list model into the hotel array list
                                                        hotelListModel = new HotelListModel(hotelName, hotelId, hotelCurrency, hotelPrice);

                                                        hotelArrayList.add(hotelListModel);


                                                        Log.d(TAG, "onResponse: hotel name: " + hotelName);
                                                        Log.d(TAG, "onResponse: hotel id: " + hotelId);
                                                        Log.d(TAG, "onResponse: hotel currency: " + hotelCurrency);
                                                        Log.d(TAG, "onResponse: hotel price: " + hotelPrice);

                                                    }

                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            //stop loading animation
                                                            loadingDialog.cancel();

                                                            //populate the hotel listview using the hotel array list
                                                            HotelListAdapter customAdapter = new HotelListAdapter(getApplicationContext(), hotelArrayList);
                                                            hotelListLV.setAdapter(customAdapter);

                                                            hotelListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                                    //start loading animation
                                                                    loadingDialog.show();

                                                                    //call the apidojo booking properties description api using the hotel_id received from the previous api
                                                                    Request request2 = new Request.Builder()
                                                                            .url("https://apidojo-booking-v1.p.rapidapi.com/properties/get-description?hotel_ids=" + hotelArrayList.get(i).hotelId + "&check_out=" + toDate + "&languagecode=en-us&check_in=" + fromDate)
                                                                            .get()
                                                                            .addHeader("X-RapidAPI-Key", "b34ecf7a0fmsh5cbb7c353f899abp1c8c15jsn43d3583a9734")
                                                                            .addHeader("X-RapidAPI-Host", "apidojo-booking-v1.p.rapidapi.com")
                                                                            .build();

                                                                    client.newCall(request2).enqueue(new Callback() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                                            Toast.makeText(HotelList.this, "Can't get description of hotel", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        @Override
                                                                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                                                            //get the json response in string
                                                                            String responseStr = response.body().string();

                                                                            Log.d(TAG, "onResponse: hotel desc response: " + responseStr);

                                                                            //try and get the hotel description
                                                                            String hotelDescription = "";
                                                                            try{

                                                                                //create a json object to hold the json object data from the response string
                                                                                JSONArray jsonArray = new JSONArray(responseStr);
                                                                                JSONObject jsonObject = jsonArray.getJSONObject(1);

                                                                                //get the hotel description from the json object
                                                                                hotelDescription = jsonObject.getString("description");

                                                                            }
                                                                            catch(Exception e){
                                                                                Log.d(TAG, "onResponse: can't parse json from string3: " + e);
                                                                            }

                                                                            Log.d(TAG, "onResponse: hotel desc: " + hotelDescription);

                                                                            //intent to go to hotel page
                                                                            Intent toHotelPage = new Intent(getApplicationContext(), HotelPage.class);

                                                                            //hotel data
                                                                            toHotelPage.putExtra("hotelName", hotelArrayList.get(i).hotelName);
                                                                            toHotelPage.putExtra("hotelId", hotelArrayList.get(i).hotelId);
                                                                            toHotelPage.putExtra("hotelOfferId", "");
                                                                            toHotelPage.putExtra("hotelCheckIn", fromDate);
                                                                            toHotelPage.putExtra("hotelCheckOut", toDate);
//                                                toHotelPage.putExtra("numBeds", numBeds);
//                                                toHotelPage.putExtra("bedType", bedType);
                                                                            toHotelPage.putExtra("hotelDescription", hotelDescription);
                                                                            toHotelPage.putExtra("hotelCurrency", hotelArrayList.get(i).hotelCurrency);
                                                                            toHotelPage.putExtra("hotelPrice", hotelArrayList.get(i).hotelPrice);
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
                                                                            toHotelPage.putExtra("flightCurrency", flightCurrency);
                                                                            toHotelPage.putExtra("flightPrice", flightPrice);
                                                                            toHotelPage.putExtra("flightCode", flightCode);
                                                                            toHotelPage.putExtra("flightItinerary", flightItinerary);

                                                                            //stop loading animation
                                                                            loadingDialog.cancel();

                                                                            startActivity(toHotelPage);

                                                                        }
                                                                    });

                                                                }
                                                            });

                                                        }
                                                    });


                                                }
                                                catch (Exception e){
                                                    Log.d(TAG, "onResponse: parse 2nd response from str error: " + e);
                                                }

                                            }else{
                                                Log.d(TAG, "onResponseFailure: Failed to load response due to: " + response.body().string());
                                            }

                                        }
                                    });

                                } catch (JSONException e) {
                                    //throw new RuntimeException(e);
                                    Log.d(TAG, "onResponse: parse response to json error: " + e);
                                }


                            }else{
                                Log.d(TAG, "onResponseFailure: Failed to load response due to: " + response.body().string());
                            }

                        }
                    });

                }
                catch (Exception e){
                    Log.d(TAG, "run: rapidapi error: " + e);
                }

            }
        });

    }

    private void setCityPopup(){

        //intialise setCityPopupView and popup window
        setCityPopupView = layoutInflater.inflate(R.layout.set_city_popup, null);

        popupWindow = new PopupWindow(setCityPopupView,width,height,focusable);

        //display the popup window
        hotelListRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(hotelListRL, Gravity.CENTER,0,0);

            }
        });

        //intialise the layouts from setCityPopupView
        RelativeLayout setCityPopupRL = setCityPopupView.findViewById(R.id.setCityRL);
        EditText cityET = setCityPopupView.findViewById(R.id.cityET);
        Button setCityBtn = setCityPopupView.findViewById(R.id.setCityBtn);

        setCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get the city that was inputted by the user and then close the popup window
                city = cityET.getText().toString();

                popupWindow.dismiss();

                //show the loading animation and get the hotel using the city input by the user
                loadingDialog.show();
                getHotel2();
            }
        });

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

                        hotelListModel = new HotelListModel(hotelName, hotelId, "", "");

                        hotelArrayList.add(hotelListModel);

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            //stop loading animation
                            loadingDialog.cancel();

                            HotelListAdapter customAdapter = new HotelListAdapter(getApplicationContext(), hotelArrayList);
                            hotelListLV.setAdapter(customAdapter);

                            hotelListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    //start loading animation
                                    loadingDialog.show();

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

                                                //stop loading animation
                                                loadingDialog.cancel();

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
                                                        //stop loading animation and display error toast
                                                        loadingDialog.cancel();
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
                    //stop loading animation
                    loadingDialog.cancel();
                    Log.d(TAG, "run: " + e);
                }



            }
        });

    }

}