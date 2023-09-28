package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentPage extends AppCompatActivity {

    EditText cardNameET, cardNumET, cardMonthET, cardYearEt, cardCVCET;
    Button paymentBtn;

    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        cardNameET = findViewById(R.id.cardNameET);
        cardNumET = findViewById(R.id.cardNumET);
        cardMonthET = findViewById(R.id.cardMonthET);
        cardYearEt = findViewById(R.id.cardYearET);
        cardCVCET = findViewById(R.id.cardCVCET);
        paymentBtn = findViewById(R.id.paymentBtn);

        Intent fromBooking = getIntent();
        String bookingName = fromBooking.getStringExtra("name");
        String bookingEmail = fromBooking.getStringExtra("email");
        String bookingPhone = fromBooking.getStringExtra("phone");

        fetchStripeAPI();

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(cardNameET.getText()) || TextUtils.isEmpty(cardNumET.getText()) || TextUtils.isEmpty(cardMonthET.getText()) || TextUtils.isEmpty(cardYearEt.getText()) || TextUtils.isEmpty(cardCVCET.getText())){
                    Toast.makeText(PaymentPage.this, "Please make sure all the necessary fields are filled", Toast.LENGTH_SHORT).show();
                }
                else{
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