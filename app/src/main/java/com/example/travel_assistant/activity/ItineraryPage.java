package com.example.travel_assistant.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.TravelItineraryDayAdapter;
import com.example.travel_assistant.adapter.TravelItineraryListAdapter;
import com.example.travel_assistant.model.ItineraryDayModel;
import com.example.travel_assistant.model.ItineraryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ItineraryPage extends AppCompatActivity {

    final String TAG = ItineraryPage.this.toString();

    //layouts
    TextView itineraryTitleTV, itineraryLocationTV, itineraryDateTV;
    LinearLayout dateCardsLL;
    Date itineraryFromDate, itineraryToDate;
    FloatingActionButton addItineraryFAB;
    android.widget.ListView itineraryDayLV;
    TravelItineraryDayAdapter customAdapter;

    //variables
    int firstCardSet = 1;
    String uid = "";
    ArrayList<ItineraryDayModel> itineraryDayArrayList = new ArrayList<>();
    ItineraryModel itineraryModel;
    String currentDate = "";

    //popup window variables
    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View itineraryAddPopupView;
    View itineraryDetailsPopupView;
    RelativeLayout itineraryPageRL;

    //initialise firebase auth and firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_page);

        //initialise the layouts
        itineraryTitleTV = findViewById(R.id.itineraryTitleTV);
        itineraryLocationTV = findViewById(R.id.itineraryLocationTV);
        itineraryDateTV = findViewById(R.id.itineraryDateTV);
        dateCardsLL = findViewById(R.id.dateCardsLL);
        addItineraryFAB = findViewById(R.id.addItineraryFAB);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        itineraryPageRL = findViewById(R.id.itineraryPageRL);
        itineraryDayLV = findViewById(R.id.itineraryDayLV);

        //get the current user's uid
        try {
            uid = auth.getCurrentUser().getUid();
            //uid = "000001";
            Log.d(TAG, "uid:" + uid);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        //get the itinerary data from the ItineraryList activity
        Intent fromItineraryList = getIntent();
        itineraryModel = (ItineraryModel) fromItineraryList.getSerializableExtra("itinerary");
//        itineraryTitle = fromItineraryList.getStringExtra("itineraryTitle");
//        itineraryLocation = fromItineraryList.getStringExtra("itineraryLocation");
//        itineraryFrom = fromItineraryList.getStringExtra("itineraryFrom");
//        itineraryTo = fromItineraryList.getStringExtra("itineraryTo");
//        itineraryDayCountStr = fromItineraryList.getStringExtra("itineraryDayCount");
//        itineraryDayCountInt = Integer.parseInt(itineraryDayCountStr);

        //Log.d(TAG, "onCreate: " + itineraryTitle);

        //set the textviews with the itinerary data
        itineraryTitleTV.setText(itineraryModel.itineraryName);
        itineraryLocationTV.setText(itineraryModel.itineraryLocation);
        itineraryDateTV.setText(itineraryModel.itineraryDateFrom + " - " + itineraryModel.itineraryDateTo);

        //inflate the date cards of the itinerary and get all the itinerary items
        inflateDateCards();
        getItineraryDay();
        

        addItineraryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setItineraryAddPopupView();

            }
        });

    }

    public void deleteItineraryItem(String docID){

        //delete the selected itinerary item
        db.collection("itinerary_item").document(docID)
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

    public void editItineraryItem(String docID, String location, String timeFrom, String timeTo, String notes){

        //update the itinerary item
        DocumentReference itineraryItemRef = db.collection("itinerary_item").document(docID);

        //if timeTo is not set, make sure it is then empty to prevent formatting errors
        if(timeTo.equals("-")){
            timeTo = "";
        }

        //update all the itinerary item data
        itineraryItemRef
                .update("itinerary_item_location", location,
                        "itinerary_item_from", timeFrom,
                        "itinerary_item_to", timeTo,
                        "itinerary_item_notes", notes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    public void setItineraryDetailsPopupView(ItineraryDayModel itineraryDayModel){

        //initialise itineraryDetailsPopupView
        itineraryDetailsPopupView = layoutInflater.inflate(R.layout.edit_travel_itinerary_popup, null);

        popupWindow = new PopupWindow(itineraryDetailsPopupView,width,height,focusable);

        //display the itinerary details popup window
        itineraryPageRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(itineraryPageRL, Gravity.CENTER,0,0);

            }
        });


        RelativeLayout editItineraryRL = itineraryDetailsPopupView.findViewById(R.id.editItineraryRL);

        //if user clicks outside of the popup window, close the popup window
        editItineraryRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        //initialise the layouts from itineraryDetailsPopupView
        EditText itineraryItemLocationET = itineraryDetailsPopupView.findViewById(R.id.itineraryItemLocationET);
        EditText itineraryItemTimeFromET = itineraryDetailsPopupView.findViewById(R.id.itineraryItemTimeFromET);
        EditText itineraryItemTimeToET = itineraryDetailsPopupView.findViewById(R.id.itineraryItemTimeToET);
        EditText itineraryItemNotesET = itineraryDetailsPopupView.findViewById(R.id.itineraryItemNotesET);
        Button saveItineraryItemBtn = itineraryDetailsPopupView.findViewById(R.id.saveItineraryItemBtn);

        //set the edit texts with the current itinerary item data
        itineraryItemLocationET.setText(itineraryDayModel.locationName);
        itineraryItemTimeFromET.setText(itineraryDayModel.locationTimeFrom);
        itineraryItemTimeToET.setText(itineraryDayModel.locationTimeTo);
        itineraryItemNotesET.setText(itineraryDayModel.notes);

        //if time to is empty, set it to display "-"
        if(itineraryDayModel.locationTimeTo.equals("")){
            itineraryItemTimeToET.setText("-");
        }

        itineraryItemTimeFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //set a time picker dialog

                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ItineraryPage.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                //if the hour or minute only has 1 digit, add a 0 in front
                                //to prevent formatting and ordering errors
                                String hourStr = String.valueOf(hourOfDay);
                                String minuteStr = String.valueOf(minute);
                                if(hourStr.length() < 2){
                                    hourStr = "0" + hourStr;
                                }
                                if(minuteStr.length() < 2){
                                    minuteStr = "0" + minuteStr;
                                }

                                //set the selected time to the edittext
                                itineraryItemTimeFromET.setText(hourStr + ":" + minuteStr);
                            }
                        }, hour, minute, false);
                // display our time picker dialog.
                timePickerDialog.show();

            }
        });

        itineraryItemTimeToET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //set a time picker dialog

                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ItineraryPage.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.

                                //if the selected hour or minute only has 1 digit, add a 0 in front of the number
                                //to prevent formatting and ordering errors
                                String hourStr = String.valueOf(hourOfDay);
                                String minuteStr = String.valueOf(minute);
                                if(hourStr.length() < 2){
                                    hourStr = "0" + hourStr;
                                }
                                if(minuteStr.length() < 2){
                                    minuteStr = "0" + minuteStr;
                                }

                                //set the selected time to the edittext
                                itineraryItemTimeToET.setText(hourStr + ":" + minuteStr);
                            }
                        }, hour, minute, false);
                // display our time picker dialog.
                timePickerDialog.show();

            }
        });

        saveItineraryItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //create the string variables to hold the input data
                String editedLocation = itineraryItemLocationET.getText().toString();
                String editedTimeFrom = itineraryItemTimeFromET.getText().toString();
                String editedTimeTo = itineraryItemTimeToET.getText().toString();
                String editedNotes = itineraryItemNotesET.getText().toString();

                //call the method that updates the itinerary item using the user input
                editItineraryItem(itineraryDayModel.docID, editedLocation, editedTimeFrom, editedTimeTo, editedNotes);

                //clear the itinerary item listview and get the itinerary items from firestore
                itineraryDayArrayList.clear();
                customAdapter.notifyDataSetChanged();
                getItineraryDay();

                //close the popup window
                popupWindow.dismiss();
            }
        });


    }

    public void addItineraryDays(ItineraryDayModel itineraryDayModel){

        // Create a new itinerary_item
        Map<String, Object> itineraryItem = new HashMap<>();
        itineraryItem.put("itinerary_id", itineraryModel.itineraryId);
        //itineraryItem.put("itinerary_item_id", "000001");
        itineraryItem.put("itinerary_item_location", itineraryDayModel.locationName);
        itineraryItem.put("itinerary_item_date", itineraryDayModel.locationDate);
        itineraryItem.put("itinerary_item_from", itineraryDayModel.locationTimeFrom);
        itineraryItem.put("itinerary_item_to", itineraryDayModel.locationTimeTo);
        itineraryItem.put("itinerary_item_notes", itineraryDayModel.notes);
        itineraryItem.put("itinerary_date_time", itineraryDayModel.locationFrom);
        itineraryItem.put("user_uid", uid);


        // Add a new itinerary item document with a generated ID
        db.collection("itinerary_item")
                .add(itineraryItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot (itinerary_item) added with ID: " + documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document (itinerary_item)", e);
                    }
                });

    }

    public void getItineraryDay(){

        //get the user's itinerary item using the user's uid and selected itinerary's id
        db.collection("itinerary_item")
                .whereEqualTo("user_uid", uid)
                .whereEqualTo("itinerary_id", itineraryModel.itineraryId)
                .whereEqualTo("itinerary_item_date", currentDate)
                .orderBy("itinerary_date_time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: got on success");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                //create the string variables to hold the data from firestore
                                String itineraryId = document.getData().get("itinerary_id").toString();
                                //String itineraryItemId = document.getData().get("itinerary_item_id").toString();
                                String itineraryItemDate = document.getData().get("itinerary_item_date").toString();
                                String itineraryItemLocation = document.getData().get("itinerary_item_location").toString();
                                String itineraryItemTimeFrom = document.getData().get("itinerary_item_from").toString();
                                String itineraryItemTimeTo = document.getData().get("itinerary_item_to").toString();
                                String itineraryItemNotes = document.getData().get("itinerary_item_notes").toString();
                                String docID = document.getId();

                                //change the string date data into date variable
                                Date itineraryFromDate, itineraryToDate;

                                try {
                                    String itineraryFromDateTime = itineraryItemDate + " " + itineraryItemTimeFrom;
                                    String itineraryToDateTime = itineraryItemDate + " " + itineraryItemTimeTo;
                                    DateFormat dateformat= new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    itineraryFromDate = dateformat.parse(itineraryFromDateTime);

                                    if(itineraryItemTimeTo.equals("")){
                                        DateFormat dateformat2= new SimpleDateFormat("dd/MM/yyyy");
                                        itineraryToDate = dateformat2.parse(itineraryToDateTime);
                                    }
                                    else{
                                        itineraryToDate = dateformat.parse(itineraryToDateTime);
                                    }

                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                //create a new itinerary day model to be added into the itinerary day array list
                                ItineraryDayModel itineraryDayModel = new ItineraryDayModel(itineraryId, itineraryItemLocation, itineraryItemDate, itineraryItemTimeFrom,itineraryItemTimeTo, itineraryItemNotes, docID, itineraryFromDate, itineraryToDate);
                                itineraryDayArrayList.add(itineraryDayModel);

                            }

                            //populate the itinerary day listview
                            customAdapter = new TravelItineraryDayAdapter(getApplicationContext(), itineraryDayArrayList);
                            itineraryDayLV.setAdapter(customAdapter);

                            itineraryDayLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    //display the popup window to allows the users to edit and update the selected itinerary item
                                    setItineraryDetailsPopupView(itineraryDayArrayList.get(i));

                                }
                            });

                            itineraryDayLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    //if user long clicks on the itinerary item,
                                    //display an alert dialog to confirm the deletion of the itinerary item

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ItineraryPage.this);

                                    //set the message for the alert dialog
                                    builder.setMessage("Do you want to delete the itinerary item?");

                                    //set alert title
                                    builder.setTitle("Alert !");

                                    //set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                    builder.setCancelable(false);

                                    //set the positive button with yes
                                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {

                                        //if user clicks yes, then delete the selected itinerary item
                                        deleteItineraryItem(itineraryDayArrayList.get(i).docID);

                                        //clear the itinerary day list view and get the itinerary items
                                        itineraryDayArrayList.clear();
                                        customAdapter.notifyDataSetChanged();
                                        getItineraryDay();

                                    });

                                    //set the negative button with no
                                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                        // If user click no then dialog box is canceled.
                                        dialog.cancel();
                                    });

                                    //create and show the Alert dialog
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



    public void setItineraryAddPopupView(){

        //initialise the itineraryAddPopupView and popup window
        itineraryAddPopupView = layoutInflater.inflate(R.layout.add_travel_itinerary_popup, null);

        popupWindow = new PopupWindow(itineraryAddPopupView,width,height,focusable);

        //display the popup window
        itineraryPageRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(itineraryPageRL, Gravity.CENTER,0,0);

            }
        });

        //initialise the layout variables from itineraryAddPopupView
        RelativeLayout addItineraryRL = itineraryAddPopupView.findViewById(R.id.addItineraryRL);
        EditText itineraryLocationET = itineraryAddPopupView.findViewById(R.id.itineraryLocationET);
        EditText itineraryFromET = itineraryAddPopupView.findViewById(R.id.itineraryFromET);
        EditText itineraryToET = itineraryAddPopupView.findViewById(R.id.itineraryToET);
        EditText itineraryNotesET = itineraryAddPopupView.findViewById(R.id.itineraryNotesET);
        Button addItineraryBtn = itineraryAddPopupView.findViewById(R.id.addItineraryBtn);

        //if user clicks outside of the popup window, close the popup window
        addItineraryRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        addItineraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if the required fields are filled
                //if not, display a toast message to notify the user to fill in the required fields
                if (TextUtils.isEmpty(itineraryLocationET.getText()) || TextUtils.isEmpty(itineraryFromET.getText())){
                    Toast.makeText(ItineraryPage.this, "Please at least fill in the location and time field", Toast.LENGTH_SHORT).show();
                }
                else {

                    //create the string variables to hold the input data
                    String itineraryLocation = itineraryLocationET.getText().toString();
                    String itineraryFrom = itineraryFromET.getText().toString();
                    String itineraryTo = "";
                    String itineraryNotes = "";

                    //for the optional fields, check if it is filled
                    //if the optional fields are filled, get the input data
                    if (!TextUtils.isEmpty(itineraryToET.getText())){
                        itineraryTo = itineraryToET.getText().toString();
                    }

                    if (!TextUtils.isEmpty(itineraryNotesET.getText())){
                        itineraryNotes = itineraryNotesET.getText().toString();
                    }

                    //change the string date data into date variable
                    Date itineraryFromDate, itineraryToDate;

                    try {
                        String itineraryFromDateTime = currentDate + " " + itineraryFrom;
                        String itineraryToDateTime = currentDate + " " + itineraryTo;
                        DateFormat dateformat= new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        itineraryFromDate = dateformat.parse(itineraryFromDateTime);

                        if(itineraryTo.equals("")){
                            DateFormat dateformat2= new SimpleDateFormat("dd/MM/yyyy");
                            itineraryToDate = dateformat2.parse(itineraryToDateTime);
                        }
                        else{
                            itineraryToDate = dateformat.parse(itineraryToDateTime);
                        }

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }


                    //create an itinerary day model to be added into firestore
                    ItineraryDayModel itineraryDayModel = new ItineraryDayModel(itineraryModel.itineraryId, itineraryLocation, currentDate, itineraryFrom, itineraryTo, itineraryNotes, "", itineraryFromDate, itineraryToDate);

                    addItineraryDays(itineraryDayModel);

                    //close the popup window
                    popupWindow.dismiss();

                    //clear the itinerary day listview and get the itinerary items
                    itineraryDayArrayList.clear();
                    customAdapter.notifyDataSetChanged();
                    getItineraryDay();

                }
            }
        });

        itineraryFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //set a time picker dialog

                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ItineraryPage.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                //if the selected hour and minute only has 1 digit, add a 0 in front of the number
                                //in order to prevent formatting and ordering errors
                                String hourStr = String.valueOf(hourOfDay);
                                String minuteStr = String.valueOf(minute);
                                if(hourStr.length() < 2){
                                    hourStr = "0" + hourStr;
                                }
                                if(minuteStr.length() < 2){
                                    minuteStr = "0" + minuteStr;
                                }

                                //set the selected time into the edittext
                                itineraryFromET.setText(hourStr + ":" + minuteStr);
                            }
                        }, hour, minute, false);
                // display our time picker dialog.
                timePickerDialog.show();

            }
        });

        itineraryToET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //set a time picker dialog

                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ItineraryPage.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                //if the selected hour and minute only has 1 digit, add a 0 in front of the number
                                //in order to prevent formatting and ordering errors
                                String hourStr = String.valueOf(hourOfDay);
                                String minuteStr = String.valueOf(minute);
                                if(hourStr.length() < 2){
                                    hourStr = "0" + hourStr;
                                }
                                if(minuteStr.length() < 2){
                                    minuteStr = "0" + minuteStr;
                                }

                                //set the selected time to the edittext
                                itineraryToET.setText(hourStr + ":" + minuteStr);
                            }
                        }, hour, minute, false);
                // display our time picker dialog.
                timePickerDialog.show();

            }
        });

    }

    public void inflateDateCards(){

        //initialise the card layout variables
        //set the array count to the number of days
        //this is to make sure we have as many views and layouts as the day count
        View[] cardViews = new View[itineraryModel.itineraryDaysCount];
        TextView[] cardDayTexts = new TextView[itineraryModel.itineraryDaysCount];
        TextView[] cardMonthTexts = new TextView[itineraryModel.itineraryDaysCount];
        TextView[] cardFullDateTexts = new TextView[itineraryModel.itineraryDaysCount];


        // Initialize cardLayouts and cardTexts arrays with references to the views
        for (int i = 0; i < itineraryModel.itineraryDaysCount; i++) {
            cardViews[i] = getLayoutInflater().inflate(R.layout.itinerary_date_card, dateCardsLL, false);
            cardDayTexts[i] = cardViews[i].findViewById(R.id.dateTV);
            cardMonthTexts[i] = cardViews[i].findViewById(R.id.monthTV);
            cardFullDateTexts[i] = cardViews[i].findViewById(R.id.fullDateTV);
            //cardLayouts[i] = findViewById(getResources().getIdentifier("card" + (i + 1), "id", getPackageName()));
            //cardTexts[i] = findViewById(getResources().getIdentifier("card" + (i + 1) + "_text", "id", getPackageName()));

            final int cardIndex = i; // Make a final copy for use in the click listener

            try {

                //get the day, month and full date in string
                Date itineraryFromDate = new SimpleDateFormat("dd/MM/yyyy").parse(itineraryModel.itineraryDateFrom);

                Calendar cal = Calendar.getInstance();

                cal.setTime(itineraryFromDate);

                //the calender will be the first date, add i to it to act as a counter for the days
                cal.add(Calendar.DAY_OF_MONTH, i);

                //get the month name string
                SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                String month_name = month_date.format(cal.getTime());

                //get the full date string
                SimpleDateFormat full_date = new SimpleDateFormat("dd/MM/yyyy");
                String fullDateStr = full_date.format(cal.getTime());

                //set the text views with the data accordingly
                cardDayTexts[i].setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                cardMonthTexts[i].setText(month_name);
                cardFullDateTexts[i].setText(fullDateStr);

                //make sure the currentDate will always be the first card's date
                //once the first card is set, set the rest of the card text views to white to signify unselected
                if(firstCardSet == 1){
                    firstCardSet--;
                    //currentDate = cardDayTexts[i].getText().toString() + "/" + cardMonthTexts[i].getText().toString();
                    currentDate = cardFullDateTexts[i].getText().toString();
                    Log.d(TAG, "inflateDateCards: currentDate: " + currentDate);
                }
                else{
                    cardDayTexts[i].setTextColor(getResources().getColor(R.color.white));
                    cardMonthTexts[i].setTextColor(getResources().getColor(R.color.white));
                }

//                dateCardView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                        dateTV.setTextColor(getResources().getColor(R.color.black));
//                        monthTV.setTextColor(getResources().getColor(R.color.black));
//                        currentDate = dateTV.getText().toString() + "/" + monthTV.getText().toString();
//
//                        return true;
//                    }
//                });

                cardViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Reset text color for all cards, white to signify unselected
                        for (int j = 0; j < itineraryModel.itineraryDaysCount; j++) {
                            cardDayTexts[j].setTextColor(getResources().getColor(R.color.white));
                            cardMonthTexts[j].setTextColor(getResources().getColor(R.color.white));
                        }

                        // Set text color for the selected card
                        cardDayTexts[cardIndex].setTextColor(getResources().getColor(R.color.black));
                        cardMonthTexts[cardIndex].setTextColor(getResources().getColor(R.color.black));

                        //get the currentDate of the selected card
                        //currentDate = cardDayTexts[cardIndex].getText().toString() + "/" + cardMonthTexts[cardIndex].getText().toString();
                        currentDate = cardFullDateTexts[cardIndex].getText().toString();
                        Log.d(TAG, "inflateDateCards: currentDate: " + currentDate);

                        //clear the itinerary day listview and get the itinerary items for the selected date
                        itineraryDayArrayList.clear();
                        customAdapter.notifyDataSetChanged();
                        getItineraryDay();
                    }
                });

                //add all the views to the linear layout holding all the cards
                dateCardsLL.addView(cardViews[i]);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

