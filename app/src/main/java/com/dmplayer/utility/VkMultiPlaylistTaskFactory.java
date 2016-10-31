package com.dmplayer.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.asynctask.AbstractMultiPlaylistTask;
import com.dmplayer.asynctask.TaskStateListener;
import com.dmplayer.helperservises.VkMusicHelper;
import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.PlaylistItemInSeveral;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySeveral;

import java.io.IOException;
import java.util.List;

public final class VkMultiPlaylistTaskFactory {
    private TaskStateListener<List<? extends PlaylistItemInSeveral>> listener;

    private VkMusicHelper musicLoader;

    public VkMultiPlaylistTaskFactory(Context context,
                                      TaskStateListener<List<? extends PlaylistItemInSeveral>> listener) {
        musicLoader = new VkMusicHelper.Builder(context).build();

        this.listener = listener;
    }

    public AsyncTask<Void, Void, AsyncTaskResult<List<? extends PlaylistItemInSeveral>>>
    getTask(final VkPlaylistCategorySeveral category) {
        switch (category) {
            case MY_ALBUMS:
                return new AbstractMultiPlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<List<? extends PlaylistItemInSeveral>> doInBackground(Void... params) {
                        try {
                            return new AsyncTaskResult<List<? extends PlaylistItemInSeveral>>(musicLoader.loadUserAlbums());
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case POPULAR:
                return new AbstractMultiPlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<List<? extends PlaylistItemInSeveral>> doInBackground(Void... params) {
                        return new AsyncTaskResult<List<? extends PlaylistItemInSeveral>>(musicLoader.loadPopularGenres());
                    }
                };
            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }
}
