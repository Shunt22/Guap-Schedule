package ru.shunt.guapschedule.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.sdworker.SdWorker;
import ru.shunt.guapschedule.week.WeekSelector;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.shunt.guapschedule.R;

public class ViewFactory implements RemoteViewsService.RemoteViewsFactory {

	private List<Study> studiesList = new ArrayList<Study>();
	private boolean flag = false;
	private String error;
	private Context context = null;

	public ViewFactory(Context context, Intent intent) {

		this.context = context;
	}

	@Override
	public int getCount() {
		return studiesList.size();
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int pos) {
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_row);
		if (!flag) {
			Study study = studiesList.get(pos);

			StringBuilder widgetItem = new StringBuilder();
			widgetItem.append("\n\n\n").append(study.getNumber()).append(" пара\n").append(study.getType())
					.append(" - ").append(study.getName()).append("\nв ").append(study.getRoom()).append("\n\n\n");

			rv.setInt(R.id.row, "setBackgroundColor", study.getType().getColor());
			rv.setTextViewText(R.id.row, widgetItem.toString());

			Intent i = new Intent();
			i.putExtra("study", study);
			rv.setOnClickFillInIntent(R.id.row, i);
		} else {

			rv.setTextViewText(R.id.row, error);
		}

		return rv;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
		refresh();
	}

	@Override
	public void onDestroy() {

	}

	private void refresh() {
		studiesList.clear();
		flag = false;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		String groupName = prefs.getString("mainGroup", null);

		if (groupName != null && new SdWorker().searchForGroupFile(groupName)) {

			int day = new DateTime().getDayOfWeek();

			Map<Integer, List<Study>> studiesMap = new SdWorker().readGroupFile(groupName,
					new WeekSelector().selectWeek());

			if (studiesMap.get(day) != null && studiesMap.get(day).size() > 0) {

				studiesList = studiesMap.get(day);

			} else {
				studiesList.add(null);
				flag = true;
				error = "Ничего не найдено!";
			}

		} else {
			studiesList.add(null);
			flag = true;
			error = "Выберите группу!";
		}
	}
}
