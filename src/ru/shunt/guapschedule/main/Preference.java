package ru.shunt.guapschedule.main;

import java.util.List;

import ru.shunt.guapschedule.alarms.AlarmSetter;
import ru.shunt.guapschedule.sdworker.SdWorker;
import ru.shunt.guapschedule.widget.ScheduleWidget;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import com.shunt.guapschedule.R;

public class Preference extends PreferenceFragment {

	private AlarmSetter alarm = new AlarmSetter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs); // Show view

		getActivity().setTitle("Настройки");

		List<String> myList = new SdWorker().readGroups();

		PreferenceScreen root = this.getPreferenceScreen();

		ListPreference mainGroupsList = (ListPreference) root.findPreference("mainGroup");

		if (!myList.isEmpty()) {

			CharSequence[] cs = myList.toArray(new CharSequence[myList.size()]);

			mainGroupsList.setEntries(cs);
			mainGroupsList.setEntryValues(cs);

			mainGroupsList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(android.preference.Preference arg0, Object arg1) {

					Intent intent = new Intent(getActivity(), ScheduleWidget.class);
					intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

					int ids[] = AppWidgetManager.getInstance(getActivity().getApplication()).getAppWidgetIds(
							new ComponentName(getActivity().getApplication(), ScheduleWidget.class));

					intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
					getActivity().sendBroadcast(intent);
					return true;
				}
			});

		} else {
			mainGroupsList.setEnabled(false);
		}
		SwitchPreference trackPreference = (SwitchPreference) root.findPreference("track");
		trackPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(android.preference.Preference preference, Object newValue) {
				Context context = getActivity().getApplicationContext();
				alarm.setAlarm(context);
				return true;
			}
		});

		// Setting alarms
		Context context = getActivity().getApplicationContext();
		alarm.setAlarm(context);

	}
}