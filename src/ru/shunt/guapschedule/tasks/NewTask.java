package ru.shunt.guapschedule.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.shunt.guapschedule.alarms.AlarmSetter;
import ru.shunt.guapschedule.main.Preference;
import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.mainobjects.Task;
import ru.shunt.guapschedule.regular.ScheduleParser.WEEK;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class NewTask extends Fragment {

	private Spinner spinner;
	private EditText text;
	private TextView timeView;
	private TextView dateView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		View viewHierarchy = inflater.inflate(R.layout.act_new_task, container, false);

		text = (EditText) viewHierarchy.findViewById(R.id.tasksEditText);
		spinner = (Spinner) viewHierarchy.findViewById(R.id.tasksStudiesSpinner);

		timeView = (TextView) viewHierarchy.findViewById(R.id.tasksTimeView);
		dateView = (TextView) viewHierarchy.findViewById(R.id.tasksDateView);

		List<String> studiesList = new ArrayList<String>();

		String groupName = getGroupName();
		if (groupName == null) {
			Toast.makeText(getActivity().getApplicationContext(), "Выберите группу!", Toast.LENGTH_SHORT).show();
			getFragmentManager().beginTransaction().replace(R.id.content_frame, new Preference()).commit();

		} else {

			Map<Integer, List<Study>> studiesMap = new SdWorker().readGroupFile(groupName, WEEK.BOTH);
			for (Entry<Integer, List<Study>> entry : studiesMap.entrySet()) {
				for (Study study : entry.getValue()) {
					if (!studiesList.contains(study.getName())) {
						studiesList.add(study.getName());
					}
				}
			}
			spinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.others_row, studiesList));

			StringBuilder sb = new StringBuilder();
			DateTime timeNow = new DateTime();
			sb.append(timeNow.getHourOfDay() >= 10 ? timeNow.getHourOfDay() : "0" + timeNow.getHourOfDay());
			sb.append(":");
			sb.append(timeNow.getMinuteOfHour() >= 10 ? timeNow.getMinuteOfHour() : "0" + timeNow.getMinuteOfHour());
			timeView.setText(sb.toString());

			sb = new StringBuilder();
			timeNow = new DateTime();
			sb.append(timeNow.getDayOfMonth());
			sb.append(".");
			sb.append(timeNow.getMonthOfYear());
			sb.append(".");
			sb.append(timeNow.getYear());

			dateView.setText(sb.toString());

			timeView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogFragment newFragment = new TimePickerClass(v.findViewById(R.id.tasksTimeView));
					newFragment.show(getFragmentManager(), "timePicker");

				}
			});

			dateView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogFragment newDateFragment = new DatePickerClass(v.findViewById(R.id.tasksDateView));
					newDateFragment.show(getFragmentManager(), "datePicker");

				}
			});

			Button saveButton = (Button) viewHierarchy.findViewById(R.id.tasksSaveButton);
			saveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!text.getText().toString().isEmpty()) {
						if (spinner.getSelectedItem() != null) {

							Task task = new Task((String) spinner.getSelectedItem());

							task.setTask(text.getText().toString());

							DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

							DateTime time = timeFormatter.parseDateTime(timeView.getText().toString());

							DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd.MM.yyyy");
							DateTime date = dateFormatter.parseDateTime(dateView.getText().toString());

							time = time.dayOfMonth().setCopy(date.getDayOfMonth()).monthOfYear()
									.setCopy(date.getMonthOfYear()).year().setCopy(date.getYear());

							task.setTimeToNotify(time);

							new SdWorker().writeTasksFile((String) spinner.getSelectedItem(), task);

							new AlarmSetter().setAlarm(getActivity().getApplicationContext());

							FragmentManager fragmentManager = getFragmentManager();
							Fragment fragment = new TasksList();
							fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

						}
					} else {
						Toast.makeText(getActivity(), "Напишите что нибудь!!", Toast.LENGTH_SHORT).show();
					}
				}
			});

		}
		return viewHierarchy;
	}

	private String getGroupName() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		return prefs.getString("mainGroup", null);
	}

}