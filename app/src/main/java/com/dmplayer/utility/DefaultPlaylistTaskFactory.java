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

    public AsyncTask<Void, Void, AsyncTaskResult<Playlist>> getTask(DefaultPlaylistCategorySingle category,
                                                                    final int id, final String name) {
        switch (category) {
            case ALL_SONGS:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.ALL, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case MOST_PLAYED:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.MOST_PLAY, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case GENRE:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.GENRE, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case ARTIST:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.ARTIST, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case ALBUM:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.ALBUM, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            case LOCAL:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        List<SongDetail> songs = PhoneMediaControl.getInstance()
                                .loadMusicList(context, id, PhoneMediaControl.SongsLoadFor.LOCAL_PLAYLIST, "");

                        return new AsyncTaskResult<>(new Playlist(name, songs));
                    }
                };
            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }
}
