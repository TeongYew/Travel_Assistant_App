package com.example.travel_assistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.example.travel_assistant.others.LoadingDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.Charsets;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommonPhrases extends AppCompatActivity {

    final String TAG = CommonPhrases.this.toString();

    //layouts
    Spinner languageSpinner;
    String currentLanguage = "";
    Button generalBtn, greetingsBtn, navigationsBtn, emergenciesBtn, accommodationsBtn;
    android.widget.ListView generalLV, greetingsLV, navigationsLV, emergenciesLV, accommodationsLV;
    FloatingActionButton translateFAB;
    boolean generalSelected = false;
    boolean greetingsSelected = false;
    boolean navigationsSelected = false;
    boolean emergenciesSelected = false;
    boolean accommodationsSelected = false;
    LoadingDialog loadingDialog;

    //array list of common phrases and questions
    ArrayList<String> generalList = new ArrayList<>();
    ArrayList<String> greetingsList = new ArrayList<>();
    ArrayList<String> navigationsList = new ArrayList<>();
    ArrayList<String> emergenciesList = new ArrayList<>();
    ArrayList<String> accommodationsList = new ArrayList<>();
    String phrasesURLEncoded = "";

    //popup variables
    RelativeLayout phrasesRL;
    LayoutInflater layoutInflater;
    int width = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean focusable = true;
    PopupWindow popupWindow;
    View translatorPopupView;
    View translationPopupView;

    //for async methods
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    //initialise okhttpclient
    OkHttpClient client = new OkHttpClient();

    //initialise tts
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_phrases);

        //initialise the layouts
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
        translateFAB = findViewById(R.id.translateFAB);
        phrasesRL = findViewById(R.id.phrasesRL);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        loadingDialog = new LoadingDialog(this);

        //set the language spinner menu items
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        languageSpinner.setAdapter(adapter);

        //intialise the common phrases and questions array list
        initialiseArrayLists();


        // create the TTS object
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if (i != TextToSpeech.ERROR){
                    // To Choose language of speech
                    //textToSpeech.setLanguage(Locale.ENGLISH);
                    //textToSpeech.speak("Hello World", TextToSpeech.QUEUE_FLUSH, null, null);

                    //try and set the initial language (malay in this case) of the tts
                    int result = textToSpeech.setLanguage(new Locale("ms", "MY"));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.setLanguage(new Locale("id", "ID"));
                    } else {
                        textToSpeech.setLanguage(new Locale("ms", "MY"));
                    }
                }
                else {
                    Log.d(TAG, "onInit: Something went wrong when initialising TTS");
                }

            }
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //get the current selected language
                currentLanguage = languageSpinner.getSelectedItem().toString();
                Log.d(TAG, "onItemClick: current language: " + currentLanguage);

                //change the tts language accordingly
                switch (currentLanguage){
                    case "Malay":
                        //textToSpeech.setLanguage(new Locale("ms", "MY"));

                        //set the tts language to the currently selected language
                        int malayResult = textToSpeech.setLanguage(new Locale("ms", "MY"));

                        if (malayResult == TextToSpeech.LANG_MISSING_DATA || malayResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                            textToSpeech.setLanguage(new Locale("id", "ID"));
                        } else {
                            textToSpeech.setLanguage(new Locale("ms", "MY"));
                        }
                        break;
                    case "Mandarin":
                        //textToSpeech.setLanguage(new Locale("zh", "CN"));

                        //set the tts language to the currently selected language
                        int mandarinResult = textToSpeech.setLanguage(new Locale("zh", "CN"));

                        if (mandarinResult == TextToSpeech.LANG_MISSING_DATA || mandarinResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                            textToSpeech.setLanguage(Locale.ENGLISH);
                        } else {
                            textToSpeech.setLanguage(new Locale("zh", "CN"));
                        }
                        break;
                    case "Tamil":
                        //textToSpeech.setLanguage(new Locale("ta", "IN"));

                        //set the tts language to the currently selected language
                        int tamilResult = textToSpeech.setLanguage(new Locale("ta", "IN"));

                        if (tamilResult == TextToSpeech.LANG_MISSING_DATA || tamilResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                            textToSpeech.setLanguage(Locale.ENGLISH);
                        } else {
                            textToSpeech.setLanguage(new Locale("ta", "IN"));
                        }
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        generalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //when the button is clicked, change the selected boolean of the button to true or false accordingly
                //if currently true, then set to false and vice versa
                //if button is currently selected, set the other buttons to gone
                //if button is not currently selected, set all the buttons to visible

                if(generalSelected == false){
                    generalSelected = true;

                    //set the rest of the buttons to gone
                    //generalBtn.setVisibility(View.GONE);
                    greetingsBtn.setVisibility(View.GONE);
                    navigationsBtn.setVisibility(View.GONE);
                    emergenciesBtn.setVisibility(View.GONE);
                    accommodationsBtn.setVisibility(View.GONE);

                    //populate the listview with the general phrases array list
                    //set the listview to visible
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.general_phrases_listview, R.id.generalTV, generalList);
                    generalLV.setAdapter(arrayAdapter);
                    generalLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    generalLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = generalList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);

                            //start the loading animation
                            loadingDialog.show();

                            //url encode the string and call the translate method using it
                            String encodedString = urlEncodeString(selectedPhrase);
                            translate(encodedString);

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

                //when the button is clicked, change the selected boolean of the button to true or false accordingly
                //if currently true, then set to false and vice versa
                //if button is currently selected, set the other buttons to gone
                //if button is not currently selected, set all the buttons to visible

                if(greetingsSelected == false){
                    greetingsSelected = true;

                    //set the rest of the buttons to gone
                    generalBtn.setVisibility(View.GONE);
                    //greetingsBtn.setVisibility(View.GONE);
                    navigationsBtn.setVisibility(View.GONE);
                    emergenciesBtn.setVisibility(View.GONE);
                    accommodationsBtn.setVisibility(View.GONE);

                    //populate the listview with the greetings phrases array list
                    //set the listview to visible
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.greeting_phrases_listview, R.id.greetingsTV, greetingsList);
                    greetingsLV.setAdapter(arrayAdapter);
                    greetingsLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    greetingsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = greetingsList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);

                            //start the loading animation
                            loadingDialog.show();

                            //url encode the string and call the translate method using it
                            String encodedString = urlEncodeString(selectedPhrase);
                            translate(encodedString);

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

                //when the button is clicked, change the selected boolean of the button to true or false accordingly
                //if currently true, then set to false and vice versa
                //if button is currently selected, set the other buttons to gone
                //if button is not currently selected, set all the buttons to visible

                if(navigationsSelected == false){
                    navigationsSelected = true;

                    //set the rest of the buttons to gone
                    generalBtn.setVisibility(View.GONE);
                    greetingsBtn.setVisibility(View.GONE);
                    //navigationsBtn.setVisibility(View.GONE);
                    emergenciesBtn.setVisibility(View.GONE);
                    accommodationsBtn.setVisibility(View.GONE);

                    //populate the listview with the navigation phrases array list
                    //set the listview to visible
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.navigation_phrases_listview, R.id.navigationsTV, navigationsList);
                    navigationsLV.setAdapter(arrayAdapter);
                    navigationsLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    navigationsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = navigationsList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);

                            //start the loading animation
                            loadingDialog.show();

                            //url encode the string and call the translate method using it
                            String encodedString = urlEncodeString(selectedPhrase);
                            translate(encodedString);

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

                //when the button is clicked, change the selected boolean of the button to true or false accordingly
                //if currently true, then set to false and vice versa
                //if button is currently selected, set the other buttons to gone
                //if button is not currently selected, set all the buttons to visible

                if(emergenciesSelected == false){
                    emergenciesSelected = true;

                    //set the rest of the buttons to gone
                    generalBtn.setVisibility(View.GONE);
                    greetingsBtn.setVisibility(View.GONE);
                    navigationsBtn.setVisibility(View.GONE);
                    //emergenciesBtn.setVisibility(View.GONE);
                    accommodationsBtn.setVisibility(View.GONE);

                    //populate the listview with the emergency phrases array list
                    //set the listview to visible
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.emergency_phrases_listview, R.id.emergenciesTV, emergenciesList);
                    emergenciesLV.setAdapter(arrayAdapter);
                    emergenciesLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    emergenciesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = emergenciesList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);

                            //start the loading animation
                            loadingDialog.show();

                            //url encode the string and call the translate method using it
                            String encodedString = urlEncodeString(selectedPhrase);
                            translate(encodedString);

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

                //when the button is clicked, change the selected boolean of the button to true or false accordingly
                //if currently true, then set to false and vice versa
                //if button is currently selected, set the other buttons to gone
                //if button is not currently selected, set all the buttons to visible

                if(accommodationsSelected == false){
                    accommodationsSelected = true;

                    //set the rest of the buttons to gone
                    generalBtn.setVisibility(View.GONE);
                    greetingsBtn.setVisibility(View.GONE);
                    navigationsBtn.setVisibility(View.GONE);
                    emergenciesBtn.setVisibility(View.GONE);
                    //accommodationsBtn.setVisibility(View.GONE);

                    //populate the listview with the accommodation phrases array list
                    //set the listview to visible
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CommonPhrases.this, R.layout.accommodation_phrases_listview, R.id.accommodationsTV, accommodationsList);
                    accommodationsLV.setAdapter(arrayAdapter);
                    accommodationsLV.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: clicked");
                    accommodationsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedPhrase = accommodationsList.get(i).toString();
                            Log.d(TAG, "onItemClick: selected phrase: " + selectedPhrase);

                            //start the loading animation
                            loadingDialog.show();

                            //url encode the string and call the translate method using it
                            String encodedString = urlEncodeString(selectedPhrase);
                            translate(encodedString);

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

        translateFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupTranslatorPopup();

            }
        });


    }

    @Override
    protected void onDestroy() {
        //make sure tts is shut down
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void translate(String urlEncodedPhrase){

        String currentLanguageCode = getString(getResources().getIdentifier(currentLanguage, "string", getPackageName()));

        executor.execute(new Runnable() {
            @Override
            public void run() {

                try{

//                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//                    //RequestBody body = RequestBody.create(mediaType, "q=" + phrasesURLEncoded + "&target=" + "ms" + "&source=en");
//                    RequestBody body = RequestBody.create(mediaType, "q=Hello%2C%20world!&target=es&source=en");
//                    Request request = new Request.Builder()
//                            .url("https://google-translate1.p.rapidapi.com/language/translate/v2")
//                            .post(body)
//                            .addHeader("content-type", "application/x-www-form-urlencoded")
//                            .addHeader("Accept-Encoding", "application/gzip")
//                            .addHeader("X-RapidAPI-Key", "b34ecf7a0fmsh5cbb7c353f899abp1c8c15jsn43d3583a9734")
//                            .addHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
//                            .build();
//
//                    //Response response = client.newCall(request).execute();

                    Request request = new Request.Builder()
                            .url("https://nlp-translation.p.rapidapi.com/v1/translate?text=" + urlEncodedPhrase + "&to=" + currentLanguageCode + "&from=en")
                            .get()
                            .addHeader("X-RapidAPI-Key", "b34ecf7a0fmsh5cbb7c353f899abp1c8c15jsn43d3583a9734")
                            .addHeader("X-RapidAPI-Host", "nlp-translation.p.rapidapi.com")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.d(TAG, "onFailure: error getting google translate api: " + e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            String responseStr = response.body().string();

                            Log.d(TAG, "onResponse: response str: " + responseStr);

                            try{

                                JSONObject jsonObject = new JSONObject(responseStr);

                                JSONObject translatedTextJson = jsonObject.getJSONObject("translated_text");

                                String translatedText = translatedTextJson.getString(currentLanguageCode);

                                setupTranslationPopup(translatedText);

                            }
                            catch (Exception e){
                                Log.d(TAG, "onResponse: error parsing translation json: " + e);
                            }

                        }
                    });

                }
                catch (Exception e){
                    Log.d(TAG, "run: error calling google translate api: " + e);
                }



            }
        });

    }

    private void textToSpeech(String text){

        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }

    }

    private String urlEncodeString(String phrase){

        String encodedString = "";

        try {
            encodedString = URLEncoder.encode(phrase, "UTF-8").replaceAll("\\+", "%20");
        }
        catch (Exception e){
            Log.d(TAG, "urlEncodeString: error encoding string: " + e);
        }


        return encodedString;
    }

    private void setupTranslationPopup(String translation){

        //stop the loading animation
        loadingDialog.cancel();

        //initialise the translatorPopupView
        translationPopupView = layoutInflater.inflate(R.layout.translation_popup, null);

        popupWindow = new PopupWindow(translationPopupView,width,height,focusable);

        //display the translator popup window
        phrasesRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(phrasesRL, Gravity.CENTER,0,0);

            }
        });

        //initialise the layout variables of translatorPopupView
        RelativeLayout translationPopupRL = translationPopupView.findViewById(R.id.translationPopupRL);
        TextView translatedTextTV = translationPopupView.findViewById(R.id.translatedTextTV);
        ImageButton speechIB = translationPopupView.findViewById(R.id.speechIB);

        //set the translation to the textview
        translatedTextTV.setText(translation);

        Log.d(TAG, "setupTranslationPopup: translation: " + translation);

        speechIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech(translation);
            }
        });

        //if user clicks on the outside of the popup window, close the popup`
