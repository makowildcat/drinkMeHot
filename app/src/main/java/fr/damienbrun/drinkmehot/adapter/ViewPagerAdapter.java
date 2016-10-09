package fr.damienbrun.drinkmehot.adapter;

import java.util.List;

import fr.damienbrun.drinkmehot.FragmentCity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	private final List<FragmentCity> fragments;
	public ViewPagerAdapter(FragmentManager fm, List<FragmentCity> fragments) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return (FragmentCity) this.fragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.fragments.size();
	}
}
