package ru.shunt.guapschedule.mainobjects;

import ru.shunt.guapschedule.main.Main.RowType;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

import com.shunt.guapschedule.R;

public class ListItem implements NavigationItem {

	private final String mName;
	private final int mDrawableResource;
	private final Class<? extends Fragment> mFragmentClass;
	private final Bundle mFragmentAgs;

	public ListItem(String mName, int mDrawableResource, Class<? extends Fragment> mClassName, Bundle args) {
		this.mName = mName;
		this.mDrawableResource = mDrawableResource;
		this.mFragmentClass = mClassName;
		this.mFragmentAgs = args;

	}

	public Bundle getFragmentArgs() {
		return mFragmentAgs;
	}

	public Class<? extends Fragment> getFragmentClass() {
		return mFragmentClass;
	}

	public String getName() {
		return mName;
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.navigation_row, null);
		} else {
			view = convertView;
		}

		TextView text = (TextView) view.findViewById(R.id.row);
		text.setText(mName);
		text.setCompoundDrawablesWithIntrinsicBounds(mDrawableResource, 0, 0, 0);
		
		final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 85); // Width , height
		view.setLayoutParams(params);
		return view;
	}
}
