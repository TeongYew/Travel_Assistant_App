package com.example.travel_assistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.ItineraryAdapter;
import com.example.travel_assistant.model.ItineraryModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ItineraryList extends AppCompatActivity {

    final String TAG = ItineraryList.this.toString();
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    android.widget.ListView itineraryLV;
    FloatingActionButton optionsFAB, createItineraryFAB, generateItineraryFAB;
    LinearLayout createItineraryLL, generateItineraryLL;
    TextView createItineraryTV, generateItineraryTV;
    boolean optionsSelected = false;
    String gptResponse = "";

    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View itineraryPopupView;
    RelativeLayout itineraryListRL;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_list);

        itineraryLV = findViewById(R.id.itineraryLV);
        optionsFAB = findViewById(R.id.optionsFAB);
        createItineraryFAB = findViewById(R.id.createItineraryFAB);
        generateItineraryFAB = findViewById(R.id.generateItineraryFAB);
        createItineraryLL = findViewById(R.id.createItineraryLL);
        generateItineraryLL = findViewById(R.id.generateItineraryLL);
        createItineraryTV = findViewById(R.id.createItineraryTV);
        generateItineraryTV = findViewById(R.id.generateItineraryTV);
        itineraryListRL = findViewById(R.id.itineraryListRL);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //generateItinerary("Who are you?");

        //Log.d(TAG, "onCreate: chatgpt: " + gptResponse);
        callAPI("How are you?");

        optionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(optionsSelected == false){

                    optionsSelected = true;

                    createItineraryFAB.setVisibility(View.VISIBLE);
                    generateItineraryFAB.setVisibility(View.VISIBLE);

                    animateButton(createItineraryLL, -200, 500);
                    animateButton(generateItineraryLL, -400, 500);

                    createItineraryTV.setVisibility(View.VISIBLE);
                    generateItineraryTV.setVisibility(View.VISIBLE);

                    //animateText(createItineraryTV, -200, 500);
                    //animateText(generateItineraryTV, -200, 500);

                }
                else if(optionsSelected == true){

                    optionsSelected = false;

                    animateButton(createItineraryLL, 0, 500);
                    animateButton(generateItineraryLL, 0, 500);
                    //animateText(createItineraryTV, 0, 500);
                    //animateText(generateItineraryTV, 0, 500);
                    //createItineraryFAB.setVisibility(View.GONE);
                    //generateItineraryFAB.setVisibility(View.GONE);
                    createItineraryTV.setVisibility(View.GONE);
                    generateItineraryTV.setVisibility(View.GONE);

                }

            }
        });

        createItineraryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopupWindow();
            }
        });

        generateItineraryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void animateButton(View view, int translationY, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", translationY);
        animator.setDuration(duration);
        animator.start();
    }

    public void setPopupWindow(){

        itineraryPopupView = layoutInflater.inflate(R.layout.create_itinerary_popup, null);

        popupWindow = new PopupWindow(itineraryPopupView,width,height,focusable);

        itineraryListRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(itineraryListRL, Gravity.CENTER,0,0);

            }
        });

        RelativeLayout createItineraryPopupRL = itineraryPopupView.findViewById(R.id.createItineraryPopupRL);
        EditText itineraryTitleET = itineraryPopupView.findViewById(R.id.itineraryTitleET);
        EditText itineraryLocationET = itineraryPopupView.findViewById(R.id.itineraryLocationET);
        EditText itineraryFromET = itineraryPopupView.findViewById(R.id.itineraryFromET);
        EditText itineraryToET = itineraryPopupView.findViewById(R.id.itineraryToET);
        Button createItineraryBtn = itineraryPopupView.findViewById(R.id.createItineraryBtn);

        createItineraryPopupRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        itineraryFromET.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    itineraryFromET.setText(current);
                    itineraryFromET.setSelection(sel < current.length() ? sel : current.length());


                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });


        itineraryToET.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    itineraryToET.setText(current);
                    itineraryToET.setSelection(sel < current.length() ? sel : current.length());


                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });


        createItineraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itineraryTitleStr = itineraryTitleET.getText().toString();
                String itineraryLocationStr = itineraryLocationET.getText().toString();
                String itineraryFromStr = itineraryFromET.getText().toString();
                String itineraryToStr = itineraryToET.getText().toString();

                if(TextUtils.isEmpty(itineraryTitleStr) || TextUtils.isEmpty(itineraryLocationStr) || TextUtils.isEmpty(itineraryFromStr) || TextUtils.isEmpty(itineraryToStr)){
                    Toast.makeText(ItineraryList.this, "Please make sure the all necessary fields are filled", Toast.LENGTH_SHORT).show();
                }
                else {

                    //ItineraryModel itineraryModel = new ItineraryModel(itineraryTitleStr, )

                }

            }
        });

    }

    public void callAPI(String question){

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","gpt-3.5-turbo");
            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", question);
            messageArr.put(obj);

            jsonBody.put("message", messageArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: Failed to load response due to: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {

                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        Log.d(TAG, "onResponse: " + result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    Log.d(TAG, "onResponseFailure: Failed to load response due to: " + response.body().string());
                }

            }
        });


    }



    public void generateItinerary(String message) {

        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "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";
        String model = "gpt-3.5-turbo";

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    // Create the HTTP POST request
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Authorization", "Bearer " + apiKey);
                    con.setRequestProperty("Content-Type", "application/json");

                    // Build the request body
                    String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
                    con.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(body);
                    writer.flush();
                    writer.close();

                    // Get the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // returns the extracted contents of the response.
                    gptResponse = extractContentFromResponse(response.toString());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }

    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }

}