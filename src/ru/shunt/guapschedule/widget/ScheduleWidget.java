package ru.shunt.guapschedule.widget;

import ru.shunt.guapschedule.main.Main;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class ScheduleWidget extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		Toast.makeText(context, "Виджет Расписание ГУАП добавлен", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		for (int i = 0; i < appWidgetIds.length; ++i) {

			Intent intent = new Intent(context, ScheduleWidgetService.class);

			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.act_widget);

			rv.setRemoteAdapter(R.id.lv, intent);

			Intent clickIntent = new Intent(context, Main.class);
			PendingIntent clickPI = PendingIntent.getActivity(context, 99, clickIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			rv.setPendingIntentTemplate(R.id.lv, clickPI);

			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.lv);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}