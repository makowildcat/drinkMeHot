package fr.damienbrun.drinkmehot.adapter;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fr.damienbrun.drinkmehot.R;

public class CustomCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	private Location mDistance;

	public CustomCursorAdapter(Context context, Cursor c, int flags,
			Location distance) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDistance = distance;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView name = (TextView) view.findViewById(R.id.name);
		TextView address = (TextView) view.findViewById(R.id.address);
		name.setText(cursor.getString(cursor.getColumnIndex("name")));
		address.setText(cursor.getString(cursor.getColumnIndex("address"))
				+ " " + cursor.getString(cursor.getColumnIndex("zipcode")));
		if (mDistance != null) {
			TextView distance = (TextView) view.findViewById(R.id.distance);
			Location locationFavorite = new Location("locFav");
			locationFavorite.setLatitude(cursor.getDouble(cursor
					.getColumnIndex("latitude")));
			locationFavorite.setLongitude(cursor.getDouble(cursor
					.getColumnIndex("longitude")));
			int distbetween = (int) mDistance.distanceTo(locationFavorite);
			if (distbetween <= 1000) {
				distance.setText((int) mDistance.distanceTo(locationFavorite)
						+ "m");
				
			} else {
				distance.setText((float) Math.round(distbetween / 100.0f) / 10.0f + "km");
			}
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.cafe_listview_item, parent, false);
	}

}
