package ru.shunt.guapschedule.internet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ru.shunt.guapschedule.other.OnTasksCompleted;

import android.os.AsyncTask;

public class MainParser extends AsyncTask<String, Void, Document> {

	private OnTasksCompleted listener;

	public MainParser(OnTasksCompleted listener) {
		this.listener = listener;
	}

	@Override
	protected Document doInBackground(String... links) {
		Document doc = null;
		try {
			doc = Jsoup.connect(links[0]).timeout(5000).get();
		} catch (Exception e) {

			return doc = null;

		}
		return doc;

	}

	@Override
	protected void onPostExecute(Document doc) {
		listener.onMainTaskComplete(doc);
	}

}
