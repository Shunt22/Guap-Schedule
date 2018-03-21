package ru.shunt.guapschedule.dialogs;

import ru.shunt.guapschedule.other.OkDialogActivity;

import com.shunt.guapschedule.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class OkDialog extends DialogFragment {

	public static OkDialog newInstance(String text) {

		OkDialog inst = new OkDialog();

		Bundle args = new Bundle();
		args.putString("text", text);
		inst.setArguments(args);

		return inst;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String text = getArguments().getString("text");

		return new AlertDialog.Builder(getActivity()).setTitle("Ошибка!").setMessage(text).setIcon(R.drawable.app_icon)
				.setCancelable(false).setPositiveButton("Ок", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						((OkDialogActivity) getActivity()).doOkClick();
					}
				}).create();
	}
}
