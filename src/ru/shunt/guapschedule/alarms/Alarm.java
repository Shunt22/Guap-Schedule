package ru.shunt.guapschedule.alarms;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ru.shunt.guapschedule.main.Main;
import ru.shunt.guapschedule.mainobjects.Study;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import com.shunt.guapschedule.R;

public class Alarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		final int NOTIFY_ID = 101;
		Study study = (Study) intent.getExtras().get("study");

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

		Intent notificationIntent = new Intent(context, Main.class);
		notificationIntent.putExtra("study", study);

		PendingIntent contentIntent = PendingIntent.getActivity(context, NOTIFY_ID, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Resources res = context.getResources();
		Notification.Builder builder = new Notification.Builder(context);

		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.app_icon_notify_ticker)
				.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_icon_notify_small))
				.setTicker("Внимание! Началась пара!").setAutoCancel(true).setContentTitle(study.getName())
				.setContentText("в " + study.getRoom());
		/*
		 * Setting in silent mode and back after 1.30 hour
		 */

		Boolean silent = prefs.getBoolean("silent", true);
		if (silent) {
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

			PendingIntent pi = PendingIntent.getBroadcast(context, 1, new Intent(context, RingerAlarmReceiver.class),
					PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager alr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			alr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5400000, pi);
		}
		/*
		 * End of setting in silent mode
		 */
		Notification n = builder.build();
		long[] vibrate = new long[] { 1000, 1000, 1000 };
		n.vibrate = vibrate;
		nm.notify(NOTIFY_ID, n);

	}

	public void CancelAlarm(Context context, List<Study> list) {
		for (Study study : list) {

			DateTime timeStart = study.getTimeStart();
			timeStart = timeStart.secondOfMinute().setCopy(0);
			timeStart = timeStart.millisOfSecond().setCopy(0);

			Intent intent = new Intent(context, Alarm.class);
			PendingIntent sender = PendingIntent.getBroadcast(context, timeStart.getMillisOfDay(), intent, 0);

			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			alarmManager.cancel(sender);
		}

	}

	public void SetAlarm(Context context, List<Study> list) {
		for (Study study : list) {

			DateTime timeStart = study.getTimeStart();
			timeStart = timeStart.secondOfMinute().setCopy(0);
			timeStart = timeStart.millisOfSecond().setCopy(0);
			timeStart = makeDate(timeStart);

			if (timeStart.isAfterNow()) {

				Intent intent = new Intent(context, Alarm.class);
				intent.putExtra("study", study);
				PendingIntent pi = PendingIntent.getBroadcast(context, timeStart.getMillisOfDay(), intent, 0);

				AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

				am.set(AlarmManager.RTC_WAKEUP, timeStart.getMillis(), pi);
			}
		}
	}

	private DateTime makeDate(DateTime defaultDate) {
		DateTime now = new DateTime();
		return defaultDate.year().setCopy(now.getYear()).monthOfYear().setCopy(now.getMonthOfYear()).dayOfMonth()
				.setCopy(now.getDayOfMonth())
				.withZoneRetainFields(DateTimeZone.forOffsetHours(getCurrentTimeZoneOffset()));
	}

	private int getCurrentTimeZoneOffset() {
		DateTimeZone tz = DateTimeZone.getDefault();
		Long instant = DateTime.now().getMillis();

		long offsetInMilliseconds = tz.getOffset(instant);
		int hours = (int) (TimeUnit.MILLISECONDS.toHours(offsetInMilliseconds));
		return hours;
	}
}