//        translationPopupRL.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                popupWindow.dismiss();
//                return true;
//            }
//        });

    }

    private void setupTranslatorPopup(){

        //initialise the translatorPopupView
        translatorPopupView = layoutInflater.inflate(R.layout.translator_popup, null);

        popupWindow = new PopupWindow(translatorPopupView,width,height,focusable);

        //display the translator popup window
        phrasesRL.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(phrasesRL, Gravity.CENTER,0,0);

            }
        });

        //initialise the layout variables of translatorPopupView
        RelativeLayout translatePopupRL = translatorPopupView.findViewById(R.id.translatorPopupRL);
        EditText translateInputET = translatorPopupView.findViewById(R.id.translateInputET);
        Button translateBtn = translatorPopupView.findViewById(R.id.translateBtn);

        //if user clicks on the outside of the popup window, close the popup
        translatePopupRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //close the popup window
                popupWindow.dismiss();

                //start the loading animation
                loadingDialog.show();

                String inputText = translateInputET.getText().toString();
                String encodedText = urlEncodeString(inputText);

                translate(encodedText);

            }
        });

    }

    private void initialiseArrayLists(){

        //populate the array lists with phrases and questions accordingly

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

//        try{
//
//            phrasesURLEncoded += URLEncoder.encode(generalList.get(0).toString(), "UTF-8").replaceAll("\\+", "%20");
//
//            for (int i = 0; i < generalList.size(); i++){
//
//            }
//
//            for (int i = 0; i < greetingsList.size(); i++){
//
//            }
//
//            for (int i = 0; i < navigationsList.size(); i++){
//
//            }
//
//            for (int i = 0; i < emergenciesList.size(); i++){
//
//            }
//
//            for (int i = 0; i < emergenciesList.size(); i++){
//
//            }
//
//            Log.d(TAG, "initialiseArrayLists: phrases url encoded: " + phrasesURLEncoded);
//
//            //translate();
//
//        }
//        catch (Exception e){
//            Log.d(TAG, "initialiseArrayLists: error encoding all the phrases: " + e);
//        }



    }

}