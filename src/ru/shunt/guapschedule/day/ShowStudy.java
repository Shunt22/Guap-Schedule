package ru.shunt.guapschedule.day;

import ru.shunt.guapschedule.geo.RoomLocation;
import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.mainobjects.Task;
import ru.shunt.guapschedule.sdworker.SdWorker;
import ru.shunt.guapschedule.tasks.ShowTask;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shunt.guapschedule.R;

public class ShowStudy extends Fragment {

	private Task task;
	private Study study;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewHierarchy = inflater.inflate(R.layout.act_show_study, container, false);

		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setTitle("Просмотр занятия");
		
		Button taskButton = (Button) viewHierarchy.findViewById(R.id.study_tasks);

		study = (Study) getArguments().getSerializable("study");

		StringBuilder sb = new StringBuilder();

		sb.append("<b>");
		sb.append(study.getNumber());
		sb.append(" пара ");
		sb.append(study.getTimeStart().toString("HH:mm"));
		sb.append(" - ");
		sb.append(study.getTimeFinish().toString("HH:mm"));
		sb.append("</b><br><br>");
		sb.append(study.getType().toString());
		sb.append(" - ");
		sb.append(study.getName());
		sb.append("<br><br>в <b>");
		sb.append(study.getRoom());
		sb.append("</b><br><br><b>Преподаватель(и):</b>");
		sb.append("<br>");
		for (String s : study.getTeachers()) {
			sb.append(s);
			sb.append("<br>");
		}

		sb.append("<b>Группы:</b> ");

		for (String s : study.getGroups()) {
			sb.append(s);
			sb.append("<br>");
		}

		((TextView) viewHierarchy.findViewById(R.id.studyView)).setText(Html.fromHtml(sb.toString()));

		/*
		 * Search if there is any tasks for this study
		 * if so trying to read this task
		 */
		if (new SdWorker().searchForTaskFile(study.getName())) {

			task = new SdWorker().readTask(study.getName());
			if (task != null) {
				taskButton.setText("Посмотреть задания");
				taskButton.setClickable(true);
			}

		} else {

			taskButton.setText("Заданий не найдено!");
			taskButton.setClickable(false);
			taskButton.setEnabled(false);
		}
		taskButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				Fragment fragment = new ShowTask();
				Bundle args = new Bundle();
				args.putSerializable("task", task);
				fragment.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			}
		});
		Button geoButton = (Button) viewHierarchy.findViewById(R.id.showStudyGeoButton);
		geoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FragmentManager fragmentManager = getFragmentManager();
				Fragment fragment = new RoomLocation();
				Bundle args = new Bundle();
				args.putSerializable("roomName", study.getRoom());
				fragment.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			}
		});

		setRetainInstance(true);
		return viewHierarchy;

	}

}
