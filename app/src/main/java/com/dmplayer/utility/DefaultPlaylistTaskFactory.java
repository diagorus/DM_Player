package com.dmplayer.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.asynctask.AbstractSinglePlaylistTask;
import com.dmplayer.asynctask.TaskStateListener;
import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.phonemedia.PhoneMediaControl;

import java.util.List;

public final class DefaultPlaylistTaskFactory {
    private Context context;
    private TaskStateListener<Playlist> listener;

    public DefaultPlaylistTaskFactory(Context context, TaskStateListener<Playlist> listener) {
        this.context = context;
        this.listener = listener;
    }

    public AsyncTask<Void, Void, AsyncTaskResult<Playlist>> getLoadPlaylistTask(DefaultPlaylistCategorySingle category,
                                                                      final int id, final String name) {
        switch (category) {
            case ALL_SONGS:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.All, "");
                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case MOST_PLAYED:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.MostPlay, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case GENRE:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.Genre, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case ARTIST:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.Artist, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case ALBUM:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.Album, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }
}
