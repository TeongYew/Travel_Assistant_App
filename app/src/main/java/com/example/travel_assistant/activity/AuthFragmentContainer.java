package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.travel_assistant.R;

public class AuthFragmentContainer extends AppCompatActivity {

    Button loginBtn, registerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_fragment_container);

    }
}