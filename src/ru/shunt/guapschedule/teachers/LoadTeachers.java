package ru.shunt.guapschedule.teachers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.shunt.guapschedule.dialogs.OkDialog;
import ru.shunt.guapschedule.internet.MainParser;
import ru.shunt.guapschedule.internet.SecondParser;
import ru.shunt.guapschedule.other.OnTasksCompleted;
import ru.shunt.guapschedule.sdworker.SdWorker;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class LoadTeachers extends Activity implements OnTasksCompleted {

	private ProgressDialog pd;
	private List<String> myList_values;
	private Spinner spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_load_teachers);

		spinner = (Spinner) findViewById(R.id.teachersDownloadSpinner);
		pd = ProgressDialog.show(LoadTeachers.this, "Подождите", "Идет загрузка...", true, false);

		MainParser mp = new MainParser(this);
		mp.execute("http://rasp.guap.ru");

	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.teachersDownloadButton:
			String i = myList_values.get((int) spinner.getSelectedItemId());

			if (!i.isEmpty()) {
				pd = ProgressDialog.show(this, "Подождите", "Идет загрузка...", true, false);

				SecondParser sp = new SecondParser(this);
				sp.execute("http://rasp.guap.ru/?p=" + i);

			} else {
				Toast.makeText(this, "Ничего не выбранно!", Toast.LENGTH_SHORT).show();
			}

			break;
		}

	}

	private void ShowAlertDialog() {

		DialogFragment newFragment = OkDialog.newInstance("Проверьте свое соединение с интернетом!");
		newFragment.show(getFragmentManager(), "dialog");
	}

	@Override
	public void onMainTaskComplete(Document doc) {

		pd.cancel();
		if (doc != null) {

			List<String> myList = new ArrayList<String>();
			myList_values = new ArrayList<String>();
			Element allOptions = doc.select("select").get(1);
			Elements options = allOptions.getElementsByTag("option");
			options.remove(0);

			for (Element option : options) {
				myList.add(option.text());
				myList_values.add(option.attr("value"));
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.others_row, myList);
			adapter.setDropDownViewResource(R.layout.others_row);
			spinner.setAdapter(adapter);

		} else {
			ShowAlertDialog();
		}
	}

	@Override
	public void onSecondTaskComplete(Document doc) {

		String div;
		String filename;
		Boolean result = true;
		pd.cancel();

		if (doc != null) {
			div = doc.select("div.result").toString();
			div = div.replaceAll("<a href=\"", "<a href=\"http://rasp.guap.ru/");

			Element fname = doc.select("h2").first();
			if (fname != null) {
				filename = fname.toString();
				if (filename.indexOf("</h2>") != -1) {
					filename = filename.substring(filename.indexOf("-") + 2, filename.lastIndexOf("-") - 2);

					new SdWorker().writeTeachersFile(filename, div);

				} else {
					result = false;
				}
			} else {
				result = false;
			}
		} else {
			result = false;
		}

		if (result) {

			Toast.makeText(this, "Сохранение завершено!", Toast.LENGTH_SHORT).show();

		} else {
			ShowAlertDialog();

		}

	}

}
