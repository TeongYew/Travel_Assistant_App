package com.example.travel_assistant.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.travel_assistant.R;
import com.example.travel_assistant.model.FlightItineraryListModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentPage extends AppCompatActivity {

    EditText usernameET, emailET, phoneET;
    Button paymentBtn;

    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration configuration;

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
    String hotelPrice = "";


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        usernameET = findViewById(R.id.usernameET);
        emailET = findViewById(R.id.emailET);
        phoneET = findViewById(R.id.phoneET);
        paymentBtn = findViewById(R.id.paymentBtn);

        Intent fromBooking = getIntent();
        String paymentFor = fromBooking.getStringExtra("paymentFor");
        if(paymentFor.equals("flightAndHotel")){

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
            hotelPrice = fromBooking.getStringExtra("hotelPrice");
            //flightPrice = fromBooking.getStringExtra("flightPrice");

        } else if (paymentFor.equals("flightOnly")) {

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
            flightPrice = fromBooking.getStringExtra("flightPrice");
            flightCode = fromBooking.getStringExtra("flightCode");
            flightItinerary = (ArrayList<FlightItineraryListModel>) fromBooking.getSerializableExtra("flightItinerary");
            if(roundOrOneWayTrip.equals("round")){
                roundTrip = true;
            }

        }


        fetchStripeAPI();

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create a new flight
                Map<String, Object> flight = new HashMap<>();
                flight.put("flight_booking_id", "000001");
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
                flight.put("flight_price", flightPrice);


                // Add a new document with a generated ID
                db.collection("flight_booking")
                        .add(flight)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot (flight_booking) added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document (flight_booking)", e);
                            }
                        });

                for (int i = 0; i < flightItinerary.size(); i++){

                    // Create a new flight_itinerary
                    Map<String, Object> flight_itinerary = new HashMap<>();
                    flight_itinerary.put("flight_booking_id", "000001");
                    flight_itinerary.put("departure_iata", flightItinerary.get(i).departureIATA);
                    flight_itinerary.put("arrival_iata", flightItinerary.get(i).arrivalIATA);
                    flight_itinerary.put("departure_date_time", flightItinerary.get(i).departureAt);
                    flight_itinerary.put("arrival_date_time", flightItinerary.get(i).arrivalAt);
                    flight_itinerary.put("departure_terminal", flightItinerary.get(i).departureTerminal);
                    flight_itinerary.put("arrival_terminal", flightItinerary.get(i).arrivalTerminal);
                    flight_itinerary.put("duration", flightItinerary.get(i).duration);
                    flight_itinerary.put("flight_code", flightItinerary.get(i).aircraftCode);
                    flight_itinerary.put("order", i + 1);


                    // Add a new document with a generated ID
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


                if(paymentFor.equals("flightAndHotel")){

                    // Create a new hotel
                    Map<String, Object> hotel = new HashMap<>();
                    hotel.put("hotel_booking_id", "000001");
                    hotel.put("hotel_id", hotelId);
                    hotel.put("hotel_name", hotelName);
                    hotel.put("offer_id", hotelOfferId);
                    hotel.put("check_in", hotelCheckIn);
                    hotel.put("check_out", hotelCheckOut);
                    hotel.put("description", hotelDescription);
                    hotel.put("hotel_price", hotelPrice);


                    // Add a new document with a generated ID
                    db.collection("hotel_booking")
                            .add(hotel)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot (hotel_booking) added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document (hotel_itinerary)", e);
                                }
                            });

                }



//                if(TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(phoneET.getText())){
//                    Toast.makeText(PaymentPage.this, "Please make sure all the necessary fields are filled", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    if(paymentIntentClientSecret != null){
//                        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret,
//                                new PaymentSheet.Configuration("TravelApp", configuration));
//                    }
//                    else {
//                        Toast.makeText(PaymentPage.this, "API still loading...", Toast.LENGTH_SHORT).show();
//                    }
//                }

            }
        });

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

    }

    public void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult){

        if(paymentSheetResult instanceof PaymentSheetResult.Canceled){
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Failed){
            Toast.makeText(this, ((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage(), Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Success!", Toast.LENGTH_SHORT).show();
        }

    }

    public void fetchStripeAPI(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.0.4/stripeAPI/index.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(PaymentPage.this, "got a response!", Toast.LENGTH_SHORT).show();

                        try {

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
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("param", "abc");
                return paramV;
            }
        };
        queue.add(stringRequest);

    }

}