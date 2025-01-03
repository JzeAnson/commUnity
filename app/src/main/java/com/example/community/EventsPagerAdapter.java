package com.example.community;

import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventsPagerAdapter extends FragmentStateAdapter {

    public EventsPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String selectedDate = getSelectedDate(position);
        return EventsTabFragment.newInstance(selectedDate, 0);
    }

    @Override
    public int getItemCount() {
        return 3; // Three tabs for today, tomorrow, and the day after tomorrow
    }

    private String getSelectedDate(int position) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        switch (position) {
            case 0: // Today
                return dateFormat.format(new Date());
            case 1: // Tomorrow
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                return dateFormat.format(calendar.getTime());
            case 2: // Day After Tomorrow
                calendar.add(Calendar.DAY_OF_YEAR, 2);
                return dateFormat.format(calendar.getTime());
            default:
                return dateFormat.format(new Date());
        }
    }
}
