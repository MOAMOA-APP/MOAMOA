package com.example.moamoa.ui.dashboard;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.widget.DatePicker;

import com.example.moamoa.MainActivity;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * 캘린더 대쉬보드에서 날짜정할때
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        MainActivity activity = (MainActivity)getActivity();
        activity.processDatePickerResult(year,month,day);
    }
}
