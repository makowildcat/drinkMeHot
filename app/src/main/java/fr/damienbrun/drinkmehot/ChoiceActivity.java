package fr.damienbrun.drinkmehot;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import fr.damienbrun.drinkmehot.FragmentCity.onPostExecutedListened;
import fr.damienbrun.drinkmehot.FragmentNewYork.OnUpdateProgress;
import fr.damienbrun.drinkmehot.adapter.CoffeeDbAdapter;
import fr.damienbrun.drinkmehot.adapter.ViewPagerAdapter;

public class ChoiceActivity extends FragmentActivity implements
		onPostExecutedListened, OnUpdateProgress {

	private static String TAG = "ChoiceActivity";

	private AnimationDrawable coffeeAnim;
	
	private PagerAdapter mPagerAdapter;
	private ViewPager mViewPager;

	private TextView txtInit;
	private ImageButton btnInit;

	private List<ImageButton> circles;
	private List<FragmentCity> fragments;
	private int mCurrentPosition;
	private int mPositionChoice;

	public static String KEY_CITY_ID = "kCityId";
	public static String PREF_P = "pOneCoffee";

	public static String KEY_NBCOFFEE_PARIS = "kNbCP";

	private CoffeeDbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice);

		// Add fragments
		fragments = new Vector<FragmentCity>();
		fragments.add(FragmentParis.newInstance());
		fragments.add(FragmentNewYork.newInstance());

		mPagerAdapter = new ViewPagerAdapter(super.getSupportFragmentManager(),
				fragments);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setAdapter(mPagerAdapter);

		txtInit = (TextView) findViewById(R.id.txtInit);
		btnInit = (ImageButton) findViewById(R.id.imgButtonInit);

		circles = new ArrayList<ImageButton>();
		circles.add((ImageButton) findViewById(R.id.circle_pos0));
		circles.add((ImageButton) findViewById(R.id.circle_pos1));

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						circles.get(mCurrentPosition).setBackgroundResource(
								R.drawable.circle_unselected);
						circles.get(position).setBackgroundResource(
								R.drawable.circle_selected);
						mCurrentPosition = position;
					}
				});

		mViewPager.setClickable(false);
		mViewPager.setEnabled(false);

		dbHelper = new CoffeeDbAdapter(this);
		dbHelper.open();

		// mViewPager.setCurrentItem(1);
		// Handler delayHandler = new Handler();
		// Runnable r = new Runnable() {
		// @Override
		// public void run() {
		// // Call this method after 1000 milliseconds
		// mViewPager.setCurrentItem(0);
		// }
		// };
		// delayHandler.postDelayed(r, 1000);
	}

	public void choiceClick(View v) {
		btnInit.setClickable(false);
		btnInit.setEnabled(false);
		mPositionChoice = mCurrentPosition;
		SharedPreferences sharedPref = getSharedPreferences(
				ChoiceActivity.PREF_P, Context.MODE_PRIVATE);
		String lastDate = sharedPref.getString("TABLE_" + mPositionChoice,
				"null");
		Log.v(TAG, "onPostExecuted > lastDate = " + lastDate);
		if (lastDate == "null") {
			fragments.get(mPositionChoice).onChoosed();
			txtInit.setAlpha(1.0f);
			btnInit.setImageResource(R.drawable.loading);
			coffeeAnim = (AnimationDrawable) btnInit.getDrawable();
			coffeeAnim.start();
		} else {
			onPostExecuted(true);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//super.onRestoreInstanceState(savedInstanceState);
		mCurrentPosition = savedInstanceState.getInt("current");
		mViewPager.setCurrentItem(mCurrentPosition);
	}
	
	@Override
	protected void onSaveInstanceState(final Bundle outState) {
	    // super.onSaveInstanceState(outState);
		outState.putInt("current", mCurrentPosition);
		
	}
	
	public void clickCircle(View v) {
		mViewPager.setCurrentItem(circles.indexOf(v));
	}

	@Override
	public void onPostExecuted(boolean b) {
		Log.v(TAG, "onPostExecuted > b = " + Boolean.toString(b));
		if (b) {

			Time today = new Time(Time.getCurrentTimezone());
			today.setToNow();
			SharedPreferences sharedPref = getSharedPreferences(PREF_P,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(KEY_CITY_ID, mPositionChoice);
			editor.putString("TABLE_" + mPositionChoice, today.format("%Y%m%d"));
			editor.commit();
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_custom,
					(ViewGroup) findViewById(R.id.toast_layout_root));

			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(getResources().getString(R.string.network_failed));

			Toast toast = new Toast(getApplicationContext());
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
			btnInit.setEnabled(true);
			btnInit.setClickable(true);
			txtInit.setAlpha(0.0f);
			coffeeAnim.stop();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbHelper.close();
	}

	@Override
	public void onDataPass(String update) {
		// TODO Auto-generated method stub
		txtInit.setText(update);
	}
}
