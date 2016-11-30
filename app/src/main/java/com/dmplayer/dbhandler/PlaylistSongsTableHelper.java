package com.dmplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;

public class PlaylistSongsTableHelper {
    public static final String TABLE_NAME = "local_playlist_and_song";

    public static final String ID = "_id";
    public static final String PLAYLIST_ID = "local_playlist_id";
    public static final String SONG_ID = "local_song_id";

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

    public void insertPlaylistSongs(Playlist playlist) {
        try {
            db = dbHelper.getDB();
            db.beginTransaction();

            String sql = "INSERT OR REPLACE INTO " + TABLE_NAME +
                    "(" + PLAYLIST_ID + ", " + SONG_ID +") VALUES (?,?);";
            SQLiteStatement insert = db.compileStatement(sql);
            for (SongDetail song : playlist.getSongs()) {
                try {
                    insert.clearBindings();

                    insert.bindLong(1, playlist.getId());
                    insert.bindLong(2, song.getId());

                    insert.execute();
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getPlaylistSongs(long id) {
        Cursor cursor = null;
        try {
            String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + PLAYLIST_ID + "=?";
            db = dbHelper.getDB();
            cursor = db.rawQuery(sqlQuery, new String [] {String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return cursor;
    }
}
