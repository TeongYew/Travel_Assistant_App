package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.travel_assistant.R;

public class PreLoginRegPage extends AppCompatActivity {

    Button loginBtn, registerBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login_reg_page);

        loginBtn = findViewById(R.id.signInBtn);
        registerBtn = findViewById(R.id.signUpBtn);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(PreLoginRegPage.this, LoginPage.class));

//                View popUpView = layoutInflater.inflate(R.layout.activity_login_page, null);
//
//                //Specify the length and width through constants
//                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//                //Make Inactive Items Outside Of PopupWindow
//                boolean focusable = true;
//
//                //Create a window with our parameters
//                final PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);
//                popupWindow.setTouchable(true);
//
//                //Set the location of the window on the screen
//                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(PreLoginRegPage.this, Register.class));

//                View popUpView = layoutInflater.inflate(R.layout.activity_register, null);
//
//                //Specify the length and width through constants
//                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//                //Make Inactive Items Outside Of PopupWindow
//                boolean focusable = true;
//
//                //Create a window with our parameters
//                final PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);
//                popupWindow.setTouchable(true);
//
//                //Set the location of the window on the screen
//                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            }
        });

    }
}