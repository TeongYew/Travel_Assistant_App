package com.example.travel_assistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.travel_assistant.R;
import com.example.travel_assistant.adapter.HistoryViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class BookingHistory extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    HistoryViewPagerAdapter historyViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        tabLayout = findViewById(R.id.historyTL);
        viewPager2 = findViewById(R.id.historyVP);
        historyViewPagerAdapter = new HistoryViewPagerAdapter(this);
        viewPager2.setAdapter(historyViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }
}