package fr.damienbrun.drinkmehot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import fr.damienbrun.drinkmehot.FragmentSearch.OnItemSearchSelectedListener;
import fr.damienbrun.drinkmehot.MarkerDialog.MarkerDialogListener;
import fr.damienbrun.drinkmehot.adapter.CafeInfoWindowAdapter;
import fr.damienbrun.drinkmehot.adapter.CoffeeDbAdapter;
import fr.damienbrun.drinkmehot.adapter.DrawerListAdapter;

public class MainActivity extends FragmentActivity implements
		OnItemSearchSelectedListener, OnMarkerClickListener,
		MarkerDialogListener {

	private static String TAG = "MainActivity";

	private Fragment fragmentSearch;
	private Fragment fragmentFavorite;
	private FragmentNearby fragmentNearby;
	private FrameLayout contentFrame;
	private Fragment fragmentAbout;

	public static String KEY_NBCOFFEE_PARIS = "nbcoffeeParis";

	private DrawerLayout drawerLayout;
	private ListView drawerListView;

	// private LocationListener locationListener;

	private View viewOverlay;
	private ImageView imgMenu;
	private ImageButton btnMenu;
	private Button btnNext;
	private Button btnPrevious;

	private int mAction;
	private boolean mMenu;

	private GoogleMap mMap;
	private float initZoom;
	private double initLatitude;
	private double initLongitude;
	private Location mLocation;

	private LocationManager lm;

	private int cityId;

	private static String STATE_ACTION = "sAction";
	private static String STATE_MENU = "sMenu";
	private static String STATE_OFFSET_NEARBY = "sOffsetNearby";
	private static String STATE_ZOOM = "sZoom";
	private static String STATE_LATITUDE = "sLat";
	private static String STATE_LONGITUDE = "sLong";

	private ArrayList<Integer> listOrder = new ArrayList<Integer>();
	private ArrayList<Marker> listMarker = new ArrayList<Marker>();
	private int[] listFavorite;
	private int offsetNearby;
	private int nbCoffee;

	private CoffeeDbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");

		// ---------------
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * before everything we check if we got a city selected
		 */
		SharedPreferences sharedPref = getSharedPreferences(
				ChoiceActivity.PREF_P, Context.MODE_PRIVATE);
		cityId = sharedPref.getInt(ChoiceActivity.KEY_CITY_ID, -1);
		if (cityId == -1) {
			Intent intent = new Intent(this, ChoiceActivity.class);
			startActivity(intent);
			finish();
		} else {
			if (cityId == 0) {
				CoffeeDbAdapter.setTableName("Paris");
			}
			if (cityId == 1) {
				CoffeeDbAdapter.setTableName("NewYork");
			}

			/*
			 * get all view and other stuff like that
			 */
			contentFrame = (FrameLayout) findViewById(R.id.content_frame);
			viewOverlay = (View) findViewById(R.id.viewOverlay);
			drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			drawerListView = (ListView) findViewById(R.id.left_drawer);
			imgMenu = (ImageView) findViewById(R.id.imgMenu);
			btnMenu = (ImageButton) findViewById(R.id.btnMenu);
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.googleMap)).getMap();
			btnNext = (Button) findViewById(R.id.buttonNext);
			btnPrevious = (Button) findViewById(R.id.buttonPrevious);

			/*
			 * few settings/options
			 */
			drawerLayout.setScrimColor(Color.TRANSPARENT);
			int[] icon = new int[] { R.drawable.ic_action_location_found,
					R.drawable.ic_action_search, R.drawable.ic_action_favorite,
					R.drawable.ic_action_web_site, R.drawable.ic_action_about };
			DrawerListAdapter drawerListAdapter = new DrawerListAdapter(
					MainActivity.this, getResources().getStringArray(
							R.array.items_title), icon);
			drawerListView.setAdapter(drawerListAdapter);

			mMap.setMyLocationEnabled(true);
			mMap.getUiSettings().setMyLocationButtonEnabled(false);
			mMap.getUiSettings().setZoomControlsEnabled(false);
			mMap.getUiSettings().setCompassEnabled(false);
			mMap.setInfoWindowAdapter(new CafeInfoWindowAdapter(
					getLayoutInflater()));
			
			mMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng arg0) {
					Log.v(TAG, Double.toString(arg0.latitude) + ":" + Double.toString(arg0.longitude));
				}
			});
			
			mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
				
				@Override
				public void onMapLongClick(LatLng arg0) {
					Log.v(TAG, "LongClic > " + Double.toString(arg0.latitude) + ":" + Double.toString(arg0.longitude));
				}
			});

			lm = (LocationManager) getApplicationContext().getSystemService(
					Context.LOCATION_SERVICE);

			mLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			if (mLocation == null) {
				mLocation = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			fragmentSearch = FragmentSearch.newInstance(true);
			fragmentFavorite = FragmentSearch.newInstance(false);
			fragmentAbout = FragmentAbout.newInstance();
			fragmentNearby = new FragmentNearby();

			/*
			 * few listener
			 */
			drawerLayout.setDrawerListener(new DrawerListener());
			drawerListView
					.setOnItemClickListener(new DrawerItemClickListener());

			dbHelper = new CoffeeDbAdapter(this);
			dbHelper.open();
			Cursor cursor;
			if (mLocation == null) {
				cursor = dbHelper.fetchAllCoffee();
				// Toast toast = Toast.makeText(getApplicationContext(),
				// getResources().getString(R.string.location_failed),
				// Toast.LENGTH_LONG);
				// toast.show();

				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.toast_custom,
						(ViewGroup) findViewById(R.id.toast_layout_root));

				TextView text = (TextView) layout.findViewById(R.id.toast_text);
				text.setText(getResources().getString(R.string.location_failed));

				Toast toast = new Toast(getApplicationContext());
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(layout);
				toast.show();

			} else {
				cursor = dbHelper.fetchAllByDistance(mLocation); // ASYNC
			}
			nbCoffee = cursor.getCount();
			listFavorite = new int[nbCoffee];
			MarkerOptions coffeeMarker = new MarkerOptions();
			if (cursor.moveToFirst()) {
				do {
					coffeeMarker
							.position(
									new LatLng(cursor.getDouble(4), cursor
											.getDouble(5)))
							.title(cursor.getString(1))
							.snippet(
									cursor.getString(2) + " "
											+ cursor.getInt(3));

					if (cursor.getInt(6) == 0) {
						coffeeMarker.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_markup_coffee));
					} else {
						coffeeMarker
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.ic_markup_favorite_coffee));
					}
					
					//coffeeMarker.visible(false);
					listMarker.add(mMap.addMarker(coffeeMarker));
					listOrder.add(cursor.getInt(0));
					listFavorite[cursor.getPosition()] = cursor.getInt(6);
				} while (cursor.moveToNext());
			}
			dbHelper.close();

			mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					DialogFragment newFragment = MarkerDialog.newInstance(
							marker.getPosition().latitude,
							marker.getPosition().longitude);
					newFragment.show(getSupportFragmentManager(), "dialog");
				}
			});

			mMap.setOnMarkerClickListener(this);
			mAction = -1;
			mMenu = false;
			imgMenu.setImageResource(R.drawable.ic_logo_coffee);
			offsetNearby = -1;
			handleCity();
			if (savedInstanceState == null) {
				drawerLayout.openDrawer(Gravity.LEFT);
				viewOverlay.setAlpha(1.0f);
				mMenu = true;
				Handler delayHandler = new Handler();
				Runnable r = new Runnable() {
					@Override
					public void run() {
						// Call this method after 1000 milliseconds
						drawerLayout.closeDrawer(Gravity.LEFT);
					}
				};
				delayHandler.postDelayed(r, 1200);
			}

		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.v(TAG, "onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);

		mAction = savedInstanceState.getInt(STATE_ACTION);
		mMenu = savedInstanceState.getBoolean(STATE_MENU);
		offsetNearby = savedInstanceState.getInt(STATE_OFFSET_NEARBY);
		initZoom = savedInstanceState.getFloat(STATE_ZOOM);
		initLatitude = savedInstanceState.getDouble(STATE_LATITUDE);
		initLongitude = savedInstanceState.getDouble(STATE_LONGITUDE);
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "onResume");
		super.onResume();
		handleAnimation();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SharedPreferences sharedPref = getSharedPreferences(TAG,
				Context.MODE_PRIVATE);
		initZoom = sharedPref.getFloat(STATE_ZOOM, initZoom);
		initLatitude = Double.longBitsToDouble(sharedPref.getLong(
				STATE_LATITUDE, Double.doubleToLongBits(initLatitude)));
		initLongitude = Double.longBitsToDouble(sharedPref.getLong(
				STATE_LONGITUDE, Double.doubleToLongBits(initLongitude)));
	}

	@Override
	protected void onStop() {
		SharedPreferences sharedPref = getSharedPreferences(TAG,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putFloat(STATE_ZOOM, mMap.getCameraPosition().zoom);
		editor.putLong(STATE_LATITUDE, Double.doubleToRawLongBits(mMap
				.getCameraPosition().target.latitude));
		editor.putLong(STATE_LONGITUDE, Double.doubleToRawLongBits(mMap
				.getCameraPosition().target.longitude));
		editor.commit();
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);

		outState.putInt(STATE_ACTION, mAction);
		outState.putBoolean(STATE_MENU, mMenu);
		outState.putInt(STATE_OFFSET_NEARBY, offsetNearby);
		outState.putFloat(STATE_ZOOM, mMap.getCameraPosition().zoom);
		outState.putDouble(STATE_LATITUDE,
				mMap.getCameraPosition().target.latitude);
		outState.putDouble(STATE_LONGITUDE,
				mMap.getCameraPosition().target.longitude);
	}

	private void handleCity() {
		switch (cityId) {
		case 0:
			// and move camera to Paris
			initZoom = 11.8f;
			initLatitude = 48.8588589;
			initLongitude = 2.3470599;
			break;
		case 1:
			initZoom = 11.8f;
			initLatitude = 40.7056308;
			initLongitude = -73.9780035;
			break;
		}
	}

	private void handleAnimation() {
		if (mMenu) { // menu
			btnNext.setAlpha(0.0f);
			btnPrevious.setAlpha(0.0f);
			drawerLayout.openDrawer(Gravity.LEFT);
			viewOverlay.setAlpha(1.0f);
			btnMenu.setAlpha(1.0f);
			imgMenu.setAlpha(0.0f);
			contentFrame.setAlpha(0.0f);
			mAction = -1;
		} else {
			if (mAction == 0) {
				btnNext.setAlpha(1.0f);
				btnPrevious.setAlpha(1.0f);
				btnNext.setClickable(true);
				btnPrevious.setClickable(true);
			}
			if (mAction > 0) {
				viewOverlay.setAlpha(1.0f);
			}
		}
		// handleAction();
		handleIconMenu();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				initLatitude, initLongitude), initZoom));
		if (offsetNearby > -1) {
			listMarker.get(offsetNearby).showInfoWindow();
		}
	}

	private void handleAction() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		switch (mAction) {
		case -1:
			fragmentTransaction.replace(R.id.content_frame, fragmentNearby);
			break;
		case 0:
			fragmentTransaction.replace(R.id.content_frame, fragmentNearby);
			break;
		case 1:
			fragmentTransaction.replace(R.id.content_frame, fragmentSearch);
			break;
		case 2:
			fragmentTransaction.replace(R.id.content_frame, fragmentFavorite);
			break;
		case 4:
			fragmentTransaction.replace(R.id.content_frame, fragmentAbout);
			break;
		}
		fragmentTransaction.commit();
		getSupportFragmentManager().executePendingTransactions();
		handleIconMenu();
	}

	private void handleIconMenu() {
		switch (mAction) {
		case -1:
			imgMenu.setImageResource(R.drawable.ic_logo_coffee);
			break;
		case 0:
			imgMenu.setImageResource(R.drawable.ic_logo_coffee);
			break;
		case 1:
			imgMenu.setImageResource(R.drawable.ic_action_search);
			break;
		case 2:
			imgMenu.setImageResource(R.drawable.ic_action_favorite);
			break;
		case 4:
			imgMenu.setImageResource(R.drawable.ic_action_about);
			break;
		}

	}

	@Override
	public void resultFromMarkerDialog(boolean result) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.content_frame, fragmentNearby);
		fragmentTransaction.commit();
		getSupportFragmentManager().executePendingTransactions();
		if (result) {
			if (listFavorite[offsetNearby] == 0) {
				listMarker
						.get(offsetNearby)
						.setIcon(
								BitmapDescriptorFactory
										.fromResource(R.drawable.ic_markup_favorite_coffee));
				listFavorite[offsetNearby] = 1;
				fragmentNearby.setFavorite(listOrder.get(offsetNearby), 1); // ASYNC
			} else {
				listMarker.get(offsetNearby).setIcon(
						BitmapDescriptorFactory
								.fromResource(R.drawable.ic_markup_coffee));
				listFavorite[offsetNearby] = 0;
				fragmentNearby.setFavorite(listOrder.get(offsetNearby), 0); // ASYNC
			}
			offsetNearby = -1;
		} else {
			LocationManager lm = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
			Location location = lm
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				location = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if (location != null) {
				Intent intent = new Intent(
						android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?saddr="
								+ location.getLatitude()
								+ ","
								+ location.getLongitude()
								+ "&daddr="
								+ listMarker.get(offsetNearby).getPosition().latitude
								+ ","
								+ listMarker.get(offsetNearby).getPosition().longitude));
				startActivity(intent);
			} else {
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.toast_custom,
						(ViewGroup) findViewById(R.id.toast_layout_root));

				TextView text = (TextView) layout.findViewById(R.id.toast_text);
				text.setText(getResources().getString(R.string.location_failed));

				Toast toast = new Toast(getApplicationContext());
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(layout);
				toast.show();
			}
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		offsetNearby = listMarker.indexOf(marker);
		return false;
	}

	@Override
	public void onBackPressed() {
		if ((mAction == 0) || (mAction == -1)) {
			finish();
		} else {
			drawerLayout.openDrawer(Gravity.LEFT);
		}
	}

	private class DrawerListener implements
			android.support.v4.widget.DrawerLayout.DrawerListener {
		@Override
		public void onDrawerClosed(View view) {
			if (mAction == 0) {
				btnNext.setClickable(true);
				btnPrevious.setClickable(true);
			}
			mMenu = false;
		}

		@Override
		public void onDrawerOpened(View arg0) {
			if (mAction == 0) {
				btnNext.setClickable(false);
				btnPrevious.setClickable(false);
			}
			mMenu = true;

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

			mAction = -1;
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.content_frame, fragmentNearby);
			fragmentTransaction.commit();
			getSupportFragmentManager().executePendingTransactions();

			imgMenu.setImageResource(R.drawable.ic_logo_coffee);
		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
			btnMenu.setAlpha(arg1);
			imgMenu.setAlpha(1.0f - arg1);
			contentFrame.setAlpha(1.0f);
			TranslateAnimation animFragment = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, arg1,
					Animation.RELATIVE_TO_SELF, arg1,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0);
			animFragment.setFillAfter(true);
			animFragment.setFillEnabled(true);
			contentFrame.startAnimation(animFragment);

			switch (mAction) {
			case -1:
				viewOverlay.setAlpha(arg1);
				break;
			case 0:
				btnNext.setAlpha(1.0f);
				btnPrevious.setAlpha(1.0f);
				viewOverlay.setAlpha(arg1);
				TranslateAnimation animPrevious = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, -arg1,
						Animation.RELATIVE_TO_SELF, -arg1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				animPrevious.setFillAfter(true);
				animPrevious.setFillEnabled(true);
				btnPrevious.startAnimation(animPrevious);
				TranslateAnimation animNext = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, arg1,
						Animation.RELATIVE_TO_SELF, arg1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				animNext.setFillAfter(true);
				animNext.setFillEnabled(true);
				btnNext.startAnimation(animNext);
				break;
			case 1:
				viewOverlay.setAlpha(1.0f);
				break;
			case 2:
				viewOverlay.setAlpha(1.0f);
				break;
			case 4:
				viewOverlay.setAlpha(1.0f);
				break;
			}
		}

		@Override
		public void onDrawerStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}
	}

	// changer le behavior ici pour potentiellement allez vers l'état -1;
	public void clickMenu(View v) {
		if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mAction = -1;
			offsetNearby = -1;
			handleAction();
			btnNext.setClickable(false);
			btnPrevious.setClickable(false);
			drawerLayout.closeDrawer(Gravity.LEFT);
		} else {
			drawerLayout.openDrawer(GravityCompat.START);
		}
	}

	// ListView click listener in the navigation drawer
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.v(TAG, "onItemClick");
			mAction = position;
			handleAction();

			if (mAction == 0) {
				if (nbCoffee > 0) {
					offsetNearby = 0;
					btnNext.setAlpha(1.0f);
					btnPrevious.setAlpha(1.0f);
					moveToMarkerAndShowInfo(listMarker.get(offsetNearby));
				} else {
					// TOAST
				}
			}
			if (mAction == 3) {
				Intent intent = new Intent(getApplicationContext(),
						ChoiceActivity.class);
				startActivity(intent);
			} else {
				drawerLayout.closeDrawer(drawerListView);
			}
		}
	}

	public void clickPrevious(View v) {
		Log.v(TAG, "clickPrevious");
		if (offsetNearby <= 0) {
			offsetNearby = nbCoffee - 1;
		} else {
			offsetNearby--;
		}
		if (listMarker.size() != 0)
			moveToMarkerAndShowInfo(listMarker.get(offsetNearby));
	}

	public void clickNext(View v) {
		Log.v(TAG, "clickNext");
		if (offsetNearby >= nbCoffee - 1) {
			offsetNearby = 0;
		} else {
			offsetNearby++;
		}
		if (listMarker.size() != 0)
			moveToMarkerAndShowInfo(listMarker.get(offsetNearby));
	}

	public void clickSetting(View v) {
		Log.v(TAG, "clickSetting");
	}

	private void moveToMarkerAndShowInfo(Marker marker) {
		Log.v(TAG, "moveToMarkerAndShowInfo");
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(marker.getPosition().latitude, marker
						.getPosition().longitude)).zoom(15).build();
		mMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
		marker.showInfoWindow();
	}

	@Override
	public void onItemSearchSelected(int id, double latitude, double longitude) {
		Log.v(TAG, "onitemSearchSelected");
		mAction = -1;
		viewOverlay.setAlpha(0.0f);
		handleAction();
		offsetNearby = listOrder.indexOf(id);
		moveToMarkerAndShowInfo(listMarker.get(offsetNearby));
	}

	public void clickMe(View v) {
		// Intent facebookIntent = getOpenFacebookIntent(this);
		// startActivity(facebookIntent);
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto", getString(R.string.about_me), null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT,
				"Hi there! (from DrinkMeHot)");
		startActivity(Intent.createChooser(emailIntent,
				getString(R.string.email_intent)));
	}

}
