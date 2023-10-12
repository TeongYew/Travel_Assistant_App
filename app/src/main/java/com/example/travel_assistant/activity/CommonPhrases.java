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

public class CommonPhrases extends AppCompatActivity {

    final String TAG = CommonPhrases.this.toString();
    Spinner languageSpinner;
    String currentLanguage = "";
    Button greetingsBtn, questionsBtn, emergenciesBtn, accommodationsBtn;
    android.widget.ListView greetingsLV, questionsLV, emergenciesLV, accommodationsLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_phrases);

        languageSpinner = findViewById(R.id.languageSpinner);
        greetingsBtn = findViewById(R.id.greetingsBtn);
        questionsBtn = findViewById(R.id.questionsBtn);
        emergenciesBtn = findViewById(R.id.emergenciesBtn);
        accommodationsBtn = findViewById(R.id.accommodationsBtn);
        greetingsLV = findViewById(R.id.greetingsLV);
        questionsLV = findViewById(R.id.questionsLV);
        emergenciesLV = findViewById(R.id.emergenciesLV);
        accommodationsLV = findViewById(R.id.accommodationsLV);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        languageSpinner.setAdapter(adapter);

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

        greetingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        questionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        emergenciesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        accommodationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}