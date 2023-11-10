package com.example.travel_assistant.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.travel_assistant.R;
import com.example.travel_assistant.model.FlightItineraryListModel;
import com.example.travel_assistant.others.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class PaymentPage extends AppCompatActivity {

    //layouts
    EditText nameET, emailET, phoneET;
    Button paymentBtn;
    LoadingDialog loadingDialog;

    //stripe api
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration configuration;

    String uid = "";

    //data for flight_booking
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

    //date for hotel booking
    String hotelName = "";
    String hotelId = "";
    String hotelOfferId = "";
    String hotelCheckIn = "";
    String hotelCheckOut = "";
//    String numBeds = "";
//    String bedType = "";
    String hotelDescription = "";
    String hotelCurrency = "";
    String hotelPrice = "";


    String paymentFor = "";

    //initialise firebase auth and firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    String totalPrice = "0";
    String totalPriceStripe = "0";

    //for async methods
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    //initialise okhttpclient
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        //intialise the layouts
        nameET = findViewById(R.id.usernameET);
        emailET = findViewById(R.id.emailET);
        phoneET = findViewById(R.id.phoneET);
        paymentBtn = findViewById(R.id.paymentBtn);
        loadingDialog = new LoadingDialog(this);

        //get the current user's uid
        try {
            uid = auth.getCurrentUser().getUid();
            Log.d(TAG, "uid:" + uid);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        //get the flight data or flight+hotel data from the previous activity accordingly
        Intent fromBooking = getIntent();
        paymentFor = fromBooking.getStringExtra("paymentFor");
        if(paymentFor.equals("flightAndHotel")){

            //if user booked flight and hotel, get both the flight and hotel data

            //flight data
            flightDepartureIATA = fromBooking.getStringExtra("flightLocation");
            flightArrivalIATA = fromBooking.getStringExtra("flightDestination");
            flightDepartureDateTime = fromBooking.getStringExtra("flightFromDate");
            flightArrivalDateTime = fromBooking.getStringExtra("flightToDate");
            flightDepartureLocation = fromBooking.getStringExtra("flightLocationName");
            flightArrivalLocation = fromBooking.getStringExtra("flightDestinationName");
            adultCount = fromBooking.getStringExtra("adultCount");
            kidCount = fromBooking.getStringExtra("kidCount");
            roundOrOneWayTrip = fromBooking.getStringExtra("roundOrOneWayTrip");
            flightClass = fromBooking.getStringExtra("class");
            airline = fromBooking.getStringExtra("airline");
            flightCurrency = fromBooking.getStringExtra("flightCurrency");
            flightPrice = fromBooking.getStringExtra("flightPrice");
            flightCode = fromBooking.getStringExtra("flightCode");
            flightItinerary = (ArrayList<FlightItineraryListModel>) fromBooking.getSerializableExtra("flightItinerary");
            if(roundOrOneWayTrip.equals("round")){
                roundTrip = true;
            }

            //hotel data
            hotelName = fromBooking.getStringExtra("hotelName");
            hotelId = fromBooking.getStringExtra("hotelId");
            hotelOfferId = fromBooking.getStringExtra("hotelOfferId");
            hotelCheckIn = fromBooking.getStringExtra("hotelCheckIn");
            hotelCheckOut = fromBooking.getStringExtra("hotelCheckOut");
            //numBeds = fromBooking.getStringExtra("numBeds");
            //bedType = fromBooking.getStringExtra("bedType");
            hotelDescription = fromBooking.getStringExtra("hotelDescription");
            //description = "Marriott Senior Discount, includes\\n1 King, 285sqft/26sqm, Wireless internet, for\\na fee, Coffee/tea maker";
            hotelCurrency = fromBooking.getStringExtra("hotelCurrency");
            hotelPrice = fromBooking.getStringExtra("hotelPrice");
            //flightPrice = fromBooking.getStringExtra("flightPrice");

            //start loading animation
            loadingDialog.show();
            //convert hotel currency
            convertCurrency();

        } else if (paymentFor.equals("flightOnly")) {

            //if user booked flight only, get the flight data

            //flight data
            flightDepartureIATA = fromBooking.getStringExtra("flightLocation");
            flightArrivalIATA = fromBooking.getStringExtra("flightDestination");
            flightDepartureDateTime = fromBooking.getStringExtra("flightFromDate");
            flightArrivalDateTime = fromBooking.getStringExtra("flightToDate");
            flightDepartureLocation = fromBooking.getStringExtra("flightLocationName");
            flightArrivalLocation = fromBooking.getStringExtra("flightDestinationName");
            adultCount = fromBooking.getStringExtra("adultCount");
            kidCount = fromBooking.getStringExtra("kidCount");
            roundOrOneWayTrip = fromBooking.getStringExtra("roundOrOneWayTrip");
            flightClass = fromBooking.getStringExtra("class");
            airline = fromBooking.getStringExtra("airline");
            flightCurrency = fromBooking.getStringExtra("flightCurrency");
            flightPrice = fromBooking.getStringExtra("flightPrice");
            flightCode = fromBooking.getStringExtra("flightCode");
            flightItinerary = (ArrayList<FlightItineraryListModel>) fromBooking.getSerializableExtra("flightItinerary");
            if(roundOrOneWayTrip.equals("round")){
                roundTrip = true;
            }

            //get the total price of just the flight to be used for stripe api
            double flightPriceDouble = Double.parseDouble(flightPrice);
            totalPrice = String.format("%.2f", flightPriceDouble);
            totalPriceStripe = totalPrice.replace(".", "");

            //start loading animation
            loadingDialog.show();
            //fetch stripe api
            fetchStripeAPI();

        }



        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if the text fields are filled
                //if not, then display a toast notifying the user to fill in the fields
                if(TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(phoneET.getText())){
                    Toast.makeText(PaymentPage.this, "Please make sure all the necessary fields are filled", Toast.LENGTH_SHORT).show();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailET.getText()).matches()) {
                    Toast.makeText(PaymentPage.this, "Please ensure that the email is proper.", Toast.LENGTH_SHORT).show();
                }
                else if (!Patterns.PHONE.matcher(phoneET.getText()).matches() || phoneET.getText().toString().length() < 7 || phoneET.getText().toString().length() > 15) {
                    Toast.makeText(PaymentPage.this, "Please ensure that the phone number is proper.", Toast.LENGTH_SHORT).show();
                }
                else{
                    //check if a response can be received from the stripe api
                    if(paymentIntentClientSecret != null){
                        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret,
                                new PaymentSheet.Configuration("TravelApp", configuration));
                    }
                    else {
                        Toast.makeText(PaymentPage.this, "API still loading...", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult){

        //depending on the result of the payment sheet display th toast messages accordingly
        if(paymentSheetResult instanceof PaymentSheetResult.Canceled){
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Failed){
            Toast.makeText(this, ((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage(), Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Success!", Toast.LENGTH_SHORT).show();

            //start loading animation
            loadingDialog.show();

            //if payment is completed, add the flight and hotel booking data into firestore
            addFlightBooking();
        }

    }

    private void convertCurrency(){

        executor.execute(new Runnable() {
            @Override
            public void run() {


                try{

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://apidojo-booking-v1.p.rapidapi.com/currency/get-exchange-rates?base_currency=" + flightCurrency + "&languagecode=en-us")
                            .get()
                            .addHeader("X-RapidAPI-Key", "b34ecf7a0fmsh5cbb7c353f899abp1c8c15jsn43d3583a9734")
                            .addHeader("X-RapidAPI-Host", "apidojo-booking-v1.p.rapidapi.com")
                            .build();

//                    okhttp3.Request request = new Request.Builder()
//                            .url("https://apidojo-booking-v1.p.rapidapi.com/currency/get-exchange-rates?base_currency=USD&languagecode=en-us")
//                            .get()
//                            .addHeader("X-RapidAPI-Key", "b34ecf7a0fmsh5cbb7c353f899abp1c8c15jsn43d3583a9734")
//                            .addHeader("X-RapidAPI-Host", "apidojo-booking-v1.p.rapidapi.com")
//                            .build();
//
//                    Response response = client.newCall(request).execute();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {

                            if (response.isSuccessful()){

                                //get the json response in string
                                String responseStr = response.body().string();

                                Log.d(TAG, "onResponse: response str: " + responseStr);
                                double exchangeRate = 0.0;

                                try{

                                    //create a json array to hold all the json data
                                    JSONObject jsonObject = new JSONObject(responseStr);
                                    JSONArray jsonArray = new JSONArray(jsonObject.get("exchange_rates").toString());

                                    //look through the json array for the currency that is equal to the hotel currency
                                    //once found, get the exchange rate
                                    for (int i = 0; i<jsonArray.length(); i++){

                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String curCurrency = jsonObject1.get("currency").toString();
                                        if (curCurrency.equals(hotelCurrency)){
                                            exchangeRate = Double.parseDouble(jsonObject1.get("exchange_rate_buy").toString());
                                            break;
                                        }

                                    }

                                    //perform the calculations to convert the hotel price to the same currency as the flight currency
                                    double hotelPriceDouble = Double.parseDouble(hotelPrice);
                                    double convertedHotelPrice = hotelPriceDouble / exchangeRate;

                                    Log.d(TAG, "onResponse: hotelPrice: " + hotelPriceDouble);
                                    Log.d(TAG, "onResponse: exchangeRate: " + exchangeRate);
                                    Log.d(TAG, "onResponse: convertedHotelPrice: " + convertedHotelPrice);

                                    //set the new hotel price and update the hotel currency
                                    hotelPrice = String.format("%.2f", convertedHotelPrice);
                                    hotelCurrency = flightCurrency;

                                    //get the total price by adding the converted hotel price and the flight price
                                    double flightPriceDouble = Double.parseDouble(flightPrice);
                                    double totalPriceDouble = flightPriceDouble + convertedHotelPrice;
                                    //totalPrice = String.valueOf(totalPriceDouble);
                                    totalPrice = String.format("%.2f", totalPriceDouble);
                                    //totalPrice = totalPrice + "00";
                                    totalPriceStripe = totalPrice.replace(".", "");

                                    //once total price is calculated, fetch the stripe api
                                    fetchStripeAPI();

                                }
                                catch (Exception e){
                                    Log.d(TAG, "onResponse: error parsing currency exchange json: " + e);
                                }

                            }
                            else{

                            }

                        }
                    });

                }
                catch (Exception e){
                    Log.d(TAG, "run: error getting currency exchange: " + e);
                }



            }
        });

    }

    private void addPayment(String flightBookingId, String hotelBookingId){

        // Create a new payment
        Map<String, Object> payment = new HashMap<>();
        payment.put("payment_id", "");
        payment.put("flight_booking_id", flightBookingId);
        payment.put("hotel_booking_id", hotelBookingId);
        payment.put("name", nameET.getText().toString());
        payment.put("email", emailET.getText().toString());
        payment.put("phone_number", phoneET.getText().toString());
        payment.put("user_uid", uid);
        payment.put("price_currency", flightCurrency);
        payment.put("total_price", totalPrice);

        // Add a new payment document with a generated ID
        db.collection("payment")
                .add(payment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot (payment) added with ID: " + documentReference.getId());

                        String paymentId = documentReference.getId();

                        //add the firebase generated id into the newly created payment document
                        DocumentReference paymentRef = db.collection("payment").document(paymentId);

                        //update the payment_id with the firestore generated id
                        paymentRef
                                .update("payment_id", paymentId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot payment_id successfully updated!");

                                        //stop loading animation
                                        loadingDialog.cancel();

                                        //finish the activity and go back to main menu
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), MainMenu.class));

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document (payment)", e);
                    }
                });



    }

    private void addHotelBooking(String flightBookingId){

        // Create a new hotel
        Map<String, Object> hotel = new HashMap<>();
        hotel.put("hotel_booking_id", "");
        hotel.put("hotel_id", hotelId);
        hotel.put("hotel_name", hotelName);
        hotel.put("offer_id", hotelOfferId);
        hotel.put("check_in", hotelCheckIn);
        hotel.put("check_out", hotelCheckOut);
        hotel.put("description", hotelDescription);
        hotel.put("hotel_currency", hotelCurrency);
        hotel.put("hotel_price", hotelPrice);
        hotel.put("user_uid", uid);


        // Add a new hotel document with a generated ID
        db.collection("hotel_booking")
                .add(hotel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot (hotel_booking) added with ID: " + documentReference.getId());
                        String hotelBookingId = documentReference.getId();

                        //add the firebase generated id into the newly created hotel_booking document
                        DocumentReference hotelBookingRef = db.collection("hotel_booking").document(hotelBookingId);

                        //update the hotel_booking_id with the firestore generated id
                        hotelBookingRef
                                .update("hotel_booking_id", hotelBookingId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot hotel_booking_id successfully updated!");

                                        addPayment(flightBookingId, hotelBookingId);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document (hotel_booking)", e);
                    }
                });

    }

    private void addFlightBooking(){

        // Create a new flight
        Map<String, Object> flight = new HashMap<>();
        flight.put("flight_booking_id", "");
        flight.put("departure_iata", flightDepartureIATA);
        flight.put("arrival_iata", flightArrivalIATA);
        flight.put("departure_date_time", flightDepartureDateTime);
        flight.put("arrival_date_time", flightArrivalDateTime);
        flight.put("departure_location", flightDepartureLocation);
        flight.put("arrival_location", flightArrivalLocation);
        flight.put("adult_count", adultCount);
        flight.put("kid_count", kidCount);
        flight.put("roundTrip", roundTrip);
        flight.put("class", flightClass);
        flight.put("airline", airline);
        flight.put("flight_code", flightCode);
        flight.put("flight_currency", flightCurrency);
        flight.put("flight_price", flightPrice);
        flight.put("user_uid", uid);


        // Add a new flight_booking document with a generated ID
        db.collection("flight_booking")
                .add(flight)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot (flight_booking) added with ID: " + documentReference.getId());
                        String flightBookingId = documentReference.getId();

                        //add the doc ref id into the newly made flight_booking
                        DocumentReference flightBookingRef = db.collection("flight_booking").document(flightBookingId);

                        //update the flight_booking_id with the firebase generated id
                        flightBookingRef
                                .update("flight_booking_id", flightBookingId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot flight_booking_id successfully updated!");

                                        //add flight itineraries
                                        for (int i = 0; i < flightItinerary.size(); i++){

                                            // Create a new flight_itinerary
                                            Map<String, Object> flight_itinerary = new HashMap<>();
                                            flight_itinerary.put("flight_booking_id", flightBookingId);
                                            flight_itinerary.put("departure_iata", flightItinerary.get(i).departureIATA);
                                            flight_itinerary.put("arrival_iata", flightItinerary.get(i).arrivalIATA);
                                            flight_itinerary.put("departure_date_time", flightItinerary.get(i).departureAt);
                                            flight_itinerary.put("arrival_date_time", flightItinerary.get(i).arrivalAt);
                                            flight_itinerary.put("departure_terminal", flightItinerary.get(i).departureTerminal);
                                            flight_itinerary.put("arrival_terminal", flightItinerary.get(i).arrivalTerminal);
                                            flight_itinerary.put("duration", flightItinerary.get(i).duration);
                                            flight_itinerary.put("flight_code", flightItinerary.get(i).aircraftCode);
                                            flight_itinerary.put("order", i + 1);
                                            flight_itinerary.put("user_uid", uid);


                                            // Add a new flight_itinerary document with a generated ID
                                            db.collection("flight_itinerary")
                                                    .add(flight_itinerary)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d(TAG, "DocumentSnapshot (flight_itinerary) added with ID: " + documentReference.getId());

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error adding document (flight_itinerary)", e);
                                                        }
                                                    });

                                        }

                                        //if user booked hotel as well
                                        //add the hotel booking data into firestore
                                        if(paymentFor.equals("flightAndHotel")){
                                            addHotelBooking(flightBookingId);
                                        }
                                        else{
                                            addPayment(flightBookingId, "");
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document (flight_booking)", e);
                    }
                });

    }

    private void fetchStripeAPI(){

        //create new request and set the url
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.0.6/stripeAPI/index.php";

        //initialise a new string request to call the stripe api
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //stop the loading animation
                        loadingDialog.cancel();

                        //Toast.makeText(PaymentPage.this, "got a response!", Toast.LENGTH_SHORT).show();

                        try {

                            //hold the response string in a json object
                            JSONObject jsonObject = new JSONObject(response);
                            configuration = new PaymentSheet.CustomerConfiguration(
                                    jsonObject.getString("customer"),
                                    jsonObject.getString("ephemeralKey")
                            );
                            paymentIntentClientSecret = jsonObject.getString("paymentIntent");
                            PaymentConfiguration.init(getApplicationContext(), jsonObject.getString("publishableKey"));

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //stop the loading animation
                loadingDialog.cancel();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("price", totalPriceStripe);
                return paramV;
            }
        };

        //set the retry policy to 10seconds
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }

}