//        for (int i = 0; i < itineraryModel.itineraryDaysCount; i++){
//
//            View dateCardView = getLayoutInflater().inflate(R.layout.itinerary_date_card, dateCardsLL, false);
//            TextView dateTV = dateCardView.findViewById(R.id.dateTV);
//            TextView monthTV = dateCardView.findViewById(R.id.monthTV);
//
//            try {
//
//                Date itineraryFromDate = new SimpleDateFormat("dd/MM/yyyy").parse(itineraryModel.itineraryDateFrom);
//
//                Calendar cal = Calendar.getInstance();
//
//                cal.setTime(itineraryFromDate);
//
//                cal.add(Calendar.DAY_OF_MONTH, i);
//
//                SimpleDateFormat month_date = new SimpleDateFormat("MMM");
//                String month_name = month_date.format(cal.getTime());
//
//                dateTV.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
//                monthTV.setText(month_name);
//
//                if(firstCardSet == 1){
//                    firstCardSet--;
//                    currentDate = dateTV.getText().toString() + "/" + monthTV.getText().toString();
//                }
//                else{
//                    dateTV.setTextColor(getResources().getColor(R.color.white));
//                    monthTV.setTextColor(getResources().getColor(R.color.white));
//                }
//
//                dateCardView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                        dateTV.setTextColor(getResources().getColor(R.color.black));
//                        monthTV.setTextColor(getResources().getColor(R.color.black));
//                        currentDate = dateTV.getText().toString() + "/" + monthTV.getText().toString();
//
//                        return true;
//                    }
//                });
//
//                dateCardsLL.addView(dateCardView);
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//
//
//        }

    }

}