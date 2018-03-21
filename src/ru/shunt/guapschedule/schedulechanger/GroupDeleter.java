package ru.shunt.guapschedule.schedulechanger;

import java.util.List;

import ru.shunt.guapschedule.main.Preference;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class GroupDeleter extends Fragment {

	private Spinner mGroupSpinner;
	private List<String> mGroupList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View viewHierarchy = inflater.inflate(R.layout.act_group_deleter, container, false);

		mGroupSpinner = (Spinner) viewHierarchy.findViewById(R.id.deleterGroupSpinner);

		setAdapter();

		((Button) viewHierarchy.findViewById(R.id.deleterButtonDelete)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SdWorker().deleteGroupFile((String) mGroupSpinner.getSelectedItem());

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
				prefs.edit().clear().commit();

				setAdapter();
			}
		});

		return viewHierarchy;
	}

	private void setAdapter() {

		mGroupList = new SdWorker().readGroups();

		if (mGroupList.isEmpty()) {
			Toast.makeText(getActivity().getApplicationContext(), "Нет сохраненных данных", Toast.LENGTH_SHORT).show();
			getFragmentManager().beginTransaction().replace(R.id.content_frame, new Preference()).commit();
		} else {
			mGroupSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.others_row, mGroupList));
		}
	}
}
