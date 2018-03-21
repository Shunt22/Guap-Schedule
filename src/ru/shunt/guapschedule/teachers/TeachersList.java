package ru.shunt.guapschedule.teachers;

import java.util.List;

import ru.shunt.guapschedule.main.Preference;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class TeachersList extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View viewHierarchy = inflater.inflate(R.layout.act_teachers_list, container, false);

		List<String> teachersList = new SdWorker().readTeachers();
		if (teachersList.isEmpty()) {
			Toast.makeText(getActivity().getApplicationContext(), "Нет сохраненных данных", Toast.LENGTH_SHORT).show();
			getFragmentManager().beginTransaction().replace(R.id.content_frame, new Preference()).commit();
		} else {
			((Spinner) viewHierarchy.findViewById(R.id.teachersListSpinner)).setAdapter(new ArrayAdapter<String>(
					getActivity(), R.layout.others_row, teachersList));

			Button showButton = (Button) viewHierarchy.findViewById(R.id.teachersShowButton);
			showButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Fragment fragment = new ShowTeachers();
					Bundle args = new Bundle();
					args.putString("teacherName",
							(String) ((Spinner) viewHierarchy.findViewById(R.id.teachersListSpinner)).getSelectedItem());
					fragment.setArguments(args);
					getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

				}
			});
		}
		return viewHierarchy;
	}
}