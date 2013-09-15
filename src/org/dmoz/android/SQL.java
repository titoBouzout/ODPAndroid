package org.dmoz.android;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQL extends SQLiteOpenHelper {

	private static boolean	mDebug	= false;

	public SQL() {
		super(Shared.mApplication.getApplicationContext(), Shared.mAppName, null, 1);
		Shared.log("SQL()", mDebug);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Shared.log("SQL().onCreate()", mDebug);
		Shared.mDatabase = database;
		EventHandler.dispatchOnSQLCreated(Shared.mDatabase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Shared.log("SQL().onUpgrade()", mDebug);
	}

	public void open() {
		Shared.log("SQL().open()", mDebug);
		Shared.mSQL = new SQLSimple();
		if (Shared.mDatabase == null)
			Shared.mDatabase = getWritableDatabase();
	}

	@Override
	public void close() {

	}

	public class SQLSimple {
		public ArrayList<ContentValues> query(String query, String[] values) {
			ArrayList<ContentValues> results = new ArrayList<ContentValues>();
			Cursor cursor = Shared.mDatabase.rawQuery(query, values);
			cursor.moveToFirst();
			String[] columnNames = cursor.getColumnNames();
			while (!cursor.isAfterLast()) {
				ContentValues item = new ContentValues();
				for (int i = 0; i < columnNames.length; i++) {
					int columnType = cursor.getType(i);
					switch (columnType) {
						case Cursor.FIELD_TYPE_INTEGER:
							item.put(columnNames[i], cursor.getInt(i));
							break;
						case Cursor.FIELD_TYPE_STRING:
							item.put(columnNames[i], cursor.getString(i));
							break;
						case Cursor.FIELD_TYPE_FLOAT:
							item.put(columnNames[i], cursor.getFloat(i));
							break;
						case Cursor.FIELD_TYPE_BLOB:
							item.put(columnNames[i], cursor.getBlob(i));
							break;
						default:
							Shared.log("SQL().SimpleSQL().select()$column type not implemented:" + columnType, mDebug);
					}
				}
				results.add(item);
				cursor.moveToNext();
			}
			cursor.close();
			return results;
		}

		public void execute(String query, String[] values) {
			Cursor cursor = Shared.mDatabase.rawQuery(query, values);
			cursor.moveToFirst();
			cursor.close();
		}
	}

}