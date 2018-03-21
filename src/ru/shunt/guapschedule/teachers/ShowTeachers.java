package ru.shunt.guapschedule.teachers;

import ru.shunt.guapschedule.sdworker.SdWorker;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shunt.guapschedule.R;

public class ShowTeachers extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewHierarchy = inflater.inflate(R.layout.act_show_teachers, container, false);

		getActivity().setTitle("Просмотр преподавателя");
		
		TextView text = (TextView) viewHierarchy.findViewById(R.id.teachersInfoView);
		text.setMovementMethod(LinkMovementMethod.getInstance());

		String rasp = new SdWorker().readTeachersFile(getArguments().getString("teacherName"));

		String answer = "Ничего не найдено!";
		if (rasp.indexOf("</div>") != -1)
			answer = rasp.substring(rasp.indexOf("</div>"));

		text.setText(Html.fromHtml(answer));

		return viewHierarchy;
	}
}