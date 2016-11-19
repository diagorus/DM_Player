package com.dmplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;

public class PlaylistSongsTableHelper {
    public static final String TABLE_NAME = "Local_PlaylistSongs";

    public static final String ID = "_id";
    public static final String PLAYLIST_ID = "playlist_id";
    public static final String SONG_ID = "song_id";

    private static final String TAG = PlaylistSongsTableHelper.class.getSimpleName();

    private DMPLayerDBHelper dbHelper;
    private SQLiteDatabase db;

    private static PlaylistSongsTableHelper instance;

    public static synchronized PlaylistSongsTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PlaylistSongsTableHelper(context);
        }
        return instance;
    }

    private PlaylistSongsTableHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = ((DMPlayerApplication) context.getApplicationContext()).DB_HELPER;
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public void addSongToPlaylist(int playlistId, int songId) {
        try {
            db = dbHelper.getDB();
            db.beginTransaction();

            String sql = "INSERT OR REPLACE INTO " + TABLE_NAME + " VALUES (?,?);";
            SQLiteStatement insert = db.compileStatement(sql);

            try {
                insert.clearBindings();

                insert.bindLong(1, playlistId);
                insert.bindLong(2, songId);

                insert.execute();
            } catch (Exception e) {
                Log.e(TAG, "Inserting error:", e);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "XML:", e);
        } finally {
            db.endTransaction();
        }
    }
}
