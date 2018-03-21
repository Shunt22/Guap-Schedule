package ru.shunt.guapschedule.main;

import java.util.ArrayList;
import java.util.List;

import ru.shunt.guapschedule.about.About;
import ru.shunt.guapschedule.alarms.AlarmSetter;
import ru.shunt.guapschedule.day.ShowDay;
import ru.shunt.guapschedule.geo.Coords;
import ru.shunt.guapschedule.internet.ConnectionManager;
import ru.shunt.guapschedule.mainobjects.HeaderItem;
import ru.shunt.guapschedule.mainobjects.ListItem;
import ru.shunt.guapschedule.mainobjects.NavigationItem;
import ru.shunt.guapschedule.newschedule.LoadNewSchedule;
import ru.shunt.guapschedule.schedulechanger.Changer;
import ru.shunt.guapschedule.schedulechanger.GroupDeleter;
import ru.shunt.guapschedule.schedulechanger.TeacherDeleter;
import ru.shunt.guapschedule.tasks.TasksList;
import ru.shunt.guapschedule.teachers.LoadTeachers;
import ru.shunt.guapschedule.teachers.TeachersList;
import ru.shunt.guapschedule.week.FullWeek;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.shunt.guapschedule.R;

/**
 * Main activity of the application.
 * Has navigation drawer and fragment place holder in layout.
 * Sets alarms when application is started
 *  
 */
public class Main extends Activity {

	private DrawerLayout mDrawerLayout; // Siding menu
	private ListView mDrawerList; // List of menu items in Drawer
	private ActionBarDrawerToggle mDrawerToggle; // Slide - listener for Drawer
	private CharSequence mDrawerTitle; // Title, when Drawer is open. Variable for saving main screen title
	private CharSequence mTitle; // Title, when Drawer is closed
	private int mSavedFragment;
	/*
	 * Items for ListView
	 */
	private final static List<NavigationItem> navItemsList = new ArrayList<NavigationItem>();
	static {
		// Main
		navItemsList.add(new ListItem("Главная", R.drawable.ic_menu_cc_am, MainFragment.class, new Bundle()));
		
		// Studnets
		navItemsList.add(new HeaderItem("Студенты"));
		Bundle args = new Bundle();
		args.putBoolean("tomorrow", false);
		navItemsList.add(new ListItem("Сегодня", R.drawable.ic_menu_day, ShowDay.class, args));

		args = new Bundle();
		args.putBoolean("tomorrow", true);
		navItemsList.add(new ListItem("Завтра", R.drawable.ic_menu_day, ShowDay.class, args));

		navItemsList.add(new ListItem("Вся неделя", R.drawable.ic_menu_week, FullWeek.class, new Bundle()));
		navItemsList.add(new ListItem("Задания", R.drawable.act_students_menu_tasks, TasksList.class, new Bundle()));

		// Teachers
		navItemsList.add(new HeaderItem("Преподаватели"));
		navItemsList.add(new ListItem("Список", R.drawable.ic_menu_friendslist, TeachersList.class, new Bundle()));
		
		// Schedule editor
		navItemsList.add(new HeaderItem("Редактор расписания"));
		navItemsList.add(new ListItem("Добавление занятий", R.drawable.ic_menu_edit, Changer.class, new Bundle()));
		navItemsList.add(new ListItem("!!! Удаление занятий !!!", R.drawable.ic_menu_edit, MainFragment.class,
				new Bundle()));
		navItemsList.add(new ListItem("Удаление групп", R.drawable.ic_menu_delete, GroupDeleter.class, new Bundle()));
		navItemsList.add(new ListItem("Удаление преподавателей", R.drawable.ic_menu_delete, TeacherDeleter.class,
				new Bundle()));

		// Other
		navItemsList.add(new HeaderItem("Прочее"));
		navItemsList.add(new ListItem("Местоположение корпусов", R.drawable.ic_menu_mylocation, Coords.class,
				new Bundle()));
		navItemsList.add(new ListItem("Настройки", R.drawable.ic_menu_preferences, Preference.class, new Bundle()));
		navItemsList.add(new ListItem("О программе", R.drawable.ic_menu_info_details, About.class, new Bundle()));

	}

