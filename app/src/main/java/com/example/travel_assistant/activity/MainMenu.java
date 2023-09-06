package com.example.travel_assistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import com.amadeus.Amadeus;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout dLayout;
    TabLayout flightTL;
    FirebaseAuth auth;
    RelativeLayout fromRl, toRL, departureArrivalRL, passengersRL;

    Amadeus amadeus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        flightTL = findViewById(R.id.flightTL);
        fromRl = findViewById(R.id.fromRL);
        toRL = findViewById(R.id.toRL);
        departureArrivalRL = findViewById(R.id.departureArrivalRL);
        passengersRL = findViewById(R.id.passengersRL);

        auth = FirebaseAuth.getInstance();

        amadeus = Amadeus
                .builder("trhahLtZ7pxfkVK2EyFygDsDJDYfJBSC","kBCYVD40NAdKBef4")
                .build();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dLayout = findViewById(R.id.drawerLayout);

        NavigationView navView = findViewById(R.id.navigationView);
        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.open, R.string.close);
        dLayout.addDrawerListener(toggle);

        toggle.syncState();

        flightTL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        //round trip

                        break;
                    case 1:
                        //one way

                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fromRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        toRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        departureArrivalRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        passengersRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.navItinerary){
            startActivity(new Intent(MainMenu.this, ItineraryList.class));
        }
        else if (item.getItemId() == R.id.navHistory) {
            //startActivity(new Intent(MainMenu.this, ));
        }
        else if (item.getItemId() == R.id.navAnalytics) {
            startActivity(new Intent());
        }
        else if (item.getItemId() == R.id.signOut) {
            signOut();
        }

        dLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(dLayout.isDrawerOpen(GravityCompat.START)){
            dLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void signOut(){
        auth.signOut();
        Toast.makeText(this, "Successfully signed out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainMenu.this, PreLoginRegPage.class));
    }

}