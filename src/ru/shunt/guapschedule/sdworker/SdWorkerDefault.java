package ru.shunt.guapschedule.sdworker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Environment;

abstract class SdWorkerDefault {

	private class DirFilter implements FilenameFilter {

		@Override
		public boolean accept(File arg0, String name) {
			if (name.equals("Tasks") || name.equals("Teachers")) {
				return false;
			}
			return true;
		}

	}

	protected List<String> readData(String dirPath) {
		// Return nothing if no SD-card
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return new ArrayList<String>();
		}

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dirPath);

		if (!path.isDirectory()) {
			path.mkdirs();
		}

		return Arrays.asList(path.list(new DirFilter()));
	}

	protected boolean delete(String filename, String dirPath) {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dirPath);

		File sdFile = new File(path, filename);

		return sdFile.delete();

	}

	protected String readFile(String filename, String dirPath) {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		String read;

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dirPath);

		File sdFile = new File(path, filename);
		try {

			BufferedReader br = new BufferedReader(new FileReader(sdFile));
			while ((read = br.readLine()) != null) {
				sb.append(read);

			}

			br.close();

		} catch (Exception e) {
			return null;
		}
		return sb.toString();

	}

	protected boolean writeFile(String filename, String data, String dirPath) {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dirPath);

		path.mkdirs();
		File sdFile = new File(path, filename);

		try {

			FileWriter bw = new FileWriter(sdFile);

			bw.write(data);
			bw.close();

		} catch (IOException e) {
			return false;
		}
		return true;
	}

	protected boolean searchForFile(String filename, String dirPath) {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dirPath);

		File sdFile = new File(path, filename);

		return sdFile.exists();
	}

}
