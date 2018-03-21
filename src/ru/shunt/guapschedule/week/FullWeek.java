package ru.shunt.guapschedule.week;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.shunt.guapschedule.day.ShowStudy;
import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.regular.ScheduleParser.WEEK;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.shunt.guapschedule.R;

public class FullWeek extends Fragment {

	@SuppressLint("UseSparseArrays")
	private static Map<Integer, String> indexToDay = new HashMap<Integer, String>();

	static {
		indexToDay.put(0, "<b>Вне сетки расписания</b>");
		indexToDay.put(1, "<b>Понедельник</b>");
		indexToDay.put(2, "<b>Вторник</b>");
		indexToDay.put(3, "<b>Среда</b>");
		indexToDay.put(4, "<b>Четверг</b>");
		indexToDay.put(5, "<b>Пятница</b>");
		indexToDay.put(6, "<b>Суббота</b>");
		indexToDay.put(7, "<b>Воскресенье</b>");
	}
	private String mGroupName;
	private ExpandableListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewHierarchy = inflater.inflate(R.layout.act_full_week, container, false);

		/*
		 * Getting action bar and setting it to the spinner mode
		 */
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		/*
		 * Get strings from resources ( Odd,even or both )
		 */
		Resources res = getResources();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_for_action_bar,
				res.getStringArray(R.array.weeks));
		adapter.setDropDownViewResource(R.layout.spinner_for_action_bar);

		actionBar.setListNavigationCallbacks(adapter, new NavCallBack());

		// Setting selected item for current type of week
		WEEK myweek = new WeekSelector().selectWeek();
		if (myweek == WEEK.EVEN) {
			actionBar.setSelectedNavigationItem(1);
		} else {
			actionBar.setSelectedNavigationItem(0);
		}

		list = (ExpandableListView) viewHierarchy.findViewById(R.id.fullWeekListView);

		mGroupName = getGroupName();

		// setRetainInstance(true);

		return viewHierarchy;
	}

	private class myAdapter extends BaseExpandableListAdapter {

		private Map<Integer, List<Study>> myMap;
		private Context context;

		private myAdapter(Context context, Map<Integer, List<Study>> myMap) {
			this.context = context;
			this.myMap = myMap;
		}

		@Override
		public Study getChild(int groupPos, int childPos) {

			return myMap.get(groupPos).get(childPos);

		}

		@Override
		public long getChildId(int groupPos, int childPos) {
			return childPos;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getChildView(int groupPos, int childPos, boolean isLastChild, View view, ViewGroup parent) {

			Study study = getChild(groupPos, childPos);

			if (view == null) {
				LayoutInflater infalInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = infalInflater.inflate(R.layout.day_row, null);
			}
			StringBuilder sb = new StringBuilder();

			if (groupPos != 0) {

				sb.append("<b>");
				switch (study.getWeekType()) {
				case UP:
					sb.append("<font color=#FF0000>" + study.getWeekType().getDrawable() + "</font>");
					break;
				case DN:
					sb.append("<font color=#0000FF>" + study.getWeekType().getDrawable() + "</font>");
					break;
				default:
					sb.append(study.getWeekType().getDrawable());
					break;
				}
				sb.append(study.getNumber());
				sb.append(" пара<br><em>");
				sb.append(study.getTimeStart().toString("HH:mm"));
				sb.append(" - ");
				sb.append(study.getTimeFinish().toString("HH:mm"));
				sb.append("</b></em><br><br>");
				sb.append(study.getName());
				sb.append("<br><br><b>");
				sb.append(study.getRoom());
				sb.append("</b><br>");

			} else { // Course Project and Course work
				sb.append("<br>");
				sb.append(study.getName());
				sb.append("<br><br><b>");
				sb.append(study.getRoom());
				sb.append("</b><br>");
			}

			view.setBackgroundColor(study.getType().getColor());

			((TextView) view.findViewById(R.id.row)).setText(Html.fromHtml(sb.toString()));

			((ImageView) view.findViewById(R.id.dayStudyTypeImage)).setImageResource(study.getType().getDrawableId());

			view.setBackgroundColor(study.getType().getColor());

			return view;
		}

		@Override
		public int getChildrenCount(int groupPos) {
			/*
			 * see getGroupCount info
			 */
			if (myMap.get(groupPos) == null) {
				return 0;
			}
			return myMap.get(groupPos).size();
		}

		@Override
		public String getGroup(int groupPos) {
			return indexToDay.get(groupPos);
		}

		@Override
		public int getGroupCount() {
			/*
			 * We use 7 here because there are 7 possible days ( 6 in fact ( week without sunday ) + out of schedule ( 0
			 * index ) )
			 * if we return studiesMap.size(), this will case some errors
			 * e.g. someone have studies on three day ( Monday, Wensday and Saturday )
			 * map size is 3
			 * now getGroup method begins to work
			 * groupPos = 0 return "outOfSchedule"
			 * groupPos = 1 return Monday
			 * groupPos = 2 return Tuesday
			 * in sum 3, but days are not correct
			 * So for now this method return 7
			 * For those days which have no studies simply return getChildCount as 0
			 */
			return 7;
		}

		@Override
		public long getGroupId(int groupPos) {
			return groupPos;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getGroupView(int groupPos, boolean isLastChild, View view, ViewGroup parentView) {

			if (view == null) {
				LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inf.inflate(R.layout.others_row, null);
			}

			((TextView) view.findViewById(R.id.row)).setText(Html.fromHtml(getGroup(groupPos)));

			((TextView) view.findViewById(R.id.row)).setHeight(75);

			return view;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}

	}

	private String getGroupName() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		return prefs.getString("mainGroup", null);
	}

	private class NavCallBack implements OnNavigationListener {

		@Override
		public boolean onNavigationItemSelected(int pos, long arg1) {

			final String name = mGroupName;

			final Map<Integer, List<Study>> studiesMap;

			switch (pos) {

			case 0:

				studiesMap = new SdWorker().readGroupFile(name, WEEK.ODD);
				break;

			case 1:

				studiesMap = new SdWorker().readGroupFile(name, WEEK.EVEN);
				break;

			default: // Both weeks

				studiesMap = new SdWorker().readGroupFile(name, WEEK.BOTH);
				break;

			}

			myAdapter adapter = new myAdapter(getActivity(), studiesMap);

			list.setAdapter(adapter);

			list.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView arg0, View arg1, int groupPos, int childPos, long arg4) {

					if (groupPos != 0) {
						FragmentManager fragmentManager = getFragmentManager();
						Fragment fragment = new ShowStudy();
						Bundle args = new Bundle();
						args.putSerializable("study", studiesMap.get(groupPos).get(childPos));
						fragment.setArguments(args);
						fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
						return true;
					} else {
						return false;
					}
				}

			});

			return true;
		}
	}

}