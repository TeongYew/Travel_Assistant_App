package com.example.travel_assistant.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.travel_assistant.R;
import com.example.travel_assistant.activity.CommonPhrases;
import com.example.travel_assistant.activity.FlightList;
import com.example.travel_assistant.activity.HotelList;
import com.example.travel_assistant.activity.ItineraryList;
import com.example.travel_assistant.activity.MainMenu;
import com.example.travel_assistant.activity.PaymentPage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreLoginFragment extends Fragment {

    View view;
    Button loginBtn, registerBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pre_login, container, false);

        loginBtn = view.findViewById(R.id.signInBtn);
        registerBtn = view.findViewById(R.id.signUpBtn);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(getActivity(), LoginFragment.class));
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LoginFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("loginFragment") // Name can be null
                        .commit();

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, RegisterFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("registerFragment") // Name can be null
                        .commit();

            }
        });

        return view;
    }
}