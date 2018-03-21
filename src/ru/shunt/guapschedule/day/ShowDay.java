package ru.shunt.guapschedule.day;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import ru.shunt.guapschedule.main.Preference;
import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.regular.ScheduleParser.WEEK;
import ru.shunt.guapschedule.sdworker.SdWorker;
import ru.shunt.guapschedule.week.WeekSelector;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class ShowDay extends Fragment {

	private List<Study> studies;
	private TextView error;
	private boolean isTomorrow = false; // Tomorrow

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewHierarchy = inflater.inflate(R.layout.act_show_day, container, false);

		error = (TextView) viewHierarchy.findViewById(R.id.dayErrorView);
		/*
		 * Extras:
		 * flag - true = tomorrow
		 */
		isTomorrow = getArguments().getBoolean("tomorrow");

		if (isTomorrow) {
			((TextView) viewHierarchy.findViewById(R.id.dayDateView)).setText(new DateTime().plusDays(1).toString(
					"dd MMMM, EEEE"));
		} else {
			((TextView) viewHierarchy.findViewById(R.id.dayDateView)).setText(new DateTime().toString("dd MMMM, EEEE"));
		}

		ListView rasp = (ListView) viewHierarchy.findViewById(R.id.dayListView);
		/*
		 * If show() returns true making list adapter, and setting on click listener
		 * else if no studies for today
		 */
		if (show(getGroupName())) {

			rasp.setAdapter(new StudiesAdatper(getActivity()));

			rasp.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

					FragmentManager fragmentManager = getFragmentManager();
					Fragment fragment = new ShowStudy();
					Bundle args = new Bundle();
					args.putSerializable("study", studies.get(pos));
					fragment.setArguments(args);
					fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

				}
			});

			/*
			 * 0 - visible
			 * 4 - invisible
			 */
			rasp.setVisibility(0);
			error.setVisibility(4);
		} else {
			rasp.setVisibility(4);
			error.setVisibility(0);
		}

		return viewHierarchy;
	}

	private String getGroupName() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		return prefs.getString("mainGroup", null);
	}

	/*
	 * @return true if there are studies today
	 * false otherwise
	 */

	private boolean show(String groupName) {

		if (groupName == null) {
			Toast.makeText(getActivity().getApplicationContext(), "Выберите группу!", Toast.LENGTH_SHORT).show();
			getFragmentManager().beginTransaction().replace(R.id.content_frame, new Preference()).commit();
			return false;
		}

		int day = new DateTime().getDayOfWeek();
		WEEK myweek = new WeekSelector().selectWeek();

		if (isTomorrow == true) { // Tomorrow

			/*
			 * Monday - 1
			 * Saturday - 6
			 * Sunday - 7
			 */
			day = (day + 1) % 7;
			if (day == 0) {
				day = 7; // Sunday
			}

			if (day == 1) { // Next week
				if (myweek == WEEK.EVEN) {
					myweek = WEEK.ODD;
				} else {
					myweek = WEEK.EVEN;
				}
			}

		}

		/*
		 * Sunday check
		 */
		if (day == 7) {
			if (!isTomorrow) {
				error.setText("Сегодня воскресенье!");
			} else {
				error.setText("Завтра воскресенье!");
			}
			return false; // Studies at Sunday ? NO-WAY
		}

		/*
		 * Trying to get list of studies for today
		 * If OK - return true
		 */
		Map<Integer, List<Study>> studiesMap = new SdWorker().readGroupFile(groupName, myweek);

		if (studiesMap.get(day) != null && studiesMap.get(day).size() > 0) {
			studies = studiesMap.get(day);
			return true;
		}

		error.setText("Занятий не найдено!");

		return false;
	}

	private class StudiesAdatper extends ArrayAdapter<Study> {

		public StudiesAdatper(Context context) {

			super(context, R.layout.day_row, studies);
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Study study = getItem(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.day_row, null);
			}

			StringBuilder sb = new StringBuilder();

			sb.append("<b>");
			sb.append(study.getNumber());
			sb.append(" пара<br><em>");
			sb.append(study.getTimeStart().toString("HH:mm"));
			sb.append(" - ");
			sb.append(study.getTimeFinish().toString("HH:mm"));
			sb.append("</b></em><br><br>");

			// Search for the current study(if any)
			if (study.getTimeStart().isBeforeNow() && study.getTimeFinish().isAfterNow() && !isTomorrow) {

				sb.append("<b>Сейчас идет эта пара!<br>");

				/*
				 * Making time to finish in minutes
				 * Time finish with 0 seconds and 0 milliseconds
				 * minus
				 * Current time with 0 seconds and 0 milliseconds
				 * then divided by 1000 ( to get seconds )
				 * and divided by 60 ( to get minutes )
				 */
				long timeToFinish = (study.getTimeFinish().secondOfMinute().setCopy(0).millisOfSecond().setCopy(0)
						.getMillis() - new DateTime().secondOfMinute().setCopy(0).millisOfSecond().setCopy(0)
						.getMillis()) / 1000 / 60;

				sb.append("Осталось: ");
				sb.append(convertTime(timeToFinish));

				sb.append("</b><br><br>");

			}

			sb.append(study.getName());
			sb.append("<br><br><b>");
			sb.append(study.getRoom());
			sb.append("</b><br>");
			for (String s : study.getTeachers()) {
				sb.append(s);
				if (study.getTeachers().size() != 1)
					sb.append(", ");
			}

			convertView.setBackgroundColor(study.getType().getColor()); // Background color
			((TextView) convertView.findViewById(R.id.row)).setText(Html.fromHtml(sb.toString())); // Study info
			((ImageView) convertView.findViewById(R.id.dayStudyTypeImage)).setImageResource(study.getType()
					.getDrawableId()); // Picture

			return convertView;
		}
	}

	private String convertTime(long time) {

		long minutes = time % 60;
		long hours = (time - minutes) / 60;

		StringBuilder sb = new StringBuilder();

		if (hours > 0) {
			sb.append(String.valueOf(hours));
			sb.append(" час, ");
		}
		sb.append(minutes);
		sb.append(" минут.");

		return sb.toString();
	}

}
