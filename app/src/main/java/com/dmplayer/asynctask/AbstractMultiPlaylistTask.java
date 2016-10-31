package com.dmplayer.asynctask;

import android.os.AsyncTask;

import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.PlaylistItemInSeveral;

import java.util.List;

public abstract class AbstractMultiPlaylistTask extends AsyncTask<Void, Void,
        AsyncTaskResult<List<? extends PlaylistItemInSeveral>>> {
    private TaskStateListener<List<? extends PlaylistItemInSeveral>> listener;

    public AbstractMultiPlaylistTask(TaskStateListener<List<? extends PlaylistItemInSeveral>> listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        listener.onLoadingStarted();
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<? extends PlaylistItemInSeveral>> result) {
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
