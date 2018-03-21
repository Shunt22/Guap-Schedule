package ru.shunt.guapschedule.mainobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Task implements Serializable {

	private static final long serialVersionUID = -6218107577369663209L;

	private final String studyName;

	private String task;

	private DateTime timeToNotify;

	public Task(String name) {
		this.studyName = name;
	}

	public DateTime getTimeToNotify() {
		return timeToNotify;
	}

	public void setTimeToNotify(DateTime timeToNotify) {
		this.timeToNotify = timeToNotify;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getStudyName() {
		return studyName;
	}
}
