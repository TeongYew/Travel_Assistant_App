package com.example.travel_assistant.fragments;

import static org.apache.commons.lang3.ClassUtils.getPackageName;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.example.travel_assistant.activity.MainMenu;
import com.example.travel_assistant.activity.PaymentPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    View view;
    private EditText emailET, passwordET;
    private Button loginBtn;
    private FirebaseAuth auth;
    LinearLayout backFromSignInLL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        emailET = view.findViewById(R.id.emailLoginET);
        passwordET = view.findViewById(R.id.passwordLoginET);
        loginBtn = view.findViewById(R.id.loginBtn);
        backFromSignInLL = view.findViewById(R.id.backFromSignInLL);

        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startActivity(new Intent(getActivity(), PaymentPage.class));

                String email, password;

                email = String.valueOf(emailET.getText());
                password = String.valueOf(passwordET.getText());

                loginUser(email,password);

            }
        });

        backFromSignInLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void loginUser(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(getActivity(), "User successfully logged in!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainMenu.class));

                }
                else{
                    Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



//    public void readCSVFile(){
//
//        try {
//            Resources resources = getResources();
//            InputStream inputStream = resources.openRawResource(R.raw.airports_codes); // Replace with the resource name (without file extension)
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            StringBuilder data = new StringBuilder();
//
//            while ((line = reader.readLine()) != null) {
//                // Process the CSV data here (e.g., split by semicolons and format)
//                String[] parts = line.split(";");
//                if (parts.length >= 2) {
//                    String airportCode = parts[0];
//                    String airportName = parts[1];
//
//                    // Format the data as needed
//                    String formattedData = "<string name = \"" + airportCode + "\">" + airportName + "</string>\n";
//                    data.append(formattedData);
//                }
//            }
//
//            // Display or use the formatted data
//            Log.d("readCSVFile: ", "formattedDate: " + data);
//
//            reader.close();
//            inputStream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

}