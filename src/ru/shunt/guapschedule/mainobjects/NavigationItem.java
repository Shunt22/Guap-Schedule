package ru.shunt.guapschedule.mainobjects;

import android.view.LayoutInflater;
import android.view.View;

public interface NavigationItem {
	public int getViewType();

	public View getView(LayoutInflater inflater, View convertView);
}
