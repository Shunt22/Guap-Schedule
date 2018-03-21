package ru.shunt.guapschedule.dialogs;

import ru.shunt.guapschedule.other.AlertDialogActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class DeleteAlertDialog extends DialogFragment {

	public static DeleteAlertDialog newInstance(String title) {

		DeleteAlertDialog inst = new DeleteAlertDialog();

		Bundle args = new Bundle();
		args.putString("title", title);
		inst.setArguments(args);

		return inst;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String title = getArguments().getString("title");

		return new AlertDialog.Builder(getActivity()).setTitle(title).setCancelable(false)
				.setPositiveButton("Да", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						((AlertDialogActivity) getActivity()).doPositiveClick();
					}
				}).setNegativeButton("Нет", null).create();
	}


}
