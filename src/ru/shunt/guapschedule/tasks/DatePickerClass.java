package ru.shunt.guapschedule.tasks;

import org.joda.time.DateTime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class DatePickerClass extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private View text;

	public DatePickerClass(View text) {
		this.text = text;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		int year = new DateTime().getYear();
		int month = new DateTime().getMonthOfYear();
		int day = new DateTime().getDayOfMonth();

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, --month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		if (text instanceof TextView) {
			((TextView) text).setText(day + "." + ++month + "." + year);
		}
	}

}
