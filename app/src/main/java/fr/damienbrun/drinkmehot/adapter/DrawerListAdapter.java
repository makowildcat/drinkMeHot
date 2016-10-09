package fr.damienbrun.drinkmehot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.damienbrun.drinkmehot.R;

public class DrawerListAdapter extends BaseAdapter {

	private Context mContext;
	private String[] mTitle;
	private int[] mIcon;
	private LayoutInflater mInflater;
	
	public DrawerListAdapter(Context context, String[] title, int[] icon) {
		this.mContext = context;
		this.mTitle = title;
		this.mIcon = icon;
	}
	
	@Override
	public int getCount() {
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		return mTitle[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView txtTitle;
		ImageView imgIcon;
		
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = mInflater.inflate(R.layout.drawer_listview_item, parent, false);
		
		txtTitle = (TextView) itemView.findViewById(R.id.title);
		imgIcon = (ImageView) itemView.findViewById(R.id.icon);
		
		txtTitle.setText(mTitle[position]);
		imgIcon.setImageResource(mIcon[position]);
		
		return itemView;
	}

}
