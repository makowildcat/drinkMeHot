package fr.damienbrun.drinkmehot.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class CoffeeDbAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_ZIPCODE = "zipcode";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_FAVORITE = "favorite";

	private static final String TAG = "CoffeeDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "DrinkMeHotDB";
	private static final int DATABASE_VERSION = 1;

	private static String SQLITE_TABLE = "";

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.v(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
			onCreate(db);
		}

	}

	public CoffeeDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public boolean createTable(String name) {
		Log.v(TAG, "createTable > name = " + name);
		SQLITE_TABLE = name;
		deleteTable();
		try {
			mDb.execSQL("CREATE TABLE " + SQLITE_TABLE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME
					+ " TEXT, " + KEY_ADDRESS + " TEXT, " + KEY_ZIPCODE
					+ " INT, " + KEY_LATITUDE + " REAL, " + KEY_LONGITUDE
					+ " REAL, " + KEY_FAVORITE + " INTEGER )");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void deleteTable() {
		mDb.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
	}

	public CoffeeDbAdapter open() throws SQLException {
		Log.v(TAG, "open");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		Log.v(TAG, "close");
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	public long createCoffee(String name, String address, int zipcode,
			double latitude, double longitude) {
		Log.v(TAG, "createCoffee");
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_ADDRESS, address);
		values.put(KEY_ZIPCODE, zipcode);
		values.put(KEY_LATITUDE, latitude);
		values.put(KEY_LONGITUDE, longitude);
		values.put(KEY_FAVORITE, 0);

		return mDb.insert(SQLITE_TABLE, null, values);
	}

	public boolean deleteAllCoffee() {
		Log.v(TAG, "deleteAllCoffee");
		int doneDelete = 0;
		doneDelete = mDb.delete(SQLITE_TABLE, null, null);
		Log.v(TAG, "deleteAllCoffee :" + Integer.toString(doneDelete));
		return doneDelete > 0;

	}

	public Cursor fetchCoffeeByName(String inputText) throws SQLException {
		Log.v(TAG, "fetchCoffeeByName > inputText = " + inputText);
		Cursor mCursor = null;
		if (inputText == null) {
			mCursor = mDb.query(SQLITE_TABLE, new String[] { KEY_ROWID,
					KEY_NAME, KEY_ADDRESS, KEY_ZIPCODE, KEY_LATITUDE,
					KEY_LONGITUDE, KEY_FAVORITE }, null, null, null, null,
					KEY_NAME + " ASC");

		} else {
			mCursor = mDb.query(true, SQLITE_TABLE, new String[] { KEY_ROWID,
					KEY_NAME, KEY_ADDRESS, KEY_ZIPCODE, KEY_LATITUDE,
					KEY_LONGITUDE, KEY_FAVORITE }, KEY_NAME + " like '%"
					+ inputText + "%' OR " + KEY_ADDRESS + " like '%"
					+ inputText + "%' OR " + KEY_ZIPCODE + " like '%"
					+ inputText + "%'", null, null, null, KEY_NAME + " ASC",
					null);
		}
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchAllCoffee() {
		Log.v(TAG, "fetchAllCoffee");
		Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] { KEY_ROWID,
				KEY_NAME, KEY_ADDRESS, KEY_ZIPCODE, KEY_LATITUDE,
				KEY_LONGITUDE, KEY_FAVORITE }, null, null, null, null, KEY_NAME
				+ " ASC");

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchFavoriteCoffee() {
		Log.v(TAG, "fetchFavoriteCoffee");
		Cursor mCursor = mDb.query(true, SQLITE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_ADDRESS, KEY_ZIPCODE, KEY_LATITUDE,
				KEY_LONGITUDE, KEY_FAVORITE }, KEY_FAVORITE + " like '1'",
				null, null, null, KEY_NAME + " ASC", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public void setFavorite(int id, int value) {
		Log.v(TAG, "setFavorite");
		ContentValues values = new ContentValues();
		values.put(KEY_FAVORITE, value);

		String selection = KEY_ROWID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(id) };

		mDb.update(SQLITE_TABLE, values, selection, selectionArgs);
	}

	public Cursor fetchAllByDistance(final Location myLocation) {
		Log.v(TAG, "fetchAllByDistance");
		double fudge = Math.pow(
				Math.cos(Math.toRadians(myLocation.getLatitude())), 2);
		String query = "SELECT *, " + "((" + myLocation.getLatitude() + " - "
				+ KEY_LATITUDE + ") * (" + myLocation.getLatitude() + " - "
				+ KEY_LATITUDE + ") " + "+ (" + myLocation.getLongitude()
				+ " - " + KEY_LONGITUDE + ") * (" + myLocation.getLongitude()
				+ " - " + KEY_LONGITUDE + ") * (" + fudge + ")) AS DISTANCE "
				+ " FROM " + SQLITE_TABLE + " ORDER BY DISTANCE, "
				+ KEY_ZIPCODE;

		return mDb.rawQuery(query, null);

		// sort by distance
		/*
		 * if (myLocation != null) { Collections.sort(coffeehouses, new
		 * Comparator<CoffeeHouse>() { public int compare(CoffeeHouse cfs1,
		 * CoffeeHouse cfs2) { Location loc1 = new Location("loc1");
		 * loc1.setLatitude(cfs1.getmLatitude());
		 * loc1.setLongitude(cfs1.getmLongitude());
		 * 
		 * //cfs1.setmDistance((int) myLocation.distanceTo(loc1));
		 * 
		 * Location loc2 = new Location("loc2");
		 * loc2.setLatitude(cfs2.getmLatitude());
		 * loc2.setLongitude(cfs2.getmLongitude()); if
		 * (myLocation.distanceTo(loc1) == myLocation .distanceTo(loc2)) {
		 * return 0; } if (myLocation.distanceTo(loc1) > myLocation
		 * .distanceTo(loc2)) { return 1; } else return -1; } }); }
		 */
		// ((<lat> - LAT_COLUMN) * (<lat> - LAT_COLUMN) +
		// (<lng> - LNG_COLUMN) * (<lng> - LNG_COLUMN))

	}

	public static String getTableName() {
		return SQLITE_TABLE;
	}

	public static void setTableName(String tableName) {
		Log.v(TAG, "setTableName > tableName = " + tableName);
		SQLITE_TABLE = tableName;
	}
}
