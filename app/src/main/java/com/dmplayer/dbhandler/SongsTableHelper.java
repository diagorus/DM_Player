package com.dmplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;

public class SongsTableHelper {
    public static final String TABLE_NAME = "local_song";

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

    public void insertSongs(Playlist playlist) {
        try {
            db = dbHelper.getDB();
            db.beginTransaction();

            String sql = "INSERT OR REPLACE INTO " + TABLE_NAME + " VALUES (?,?,?,?,?,?,?);";
            SQLiteStatement insert = db.compileStatement(sql);

            for (SongDetail song : playlist.getSongs()) {
                try {
                    if (song != null) {
                        insert.clearBindings();
                        insert.bindLong(1, song.getId());
                        insert.bindLong(2, song.getAlbumId());
                        insert.bindString(3, song.getArtist());
                        insert.bindString(4, song.getTitle());
                        insert.bindString(5, song.getDisplayName());
                        insert.bindString(6, song.getDuration());
                        insert.bindString(7, song.getPath());

                        insert.execute();
                    }
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

    public Cursor getSongsList(long id) {
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

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
