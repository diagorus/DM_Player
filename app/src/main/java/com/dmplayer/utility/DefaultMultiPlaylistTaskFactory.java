package com.dmplayer.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.dmplayer.asynctask.AbstractMultiPlaylistTask;
import com.dmplayer.asynctask.TaskStateListener;
import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.PlaylistItemInSeveral;
import com.dmplayer.models.SongDetail;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemInSeveral;
import com.dmplayer.phonemedia.PhoneMediaControl;

import java.util.ArrayList;
import java.util.List;

public final class DefaultMultiPlaylistTaskFactory {
    private static final String TAG = "D_MultiPlaylistTaskFact";
    private Context context;
    private TaskStateListener<List<? extends PlaylistItemInSeveral>> listener;

    public DefaultMultiPlaylistTaskFactory(Context context,
                                           TaskStateListener<List<? extends PlaylistItemInSeveral>> listener) {
        this.context = context;
        this.listener = listener;
    }

    public AsyncTask<Void, Void, AsyncTaskResult<List<? extends PlaylistItemInSeveral>>>
        getTask(final DefaultPlaylistCategorySeveral category) {
        switch (category) {
            case ARTISTS:
                return new AbstractMultiPlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<List<? extends PlaylistItemInSeveral>> doInBackground(Void... params) {
                        String[] cols = new String[] {
                                MediaStore.Audio.Artists._ID,
                                MediaStore.Audio.Artists.ARTIST};

                        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

                        String filter = "";
                        if (!TextUtils.isEmpty(filter)) {
                            uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
                        }

                        Cursor cursor = DMPlayerUtility.query(context, uri, cols, null, null,
                                MediaStore.Audio.Artists.ARTIST_KEY);

                        return new AsyncTaskResult<List<? extends PlaylistItemInSeveral>>
                                (getPlaylistItemsFromCursor(category, cursor));
                    }
                };
            case GENRES:
                return new AbstractMultiPlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<List<? extends PlaylistItemInSeveral>> doInBackground(Void... params) {
                        String[] cols = new String[] {
                                MediaStore.Audio.Genres._ID,
                                MediaStore.Audio.Genres.NAME};
                        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

                        String filter = "";
                        if (!TextUtils.isEmpty(filter)) {
                            uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
                        }

                        Cursor cursor = DMPlayerUtility.query(context, uri, cols, null, null,
                                MediaStore.Audio.Genres.DEFAULT_SORT_ORDER);

                        return new AsyncTaskResult<List<? extends PlaylistItemInSeveral>>
                                (getPlaylistItemsFromCursor(category, cursor));
                    }
                };
            case ALBUMS:
                return new AbstractMultiPlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<List<? extends PlaylistItemInSeveral>> doInBackground(Void... params) {
                        String[] cols = new String[] {
                                MediaStore.Audio.Albums._ID,
                                MediaStore.Audio.Albums.ALBUM};

                        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

                        String filter = "";
                        if (!TextUtils.isEmpty(filter)) {
                            uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
                        }

                        Cursor cursor = DMPlayerUtility.query(context, uri, cols, null, null,
                                MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);

                        return new AsyncTaskResult<List<? extends PlaylistItemInSeveral>>
                                (getPlaylistItemsFromCursor(category, cursor));
                    }
                };
            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }

    //TODO: optimize handling here
    private List<DefaultPlaylistItemInSeveral> getPlaylistItemsFromCursor(DefaultPlaylistCategorySeveral category,
                                                                          Cursor cursor) {
        List<DefaultPlaylistItemInSeveral> items = new ArrayList<>();

        switch (category) {
            case ARTISTS:
                try {
                    if (cursor != null && cursor.getCount() >= 1) {
                        int _id = cursor.getColumnIndex(MediaStore.Audio.Artists._ID);
                        int title = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);

                        while (cursor.moveToNext()) {
                            int ID = cursor.getInt(_id);
                            String ARTIST = cursor.getString(title);

                            items.add(new DefaultPlaylistItemInSeveral(ID, ARTIST,
                                    DefaultPlaylistCategorySingle.ARTIST));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Can't get songs from cursor:", e);
                } finally {
                    closeCursor(cursor);
                }
                break;
            case GENRES:
                try {
                    if (cursor != null && cursor.getCount() >= 1) {
                        int _id = cursor.getColumnIndex(MediaStore.Audio.Genres._ID);
                        int title = cursor.getColumnIndex(MediaStore.Audio.Genres.NAME);

                        while (cursor.moveToNext()) {
                            int ID = cursor.getInt(_id);
                            String ARTIST = cursor.getString(title);

                            List<SongDetail> songsList = PhoneMediaControl.getInstance()
                                    .getList(context, ID, PhoneMediaControl.SongsLoadFor.Genre, "");
                            if (songsList.size() > 0) {
                                items.add(new DefaultPlaylistItemInSeveral(ID, ARTIST,
                                        DefaultPlaylistCategorySingle.GENRE));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Can't get songs from cursor:", e);
                } finally {
                    closeCursor(cursor);
                }
                break;
            case ALBUMS:
                try {
                    if (cursor != null && cursor.getCount() >= 1) {
                        int _id = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);
                        int title = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);

                        while (cursor.moveToNext()) {
                            int ID = cursor.getInt(_id);
                            String ALBUM = cursor.getString(title);

                            items.add(new DefaultPlaylistItemInSeveral(ID, ALBUM,
                                    DefaultPlaylistCategorySingle.ALBUM));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Can't get songs from cursor:", e);
                } finally {
                    closeCursor(cursor);
                }
                break;
        }

        return items;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }
}