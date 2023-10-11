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
    ItineraryModel itineraryModel;
    String currentDate = "";
    Date itineraryFromDate, itineraryToDate;
    TextView itineraryTitleTV, itineraryLocationTV, itineraryDateTV;
    LinearLayout dateCardsLL;
    int firstCardSet = 1;
    ArrayList<ItineraryDayModel> itineraryDayArrayList = new ArrayList<>();
    FloatingActionButton addItineraryFAB;
    android.widget.ListView itineraryDayLV;
    TravelItineraryDayAdapter customAdapter;

    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View itineraryAddPopupView;
    View itineraryDetailsPopupView;
    RelativeLayout itineraryPageRL;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_page);

        itineraryTitleTV = findViewById(R.id.itineraryTitleTV);
        itineraryLocationTV = findViewById(R.id.itineraryLocationTV);
        itineraryDateTV = findViewById(R.id.itineraryDateTV);
        dateCardsLL = findViewById(R.id.dateCardsLL);
        addItineraryFAB = findViewById(R.id.addItineraryFAB);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        itineraryPageRL = findViewById(R.id.itineraryPageRL);
        itineraryDayLV = findViewById(R.id.itineraryDayLV);

        Intent fromItineraryList = getIntent();
        itineraryModel = (ItineraryModel) fromItineraryList.getSerializableExtra("itinerary");
//        itineraryTitle = fromItineraryList.getStringExtra("itineraryTitle");
//        itineraryLocation = fromItineraryList.getStringExtra("itineraryLocation");
//        itineraryFrom = fromItineraryList.getStringExtra("itineraryFrom");
//        itineraryTo = fromItineraryList.getStringExtra("itineraryTo");
//        itineraryDayCountStr = fromItineraryList.getStringExtra("itineraryDayCount");
//        itineraryDayCountInt = Integer.parseInt(itineraryDayCountStr);

        //Log.d(TAG, "onCreate: " + itineraryTitle);

        itineraryTitleTV.setText(itineraryModel.itineraryName);
        itineraryLocationTV.setText(itineraryModel.itineraryLocation);
        itineraryDateTV.setText(itineraryModel.itineraryDateFrom + " - " + itineraryModel.itineraryDateTo);

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

        DocumentReference itineraryItemRef = db.collection("itinerary_item").document(docID);

        if(timeTo.equals("-")){
            timeTo = "";
        }
