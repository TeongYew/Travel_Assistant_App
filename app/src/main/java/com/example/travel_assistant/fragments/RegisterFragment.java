package com.example.travel_assistant.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    View view;
    private static final String TAG = "register activity";
    private EditText emailET, usernameET, passwordET;
    private Button regBtn;
    private FirebaseAuth auth;
    LinearLayout backFromSignUpLL;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);

        usernameET = view.findViewById(R.id.usernameRegET);
        emailET = view.findViewById(R.id.emailRegET);
        passwordET = view.findViewById(R.id.passwordRegET);
        regBtn = view.findViewById(R.id.regBtn);

        auth = FirebaseAuth.getInstance();

        backFromSignUpLL = view.findViewById(R.id.backFromSignUpLL);

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
                    Toast.makeText(getActivity(), "Please fill in the necessary fields", Toast.LENGTH_SHORT).show();
                }
                else if (passwordStr.length() < 6) {
                    Toast.makeText(getActivity(), "Password length needs to be more than 6", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(usernameStr, emailStr, passwordStr);
                }

            }
        });

        backFromSignUpLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void registerUser(String username, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(), "User successfully registered!", Toast.LENGTH_SHORT).show();

                    addNewUser(username, email);

                    startActivity(new Intent(getActivity(), MainMenu.class));
                }
                else{
                    Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void addNewUser(String username, String email){

        String uid = "";

        try {
            uid = auth.getCurrentUser().getUid();
            Log.d(TAG, "addNewUser: uid:" + uid);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        // Create a new itinerary
        Map<String, Object> user = new HashMap<>();
        user.put("user_id", "000002");
        user.put("user_name", username);
        user.put("user_email", email);
        user.put("user_auth_id", uid);


        // Add a new document with a generated ID
        db.collection("user")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot (user) added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document (user)", e);
                    }
                });

    }

}