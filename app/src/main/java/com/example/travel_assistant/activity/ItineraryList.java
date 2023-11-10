package com.example.travel_assistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.TravelItineraryListAdapter;
import com.example.travel_assistant.model.ItineraryItemModel;
import com.example.travel_assistant.model.ItineraryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItineraryList extends AppCompatActivity {

    final String TAG = ItineraryList.this.toString();

    //for async methods
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    //layouts
    android.widget.ListView itineraryLV;
    FloatingActionButton optionsFAB, createItineraryFAB, generateItineraryFAB;
    LinearLayout createItineraryLL, generateItineraryLL;
    TextView createItineraryTV, generateItineraryTV;
    boolean optionsSelected = false;
    String gptResponse = "";
    String uid = "";
    ArrayList<ItineraryModel> itineraryArrayList = new ArrayList<>();
    TravelItineraryListAdapter customAdapter;

    //for itinerary generation
    String generatingItineraryId = "";
    String generatingItineraryTitle = "";
    String generatingItineraryLocation = "";
    boolean currentlyGenerating = false;

    //variables for popup window
    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View itineraryPopupView;
    RelativeLayout itineraryListRL;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    //intialise okhttpclient
    OkHttpClient client = new OkHttpClient();

    //initialise firebase auth and firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    //initialise tts
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_list);

        //initialise the layouts
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

        //get the current user's uid
        try {
            uid = auth.getCurrentUser().getUid();
            //uid = "000001";
            Log.d(TAG, "uid:" + uid);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        //generateItinerary("Who are you?");

        //Log.d(TAG, "onCreate: chatgpt: " + gptResponse);
        //callAPI("How are you?");

        //get the user's travel itinerary from firestore and populate the listview
        getUserItinerary();

        optionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if the button is clicked, animate and display the create and generate itinerary button
                //if button is clicked again, set the create and generate button to gone

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
                setPopupWindow("create");
            }
        });

        generateItineraryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //set the generating status to true
                currentlyGenerating = true;

                setPopupWindow("generate");
                //generateItinerary();
            }
        });

    }

    private void getUserItinerary(){

        //get the user's travel itinerary using the user_uid
        db.collection("itinerary")
                .whereEqualTo("user_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, document.getId() + " => " + document.getData().get("itinerary_day_count"));

                                //set the string variables to hold the data recevied from firestore
                                String itineraryId = document.getData().get("itinerary_id").toString();
                                String itineraryTitle = document.getData().get("itinerary_title").toString();
                                String itineraryLocation = document.getData().get("itinerary_location").toString();
                                String itineraryFrom = document.getData().get("itinerary_from").toString();
                                String itineraryTo = document.getData().get("itinerary_to").toString();
                                String itineraryDayCount = document.getData().get("itinerary_day_count").toString();

                                //create an itinerary model to be added into the itinerary array list
                                ArrayList<ItineraryItemModel> itineraryDayArrayList = new ArrayList<>();

                                ItineraryModel itineraryModel = new ItineraryModel(itineraryId, itineraryTitle, itineraryLocation, itineraryFrom, itineraryTo, Integer.parseInt(itineraryDayCount), itineraryDayArrayList);

                                itineraryArrayList.add(itineraryModel);

                            }

                            //populate the itinerary listview using the data from the itinerary array list
                            customAdapter = new TravelItineraryListAdapter(getApplicationContext(), itineraryArrayList);
                            itineraryLV.setAdapter(customAdapter);

                            itineraryLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    //create the intent to send the user to the ItineraryPage activity
                                    //send the selected itinerary data to the ItineraryPage activity
                                    Intent toItineraryPage = new Intent(getApplicationContext(), ItineraryPage.class);
                                    toItineraryPage.putExtra("from", "existing");
                                    toItineraryPage.putExtra("itinerary", customAdapter.getItem(i));

                                    startActivity(toItineraryPage);

                                }
                            });

                            itineraryLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    //on long click, create and display an alert dialog to confirm the deletion of the itinerary

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ItineraryList.this);

                                    //set the message for the alert dialog
                                    builder.setMessage("Do you want to delete the itinerary item?");

                                    //set alert title
                                    builder.setTitle("Alert !");

                                    //set cancelable false for when the user clicks on the outside the dialog box then it will remain show
                                    builder.setCancelable(false);

                                    //set the positive button with yes
                                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {

                                        //if user clicks yes, delete the selected itinerary
                                        deleteItinerary(customAdapter.getItem(i).itineraryId);

                                    });

                                    //set the negative button with no
                                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                        // If user click no then dialog box is canceled.
                                        dialog.cancel();
                                    });

                                    //create and show the alert dialog
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                    return true;
                                }
                            });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void animateButton(View view, int translationY, long duration) {
        //create an animation where the button will move on the y axis (vertically)
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", translationY);
        animator.setDuration(duration);
        animator.start();
    }

    private void setPopupWindow(String from){

        //initialise the itineraryPopupView
        itineraryPopupView = layoutInflater.inflate(R.layout.create_itinerary_popup, null);

        popupWindow = new PopupWindow(itineraryPopupView,width,height,focusable);

        //display the popup window
        itineraryListRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(itineraryListRL, Gravity.CENTER,0,0);

            }
        });

        //initialise the layouts from itineraryPopupView
        RelativeLayout createItineraryPopupRL = itineraryPopupView.findViewById(R.id.createItineraryPopupRL);
        EditText itineraryTitleET = itineraryPopupView.findViewById(R.id.itineraryTitleET);
        EditText itineraryLocationET = itineraryPopupView.findViewById(R.id.itineraryLocationET);
        EditText itineraryFromET = itineraryPopupView.findViewById(R.id.itineraryFromET);
        EditText itineraryToET = itineraryPopupView.findViewById(R.id.itineraryToET);
        Button createItineraryBtn = itineraryPopupView.findViewById(R.id.createItineraryBtn);

        //if user clicks outside the popup window, close the popup window
        createItineraryPopupRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        //ensure that the itineraryFromET will only take in date inputs in the format of DD/MM/YYYY
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

        //ensure that the itineraryToET will only take in date inputs in the format of DD/MM/YYYY
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

                //take in the input and create the variables to hold the inputs
                String itineraryTitleStr = itineraryTitleET.getText().toString();
                String itineraryLocationStr = itineraryLocationET.getText().toString();
                String itineraryFromStr = itineraryFromET.getText().toString();
                String itineraryToStr = itineraryToET.getText().toString();

                if (from.equals("generate")){
                    generatingItineraryTitle = itineraryTitleStr;
                    generatingItineraryLocation = itineraryLocationStr;
                    itineraryTitleStr += " - (Generating...)";
                }







                //check if the required fields are filled
                //if not, display a toast message to notify the user
                if(TextUtils.isEmpty(itineraryTitleStr) || TextUtils.isEmpty(itineraryLocationStr) || TextUtils.isEmpty(itineraryFromStr) || TextUtils.isEmpty(itineraryToStr)){
                    Toast.makeText(ItineraryList.this, "Please make sure all the necessary fields are filled", Toast.LENGTH_SHORT).show();
                }
                else {

                    Log.d(TAG, "onClick: itinerary from str: " + itineraryFromStr);

                    try {

                        //format the input date and get the day count
                        Date itineraryFromDate = new SimpleDateFormat("dd/MM/yyyy").parse(itineraryFromStr);
                        Date itineraryToDate = new SimpleDateFormat("dd/MM/yyyy").parse(itineraryToStr);

                        if(itineraryToDate.before(itineraryFromDate)){
                            Toast.makeText(ItineraryList.this, "The second date cannot be before the first date.", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            Log.d(TAG, "onClick: itinerary from date: " + itineraryFromDate.toString());

                            long difference = Math.abs(itineraryToDate.getTime() - itineraryFromDate.getTime());
                            long differenceDates = difference / (24 * 60 * 60 * 1000);

                            //add 1 more day as we want day count
                            differenceDates++;
                            String dayCount = Long.toString(differenceDates);

                            Log.d(TAG, "onClick: day count: " + dayCount);

                            //create a new itinerary day model to be inserted into firestore
                            ArrayList<ItineraryItemModel> itineraryDayArrayList = new ArrayList<>();

                            ItineraryModel itineraryModel = new ItineraryModel("", itineraryTitleStr, itineraryLocationStr, itineraryFromStr, itineraryToStr, Integer.parseInt(dayCount), itineraryDayArrayList);

                            insertItinerary(itineraryModel, from);

                        }

//                        popupWindow.dismiss();
//                        getUserItinerary();
//
//                        Intent toItineraryPage = new Intent(getApplicationContext(), ItineraryPage.class);
//                        toItineraryPage.putExtra("itinerary", itineraryModel);
//
//                        startActivity(toItineraryPage);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    //ItineraryModel itineraryModel = new ItineraryModel(itineraryTitleStr, )

                }

            }
        });

    }

    private void insertItinerary(ItineraryModel itineraryModel, String from){

        // Create a new itinerary
        Map<String, Object> itinerary = new HashMap<>();
        itinerary.put("itinerary_id", itineraryModel.itineraryId);
        itinerary.put("itinerary_title", itineraryModel.itineraryName);
        itinerary.put("itinerary_location", itineraryModel.itineraryLocation);
        itinerary.put("itinerary_from", itineraryModel.itineraryDateFrom);
        itinerary.put("itinerary_to", itineraryModel.itineraryDateTo);
        itinerary.put("itinerary_day_count", itineraryModel.itineraryDaysCount);
        itinerary.put("user_uid", uid);


        // Add a new itinerary document with a generated ID
        db.collection("itinerary")
                .add(itinerary)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot (itinerary) added with ID: " + documentReference.getId());
                        String itineraryDocId = documentReference.getId();

                        //if the itinerary is being generated, assign the generating itinerary's id
                        if(from.equals("generate")){
                            generatingItineraryId = itineraryDocId;
                        }

                        //add the doc ref id into the itinerary_id
                        DocumentReference itineraryRef = db.collection("itinerary").document(itineraryDocId);

                        //update the itinerary_id with the firebase generated id
                        itineraryRef
                                .update("itinerary_id", itineraryDocId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                                        if(from.equals("generate")){
                                            generateItinerary(itineraryModel.itineraryDateFrom, itineraryModel.itineraryDaysCount);
                                        }

                                        //dismiss the popup window and reset the listview
                                        popupWindow.dismiss();
                                        itineraryArrayList.clear();
                                        customAdapter.notifyDataSetChanged();

                                        //close the buttons
                                        optionsSelected = false;
                                        animateButton(createItineraryLL, 0, 500);
                                        animateButton(generateItineraryLL, 0, 500);
                                        createItineraryTV.setVisibility(View.GONE);
                                        generateItineraryTV.setVisibility(View.GONE);

                                        //get the user's itinerary
                                        getUserItinerary();

//                                        Intent toItineraryPage = new Intent(getApplicationContext(), ItineraryPage.class);
//                                        toItineraryPage.putExtra("itinerary", itineraryModel);
//
//                                        startActivity(toItineraryPage);

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
                        Log.w(TAG, "Error adding document (itinerary)", e);
                    }
                });

    }

    private void deleteItinerary(String docID){

        //delete the user's itinerary
        db.collection("itinerary").document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");

                        //once the itinerary is deleted, get all the itinerary item of the deleted itinerary
                        db.collection("itinerary_item")
                                .whereEqualTo("user_uid", uid)
                                .whereEqualTo("itinerary_id", docID)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            //delete all the itinerary item related to the deleted itinerary
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                String docId = document.getId();

                                                db.collection("itinerary_item").document(docId)
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");


                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error deleting document", e);
                                                            }
                                                        });

                                            }

                                            //clear the current itinerary listview and get the user's travel itinerary again
                                            itineraryArrayList.clear();
                                            customAdapter.notifyDataSetChanged();
                                            getUserItinerary();

                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

