package com.example.travel_assistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private static final String TAG = "register activity";
    private EditText emailET, usernameET, passwordET;
    private Button regBtn;
    private FirebaseAuth auth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameET = findViewById(R.id.usernameRegET);
        emailET = findViewById(R.id.emailRegET);
        passwordET = findViewById(R.id.passwordRegET);
        regBtn = findViewById(R.id.regBtn);

        auth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: ");


                String emailStr, usernameStr, passwordStr;

                emailStr = String.valueOf(emailET.getText());
                usernameStr = String.valueOf(usernameET.getText());
                passwordStr = String.valueOf(passwordET.getText());

                //check for empty edittext
                if(TextUtils.isEmpty(emailStr) || TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(passwordStr)){
                    Toast.makeText(Register.this, "Please fill in the necessary fields", Toast.LENGTH_SHORT).show();
                }
                else if (passwordStr.length() < 6) {
                    Toast.makeText(Register.this, "Password length needs to be more than 6", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(emailStr, passwordStr);
                }

            }
        });

    }

    private void registerUser(String email, String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Register.this, "User successfully registered!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainMenu.class));
                    finish();
                }
                else{
                    Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
                
            }
        });

    }

    private void signOut(){
        auth.signOut();
        Toast.makeText(this, "Successfully signed out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Register.this, PreLoginRegPage.class));
    }

}