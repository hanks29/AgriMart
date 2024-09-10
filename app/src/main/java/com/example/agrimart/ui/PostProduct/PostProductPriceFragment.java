package com.example.agrimart.ui.PostProduct;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agrimart.R;

import java.util.Calendar;

public class PostProductPriceFragment extends Fragment {

    private TextView textViewDate;

    public PostProductPriceFragment() {
        // Required empty public constructor
    }

    public static PostProductPriceFragment newInstance(String param1, String param2) {
        PostProductPriceFragment fragment = new PostProductPriceFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get arguments if any
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_product_price, container, false);

        // Initialize views
        textViewDate = view.findViewById(R.id.textViewDate);
        LinearLayout calendarContainer = view.findViewById(R.id.linearLayoutCalendarContainer);

        // Set the date 5 days from now to TextView
        setDateFiveDaysLater();

        // Set click listener for calendar container
        calendarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
            }
        });

        return view;
    }

    private void setDateFiveDaysLater() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 5); // Add 5 days to current date

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Format the date as "Ngày tháng năm"
        String dateInFiveDays = String.format("%d tháng %02d năm %d", day, month + 1, year);
        // Update the TextView with the date 5 days later
        textViewDate.setText(dateInFiveDays);
    }

    private void showCalendarDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Format the selected date as "Ngày tháng năm"
                String selectedDate = String.format("%d tháng %02d năm %d", dayOfMonth, month + 1, year);
                // Update the TextView with the selected date
                textViewDate.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }
}
