package ru.shunt.guapschedule.about;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.shunt.guapschedule.R;

public class About extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		return new AlertDialog.Builder(getActivity()).setTitle("О программе").setMessage("Расписание ГУАП\nv. 6.0")
				.setIcon(R.drawable.app_icon).setCancelable(false).setPositiveButton("Ок", null).create();

	}
}