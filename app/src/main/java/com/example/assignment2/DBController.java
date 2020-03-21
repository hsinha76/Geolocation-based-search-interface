package com.example.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.assignment2.model.PlaceData;

import java.util.ArrayList;
import java.util.List;

public class DBController extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    static final String TABLE_NAME = "place_temples";
    private static DBController instance;

    private DBController(Context context) {
        super(context, context.getResources().getString(R.string.db_name), null, DB_VERSION);
    }

    public static DBController getInstance(Context mContext) {
        if (instance == null)
            instance = new DBController(mContext);
        return instance;
    }
//    public DBController(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        CREATE TABLE `place_temples` (
  `place_id` varchar(50) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `name` varchar(500) NOT NULL,
  `rating` double DEFAULT NULL,
  `types` varchar(200) DEFAULT NULL,
  `added_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`,`place_id`),
  UNIQUE KEY `place_unique` (`place_id`)
)*/
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                "id" + " INTEGER PRIMARY KEY," +
                "place_id" + " TEXT NOT NULL," +
                "latitude" + " TEXT NOT NULL," +
                "longitude" + " TEXT NOT NULL," +
                "name" + " TEXT NOT NULL, " +
                "rating" + " REAL DEFAULT NULL, " +
                "types" + " TEXT DEFAULT NULL, " +
                "added_date" + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_date" + " TIMESTAMP DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public long insert(String pTableName, ContentValues pContentValues) {
        return getWritableDatabase().insert(pTableName, null, pContentValues);
    }

    public Cursor selectAll(String pTableName) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + pTableName, new String[]{});
    }

    public Cursor select(String pTableName, String[] pWhereClause, String[] pWhereArgs) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + pTableName + " WHERE " + createWhereClause(pWhereClause), pWhereArgs);
    }

    /**
     * Creates whereClause String of the format "COLUMN_NAME = ? AND" from pWhereClause
     *
     * @param pWhereClause
     * @author Anand
     */
    private String createWhereClause(String[] pWhereClause) {
        int length = pWhereClause.length;
        String toReturn = "";
        for (int i = 0; i < length - 1; i++) {
            toReturn += pWhereClause[i] + " = ? AND ";
        }
        toReturn += pWhereClause[pWhereClause.length - 1] + " =?";
        return toReturn;
    }

    /***********************************Generic Methods end************************************************/

    public boolean isDataAvailable(String pTableName) {
        return getReadableDatabase().rawQuery("SELECT id FROM " + pTableName, new String[]{}).getCount() > 0;
    }

    public long insertPlaceDetails(String placeRow) {
        long insertOrUpdateRowId;
        ContentValues contentValues = placeToContentValues(placeRow);
        insertOrUpdateRowId = insert(TABLE_NAME, contentValues);
        return insertOrUpdateRowId;
    }

    private ContentValues placeToContentValues(String placeRow) {
        ContentValues contentValues = new ContentValues();
        try {
            String[] placeRowFields = placeRow.split(",");
            contentValues.put("place_id", placeRowFields[0]);
            contentValues.put("latitude", placeRowFields[1]);
            contentValues.put("longitude", placeRowFields[2]);
            contentValues.put("name", placeRowFields[3]);
            contentValues.put("rating", placeRowFields[4]);
            contentValues.put("types", placeRowFields[5]);
            contentValues.put("added_date", placeRowFields[6]);
            contentValues.put("updated_date", placeRowFields[7]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentValues;
    }

    public List<PlaceData> getPlaceList(String name) {
        List<PlaceData> placeDataList = new ArrayList<>();
        if (name.isEmpty()) {
            Cursor cursor = selectAll(TABLE_NAME);
            placeDataList = cursorToPlaceList(cursor);
        } else {
            // search
        }
        return placeDataList;
    }

    private List<PlaceData> cursorToPlaceList(Cursor cursor) {
        List<PlaceData> placeDataList = new ArrayList<>();
        while (cursor.moveToNext()) {
            PlaceData placeData = new PlaceData();
            placeData.setPlaceId(cursor.getString(cursor.getColumnIndex("place_id")));
            placeData.setName(cursor.getString(cursor.getColumnIndex("name")));
            placeData.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            placeData.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            placeData.setRating(cursor.getDouble(cursor.getColumnIndex("rating")));
            placeData.setType(cursor.getString(cursor.getColumnIndex("types")));
            placeData.setId("" + cursor.getInt(cursor.getColumnIndex("id")));
            placeDataList.add(placeData);
        }
        cursor.close();
        return placeDataList;
    }
}
