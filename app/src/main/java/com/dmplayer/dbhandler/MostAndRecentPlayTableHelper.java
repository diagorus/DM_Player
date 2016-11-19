package com.dmplayer.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.models.SongDetail;

public class MostAndRecentPlayTableHelper {
    public static final String TABLE_NAME = "MostPlay";

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
    public static final String PLAY_COUNT = "play_count";

    public static final String TAG = MostAndRecentPlayTableHelper.class.getSimpleName();

    private DMPLayerDBHelper dbHelper;
    private SQLiteDatabase db;

    private static MostAndRecentPlayTableHelper instance;

    public static synchronized MostAndRecentPlayTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MostAndRecentPlayTableHelper(context);
        }
        return instance;
    }

    private MostAndRecentPlayTableHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = ((DMPlayerApplication) context.getApplicationContext()).DB_HELPER;
        }
    }

    public void insertSong(SongDetail songDetail) {
        try {
            if (isSongExist(songDetail.getId())) {
                return;
            }

            db = dbHelper.getDB();
            db.beginTransaction();

            String sql = "Insert or Replace into " + TABLE_NAME + " values(?,?,?,?,?,?,?,?,?,?,?);";
            SQLiteStatement insert = db.compileStatement(sql);

            try {
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
                insert.bindLong(11, 1);

                insert.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e("XML:", e.toString());
        } finally {
            db.endTransaction();
        }
    }


    private boolean isSongExist(int id) {
        Cursor mCursor = null;
        boolean isExist = false;
        try {
            String sqlQuery = "select * from " + TABLE_NAME + " where " + ID + "=" + id;
            db = dbHelper.getDB();
            mCursor = db.rawQuery(sqlQuery, null);
            if (mCursor != null && mCursor.getCount() >= 1) {
                mCursor.moveToNext();
                long count = mCursor.getLong(mCursor.getColumnIndex(PLAY_COUNT));
                count++;
                updateStatus(count, id);
                isExist = true;
            }
            closeCursor(mCursor);
        } catch (Exception e) {
            closeCursor(mCursor);
            e.printStackTrace();
        }
        return isExist;
    }

    private void updateStatus(long count, int musicid) {
        try {
            ContentValues values = new ContentValues();
            values.put(PLAY_COUNT, count);
            long success = db.update(TABLE_NAME, values, ID + "=?", new String[]{String.valueOf(musicid)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public Cursor getMostPlay() {
        Cursor mCursor = null;
        try {
            String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + PLAY_COUNT + ">=2 ORDER BY " + LAST_PLAY_TIME + " ASC LIMIT 20";
            db = dbHelper.getDB();
            mCursor = db.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            closeCursor(mCursor);
            e.printStackTrace();
        }
        return mCursor;
    }
}
