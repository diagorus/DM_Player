package com.dmplayer.asynctask;

public interface TaskStateListener<T> {
        void onLoadingStarted();
        void onLoadingSuccessful(T result);
        void onError(Exception e);
}