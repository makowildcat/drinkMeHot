package fr.damienbrun.drinkmehot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.damienbrun.drinkmehot.adapter.CoffeeDbAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentParis extends FragmentCity {

	private final static String TAG = "FragmentParis";
	
	private String url = "https://parisdata.opendatasoft.com/api/records/1.0/search?dataset=liste-des-cafes-a-un-euro&rows=";
	private String tableName = "Paris";
	
	private CoffeeDbAdapter dbHelper;

	private AsyncTaskGetAllCoffee mTask;

	private boolean asyncCreated;

	public static FragmentParis newInstance() {
		FragmentParis fragmentParis = new FragmentParis();
		return fragmentParis;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveInstanceState) {
		Log.v(TAG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_paris, container, false);
		dbHelper = new CoffeeDbAdapter(getActivity().getApplicationContext());
		dbHelper.open();
		return v;
	}

	@Override
	public void onChoosed() {
		dbHelper.createTable(tableName);
		mTask = (AsyncTaskGetAllCoffee) new AsyncTaskGetAllCoffee()
				.execute(url + 1500);
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		super.onDestroyView();
		// if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
		if (asyncCreated) {
			mTask.cancel(true);
			Log.v(TAG, "test");
		}
		dbHelper.close();
	}

	private class AsyncTaskGetAllCoffee extends
			AsyncTask<String, Void, Boolean> {

		protected Boolean doInBackground(String... urls) {
			Log.v(TAG, "AsyncTaskGetAllCoffee -> doInBackground");
			asyncCreated = true;
			JSONObject json;
			String txt = GET(urls[0]);
			if (txt != "") {
				try {
					json = new JSONObject(txt);
					JSONArray records = json.getJSONArray("records");
					Log.v(TAG,
							"AsyncTaskGetAllCoffee -> doInBackground : records.length = "
									+ Integer.toString(records.length()));
					for (int i = 0; i < records.length(); i++) {
						try {
							JSONObject fields = records.getJSONObject(i)
									.getJSONObject("fields");
							JSONArray lat_lon = fields
									.getJSONArray("geo_latitude");
							Log.v(TAG,
									"AsyncTaskGetAllCoffee -> doInBackground -> createCoffee : "
											+ Long.toString(dbHelper.createCoffee(
													fields.getString("nom"),
													fields.getString("adresse"),
													fields.getInt("arrondissement"),
													lat_lon.getDouble(0),
													lat_lon.getDouble(1))));

						} catch (JSONException e) {
							e.printStackTrace();
						}
						Log.v(TAG,
								"AsyncTaskGetAllCoffee -> doInBackground : i = "
										+ Integer.toString(i));
					}
				} catch (JSONException e) {
					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), e.toString(),
							Toast.LENGTH_LONG);
					toast.show();
					return false;
					// e.printStackTrace();
				}
			} else
				return false;
			return true;
		}

		@Override
		protected void onPostExecute(Boolean b) {
			Log.v(TAG, "AsyncTaskGetAllCoffee -> onPostExecute");
			asyncCreated = false;
			getmCallback().onPostExecuted(b);
		}
	}
}
