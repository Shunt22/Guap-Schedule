package ru.shunt.guapschedule.newschedule;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.shunt.guapschedule.dialogs.OkDialog;
import ru.shunt.guapschedule.internet.MainParser;
import ru.shunt.guapschedule.internet.SecondParser;
import ru.shunt.guapschedule.main.Main;
import ru.shunt.guapschedule.other.OkDialogActivity;
import ru.shunt.guapschedule.other.OnTasksCompleted;
import ru.shunt.guapschedule.regular.ScheduleParser;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class LoadNewSchedule extends Activity implements OnTasksCompleted, OkDialogActivity {

	private Spinner spinner;
	private List<String> myList_values;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_load_new_schedule);

		spinner = (Spinner) findViewById(R.id.downloadSpinner);

		pd = ProgressDialog.show(LoadNewSchedule.this, "Подождите", "Идет загрузка...", true, false);

		MainParser mp = new MainParser(this);
		mp.execute("http://rasp.guap.ru");

	}

	private void ShowAlertDialog() {

		DialogFragment newFragment = OkDialog.newInstance("Проверьте свое соединение с интернетом!");
		newFragment.show(getFragmentManager(), "dialog");
	}

	public void onClick(View V) {

		switch (V.getId()) {

		case R.id.scheduleDownloadButton:

			String i = myList_values.get((int) spinner.getSelectedItemId());

			if (!i.isEmpty() || i == null) {
				pd = ProgressDialog.show(LoadNewSchedule.this, "Подождите", "Идет загрузка...", true, false);

				SecondParser sp = new SecondParser(this);
				sp.execute("http://rasp.guap.ru/?g=" + i);

			} else {
				Toast.makeText(LoadNewSchedule.this, "Ничего не выбранно!", Toast.LENGTH_SHORT).show();
			}

			break;
		}
	}

	@Override
	public void onMainTaskComplete(Document doc) {

		pd.cancel();
		if (doc != null) {

			List<String> myList = new ArrayList<String>();
			myList_values = new ArrayList<String>();

			Element allOptions = doc.select("select").first();
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

			Element fname = doc.select("h2").first();
			if (fname != null) {
				filename = fname.toString();
				if (filename.indexOf("</h2>") != -1) {
					filename = filename.substring(filename.lastIndexOf(" ") + 1, filename.indexOf("</h2>"));

					new ScheduleParser(div, filename);

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

			Toast.makeText(LoadNewSchedule.this, "Сохранение завершено!", Toast.LENGTH_SHORT).show();

		} else {
			ShowAlertDialog();

		}

	}

	/*
	 * This is called from OkDialog class
	 */
	@Override
	public void doOkClick() {

		startActivity(new Intent(this, Main.class));

	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, Main.class));
	}

}
