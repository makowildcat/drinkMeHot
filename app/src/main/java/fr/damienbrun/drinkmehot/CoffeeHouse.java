package fr.damienbrun.drinkmehot;

import android.os.Parcel;
import android.os.Parcelable;

public class CoffeeHouse implements Parcelable {

	private int _id;
	private String mName;
	private String mAddress;
	private int mZipCode;
	private String sId;
	
	private double mLatitude;
	private double mLongitude;
	
	private int mFavorite;
	//private int mDistance;


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(mName);
		dest.writeString(mAddress);
		dest.writeInt(mZipCode);
		dest.writeDouble(mLatitude);
		dest.writeDouble(mLongitude);
		dest.writeInt(mFavorite);
		//dest.writeInt(mDistance);
	}
	
	public CoffeeHouse() { };
	
	public CoffeeHouse(String name, String address, int zipCode, double latitude, double longitude, int favorite) {
		mName = name;
		mAddress = address;
		mZipCode = zipCode;
		mLatitude = latitude;
		mLongitude = longitude;
		mFavorite = favorite;
		//mDistance = 0;
	}

	private CoffeeHouse(Parcel in) {
		this._id = in.readInt();
		this.mName = in.readString();
		this.mAddress = in.readString();
		this.mZipCode = in.readInt();
		this.mLatitude = in.readDouble();
		this.mLongitude = in.readDouble();
		this.mFavorite = in.readInt();
		//this.mDistance = in.readInt();
	}
	
	public static final Parcelable.Creator<CoffeeHouse> CREATOR = new Parcelable.Creator<CoffeeHouse>() {
		
		@Override
		public CoffeeHouse createFromParcel(Parcel source) {
			return new CoffeeHouse(source);
		}
		
		@Override
		public CoffeeHouse[] newArray(int size) {
			return new CoffeeHouse[size];
		}
	};
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmAddress() {
		return mAddress;
	}

	public void setmAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public int getmZipCode() {
		return mZipCode;
	}

	public void setmZipCode(int mZipCode) {
		this.mZipCode = mZipCode;
	}

	public double getmLatitude() {
		return mLatitude;
	}

	public void setmLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getmLongitude() {
		return mLongitude;
	}

	public void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}
	
	public int getmFavorite() {
		return mFavorite;
	}
	
	public void setmFavorite(int mFavorite) {
		this.mFavorite = mFavorite;
	}
	
	public String getsId() {
		return this.sId;
	}
	
	public void setsId(String sId) {
		this.sId = sId;
	}
	
//	public int getmDistance() {
//		return mDistance;
//	}
//	
//	public void setmDistance(int mDistance) {
//		this.mDistance = mDistance;
//	}
}