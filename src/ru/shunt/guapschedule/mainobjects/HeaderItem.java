package ru.shunt.guapschedule.mainobjects;

import ru.shunt.guapschedule.main.Main.RowType;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

import com.shunt.guapschedule.R;

public class HeaderItem implements NavigationItem {

	private final String mName;

	public HeaderItem(String mName) {
		this.mName = mName;
	}

	@Override
	public int getViewType() {
		return RowType.HEADER_ITEM.ordinal();
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

		final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 35); // Width , height
		view.setLayoutParams(params);


		text.setGravity(Gravity.LEFT);
		text.setTextColor(Color.parseColor("#F5DC49"));
		return view;
	}

}
