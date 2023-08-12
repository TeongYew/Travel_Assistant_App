package com.example.travel_assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.PopupWindow;
import android.view.MotionEvent;

public class PreLoginRegPage extends AppCompatActivity {

    Button login, register;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login_reg_page);

        login = findViewById(R.id.signInBtn);
        register = findViewById(R.id.signUpBtn);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View popUpView = layoutInflater.inflate(R.layout.activity_login_page, null);

                //Specify the length and width through constants
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                //Make Inactive Items Outside Of PopupWindow
                boolean focusable = true;

                //Create a window with our parameters
                final PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);

                //Set the location of the window on the screen
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View popUpView = layoutInflater.inflate(R.layout.activity_register, null);

                //Specify the length and width through constants
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                //Make Inactive Items Outside Of PopupWindow
                boolean focusable = true;

                //Create a window with our parameters
                final PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);

                //Set the location of the window on the screen
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            }
        });

    }
}