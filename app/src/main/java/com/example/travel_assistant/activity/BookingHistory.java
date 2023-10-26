package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.HistoryViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class BookingHistory extends AppCompatActivity {

    //layouts
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    HistoryViewPagerAdapter historyViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        //intialise the layouts
        tabLayout = findViewById(R.id.historyTL);
        viewPager2 = findViewById(R.id.historyVP);
        historyViewPagerAdapter = new HistoryViewPagerAdapter(this);
        viewPager2.setAdapter(historyViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //set the current fragment accordingly with the selected tab
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //if user scrolls horizontally, make sure the tab is able to update accordingly
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }
}