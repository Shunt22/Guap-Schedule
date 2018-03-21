package ru.shunt.guapschedule.schedulechanger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import ru.shunt.guapschedule.day.ShowStudy;
import ru.shunt.guapschedule.main.MainFragment;
import ru.shunt.guapschedule.main.Preference;
import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.mainobjects.Study.STUDYTYPE;
import ru.shunt.guapschedule.mainobjects.Study.STUDYWEEK;
import ru.shunt.guapschedule.regular.ScheduleParser.WEEK;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class Changer extends Fragment {
	private String groupName;
	private Spinner daysSpinner;
	private Spinner studyTypeSpinner;
	private Spinner weekTypeSpinner;
	private Spinner studyIdSpinner;
	private EditText studyName;
	private EditText room;
	private WEEK week;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewHierarchy = inflater.inflate(R.layout.act_changer_main, container, false);

		groupName = getGroupName();
		if (groupName == null) {
			Toast.makeText(getActivity().getApplicationContext(), "Выберите группу!", Toast.LENGTH_SHORT).show();
			getFragmentManager().beginTransaction().replace(R.id.content_frame, new Preference()).commit();

		} else {

			daysSpinner = (Spinner) viewHierarchy.findViewById(R.id.changerDaySpinner);
			studyTypeSpinner = (Spinner) viewHierarchy.findViewById(R.id.changerTypeSpinner);
			studyName = (EditText) viewHierarchy.findViewById(R.id.changerStudyNameEdit);
			room = (EditText) viewHierarchy.findViewById(R.id.changerStudyRoomEdit);
			weekTypeSpinner = (Spinner) viewHierarchy.findViewById(R.id.changerWeekSpinner);
			studyIdSpinner = (Spinner) viewHierarchy.findViewById(R.id.changerStudyIdSpinner);

			Resources res = getResources();

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.others_row,
					res.getStringArray(R.array.days));
			adapter.setDropDownViewResource(R.layout.others_row);
			daysSpinner.setAdapter(adapter);

			ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.others_row,
					res.getStringArray(R.array.studyTypes));
			adapter1.setDropDownViewResource(R.layout.others_row);
			studyTypeSpinner.setAdapter(adapter1);

			ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.others_row,
					res.getStringArray(R.array.weeks));
			adapter2.setDropDownViewResource(R.layout.others_row);
			weekTypeSpinner.setAdapter(adapter2);

			ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(), R.layout.others_row,
					new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8")));
			adapter3.setDropDownViewResource(R.layout.others_row);
			studyIdSpinner.setAdapter(adapter3);

			Button saveButton = (Button) viewHierarchy.findViewById(R.id.changerSaveButton);
			saveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					getData();

				}
			});

		}
		return viewHierarchy;
	}

	private String getGroupName() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		return prefs.getString("mainGroup", null);
	}

	private boolean contains(List<? extends Study> list, Study study) {

		int studyId = study.getNumber();
		STUDYWEEK week = study.getWeekType();

		for (Study studyFromList : list) {
			if (studyFromList.getNumber() == studyId) {

				if (week.equals(STUDYWEEK.BOTH)
						&& (studyFromList.getWeekType().equals(STUDYWEEK.UP) || studyFromList.getWeekType().equals(
								STUDYWEEK.DN))) {
					return true;
				} else if ((week.equals(STUDYWEEK.UP) || week.equals(STUDYWEEK.DN)
						&& studyFromList.getWeekType().equals(STUDYWEEK.BOTH))) {
					return true;
				} else {
					if (studyFromList.getWeekType().equals(week)) {
						return true;
					}
				}

			}
		}

		return false;
	}

	private void changeSchedule(Map<Integer, List<Study>> mapToChange, Study study) {

	}

	private void change(Study study) {

		if (week == WEEK.BOTH) {
			Map<Integer, List<Study>> studiesMapWeek = new SdWorker().readGroupFile(groupName, WEEK.EVEN);
			List<Study> studiesListWeek = studiesMapWeek.get(daysSpinner.getSelectedItemPosition());
			if (studiesListWeek == null) {
				studiesListWeek = new ArrayList<Study>();
				studiesMapWeek.put(daysSpinner.getSelectedItemPosition(), studiesListWeek);
			}
			if (!contains(studiesListWeek, study)) {
				studiesListWeek.add(study);
			} else {
				error();
				return;
			}

			Collections.sort(studiesListWeek);

			new SdWorker().writeGroupFile(groupName, studiesMapWeek, WEEK.EVEN);

			studiesMapWeek = new SdWorker().readGroupFile(groupName, WEEK.ODD);
			studiesListWeek = studiesMapWeek.get(daysSpinner.getSelectedItemPosition());
			if (studiesListWeek == null) {
				studiesListWeek = new ArrayList<Study>();
				studiesMapWeek.put(daysSpinner.getSelectedItemPosition(), studiesListWeek);
			}
			if (!contains(studiesListWeek, study)) {
				studiesListWeek.add(study);
			} else {
				error();
				return;
			}

			Collections.sort(studiesListWeek);
			new SdWorker().writeGroupFile(groupName, studiesMapWeek, WEEK.ODD);

			studiesMapWeek = new SdWorker().readGroupFile(groupName, WEEK.BOTH);
			studiesListWeek = studiesMapWeek.get(daysSpinner.getSelectedItemPosition());
			if (studiesListWeek == null) {
				studiesListWeek = new ArrayList<Study>();
				studiesMapWeek.put(daysSpinner.getSelectedItemPosition(), studiesListWeek);
			}
			if (!contains(studiesListWeek, study)) {
				studiesListWeek.add(study);
			} else {
				error();
				return;
			}

			Collections.sort(studiesListWeek);
			new SdWorker().writeGroupFile(groupName, studiesMapWeek, WEEK.BOTH);

			saveCompleted();
		} else {

			Map<Integer, List<Study>> studiesMapWeek = new SdWorker().readGroupFile(groupName, week);

			List<Study> studiesListWeek = studiesMapWeek.get(daysSpinner.getSelectedItemPosition());
			if (studiesListWeek == null) {
				studiesListWeek = new ArrayList<Study>();
				studiesMapWeek.put(daysSpinner.getSelectedItemPosition(), studiesListWeek);
			}
			if (!contains(studiesListWeek, study)) {
				studiesListWeek.add(study);
			} else {
				error();
				return;
			}

			Collections.sort(studiesListWeek);

			new SdWorker().writeGroupFile(groupName, studiesMapWeek, week);

			/*
			 * BOTH week file here
			 */
			Map<Integer, List<Study>> studiesMap = new SdWorker().readGroupFile(groupName, WEEK.BOTH);

			List<Study> studiesList = studiesMap.get(daysSpinner.getSelectedItemPosition());
			if (studiesList == null) {
				studiesList = new ArrayList<Study>();
				studiesMap.put(daysSpinner.getSelectedItemPosition(), studiesList);
			}

			if (!contains(studiesList, study)) {
				studiesList.add(study);
			} else {
				error();
				return;
			}

			Collections.sort(studiesList);

			new SdWorker().writeGroupFile(groupName, studiesMap, WEEK.BOTH);

			saveCompleted();
		}

	}

	private void saveCompleted() {
		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = new MainFragment();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

	private void error() {
		Toast.makeText(getActivity(), "Такая пара уже существует!", Toast.LENGTH_SHORT).show();
	}

	private void getData() {
		DateTime timeStart = new DateTime();
		DateTime timeFinish = new DateTime();
		switch (studyIdSpinner.getSelectedItemPosition()) {
		case 0:
			timeStart = timeStart.hourOfDay().setCopy(9).minuteOfHour().setCopy(0);
			timeFinish = timeFinish.hourOfDay().setCopy(10).minuteOfHour().setCopy(30);
			break;
		case 1:
			timeStart = timeStart.hourOfDay().setCopy(10).minuteOfHour().setCopy(40);
			timeFinish = timeFinish.hourOfDay().setCopy(12).minuteOfHour().setCopy(10);
			break;
		case 2:
			timeStart = timeStart.hourOfDay().setCopy(12).minuteOfHour().setCopy(20);
			timeFinish = timeFinish.hourOfDay().setCopy(13).minuteOfHour().setCopy(50);
			break;
		case 3:
			timeStart = timeStart.hourOfDay().setCopy(14).minuteOfHour().setCopy(10);
			timeFinish = timeFinish.hourOfDay().setCopy(15).minuteOfHour().setCopy(40);
			break;
		case 4:
			timeStart = timeStart.hourOfDay().setCopy(15).minuteOfHour().setCopy(50);
			timeFinish = timeFinish.hourOfDay().setCopy(17).minuteOfHour().setCopy(20);
			break;
		case 5:
			timeStart = timeStart.hourOfDay().setCopy(17).minuteOfHour().setCopy(30);
			timeFinish = timeFinish.hourOfDay().setCopy(19).minuteOfHour().setCopy(0);
			break;
		case 6:
			timeStart = timeStart.hourOfDay().setCopy(19).minuteOfHour().setCopy(10);
			timeFinish = timeFinish.hourOfDay().setCopy(20).minuteOfHour().setCopy(30);
			break;
		case 7:
			timeStart = timeStart.hourOfDay().setCopy(20).minuteOfHour().setCopy(40);
			timeFinish = timeFinish.hourOfDay().setCopy(22).minuteOfHour().setCopy(0);
			break;

		}
		timeStart = timeStart.secondOfMinute().setCopy(0).millisOfSecond().setCopy(0);
		timeFinish = timeFinish.secondOfMinute().setCopy(0).millisOfSecond().setCopy(0);

		Study study = new Study(String.valueOf(studyIdSpinner.getSelectedItemPosition() + 1), timeStart, timeFinish);

		study.setType(STUDYTYPE.forName((String) studyTypeSpinner.getSelectedItem()));

		switch (weekTypeSpinner.getSelectedItemPosition()) {
		case 0:
			week = WEEK.ODD;
			study.setWeekType(STUDYWEEK.UP);
			break;
		case 1:
			week = WEEK.EVEN;
			study.setWeekType(STUDYWEEK.DN);
			break;
		default:
			week = WEEK.BOTH;
			study.setWeekType(STUDYWEEK.BOTH);
			break;
		}
		study.setName(studyName.getText().toString());
		study.setRoom(room.getText().toString());

		change(study);
	}

}
