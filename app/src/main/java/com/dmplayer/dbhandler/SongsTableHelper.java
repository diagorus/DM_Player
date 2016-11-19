package com.dmplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dmplayer.DMPlayerApplication;

public class SongsTableHelper {
    public static final String TABLE_NAME = "Local_Songs";

    public static final String ID = "_id";
    public static final String ALBUM_ID = "album_id";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String DISPLAY_NAME = "display_name";
    public static final String DURATION = "duration";
    public static final String PATH = "path";

    private static final String TAG = SongsTableHelper.class.getSimpleName();

    private DMPLayerDBHelper dbHelper;
    private SQLiteDatabase db;

    private static SongsTableHelper instance;

    public static synchronized SongsTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SongsTableHelper(context);
        }
        return instance;
    }

    private SongsTableHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = ((DMPlayerApplication) context.getApplicationContext()).DB_HELPER;
        }
    }

    public Cursor getSongsList() {
        Cursor cursor = null;
        try {
            String sqlQuery = "Select * from " + TABLE_NAME;
            db = dbHelper.getDB();
            cursor = db.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            closeCursor(cursor);
        }
        return cursor;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
