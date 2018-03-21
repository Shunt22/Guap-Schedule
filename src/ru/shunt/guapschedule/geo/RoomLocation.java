package ru.shunt.guapschedule.geo;

import ru.shunt.guapschedule.main.Main;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shunt.guapschedule.R;

public class RoomLocation extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewHierarchy = inflater.inflate(R.layout.act_room_location, container, false);

		String room = getArguments().getString("roomName");

		String[] temp = room.split(". ");
		if (temp.length > 1) {
			room = temp[1];
			room = room.replace("-", "_");
			room = "_" + room;
			int arrayID = getResources().getIdentifier(room, "array", getActivity().getPackageName());

			if (!temp[0].equals("Б.М")) {
				Toast.makeText(getActivity(), "Извините, карта аудиторий доступна только для Б.М.", Toast.LENGTH_SHORT)
						.show();
				startActivity(new Intent(getActivity(), Main.class));
			}
			if (arrayID > 0) {
				int roomCoords[] = getResources().getIntArray(arrayID);
				draw(roomCoords[0], roomCoords[1], viewHierarchy);
			}
			char hull = temp[1].charAt(0);
			char floor = temp[1].charAt(1);
			String exactRoom = temp[1].substring(temp[1].indexOf("-") + 1);

			StringBuilder sb = new StringBuilder();
			sb.append(hull).append("-ый корпус, ").append(floor).append("-й этаж, ").append(exactRoom)
					.append("-ая аудитория.");

			((TextView) viewHierarchy.findViewById(R.id.roomInfoView)).setText(sb.toString());
		} else {
			((TextView) viewHierarchy.findViewById(R.id.roomInfoView)).setText("Расположение неизвестно");
		}
		return viewHierarchy;
	}

	private void draw(int x, int y, View view) {

		BitmapFactory.Options myOptions = new BitmapFactory.Options();
		myOptions.inDither = true;
		myOptions.inScaled = false;
		myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
		myOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shem_bm, myOptions);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);

		Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
		Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

		Canvas canvas = new Canvas(mutableBitmap);
		canvas.drawCircle(x, y, 8, paint);

		ImageView imageView = (ImageView) view.findViewById(R.id.roomsGeoImage);
		imageView.setAdjustViewBounds(true);
		imageView.setImageBitmap(mutableBitmap);
	}
}
