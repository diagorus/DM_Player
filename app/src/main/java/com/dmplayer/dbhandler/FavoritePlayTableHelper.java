package com.dmplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.models.SongDetail;

public class FavoritePlayTableHelper {
    public static final String TABLE_NAME = "Favourite";

    public static final String ID = "_id";
    public static final String ALBUM_ID = "album_id";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String DISPLAY_NAME = "display_name";
    public static final String DURATION = "duration";
    public static final String PATH = "path";
    public static final String AUDIO_PROGRESS = "audio_progress";
    public static final String AUDIO_PROGRESS_SEC = "audio_progress_sec";
    public static final String LAST_PLAY_TIME = "last_play_time";
    public static final String IS_FAVORITE = "is_favorite";

    private DMPLayerDBHelper dbHelper;
    private SQLiteDatabase sampleDB;

    private static FavoritePlayTableHelper instance;

    public static synchronized FavoritePlayTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritePlayTableHelper(context);
        }
        return instance;
    }

    public FavoritePlayTableHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = ((DMPlayerApplication) context.getApplicationContext()).DB_HELPER;
        }
    }

    public void insertSong(SongDetail songDetail, int isFav) {
        try {
            sampleDB = dbHelper.getDB();
            sampleDB.beginTransaction();

            String sql = "Insert or Replace into " + TABLE_NAME + " values(?,?,?,?,?,?,?,?,?,?,?);";
            SQLiteStatement insert = sampleDB.compileStatement(sql);

            try {
                if (songDetail != null) {
                    insert.clearBindings();
                    insert.bindLong(1, songDetail.getId());
                    insert.bindLong(2, songDetail.getAlbum_id());
                    insert.bindString(3, songDetail.getArtist());
                    insert.bindString(4, songDetail.getTitle());
                    insert.bindString(5, songDetail.getDisplay_name());
                    insert.bindString(6, songDetail.getDuration());
                    insert.bindString(7, songDetail.getPath());
                    insert.bindString(8, songDetail.audioProgress + "");
                    insert.bindString(9, songDetail.audioProgressSec + "");
                    insert.bindString(10, System.currentTimeMillis() + "");
                    insert.bindLong(11, isFav);

                    insert.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            sampleDB.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e("XML:", e.toString());
        } finally {
            sampleDB.endTransaction();
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public Cursor getFavoriteSongList() {
        Cursor cursor = null;
        try {
            String sqlQuery = "Select * from " + TABLE_NAME + " where " + IS_FAVORITE + "=1";
            sampleDB = dbHelper.getDB();
            cursor = sampleDB.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            closeCursor(cursor);
            e.printStackTrace();
        }
        return cursor;
    }

    public boolean getIsFavorite(SongDetail mDetail) {
        Cursor cursor = null;
        try {
            String sqlQuery = "Select * from " + TABLE_NAME + " where " + ID + "=" + mDetail.getId() + " and " + IS_FAVORITE + "=1";
            sampleDB = dbHelper.getDB();
            cursor = sampleDB.rawQuery(sqlQuery, null);
            if (cursor != null && cursor.getCount() >= 1) {
                closeCursor(cursor);
                return true;
            }
        } catch (Exception e) {
            closeCursor(cursor);
            e.printStackTrace();
        }
        return false;
    }
}
