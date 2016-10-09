package fr.damienbrun.drinkmehot;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MarkerDialog extends DialogFragment {

	private MarkerDialogListener mCallback;

	public interface MarkerDialogListener {
		public void resultFromMarkerDialog(boolean result);
	}

	public MarkerDialog() {
		// Empty constructor required for DialogFragment
	}

	static MarkerDialog newInstance(double latitude, double longitude) {
		MarkerDialog f = new MarkerDialog();
		Bundle args = new Bundle();
		args.putDouble("latitude", latitude);
		args.putDouble("longitude", longitude);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (MarkerDialogListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialogfragment_marker, container);

		((ImageButton) view.findViewById(R.id.button_favorite))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						getDialog().dismiss();
						mCallback.resultFromMarkerDialog(true);
					}
				});

		((ImageButton) view.findViewById(R.id.button_way))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						getDialog().dismiss();
						mCallback.resultFromMarkerDialog(false);
					}
				});

		LocationManager lm = (LocationManager) getActivity()
				.getApplicationContext().getSystemService(
						Context.LOCATION_SERVICE);

		Location myLocation = lm
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (myLocation == null) {
			myLocation = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (myLocation != null) {
			TextView distance = (TextView) view.findViewById(R.id.txt_distance);
			TextView fake = (TextView) view.findViewById(R.id.txt_distancefake);
			Location locationFavorite = new Location("locDialog");
			locationFavorite.setLatitude(getArguments().getDouble("latitude"));
			locationFavorite
					.setLongitude(getArguments().getDouble("longitude"));
			int distbetween = (int) myLocation.distanceTo(locationFavorite);
			if (distbetween <= 1000) {
				distance.setText((int) myLocation.distanceTo(locationFavorite)
						+ "m");
				fake.setText((int) myLocation.distanceTo(locationFavorite)
						+ "m");

			} else {
				distance.setText((float) Math.round(distbetween / 100.0f)
						/ 10.0f + "km");
				fake.setText((float) Math.round(distbetween / 100.0f)
						/ 10.0f + "km");
			}
		}

		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(STYLE_NO_TITLE);
		Drawable d = new ColorDrawable(Color.TRANSPARENT);
		dialog.getWindow().setBackgroundDrawable(d);
		return dialog;
	}

}
