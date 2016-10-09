package fr.damienbrun.drinkmehot;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import fr.damienbrun.drinkmehot.adapter.CoffeeDbAdapter;

//penser refactoriser ces deux merde de fragment Paris et New York pour tout mettre dans le fragmentCity au lieu de l'étendre.
public class FragmentNewYork extends FragmentCity {

	private final static String TAG = "FragmentNewYork";

	private String url = "https://nycopendata.socrata.com/api/views/6k68-kc8u/rows.json?accessType=DOWNLOAD";
	private String tableName = "NewYork";

	private CoffeeDbAdapter dbHelper;

	private AsyncTaskNewYorkDL mTask;

	private boolean asyncCreated;

	public static FragmentNewYork newInstance() {
		FragmentNewYork fragmentNewYork = new FragmentNewYork();
		return fragmentNewYork;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveInstanceState) {
		Log.v(TAG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_newyork, container, false);
		dbHelper = new CoffeeDbAdapter(getActivity().getApplicationContext());
		dbHelper.open();
		return v;
	}

	@Override
	public void onChoosed() {
		dbHelper.createTable(tableName);
		mTask = (AsyncTaskNewYorkDL) new AsyncTaskNewYorkDL().execute(url);
	}

	@Override
	public void onDestroyView() {
		Log.d(TAG, "onDestroyView");
		super.onDestroyView();
		// if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
		if (asyncCreated) {
			mTask.cancel(true);
			Log.v(TAG, "test");
		}
		dbHelper.close();
	}

	private class AsyncTaskNewYorkDL extends AsyncTask<String, String, Boolean> {

		protected Boolean doInBackground(String... urls) {
			Log.v(TAG, "AsyncTaskNewYork -> doInBackground");
			asyncCreated = true;
			JSONArray data;
			String txt = GET(urls[0]);
			if (txt.contains("\"data\" : ")) {
				txt = txt.split("\"data\" : ")[1];
				try {
					data = new JSONArray(txt);
					Log.v(TAG,
							"AsyncTaskNewYork -> doInBackground : records.length = "
									+ Integer.toString(data.length()));
					publishProgress(getString(R.string.txt_init_update));
					for (int i = 0; i < data.length(); i++) {
						try {
							JSONArray cafe = data.getJSONArray(i);
							JSONArray location = cafe.getJSONArray(19);
							Log.v(TAG,
									"AsyncTaskNewYork -> doInBackground -> createCoffee : "
											+ Long.toString(dbHelper.createCoffee(
													cafe.getString(12), cafe
															.getString(15),
													Integer.parseInt(cafe
															.getString(17)),
													Double.parseDouble(location
															.getString(1)),
													Double.parseDouble(location
															.getString(2)))));

						} catch (JSONException e) {
							e.printStackTrace();
						}
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

		protected void onProgressUpdate(String... progress) {
			Log.v(TAG, progress[0]);
			passData(progress[0]);
		}

		@Override
		protected void onPostExecute(Boolean b) {
			Log.v(TAG, "AsyncTaskGetAllCoffee -> onPostExecute");
			asyncCreated = false;
			getmCallback().onPostExecuted(b);
		}
	}
	
	public interface OnUpdateProgress {
		public void onDataPass(String update);
	}
	
	OnUpdateProgress dataPasser;
	
	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		dataPasser = (OnUpdateProgress) a;
	}
	
	public void passData(String update) {
		dataPasser.onDataPass(update);
	}
}
