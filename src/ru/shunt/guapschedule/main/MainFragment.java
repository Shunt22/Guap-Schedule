package ru.shunt.guapschedule.main;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.regular.ScheduleParser.WEEK;
import ru.shunt.guapschedule.sdworker.SdWorker;
import ru.shunt.guapschedule.week.WeekSelector;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shunt.guapschedule.R;

public class MainFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.act_main_screen, container, false);
		TextView currentStudy = (TextView) view.findViewById(R.id.mainStudy);

		WEEK myweek = new WeekSelector().selectWeek();

		/*
		 * Time and current day of week here
		 */

		String groupName = getGroupName();

		StringBuilder time = new StringBuilder();
		time.append("Сейчас: ");
		time.append(new DateTime().toString("HH:mm, EEEEE\n"));
		time.append(!myweek.equals(WEEK.EVEN) ? "Верхняя, нечетная неделя\n" : "Нижняя, четная неделя\n");
		time.append("Текущая пара для группы ");
		time.append(groupName == null ? "Не выбранно" : groupName + ":\n");

		currentStudy.setText(time.toString());
		if (groupName != null) {

			int day = new DateTime().getDayOfWeek();

			StringBuilder answer = new StringBuilder();

			Map<Integer, List<Study>> studiesMap = new SdWorker().readGroupFile(groupName, myweek);

			if (studiesMap.get(day) != null) {

				for (Study study : studiesMap.get(day)) {

					DateTime timeStart = study.getTimeStart();
					DateTime timeFinish = study.getTimeFinish();

					DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();

					if (comparator.compare(timeStart, new DateTime()) <= 0
							&& comparator.compare(timeFinish, new DateTime()) >= 0) {

						answer.append("<br><b>");
						answer.append(study.getNumber());
						answer.append(" пара<br>");
						answer.append(timeStart.toString("HH:mm"));
						answer.append(" - ");
						answer.append(timeFinish.toString("HH:mm"));
						answer.append("</b><br><br>");
						answer.append(study.getType().toString());
						answer.append(" - ");
						answer.append(study.getName());
						answer.append("<br><br><b>");
						answer.append(study.getRoom());
						answer.append("</b><br>");

						for (String s : study.getTeachers()) {
							answer.append(s);
							answer.append(", ");
						}

						currentStudy.append(Html.fromHtml(answer.toString()));

						return view; // If found something, return from this method, otherwise show "No studies"
					}

				}

			}
		}

		currentStudy.append("\nЗанятий не найдено!");

		return view;
	}

	private String getGroupName() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		return prefs.getString("mainGroup", null);
	}

}
