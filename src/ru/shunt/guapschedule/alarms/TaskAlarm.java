package ru.shunt.guapschedule.alarms;

import java.util.List;

import ru.shunt.guapschedule.main.Main;
import ru.shunt.guapschedule.mainobjects.Task;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import com.shunt.guapschedule.R;

public class TaskAlarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Task task = (Task) intent.getExtras().get("tasks");
		final int NOTIFY_ID = 102;

		Intent notificationIntent = new Intent(context, Main.class);
		notificationIntent.putExtra("task", task);

		PendingIntent contentIntent = PendingIntent.getActivity(context, NOTIFY_ID, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Resources res = context.getResources();
		Notification.Builder builder = new Notification.Builder(context);

		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.app_icon_notify_ticker)
				.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_icon_notify_small))
				.setTicker("Внимание! У вас есть задания!").setAutoCancel(true).setContentTitle(task.getStudyName())
				.setContentText(task.getTask());

		Notification n = builder.build();

		long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
		n.vibrate = vibrate;
		nm.notify(NOTIFY_ID, n);

	}

	public void setAlarms(Context context, List<Task> tasksList) {
		for (Task t : tasksList) {

			if (t.getTimeToNotify().isAfterNow()) {

				Intent intent = new Intent(context, TaskAlarm.class);
				intent.putExtra("tasks", t);
				PendingIntent pi = PendingIntent
						.getBroadcast(context, (int) t.getTimeToNotify().getMillis(), intent, 0);

				AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

				am.set(AlarmManager.RTC_WAKEUP, t.getTimeToNotify().getMillis(), pi);
			}
		}
	}

}
