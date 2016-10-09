package fr.damienbrun.drinkmehot;

import fr.damienbrun.drinkmehot.adapter.CoffeeDbAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentNearby extends Fragment {
	
	private CoffeeDbAdapter dbHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nearby, container,
				false);
		
		dbHelper = new CoffeeDbAdapter(getActivity());
		dbHelper.open();
		
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		dbHelper.close();
	}
	
	public void setFavorite(int id, int value) {
		dbHelper.setFavorite(id, value); // SHOULD BE ASYNC ? CHECK LOADER.
	}
	
}
