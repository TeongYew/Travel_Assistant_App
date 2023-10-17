package com.example.travel_assistant.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.travel_assistant.fragments.FlightHistoryFragment;
import com.example.travel_assistant.fragments.HotelHistoryFragment;

public class HistoryViewPagerAdapter extends FragmentStateAdapter {


    public HistoryViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new FlightHistoryFragment();
            case 1:
                return new HotelHistoryFragment();
            default:
                return new FlightHistoryFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
