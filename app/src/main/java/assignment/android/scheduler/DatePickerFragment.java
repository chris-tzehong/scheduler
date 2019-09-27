package assignment.android.scheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARGS_DATE = "date";

    private DatePicker mDatePicker;
    private OnDateSelectedListener mOnDateSelectedListener;

    // implement an interface to allow callback to the parent activity to pass the date
    public interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }

    // receive the date if a non-default date exists
    public static DatePickerFragment newInstance(Date date) {
        
        Bundle args = new Bundle();
        args.putSerializable(ARGS_DATE, date);
        
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // cast the parent activity into the interface to receive callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.mOnDateSelectedListener = (OnDateSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnDateSelectedListener!");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARGS_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        // initialise the date
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        // return the date to the parent activity when the user press ok
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        mOnDateSelectedListener.onDateSelected(date);
                    }
                })
                .create();
    }


}
