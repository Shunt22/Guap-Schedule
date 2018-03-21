package ru.shunt.guapschedule.regular;

public class Regulars {

	/*
	 * This class is only holder for regular expressions
	 */

	/*
	 * Pattern for days Days are: <h3>Monday</h3>, etc
	 * Group(1) - day name ( e.g Monday, Tuesday, etc )
	 * Group(2) - rest of div before next day
	 */
	public final static String daysPattern = "<h3>(.*?)</h3>(.*?)(?=<h3>|$)";

	/*
	 * Pattern for studies number and time
	 * Group(1) - study number ( 1,2,3,4,5, etc )
	 * Group(2,3,4,5) - study time ( e.g 09:00-10:30 )
	 * Group(6) - rest of div
	 */
	public final static String studiesNumberPattern = "<h4>(.+?).*?\\((.*?):(.*?)-(.*?):(.*?)\\)</h4>(.*?)(?=<h4>|$)";

	/*
	 * Odd studies ( upper, red week )
	 * Can be also used for both-week studies
	 * Using negative lookahead (?! span = dn )
	 * Group(1) - study week type ( if study has it, otherwise null )
	 * Group(2) - study type
	 * Group(2) - study name
	 * Group(3) - study room
	 * Group(4) - teachers
	 * Group(5) - groups
	 */
	public final static String oddStudyPattern = "<div class=\"study\">\\s*?<span>(?!<b class=\"dn\".*?</b>).*?(?:<b class=\"(up)\")*.*?<b>(.*?)</b>.*? – (.*?) <em> – (.*?)</em>.*?<span class=\"preps\">(.*?)</span>.*?<span class=\"groups\">(.*?)</span>";

	/*
	 * Even studies ( lower, blue week)
	 * Can be also used for both-week studies
	 * Almost same as Odd studies
	 * Negative lookahead (?! span = up )
	 */
	public final static String evenStudyPattern = "<div class=\"study\">\\s*?<span>(?!<b class=\"up\".*?</b>).*?(?:<b class=\"(dn)\")*.*?<b>(.*?)</b>.*? – (.*?) <em> – (.*?)</em>.*?<span class=\"preps\">(.*?)</span>.*?<span class=\"groups\">(.*?)</span>";

	/*
	 * Both studies ( includes lower and upper weeks )
	 */

	public final static String bothStudyPattern = "<div class=\"study\">\\s*?<span>(?:<b class=\"(up|dn)\")*.*?<b>(.*?)</b>.*? – (.*?) <em> – (.*?)</em>.*?<span class=\"preps\">(.*?)</span>.*?<span class=\"groups\">(.*?)</span>";

	/*
	 * Teachers pattern
	 * Group(1) - teacher name
	 * Group(2) - rest of div
	 */
	public final static String teachersPattern = "<a href=\".*?\\?p.*?>(.*?)(?=</a>|$)";

	/*
	 * Groups pattern
	 * Almost same as Teachers Pattern
	 * Group(1) - group name
	 * End of day div
	 */
	public final static String groupsPattern = "<a href=\".*?\\?g.*?>(.*?)(?=</a>|$)";

	/*
	 * This is used if Scheduler Parser finds out of schedule study(we should parse it in different way)
	 */
	public final static String outOfSchedule = "<div class=\"study\">.*?<b>(.*?)</b> – (.*?) <em> – (.*?)</em>";
}
