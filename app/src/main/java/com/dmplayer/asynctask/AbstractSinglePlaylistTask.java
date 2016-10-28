package com.dmplayer.asynctask;

import android.os.AsyncTask;

import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.Playlist;

public abstract class AbstractSinglePlaylistTask extends AsyncTask<Void, Void,
        AsyncTaskResult<Playlist>> {
    private TaskStateListener<Playlist> listener;

    public AbstractSinglePlaylistTask(TaskStateListener<Playlist> listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        listener.onLoadingStarted();
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Playlist> result) {
        super.onPostExecute(result);
        if (result.getError() != null) {
            listener.onError(result.getError());
        } else if (isCancelled()) {
            //TODO: think about cancel
        } else  {
            listener.onLoadingSuccessful(result.getResult());
        }
    }
}