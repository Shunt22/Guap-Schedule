package ru.shunt.guapschedule.tasks;

import org.joda.time.DateTime;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePickerClass extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	private View text;

	public TimePickerClass(View text) {
		this.text = text;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		DateTime dt = new DateTime();
		int hour = dt.getHourOfDay();
		int minute = dt.getMinuteOfHour();

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (text instanceof TextView) {
			((TextView) text).setText((hourOfDay < 10 ? "0" + hourOfDay
					: hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute));
		}

	}
}