package ru.shunt.guapschedule.week;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Weeks;

import ru.shunt.guapschedule.regular.ScheduleParser.WEEK;

public class WeekSelector {

	/*
	 * Shows if this week is odd or even relatively to 01.09
	 * 
	 * @return odd or even
	 */

	public WEEK selectWeek() {

		DateTime timeNow = new DateTime().withTimeAtStartOfDay();
		DateTime timeThen = new DateTime().withTimeAtStartOfDay();

		int month = timeNow.getMonthOfYear();

		/*
		 * If current month is less then 9 we need to count from 9 moth past year
		 */
		if (month < 9) {
			timeThen = timeNow.minusYears(1);
		}

		/*
		 * Setting time to the monday of the first week of september
		 */

		timeThen = timeThen.dayOfMonth().setCopy(1);
		timeThen = timeThen.monthOfYear().setCopy(9);
		timeThen = timeThen.dayOfWeek().setCopy(DateTimeConstants.MONDAY);

		int weeks = Math.abs(Weeks.weeksBetween(timeNow, timeThen).getWeeks());

		weeks++; // The first week is 0

		WEEK week;

		if (weeks % 2 == 0) {

			week = WEEK.EVEN;

		} else {

			week = WEEK.ODD;
		}

		return week;
	}

}
