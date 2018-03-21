package ru.shunt.guapschedule.other;

import org.jsoup.nodes.Document;

public interface OnTasksCompleted {

	void onMainTaskComplete(Document doc);

	void onSecondTaskComplete(Document doc);

}
