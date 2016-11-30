package com.dmplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.models.Playlist;

public class PlaylistTableHelper {
    public static final String TABLE_NAME = "local_playlist";

    public static final String ID = "_id";
    public static final String NAME = "name";

    private static final String TAG = PlaylistTableHelper.class.getSimpleName();

    private DMPLayerDBHelper dbHelper;
    private SQLiteDatabase db;

    private static PlaylistTableHelper instance;

    public static synchronized PlaylistTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PlaylistTableHelper(context);
        }
        return instance;
    }

    private PlaylistTableHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = ((DMPlayerApplication) context.getApplicationContext()).DB_HELPER;
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public void insertPlaylist(Playlist playlist) {
        try {
            db = dbHelper.getDB();
            db.beginTransaction();

            String sql = "INSERT OR REPLACE INTO " + TABLE_NAME + " VALUES (?,?);";
            SQLiteStatement insert = db.compileStatement(sql);

            try {
                if (playlist != null) {
                    insert.clearBindings();

                    insert.bindLong(1, playlist.getId());
                    insert.bindString(2, playlist.getName());

                    insert.execute();
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getPlaylists() {
        Cursor cursor = null;
        try {
            String sqlQuery = "SELECT * FROM " + TABLE_NAME;
            db = dbHelper.getDB();
            cursor = db.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return cursor;
    }
}
