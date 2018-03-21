package ru.shunt.guapschedule.internet;

import android.net.ConnectivityManager;

public class ConnectionManager {

	public boolean checkInternetConnection(ConnectivityManager conMgr) {

		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {

			return true;

		} else {
			return false;
		}

	}
}
