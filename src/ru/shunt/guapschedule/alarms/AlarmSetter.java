package ru.shunt.guapschedule.alarms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.sdworker.SdWorker;
import ru.shunt.guapschedule.week.WeekSelector;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AlarmSetter extends BroadcastReceiver {

	private Alarm alarm = new Alarm();

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(arg0.getApplicationContext());

		List<Study> list = getlist(arg0);

		if (prefs.getBoolean("track", false)) {

			if (!list.isEmpty()) {
				alarm.SetAlarm(arg0, list);
			}

		} else {
			if (!list.isEmpty()) {
				alarm.CancelAlarm(arg0, list);
			}

		}

		new TaskAlarm().setAlarms(arg0, new SdWorker().readTasksList());
	}

	/*
	 * Sets alarm which controls other alarms
	 */

	public void setAlarm(Context context) {

		DateTime dt = new DateTime();
		dt = dt.hourOfDay().setCopy(0);
		dt = dt.minuteOfDay().setCopy(0);
		dt = dt.secondOfDay().setCopy(30);
		dt = dt.millisOfSecond().setCopy(0);

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, AlarmSetter.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, dt.getMillis(), 86400000, pi);
	}

	private List<Study> getlist(Context cont) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cont.getApplicationContext());
		String groupName = prefs.getString("mainGroup", null);

		if (groupName != null) {

			int day = new DateTime().getDayOfWeek();

			Map<Integer, List<Study>> studiesMap = new SdWorker().readGroupFile(groupName,
					new WeekSelector().selectWeek());

			if (studiesMap.get(day) != null) {
				return studiesMap.get(day);

			}
		}

		return new ArrayList<Study>();
	}

}