// Set the "isCapital" field of the city 'DC'
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

        itineraryDetailsPopupView = layoutInflater.inflate(R.layout.edit_travel_itinerary_popup, null);

        popupWindow = new PopupWindow(itineraryDetailsPopupView,width,height,focusable);

        itineraryPageRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(itineraryPageRL, Gravity.CENTER,0,0);

            }
        });

        RelativeLayout editItineraryRL = itineraryDetailsPopupView.findViewById(R.id.editItineraryRL);

        editItineraryRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        EditText itineraryItemLocationET = itineraryDetailsPopupView.findViewById(R.id.itineraryItemLocationET);
        EditText itineraryItemTimeFromET = itineraryDetailsPopupView.findViewById(R.id.itineraryItemTimeFromET);
        EditText itineraryItemTimeToET = itineraryDetailsPopupView.findViewById(R.id.itineraryItemTimeToET);
        EditText itineraryItemNotesET = itineraryDetailsPopupView.findViewById(R.id.itineraryItemNotesET);
        Button saveItineraryItemBtn = itineraryDetailsPopupView.findViewById(R.id.saveItineraryItemBtn);

        itineraryItemLocationET.setText(itineraryDayModel.locationName);
        itineraryItemTimeFromET.setText(itineraryDayModel.locationTimeFrom);
        itineraryItemTimeToET.setText(itineraryDayModel.locationTimeTo);
        itineraryItemNotesET.setText(itineraryDayModel.notes);

        if(itineraryDayModel.locationTimeTo.equals("")){
            itineraryItemTimeToET.setText("-");
        }

        saveItineraryItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String editedLocation = itineraryItemLocationET.getText().toString();
                String editedTimeFrom = itineraryItemTimeFromET.getText().toString();
                String editedTimeTo = itineraryItemTimeToET.getText().toString();
                String editedNotes = itineraryItemNotesET.getText().toString();

                editItineraryItem(itineraryDayModel.docID, editedLocation, editedTimeFrom, editedTimeTo, editedNotes);

                itineraryDayArrayList.clear();
                customAdapter.notifyDataSetChanged();
                getItineraryDay();

                popupWindow.dismiss();
            }
        });


    }

    public void addItineraryDays(ItineraryDayModel itineraryDayModel){

        // Create a new itinerary_item
        Map<String, Object> itineraryItem = new HashMap<>();
        itineraryItem.put("itinerary_id", itineraryModel.itineraryId);
        itineraryItem.put("itinerary_item_id", "000001");
        itineraryItem.put("itinerary_item_location", itineraryDayModel.locationName);
        itineraryItem.put("itinerary_item_date", itineraryDayModel.locationDate);
        itineraryItem.put("itinerary_item_from", itineraryDayModel.locationTimeFrom);
        itineraryItem.put("itinerary_item_to", itineraryDayModel.locationTimeTo);
        itineraryItem.put("itinerary_item_notes", itineraryDayModel.notes);
        itineraryItem.put("itinerary_date_time", itineraryDayModel.locationFrom);
        itineraryItem.put("user_id", "000001");


        // Add a new document with a generated ID
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

        db.collection("itinerary_item")
                .whereEqualTo("user_id", "000001")
                .whereEqualTo("itinerary_id", "000002")
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

                                String itineraryId = document.getData().get("itinerary_id").toString();
                                String itineraryItemId = document.getData().get("itinerary_item_id").toString();
                                String itineraryItemDate = document.getData().get("itinerary_item_date").toString();
                                String itineraryItemLocation = document.getData().get("itinerary_item_location").toString();
                                String itineraryItemTimeFrom = document.getData().get("itinerary_item_from").toString();
                                String itineraryItemTimeTo = document.getData().get("itinerary_item_to").toString();
                                String itineraryItemNotes = document.getData().get("itinerary_item_notes").toString();
                                String docID = document.getId();

                                //change into date
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


                                ItineraryDayModel itineraryDayModel = new ItineraryDayModel(itineraryId, itineraryItemLocation, itineraryItemDate, itineraryItemTimeFrom,itineraryItemTimeTo, itineraryItemNotes, docID, itineraryFromDate, itineraryToDate);
                                itineraryDayArrayList.add(itineraryDayModel);

                            }

                            customAdapter = new TravelItineraryDayAdapter(getApplicationContext(), itineraryDayArrayList);
                            itineraryDayLV.setAdapter(customAdapter);

                            itineraryDayLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    setItineraryDetailsPopupView(itineraryDayArrayList.get(i));

                                }
                            });

                            itineraryDayLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ItineraryPage.this);

                                    // Set the message show for the Alert time
                                    builder.setMessage("Do you want to delete the itinerary item?");

                                    // Set Alert Title
                                    builder.setTitle("Alert !");

                                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                    builder.setCancelable(false);

                                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {

                                        deleteItineraryItem(itineraryDayArrayList.get(i).docID);

                                        itineraryDayArrayList.clear();
                                        customAdapter.notifyDataSetChanged();
                                        getItineraryDay();

                                    });

                                    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                        // If user click no then dialog box is canceled.
                                        dialog.cancel();
                                    });

                                    // Create the Alert dialog
                                    AlertDialog alertDialog = builder.create();
                                    // Show the Alert Dialog box
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

        itineraryAddPopupView = layoutInflater.inflate(R.layout.add_travel_itinerary_popup, null);

        popupWindow = new PopupWindow(itineraryAddPopupView,width,height,focusable);

        itineraryPageRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(itineraryPageRL, Gravity.CENTER,0,0);

            }
        });

        RelativeLayout addItineraryRL = itineraryAddPopupView.findViewById(R.id.addItineraryRL);
        EditText itineraryLocationET = itineraryAddPopupView.findViewById(R.id.itineraryLocationET);
        EditText itineraryFromET = itineraryAddPopupView.findViewById(R.id.itineraryFromET);
        EditText itineraryToET = itineraryAddPopupView.findViewById(R.id.itineraryToET);
        EditText itineraryNotesET = itineraryAddPopupView.findViewById(R.id.itineraryNotesET);
        Button addItineraryBtn = itineraryAddPopupView.findViewById(R.id.addItineraryBtn);

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

                if (TextUtils.isEmpty(itineraryLocationET.getText()) || TextUtils.isEmpty(itineraryFromET.getText())){
                    Toast.makeText(ItineraryPage.this, "Please at least fill in the location and time field", Toast.LENGTH_SHORT).show();
                }
                else {

                    String itineraryLocation = itineraryLocationET.getText().toString();
                    String itineraryFrom = itineraryFromET.getText().toString();
                    String itineraryTo = "";
                    String itineraryNotes = "";

                    if (!TextUtils.isEmpty(itineraryToET.getText())){
                        itineraryTo = itineraryToET.getText().toString();
                    }

                    if (!TextUtils.isEmpty(itineraryNotesET.getText())){
                        itineraryNotes = itineraryNotesET.getText().toString();
                    }

                    //change into date
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


                    ItineraryDayModel itineraryDayModel = new ItineraryDayModel(itineraryModel.itineraryId, itineraryLocation, currentDate, itineraryFrom, itineraryTo, itineraryNotes, "", itineraryFromDate, itineraryToDate);

                    addItineraryDays(itineraryDayModel);
                    popupWindow.dismiss();

                    itineraryDayArrayList.clear();
                    customAdapter.notifyDataSetChanged();
                    getItineraryDay();

                }
            }
        });

        itineraryFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                                String hourStr = String.valueOf(hourOfDay);
                                String minuteStr = String.valueOf(minute);
                                if(hourStr.length() < 2){
                                    hourStr = "0" + hourStr;
                                }
                                if(minuteStr.length() < 2){
                                    minuteStr = "0" + minuteStr;
                                }
                                itineraryFromET.setText(hourStr + ":" + minuteStr);
                            }
                        }, hour, minute, false);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();

            }
        });

    }

    public void inflateDateCards(){

        View[] cardViews = new View[itineraryModel.itineraryDaysCount];
        TextView[] cardDayTexts = new TextView[itineraryModel.itineraryDaysCount];
        TextView[] cardMonthTexts = new TextView[itineraryModel.itineraryDaysCount];
        TextView[] cardFullDateTexts = new TextView[itineraryModel.itineraryDaysCount];


        // Initialize cardLayouts and cardTexts arrays with references to your views
        for (int i = 0; i < itineraryModel.itineraryDaysCount; i++) {
            cardViews[i] = getLayoutInflater().inflate(R.layout.itinerary_date_card, dateCardsLL, false);
            cardDayTexts[i] = cardViews[i].findViewById(R.id.dateTV);
            cardMonthTexts[i] = cardViews[i].findViewById(R.id.monthTV);
            cardFullDateTexts[i] = cardViews[i].findViewById(R.id.fullDateTV);
            //cardLayouts[i] = findViewById(getResources().getIdentifier("card" + (i + 1), "id", getPackageName()));
            //cardTexts[i] = findViewById(getResources().getIdentifier("card" + (i + 1) + "_text", "id", getPackageName()));

            final int cardIndex = i; // Make a final copy for use in the click listener

            try {

                Date itineraryFromDate = new SimpleDateFormat("dd/MM/yyyy").parse(itineraryModel.itineraryDateFrom);

                Calendar cal = Calendar.getInstance();

                cal.setTime(itineraryFromDate);

                cal.add(Calendar.DAY_OF_MONTH, i);

                SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                String month_name = month_date.format(cal.getTime());

                SimpleDateFormat full_date = new SimpleDateFormat("dd/MM/yyyy");
                String fullDateStr = full_date.format(cal.getTime());

                cardDayTexts[i].setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                cardMonthTexts[i].setText(month_name);
                cardFullDateTexts[i].setText(fullDateStr);

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
                        // Reset text color for all cards
                        for (int j = 0; j < itineraryModel.itineraryDaysCount; j++) {
                            cardDayTexts[j].setTextColor(getResources().getColor(R.color.white));
                            cardMonthTexts[j].setTextColor(getResources().getColor(R.color.white));
                        }

                        // Set text color for the selected card
                        cardDayTexts[cardIndex].setTextColor(getResources().getColor(R.color.black));
                        cardMonthTexts[cardIndex].setTextColor(getResources().getColor(R.color.black));

                        //currentDate = cardDayTexts[cardIndex].getText().toString() + "/" + cardMonthTexts[cardIndex].getText().toString();
                        currentDate = cardFullDateTexts[cardIndex].getText().toString();
                        Log.d(TAG, "inflateDateCards: currentDate: " + currentDate);

                        itineraryDayArrayList.clear();
                        customAdapter.notifyDataSetChanged();
                        getItineraryDay();
                    }
                });

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