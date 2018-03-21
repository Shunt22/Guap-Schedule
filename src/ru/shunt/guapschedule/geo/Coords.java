package ru.shunt.guapschedule.geo;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.shunt.guapschedule.R;

public class Coords extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewHierarchy = inflater.inflate(R.layout.act_coords, container, false);
		ListView listview = (ListView) viewHierarchy.findViewById(R.id.coordListView);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View view, int position, long id) {
				String geoURI = "geo:59.929500,30.296602?z=19";
				switch (position) {
				case 0:
					geoURI = "geo:59.929500,30.296602?z=19";
					break;

				case 1:
					geoURI = "geo:59.857441,30.327769?z=19";
					break;

				case 2:
					geoURI = "geo:59.856076,30.330361?z=19";
					break;

				case 3:
					geoURI = "geo:59.873679,30.316113?z=19";
					break;

				case 4:
					geoURI = "geo:59.860323,30.235574?z=19";
					break;

				case 5:
					geoURI = "geo:59.941354,30.467030?z=19";
					break;
				}

				Uri geo = Uri.parse(geoURI);
				Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
				startActivity(geoMap);

			}
		});

		return viewHierarchy;
	}

}
