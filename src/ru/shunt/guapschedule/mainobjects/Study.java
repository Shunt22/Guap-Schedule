package ru.shunt.guapschedule.mainobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import android.graphics.Color;

import com.shunt.guapschedule.R;

public class Study implements Serializable, Comparable<Study> {

	/*
	 * Study type enum
	 * The constructor is name(type), color(background in listview), picture(drawable in listview)
	 */
	public enum STUDYTYPE {

		LAB("ЛР", "#f2dede", R.drawable.lab), LECTURE("Л", "#dff0d8", R.drawable.lecture), PRACTICE("ПР", "#d9edf7",
				R.drawable.practice), COURSEPROJECT("КР", "#fcf8e3", R.drawable.cwork), COURSEWORK("КП", "#428bca",
				R.drawable.cproject);

		private String type;
		private String color;
		private int icon;

		private static final Map<String, STUDYTYPE> nameToValueMap = new HashMap<String, STUDYTYPE>();

		static {
			for (STUDYTYPE value : EnumSet.allOf(STUDYTYPE.class)) {
				nameToValueMap.put(value.toString(), value);
			}
		}

		public int getDrawableId() {
			return icon;
		}

		public int getColor() {
			return Color.parseColor(color);
		}

		public static STUDYTYPE forName(String name) {
			/*
			 * Returns enum type for selected string name from parsing
			 */
			return nameToValueMap.get(name);
		}

		private STUDYTYPE(String type, String color, int icon) {
			this.type = type;
			this.color = color;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return type;
		}
	}

	/*
	 * Study week type. Weeks can be even(upper), odd(down), or both
	 */
	public enum STUDYWEEK {
		UP("up", "▲"), DN("dn", "▼"), BOTH(null, "");

		private static final Map<String, STUDYWEEK> weekTypeToValueMap = new HashMap<String, STUDYWEEK>();
		static {
			for (STUDYWEEK value : EnumSet.allOf(STUDYWEEK.class)) {
				weekTypeToValueMap.put(value.toString(), value);
			}
		}

		public static STUDYWEEK forName(String name) {
			/*
			 * Returns enum type for selected string name from parsing
			 */
			return weekTypeToValueMap.get(name);
		}

		private String studyWeekType;
		private String drawable;

		private STUDYWEEK(String type, String drawable) {
			this.studyWeekType = type;
			this.drawable = drawable;
		}

		public String getDrawable() {
			return drawable;
		}

		@Override
		public String toString() {
			return studyWeekType;
		}
	}

	private static final long serialVersionUID = 1213656109791288229L;

	private final int number; // Index number for study (e.g. 1, 2, ...)
	private final DateTime timeStart; // Time, when study starts
	private final DateTime timeFinish; // Time, when study finishes
	private STUDYWEEK weekType; // Week type for study
	private String name; // Study name(name of subject)
	private STUDYTYPE type; // Study type (e.g. Lecture, Course work, ...)
	private String room; // Room, where study passes
	private List<String> teachers = new ArrayList<String>(); // List of teachers teaching this study
	private List<String> groups = new ArrayList<String>(); // List of groups which are taught on this study

	public Study(String id, DateTime timeStart, DateTime timeFinish) {

		this.number = Integer.valueOf(id);
		this.timeStart = timeStart;
		this.timeFinish = timeFinish;
	}

	public int getNumber() {
		return number;
	}

	public DateTime getTimeStart() {
		return timeStart;
	}

	public DateTime getTimeFinish() {
		return timeFinish;
	}

	public void setName(String studyName) {
		this.name = studyName;
	}

	public STUDYWEEK getWeekType() {
		return weekType;
	}

	public void setWeekType(STUDYWEEK studyWeekType) {
		this.weekType = studyWeekType;
	}

	public void setType(STUDYTYPE type) {
		this.type = type;
	}

	public STUDYTYPE getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public List<String> getTeachers() {
		return teachers;
	}

	public void addTeacher(String teacher) {
		this.teachers.add(teacher);
	}

	public List<String> getGroups() {
		return groups;
	}

	public void addGroup(String group) {
		this.groups.add(group);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(number).append(" ").append(name);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		result = prime * result + number;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((weekType == null) ? 0 : weekType.hashCode());
		result = prime * result + ((timeFinish == null) ? 0 : timeFinish.hashCode());
		result = prime * result + ((timeStart == null) ? 0 : timeStart.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Study other = (Study) obj;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		if (number != other.number)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (weekType != other.weekType)
			return false;
		if (timeFinish == null) {
			if (other.timeFinish != null)
				return false;
		} else if (!timeFinish.equals(other.timeFinish))
			return false;
		if (timeStart == null) {
			if (other.timeStart != null)
				return false;
		} else if (!timeStart.equals(other.timeStart))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public int compareTo(Study study) {
		return number - study.number;
	}

}
