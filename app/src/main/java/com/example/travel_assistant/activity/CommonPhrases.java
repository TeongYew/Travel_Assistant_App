package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.travel_assistant.R;

import java.util.ArrayList;

public class CommonPhrases extends AppCompatActivity {

    final String TAG = CommonPhrases.this.toString();
    Spinner languageSpinner;
    String currentLanguage = "";
    Button generalBtn, greetingsBtn, navigationsBtn, emergenciesBtn, accommodationsBtn;
    android.widget.ListView generalLV, greetingsLV, navigationsLV, emergenciesLV, accommodationsLV;
    boolean generalSelected = false;
    boolean greetingsSelected = false;
    boolean navigationsSelected = false;
    boolean emergenciesSelected = false;
    boolean accommodationsSelected = false;

    ArrayList<String> generalList = new ArrayList<>();
    ArrayList<String> greetingsList = new ArrayList<>();
    ArrayList<String> navigationsList = new ArrayList<>();
    ArrayList<String> emergenciesList = new ArrayList<>();
    ArrayList<String> accommodationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_phrases);

        languageSpinner = findViewById(R.id.languageSpinner);
        generalBtn = findViewById(R.id.generalBtn);
        greetingsBtn = findViewById(R.id.greetingsBtn);
        navigationsBtn = findViewById(R.id.navigationsBtn);
        emergenciesBtn = findViewById(R.id.emergenciesBtn);
        accommodationsBtn = findViewById(R.id.accommodationsBtn);
        generalLV = findViewById(R.id.generalLV);
        greetingsLV = findViewById(R.id.greetingsLV);
        navigationsLV = findViewById(R.id.navigationsLV);
        emergenciesLV = findViewById(R.id.emergenciesLV);
        accommodationsLV = findViewById(R.id.accommodationsLV);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        languageSpinner.setAdapter(adapter);

        initialiseArrayLists();

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentLanguage = languageSpinner.getSelectedItem().toString();
                Log.d(TAG, "onItemClick: current language: " + currentLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        generalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(generalSelected == false){
                    generalSelected = true;

                    //set the rest of the buttons to gone
                    //generalBtn.setVisibility(View.GONE);
                    greetingsBtn.setVisibility(View.GONE);
                    navigationsBtn.setVisibility(View.GONE);
                    emergenciesBtn.setVisibility(View.GONE);
                    accommodationsBtn.setVisibility(View.GONE);


                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.general_phrases_listview, R.id.generalTV, generalList);
                    generalLV.setAdapter(arrayAdapter);
                    generalLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    generalLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = generalList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);
                        }
                    });

                }
                else{
                    generalSelected = false;
                    generalLV.setVisibility(View.GONE);

                    //set the rest of the buttons to visible
                    //generalBtn.setVisibility(View.VISIBLE);
                    greetingsBtn.setVisibility(View.VISIBLE);
                    navigationsBtn.setVisibility(View.VISIBLE);
                    emergenciesBtn.setVisibility(View.VISIBLE);
                    accommodationsBtn.setVisibility(View.VISIBLE);

                }
            }
        });

        greetingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(greetingsSelected == false){
                    greetingsSelected = true;

                    //set the rest of the buttons to gone
                    generalBtn.setVisibility(View.GONE);
                    //greetingsBtn.setVisibility(View.GONE);
                    navigationsBtn.setVisibility(View.GONE);
                    emergenciesBtn.setVisibility(View.GONE);
                    accommodationsBtn.setVisibility(View.GONE);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.greeting_phrases_listview, R.id.greetingsTV, greetingsList);
                    greetingsLV.setAdapter(arrayAdapter);
                    greetingsLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    greetingsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = greetingsList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);
                        }
                    });
                }
                else{
                    greetingsSelected = false;
                    greetingsLV.setVisibility(View.GONE);

                    //set the rest of the buttons to visible
                    generalBtn.setVisibility(View.VISIBLE);
                    //greetingsBtn.setVisibility(View.VISIBLE);
                    navigationsBtn.setVisibility(View.VISIBLE);
                    emergenciesBtn.setVisibility(View.VISIBLE);
                    accommodationsBtn.setVisibility(View.VISIBLE);

                }
            }
        });

        navigationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(navigationsSelected == false){
                    navigationsSelected = true;

                    //set the rest of the buttons to gone
                    generalBtn.setVisibility(View.GONE);
                    greetingsBtn.setVisibility(View.GONE);
                    //navigationsBtn.setVisibility(View.GONE);
                    emergenciesBtn.setVisibility(View.GONE);
                    accommodationsBtn.setVisibility(View.GONE);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.navigation_phrases_listview, R.id.navigationsTV, navigationsList);
                    navigationsLV.setAdapter(arrayAdapter);
                    navigationsLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    navigationsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = navigationsList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);
                        }
                    });
                }
                else{
                    navigationsSelected = false;
                    navigationsLV.setVisibility(View.GONE);

                    //set the rest of the buttons to visible
                    generalBtn.setVisibility(View.VISIBLE);
                    greetingsBtn.setVisibility(View.VISIBLE);
                    //navigationsBtn.setVisibility(View.VISIBLE);
                    emergenciesBtn.setVisibility(View.VISIBLE);
                    accommodationsBtn.setVisibility(View.VISIBLE);

                }
            }
        });

        emergenciesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emergenciesSelected == false){
                    emergenciesSelected = true;

                    //set the rest of the buttons to gone
                    generalBtn.setVisibility(View.GONE);
                    greetingsBtn.setVisibility(View.GONE);
                    navigationsBtn.setVisibility(View.GONE);
                    //emergenciesBtn.setVisibility(View.GONE);
                    accommodationsBtn.setVisibility(View.GONE);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.emergency_phrases_listview, R.id.emergenciesTV, emergenciesList);
                    emergenciesLV.setAdapter(arrayAdapter);
                    emergenciesLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    emergenciesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = emergenciesList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);
                        }
                    });
                }
                else{
                    emergenciesSelected = false;
                    emergenciesLV.setVisibility(View.GONE);

                    //set the rest of the buttons to visible
                    generalBtn.setVisibility(View.VISIBLE);
                    greetingsBtn.setVisibility(View.VISIBLE);
                    navigationsBtn.setVisibility(View.VISIBLE);
                    //emergenciesBtn.setVisibility(View.VISIBLE);
                    accommodationsBtn.setVisibility(View.VISIBLE);

                }
            }
        });

        accommodationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accommodationsSelected == false){
                    accommodationsSelected = true;

                    //set the rest of the buttons to gone
                    generalBtn.setVisibility(View.GONE);
                    greetingsBtn.setVisibility(View.GONE);
                    navigationsBtn.setVisibility(View.GONE);
                    emergenciesBtn.setVisibility(View.GONE);
                    //accommodationsBtn.setVisibility(View.GONE);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.accommodation_phrases_listview, R.id.accommodationsTV, accommodationsList);
                    accommodationsLV.setAdapter(arrayAdapter);
                    accommodationsLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    accommodationsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = accommodationsList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);
                        }
                    });
                }
                else{
                    accommodationsSelected = false;
                    accommodationsLV.setVisibility(View.GONE);

                    //set the rest of the buttons to visible
                    generalBtn.setVisibility(View.VISIBLE);
                    greetingsBtn.setVisibility(View.VISIBLE);
                    navigationsBtn.setVisibility(View.VISIBLE);
                    emergenciesBtn.setVisibility(View.VISIBLE);
                    //accommodationsBtn.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    public void initialiseArrayLists(){

        //general
        generalList.add("Do you speak English?");
        generalList.add("Where is the bathroom?");
        generalList.add("Where is…?");
        generalList.add("How much does this cost?");
        generalList.add("I would like to have…");
        generalList.add("Please write that down for me.");
        generalList.add("Speak slowly, please.");
        generalList.add("I do not understand.");
        generalList.add("Do you take credit cards?");
        generalList.add("Is there a bank/ATM nearby?");
        generalList.add("What do you recommend?");

        //greetings
        greetingsList.add("Hello");
        greetingsList.add("Goodbye");
        greetingsList.add("Yes");
        greetingsList.add("No");
        greetingsList.add("Thank you");
        greetingsList.add("Please");
        greetingsList.add("I'm sorry");
        greetingsList.add("Excuse me");
        greetingsList.add("Nice to meet you");
        greetingsList.add("My name is ...");
        greetingsList.add("How are you?");

        //navigation
        navigationsList.add("Left");
        navigationsList.add("Right");
        navigationsList.add("Up");
        navigationsList.add("Down");
        navigationsList.add("Straight");
        navigationsList.add("Back");
        navigationsList.add("Corner");
        navigationsList.add("Here");
        navigationsList.add("There");
        navigationsList.add("North");
        navigationsList.add("South");
        navigationsList.add("East");
        navigationsList.add("West");
        navigationsList.add("Turn around");
        navigationsList.add("Blocks");
        navigationsList.add("How far is ... ?");
        navigationsList.add("Airport");
        navigationsList.add("Bus");
        navigationsList.add("Train");
        navigationsList.add("Airport");
        navigationsList.add("Station");
        navigationsList.add("How long does it take to get to ... ?");
        navigationsList.add("How far away is ... ?");
        navigationsList.add("What time is it?");
        navigationsList.add("Do you have a map?");
        navigationsList.add("Where can i find a taxi?");

        //emergencies
        emergenciesList.add("I need help");
        emergenciesList.add("I need a doctor");
        emergenciesList.add("Where is the hospital?");
        emergenciesList.add("I'm having an emergency");
        emergenciesList.add("I'm allergic to ...");
        emergenciesList.add("Please call the police");
        emergenciesList.add("Where is the ... embassy?");
        emergenciesList.add("I am lost");
        emergenciesList.add("This hurts");
        emergenciesList.add("I need medicine");
        emergenciesList.add("Where is the pharmacy?");
        emergenciesList.add("I lost my passport");
        emergenciesList.add("I'm at ...");
        emergenciesList.add("Fire");

        //accommodations
        accommodationsList.add("Hotel");
        accommodationsList.add("Hostel");
        accommodationsList.add("Resort");
        accommodationsList.add("House");
        accommodationsList.add("Room");
        accommodationsList.add("Toilet paper");
        accommodationsList.add("Can i drink the tap water?");
        accommodationsList.add("I lost my key");
        accommodationsList.add("... is broken");
        accommodationsList.add("Laundry");
        accommodationsList.add("Air conditioner");
        accommodationsList.add("Heater");
        accommodationsList.add("Fan");
        accommodationsList.add("There are bugs in my room");
        accommodationsList.add("How do i access the internet?");
        accommodationsList.add("Towel");
        accommodationsList.add("I need my room cleaned");

    }

}