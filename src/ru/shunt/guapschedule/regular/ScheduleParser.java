package ru.shunt.guapschedule.regular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.mainobjects.Study.STUDYTYPE;
import ru.shunt.guapschedule.mainobjects.Study.STUDYWEEK;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.annotation.SuppressLint;

public class ScheduleParser {

	public static enum WEEK {
		ODD, EVEN, BOTH;
	}

	private static Map<String, Integer> dayToIndex = new HashMap<String, Integer>();

	static {
		dayToIndex.put("Вне сетки расписания", 0);
		dayToIndex.put("Понедельник", 1);
		dayToIndex.put("Вторник", 2);
		dayToIndex.put("Среда", 3);
		dayToIndex.put("Четверг", 4);
		dayToIndex.put("Пятница", 5);
		dayToIndex.put("Суббота", 6);
		dayToIndex.put("Воскресенье", 7);
	}

	private final String str;

	private WEEK week;

	private Map<Integer, List<Study>> days;

	public ScheduleParser(String str, String fileName) {
		this.str = str;

		SdWorker worker = new SdWorker();

		week = WEEK.EVEN;
		parse();
		worker.writeGroupFile(fileName, days, week);

		week = WEEK.ODD;
		parse();
		worker.writeGroupFile(fileName, days, week);

		week = WEEK.BOTH;
		parse();
		worker.writeGroupFile(fileName, days, week);
	}

	@SuppressLint("UseSparseArrays")
	private void parse() {

		days = new HashMap<Integer, List<Study>>(); // ( 1 - Monday, etc )

		Pattern p = Pattern.compile(Regulars.daysPattern, Pattern.DOTALL);
		Matcher m = p.matcher(str);

		while (m.find()) { // Finding day

			List<Study> studies = new ArrayList<Study>(); // List of Studies for
															// single day

			days.put(dayToIndex.get(m.group(1)), studies);
			if (m.group(1).equals("Вне сетки расписания")) {
				Pattern p0 = Pattern.compile(Regulars.outOfSchedule, Pattern.DOTALL);
				Matcher m0 = p0.matcher(m.group(2));
				while (m0.find()) {

					Study out = new Study("0", null, null);
					out.setName(m0.group(2));
					out.setRoom(m0.group(3));
					out.setType(STUDYTYPE.forName(m0.group(1)));
					studies.add(out);
				}
			}

			Pattern p2 = Pattern.compile(Regulars.studiesNumberPattern, Pattern.DOTALL);
			Matcher m2 = p2.matcher(m.group(2));

			while (m2.find()) { // Finding study id

				String patternToUse;
				switch (week) {
				case EVEN:
					patternToUse = Regulars.evenStudyPattern;
					break;
				case ODD:
					patternToUse = Regulars.oddStudyPattern;
					break;
				default:
					patternToUse = Regulars.bothStudyPattern;
					break;
				}
				// Now parse Odd or Even study or Both
				Pattern p3 = Pattern.compile(patternToUse, Pattern.DOTALL);

				Matcher m3 = p3.matcher(m2.group(6));

				while (m3.find()) { // Finding actual study

					// Find first study, parse time, and study number

					DateTime timeStart = new DateTime();
					timeStart = timeStart.hourOfDay().setCopy(m2.group(2));
					timeStart = timeStart.minuteOfHour().setCopy(m2.group(3));

					DateTime timeFinish = new DateTime();
					timeFinish = timeFinish.hourOfDay().setCopy(m2.group(4));
					timeFinish = timeFinish.minuteOfHour().setCopy(m2.group(5));

					Study study = new Study(m2.group(1), timeStart, timeFinish);

					// Setting study type, study name and study room

					study.setWeekType(STUDYWEEK.forName(m3.group(1))); // if studies has no type null here
					study.setType(STUDYTYPE.forName(m3.group(2)));
					study.setName(m3.group(3));
					study.setRoom(m3.group(4));
					// Time to parse teachers for this study

					Pattern p4 = Pattern.compile(Regulars.teachersPattern, Pattern.DOTALL);
					Matcher m4 = p4.matcher(m3.group(5));

					while (m4.find()) {

						study.addTeacher(m4.group(1));

					}

					// Time to parse groups for this study
					Pattern p5 = Pattern.compile(Regulars.groupsPattern, Pattern.DOTALL);
					Matcher m5 = p5.matcher(m3.group(6));

					while (m5.find()) {
						study.addGroup(m5.group(1));
					}

					// We're good to add this study to our studies list and
					// going to parse next study for this day
					studies.add(study);

				}

			}

		}

	}
}
