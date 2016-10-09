package fr.damienbrun.drinkmehot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import fr.damienbrun.drinkmehot.R;

public class CafeInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

	//private LayoutInflater inflater = null;
	private final View myContentsView;
	
	public CafeInfoWindowAdapter(LayoutInflater inflater) {
		// TODO Auto-generated constructor stub
		myContentsView = inflater.inflate(R.layout.custom_infowindow, null);
	}
	
	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		TextView txtName = (TextView)myContentsView.findViewById(R.id.infoWindow_name);
		txtName.setText(marker.getTitle());
		TextView txtAddress = (TextView)myContentsView.findViewById(R.id.infoWindow_address);
		txtAddress.setText(marker.getSnippet());
		return myContentsView;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

}
