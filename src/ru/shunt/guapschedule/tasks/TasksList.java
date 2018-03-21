package ru.shunt.guapschedule.tasks;

import java.util.List;

import ru.shunt.guapschedule.mainobjects.Task;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shunt.guapschedule.R;

public class TasksList extends Fragment {

	private List<Task> tasksList = new SdWorker().readTasksList();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		View viewHierarchy = inflater.inflate(R.layout.act_tasks_list, container, false);

		ListView listview = (ListView) viewHierarchy.findViewById(R.id.taskListView);
		TextView text = (TextView) viewHierarchy.findViewById(R.id.tasksNoTasksView);

		if (tasksList.isEmpty()) {
			listview.setVisibility(4);
			text.setVisibility(0);
		}

		ListAdapter adapter = new TasksAdapter(getActivity());
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View view, int position, long id) {

				FragmentManager fragmentManager = getFragmentManager();
				Fragment fragment = new ShowTask();
				Bundle args = new Bundle();
				args.putSerializable("task", tasksList.get(position));
				fragment.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			}
		});
		return viewHierarchy;

	}

	private class TasksAdapter extends ArrayAdapter<Task> {

		public TasksAdapter(Context context) {
			super(context, R.layout.others_row, tasksList);
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.others_row, null);
			}

			Task task = getItem(position);
			((TextView) convertView.findViewById(R.id.row)).setText(task.getStudyName());
			((TextView) convertView.findViewById(R.id.row)).setHeight(90);
			return convertView;

		}
	}

	// Menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.tasks_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menuNewTask:
			FragmentManager fragmentManager = getFragmentManager();
			Fragment fragment = new NewTask();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
