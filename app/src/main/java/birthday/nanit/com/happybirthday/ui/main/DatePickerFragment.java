package birthday.nanit.com.happybirthday.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import birthday.nanit.com.happybirthday.R;
import birthday.nanit.com.happybirthday.utils.Constants;

/**
 * Project: Nanit
 * Created by Gil on 5/15/2018.
 */
public class DatePickerFragment extends DialogFragment implements DatePicker.OnDateChangedListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatePicker datePicker = view.findViewById(R.id.date_picker);
        Calendar calendar = GregorianCalendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(year, month, day);
        SharedPreferences sharedPreferences = datePicker.getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(Constants.PREF_BIRTH_DATE, calendar.getTimeInMillis()).apply();
        dismiss();
    }
}
