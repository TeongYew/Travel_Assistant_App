package com.example.travel_assistant.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

}