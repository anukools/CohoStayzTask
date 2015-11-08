package com.tesk.cohostayz.adapters;

/**
 * Created by Anukool Srivastav
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "COHOStayz";

	// USERS table name
	private static final String TABLE_USERS = "Users";

	// USERS Table Columns names
	private static String KEY_UID = "userid";


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(" + KEY_UID
				+ " INTEGER PRIMARY KEY"  + ")";
		db.execSQL(CREATE_USER_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String uid) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UID, uid); // user id
		
		// Inserting Row
		db.insert(TABLE_USERS, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> GET_USER_INFO() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USERS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put(KEY_UID, cursor.getString(cursor.getColumnIndex(KEY_UID)));
		}
		cursor.close();
		db.close();

		return user;
	}

}