//    public void callAPI(String question){
//
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("model","gpt-3.5-turbo");
//            JSONArray messageArr = new JSONArray();
//            JSONObject obj = new JSONObject();
//            obj.put("role", "user");
//            obj.put("content", question);
//            messageArr.put(obj);
//
//            jsonBody.put("message", messageArr);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url("https://api.openai.com/v1/chat/completions")
//                .header("Authorization","Bearer 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")
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.d(TAG, "onFailure: Failed to load response due to: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
//
//                if(response.isSuccessful()){
//                    JSONObject  jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(response.body().string());
//                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
//                        String result = jsonArray.getJSONObject(0)
//                                .getJSONObject("message")
//                                .getString("content");
//                        Log.d(TAG, "onResponse: " + result.trim());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }else{
//                    Log.d(TAG, "onResponseFailure: Failed to load response due to: " + response.body().string());
//                }
//
//            }
//        });
//
//
//    }

    private void setItineraryTitle(String docID){

        DocumentReference itineraryRef = db.collection("itinerary").document(docID);

        //update the itinerary title
        itineraryRef
                .update("itinerary_title", generatingItineraryTitle)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        //clear the current itinerary listview and get the user's travel itinerary again
                        itineraryArrayList.clear();
                        customAdapter.notifyDataSetChanged();
                        getUserItinerary();
                        currentlyGenerating = false;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    private void addItineraryItems(ItineraryItemModel itineraryItemModel, int currentNumber, int numberCount){

        // Create a new itinerary_item
        Map<String, Object> itineraryItem = new HashMap<>();
        itineraryItem.put("itinerary_id", generatingItineraryId);
        itineraryItem.put("itinerary_item_location", itineraryItemModel.locationName);
        itineraryItem.put("itinerary_item_date", itineraryItemModel.locationDate);
        itineraryItem.put("itinerary_item_from", itineraryItemModel.locationTimeFrom);
        itineraryItem.put("itinerary_item_to", itineraryItemModel.locationTimeTo);
        itineraryItem.put("itinerary_item_notes", itineraryItemModel.notes);
        itineraryItem.put("itinerary_date_time", itineraryItemModel.locationFrom);
        itineraryItem.put("user_uid", uid);


        // Add a new itinerary item document with a generated ID
        db.collection("itinerary_item")
                .add(itineraryItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot (itinerary_item) added with ID: " + documentReference.getId());

                        if (currentNumber == numberCount){
                            setItineraryTitle(generatingItineraryId);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document (itinerary_item)", e);
                    }
                });

    }

    private void generateItinerary(String date, int dayCount){

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try{

                    Request request = new Request.Builder()
                            .url("https://ai-trip-planner.p.rapidapi.com/?days=" + dayCount + "&destination=" + generatingItineraryLocation)
                            .get()
                            .addHeader("X-RapidAPI-Key", "b34ecf7a0fmsh5cbb7c353f899abp1c8c15jsn43d3583a9734")
                            .addHeader("X-RapidAPI-Host", "ai-trip-planner.p.rapidapi.com")
                            .build();

                    Response response = client.newCall(request).execute();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.d(TAG, "onFailure: error getting trip: " + e);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ItineraryList.this, "Please ensure that the inputs (specifically the location) is proper.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            deleteItinerary(generatingItineraryId);
                            currentlyGenerating = false;

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            String responseStr = response.body().string();

                            Log.d(TAG, "onResponse: response str: " + responseStr);

                            try{

                                JSONObject jsonObject = new JSONObject(responseStr);
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("plan"));

                                ArrayList<ItineraryItemModel> itineraryItemArrayList = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject dayJson = jsonArray.getJSONObject(i);
                                    String dayStr = dayJson.getString("day");

                                    Log.d(TAG, "onResponse: dayStr: " + dayStr);

                                    //get the day, month and full date in string
                                    SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
                                    Date itineraryFromDate = date_format.parse(date);

                                    Calendar cal = Calendar.getInstance();

                                    cal.setTime(itineraryFromDate);

                                    //the calender will be the first date, add i to it to act as a counter for the days
                                    cal.add(Calendar.DAY_OF_MONTH, i);

                                    //get the current date in the for loop in string
                                    String currentDate = date_format.format(cal.getTime());

                                    //create a json array to hold all the activities of the current itinerary day
                                    JSONArray activityJson = dayJson.getJSONArray("activities");

                                    for (int x = 0; x < activityJson.length(); x++){

                                        JSONObject dayDetails = activityJson.getJSONObject(x);
                                        String time = dayDetails.getString("time");
                                        String description = dayDetails.getString("description");

                                        Log.d(TAG, "onResponse: time: " + time);
                                        Log.d(TAG, "onResponse: description: " + description);


                                        //change the string date data into date time variable
                                        Date itineraryFromDateTime;

                                        //need to first make sure the time is in the format of HH:mm
                                        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a");
                                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

                                        try {
                                            Date date = inputFormat.parse(time); // This converts the time string to a Date object.
                                            time = outputFormat.format(date); // This formats the Date object back to a time string in "HH:mm" format.
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                        String itineraryFromDateTimeStr = currentDate + " " + time;
                                        DateFormat dateTimeformat= new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        itineraryFromDateTime = dateTimeformat.parse(itineraryFromDateTimeStr);


                                        ItineraryItemModel itineraryItemModel = new ItineraryItemModel(generatingItineraryId, description, currentDate, time, "", "", "", itineraryFromDateTime);

                                        itineraryItemArrayList.add(itineraryItemModel);

                                    }

                                }

                                //for loop the insertion of the itinerary days into firestore
                                for(int i = 0; i<itineraryItemArrayList.size(); i++){

                                    addItineraryItems(itineraryItemArrayList.get(i), (i + 1), itineraryItemArrayList.size());

                                }

                            }
                            catch (Exception e){
                                Log.d(TAG, "onResponse: error parsing json: " + e);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ItineraryList.this, "Please ensure that the inputs (specifically the location) is proper.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                deleteItinerary(generatingItineraryId);
                                currentlyGenerating = false;
                            }

                        }
                    });

                }
                catch (Exception e){
                    Log.d(TAG, "run: error fetching trip planner api");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ItineraryList.this, "Please ensure that the inputs (specifically the location) is proper.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    deleteItinerary(generatingItineraryId);
                    currentlyGenerating = false;
                }

            }
        });



    }


//    public void generateItinerary(String message) {
//
//        String url = "https://api.openai.com/v1/chat/completions";
//        String apiKey = "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";
//        String model = "gpt-3.5-turbo";
//
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//
//                    // Create the HTTP POST request
//                    URL obj = new URL(url);
//                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//                    con.setRequestMethod("POST");
//                    con.setRequestProperty("Authorization", "Bearer " + apiKey);
//                    con.setRequestProperty("Content-Type", "application/json");
//
//                    // Build the request body
//                    String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
//                    con.setDoOutput(true);
//                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
//                    writer.write(body);
//                    writer.flush();
//                    writer.close();
//
//                    // Get the response
//                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                    String inputLine;
//                    StringBuffer response = new StringBuffer();
//                    while ((inputLine = in.readLine()) != null) {
//                        response.append(inputLine);
//                    }
//                    in.close();
//
//                    // returns the extracted contents of the response.
//                    gptResponse = extractContentFromResponse(response.toString());
//
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        });
//
//    }
//
//    // This method extracts the response expected from chatgpt and returns it.
//    public static String extractContentFromResponse(String response) {
//        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
//        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
//        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
//    }

}