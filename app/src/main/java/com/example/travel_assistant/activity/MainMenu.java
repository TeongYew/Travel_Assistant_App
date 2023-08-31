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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.travel_assistant.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout dLayout;
    FirebaseAuth auth;

    EditText destSearchET, bedNumET, adultNumET, childNumET;
    ImageButton destSearchBtn, bedAddBtn, bedRemoveBtn, adultAddBtn, adultRemovebtn, childAddBtn, childRemoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        auth = FirebaseAuth.getInstance();

        destSearchET = findViewById(R.id.destSearchET);
        bedNumET = findViewById(R.id.bedNumET);
        adultNumET = findViewById(R.id.adultNumET);
        childNumET = findViewById(R.id.childNumET);
        destSearchBtn = findViewById(R.id.destSearchBtn);
        bedAddBtn = findViewById(R.id.bedAddBtn);
        bedRemoveBtn = findViewById(R.id.bedRemoveBtn);
        adultAddBtn = findViewById(R.id.adultAddBtn);
        adultRemovebtn = findViewById(R.id.adultRemoveBtn);
        childAddBtn = findViewById(R.id.childAddBtn);
        childRemoveBtn = findViewById(R.id.childRemoveBtn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dLayout = findViewById(R.id.drawerLayout);

        NavigationView navView = findViewById(R.id.navigationView);
        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.open, R.string.close);
        dLayout.addDrawerListener(toggle);

        toggle.syncState();


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