	private static long mBackPressed; // For double-click exiting

	/**
	 * 
	 * ListView in Navigation Drawer consists of two types of view
	 * Header is non-clickable, just for showing section headers
	 * List Item is clickable. Loads specific content when user clicks on it.
	 * 
	 */
	public enum RowType {
		HEADER_ITEM, LIST_ITEM
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nagivation_drawer);

		/*
		 * Just to make sure all alarms have been set(or deleted).
		 */
		new AlarmSetter().setAlarm(this.getApplicationContext());

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new NavigationAdapter(this, navItemsList));

		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mTitle = mDrawerTitle = getTitle(); // Saving current(main) title

		/*
		 * Toggle listener
		 */
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.openDrawer,
				R.string.closeDrawer) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		/*
		 * When application is started we need to load main fragment
		 */
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
		} else {

			NavigationItem navItem = navItemsList.get(savedInstanceState.getInt("fragment"));
			if (navItem instanceof ListItem) {

				mSavedFragment = savedInstanceState.getInt("fragment");
				ListItem myItem = (ListItem) navItem;

				Fragment fragment = Fragment.instantiate(getApplicationContext(), myItem.getFragmentClass().getName(),
						myItem.getFragmentArgs());

				setTitle(myItem.getName());

				getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

			}

		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Adapter that is teaching ListView how to show items
	 */
	private class NavigationAdapter extends ArrayAdapter<NavigationItem> {

		public NavigationAdapter(Context context, List<NavigationItem> itemsList) {

			super(context, R.layout.navigation_row, itemsList);
		}

		@Override
		public int getViewTypeCount() {
			return RowType.values().length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getItem(position).getView(LayoutInflater.from(getContext()), convertView);
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).getViewType();
		}

		@Override
		public boolean isEnabled(int position) {
			return (getItem(position) instanceof ListItem);
		}
	}

	/**
	 * On click listener for list view
	 * 
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			mSavedFragment = position;

			getActionBar().setDisplayShowTitleEnabled(true);
			getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

			FragmentManager fragmentManager = getFragmentManager();

			NavigationItem navItem = navItemsList.get(position);

			if (navItem instanceof ListItem) {

				ListItem myItem = (ListItem) navItem;

				if (myItem.getFragmentClass() == About.class) {
					new About().show(getFragmentManager(), "dialog");
					return;
				}

				Fragment fragment = Fragment.instantiate(getApplicationContext(), myItem.getFragmentClass().getName(),
						myItem.getFragmentArgs());

				// Insert the fragment by replacing any existing fragment
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

				// Highlight the selected item, update the title, and close the drawer
				mDrawerList.setItemChecked(position, true);

				setTitle(myItem.getName()); // Setting title

			}
			mDrawerLayout.closeDrawer(mDrawerList);

		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("fragment", mSavedFragment);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/*
	 * Exiting
	 */
	@Override
	public void onBackPressed() {

		// By default backPressed is 0

		if (mBackPressed + 2000 > System.currentTimeMillis()) {

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME); // Opens home-screen
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Clears activities stack
			startActivity(intent);

		} else {

			Toast.makeText(this, "Нажмите еще раз, что бы выйти", Toast.LENGTH_SHORT).show();
			mBackPressed = System.currentTimeMillis();
		}

	}

	/*
	 * From preferences screen
	 */
	public void onClick(View v) {
		if (!new ConnectionManager().checkInternetConnection((ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE))) {
			Toast.makeText(this, "Ошибка подключения! Проверьте свое соединение с интернетом!", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		switch (v.getId()) {

		case R.id.download_groups:

			startActivity(new Intent(this, LoadNewSchedule.class));

			break;

		case R.id.download_teachers:

			startActivity(new Intent(this, LoadTeachers.class));

			break;

		}
	}

}
