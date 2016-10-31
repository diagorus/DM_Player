package com.dmplayer.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.asynctask.AbstractSinglePlaylistTask;
import com.dmplayer.asynctask.TaskStateListener;
import com.dmplayer.helperservises.VkMusicHelper;
import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySingle;

import java.io.IOException;
import java.util.List;

public final class VkPlaylistTaskFactory {
    private TaskStateListener<Playlist> listener;

    private VkMusicHelper musicLoader;

    public VkPlaylistTaskFactory(Context context, TaskStateListener<Playlist> listener) {
        musicLoader = new VkMusicHelper.Builder(context).build();

        this.listener = listener;
    }

    public AsyncTask<Void, Void, AsyncTaskResult<Playlist>>
    getTask(VkPlaylistCategorySingle category, final int id, final String name) {
        switch (category) {
            case MY_MUSIC:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            List<SongDetail> songs = musicLoader.loadAlbum(0, 100, -1);
                            return new AsyncTaskResult<>(new Playlist(name, songs));
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case RECOMMENDATIONS:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            List<SongDetail> songs = musicLoader.loadRecommended(0, 100);
                            return new AsyncTaskResult<>(new Playlist(name, songs));
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case GENRE:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            List<SongDetail> songs = musicLoader.loadPopular(0, 100, id);
                            return new AsyncTaskResult<>(new Playlist(name, songs));
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case ALBUM:
                return new AbstractSinglePlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<Playlist> doInBackground(Void... params) {
                        try {
                            List<SongDetail> songs = musicLoader.loadAlbum(0, 100, id);
                            return new AsyncTaskResult<>(new Playlist(name, songs));
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }

}
