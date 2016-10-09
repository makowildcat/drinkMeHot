package fr.damienbrun.drinkmehot;

import fr.damienbrun.drinkmehot.adapter.CoffeeDbAdapter;
import fr.damienbrun.drinkmehot.adapter.CustomCursorAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

public class FragmentSearch extends ListFragment {

	private OnItemSearchSelectedListener mCallback;

	private CoffeeDbAdapter dbHelper;
	private CustomCursorAdapter dataAdapter;

	// private EditText myFilter;

	public interface OnItemSearchSelectedListener {
		public void onItemSearchSelected(int id, double latitude,
				double longitude);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (OnItemSearchSelectedListener) activity;
	}

	public static FragmentSearch newInstance(boolean edittext) {
		FragmentSearch f = new FragmentSearch();

		Bundle args = new Bundle();
		args.putBoolean("editText", edittext);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		dbHelper.close();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getArguments().getBoolean("editText", false)) {
			// myFilter.setText("");
			// myFilter.requestFocus();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.w("test", "onCreateView");
		dbHelper = new CoffeeDbAdapter(getActivity());
		dbHelper.open();

		View rootView;
		if (getArguments().getBoolean("editText", false)) {
			rootView = inflater.inflate(R.layout.fragment_search, container,
					false);

			Cursor cursor = dbHelper.fetchAllCoffee();
			dataAdapter = new CustomCursorAdapter(getActivity(), cursor, 0,
					null);

			EditText myFilter = (EditText) rootView
					.findViewById(R.id.editTextSearch);
			myFilter.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					dataAdapter.getFilter().filter(s.toString());
				}
			});

			dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
				public Cursor runQuery(CharSequence constraint) {
					return dbHelper.fetchCoffeeByName(constraint.toString());
				}
			});

		} else {
			rootView = inflater.inflate(R.layout.fragment_favorite, container,
					false);

			Cursor cursor = dbHelper.fetchFavoriteCoffee();
			LocationManager lm = (LocationManager) getActivity()
					.getApplicationContext().getSystemService(
							Context.LOCATION_SERVICE);
			Location location = lm
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				location = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			dataAdapter = new CustomCursorAdapter(getActivity(), cursor, 0,
					location);
		}

		setListAdapter(dataAdapter);

		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
				.getWindowToken(), 0);

		Cursor cursor = (Cursor) getListView().getItemAtPosition(position);

		mCallback
				.onItemSearchSelected(cursor.getInt(cursor
						.getColumnIndex(CoffeeDbAdapter.KEY_ROWID)), cursor
						.getDouble(cursor
								.getColumnIndex(CoffeeDbAdapter.KEY_LATITUDE)),
						cursor.getDouble(cursor
								.getColumnIndex(CoffeeDbAdapter.KEY_LONGITUDE)));
	}

}
