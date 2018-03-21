package ru.shunt.guapschedule.tasks;

import ru.shunt.guapschedule.mainobjects.Task;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shunt.guapschedule.R;

public class ShowTask extends Fragment {

	private Task task;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		View viewHierarchy = inflater.inflate(R.layout.act_show_task, container, false);

		task = (Task) getArguments().getSerializable("task");
		((TextView) viewHierarchy.findViewById(R.id.taskView)).setText(task.getTask());

		getActivity().setTitle("Задание для: " + task.getStudyName());

		return viewHierarchy;
	}

	// Menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.show_task_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.tasksMenuDelete:

			new SdWorker().deleteTasksFile(task.getStudyName());

			FragmentManager fragmentManager = getFragmentManager();
			Fragment fragment = new TasksList();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
