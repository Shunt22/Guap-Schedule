package ru.shunt.guapschedule.sdworker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.shunt.guapschedule.mainobjects.Study;
import ru.shunt.guapschedule.mainobjects.Task;
import ru.shunt.guapschedule.regular.ScheduleParser.WEEK;
import android.annotation.SuppressLint;
import android.os.Environment;

@SuppressLint("UseSparseArrays")
public class SdWorker extends SdWorkerDefault {

	private static final String GROUPSPATH = "/GS/"; // Main path

	private static final String TASKSPATH = "/GS/Tasks"; // Tasks path

	private static final String TEACHERSPATH = "/GS/Teachers"; // Teachers path

	/*
	 * Recursive deleting
	 * 
	 * @param file - File that contains file(or directory) to delete
	 */
	private void delete(File file) {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			// if file, then delete it
			file.delete();
		}
	}

	/*
	 * GROUPS PART
	 */

	/*
	 * Reads list of groups name from SD-card
	 * 
	 * @return list of group names
	 */

	public List<String> readGroups() {
		return readData(GROUPSPATH);
	}

	/*
	 * Deletes group directory
	 * 
	 * @param filename Name of the file
	 */

	public void deleteGroupFile(String fileName) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + GROUPSPATH + fileName);
		delete(path);

	}

	/*
	 * Finds whether selected file is exists or not
	 * Used in widget in case group file for widget can be deleted
	 * 
	 * 
	 * @param fileName - name of the file
	 * 
	 * @return true if yes, false otherwise
	 */
	public boolean searchForGroupFile(String fileName) {
		return searchForFile(fileName, GROUPSPATH);
	}

	/*
	 * Reads serialized Map<Integer,List<Study>> from SD-card for specific group
	 * 
	 * @param filename - Name of the file
	 * 
	 * @param week - week type to define a file
	 * 
	 * @return map with studies
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, List<Study>> readGroupFile(String fileName, WEEK week) {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return new HashMap<Integer, List<Study>>();
		}

		String actualFileName;
		switch (week) {
		case EVEN:
			actualFileName = fileName + "Even";
			break;
		case ODD:
			actualFileName = fileName + "Odd";
			break;
		default:
			actualFileName = fileName + "Both";
			break;
		}
		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + GROUPSPATH + fileName + "/"
				+ actualFileName);

		ObjectInputStream in = null;
		try {

			in = new ObjectInputStream(new FileInputStream(path));
			Map<Integer, List<Study>> returnMap = (Map<Integer, List<Study>>) in.readObject();
			return returnMap;

		} catch (IOException | ClassNotFoundException e) {

			return new HashMap<Integer, List<Study>>();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Just ignore
				}
			}
		}

	}

	/*
	 * Writes whole map of studies to group file
	 * 
	 * @param fileName - name of the file
	 * 
	 * @param dataMap - Integer,List<Study> map with studies data
	 * 
	 * @param week - week type to define a file
	 * 
	 * @return true if OK, false otherwise
	 */

	public boolean writeGroupFile(String fileName, Map<Integer, List<Study>> dataMap, WEEK week) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		String actualFileName;
		switch (week) {
		case EVEN:
			actualFileName = fileName + "Even";
			break;
		case ODD:
			actualFileName = fileName + "Odd";
			break;
		default:
			actualFileName = fileName + "Both";
			break;
		}

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + GROUPSPATH + fileName + "/");

		path.mkdirs();

		path = new File(path, actualFileName);

		ObjectOutputStream out = null;

		try {

			out = new ObjectOutputStream(new FileOutputStream(path));
			out.writeObject(dataMap);
			return true;

		} catch (IOException e) {

			return false;

		} finally {

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// Just ignore
				}
			}
		}

	}

	/*
	 * TEACHERS PART
	 */
	public boolean writeTeachersFile(String filename, String data) {
		return writeFile(filename, data, TEACHERSPATH);
	}

	public String readTeachersFile(String filename) {
		return readFile(filename, TEACHERSPATH);
	}

	public boolean deleteTeacher(String filename) {
		return delete(filename, TEACHERSPATH);
	}

	/*
	 * Reads all tasks from SD-card from SD-cards/sdTasks path
	 * 
	 * @return list of teachers names
	 */
	public List<String> readTeachers() {
		return readData(TEACHERSPATH);
	}

	/*
	 * TASKS PART
	 */

	/*
	 * Writes tasks data
	 * 
	 * @param filename -- name of the file
	 * 
	 * @param task -- Task to write
	 * 
	 * @return true if OK, false otherwise
	 */

	public boolean writeTasksFile(String filename, Task task) {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + TASKSPATH);

		path.mkdirs();
		File sdFile = new File(path, filename);

		ObjectOutputStream out = null;
		try {

			out = new ObjectOutputStream(new FileOutputStream(sdFile));
			out.writeObject(task);
			out.flush();

		} catch (IOException e) {
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// Just ignore
				}
			}
		}
		return true;
	}

	public boolean searchForTaskFile(String filename) {
		return searchForFile(filename, TASKSPATH);
	}

	/*
	 * Deletes tasks file
	 * 
	 * @param filename Name of the file
	 * 
	 * @return true if file has been deleted, false otherwise
	 */

	public boolean deleteTasksFile(String filename) {
		return delete(filename, TASKSPATH);
	}

	public List<Task> readTasksList() {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return new ArrayList<Task>();
		}

		List<Task> tasksList = new ArrayList<Task>();

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + TASKSPATH);

		path.mkdirs();

		for (File s : path.listFiles()) {
			ObjectInputStream in = null;

			try {
				in = new ObjectInputStream(new FileInputStream(s));
				tasksList.add((Task) in.readObject());
				in.close();

			} catch (IOException | ClassNotFoundException e) {

				return new ArrayList<Task>();

			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Just ignore
					}
				}
			}

		}
		return tasksList;
	}

	public Task readTask(String filename) {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return null;
		}

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + TASKSPATH);

		File sdFile = new File(path, filename);

		if (sdFile.exists()) {

			ObjectInputStream in = null;
			try {
				in = new ObjectInputStream(new FileInputStream(sdFile));

				Task task = (Task) in.readObject();

				in.close();
				return task;
			} catch (IOException | ClassNotFoundException e) {
				return null;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Just ignore
					}
				}
			}
		}
		return null;
	}
}
