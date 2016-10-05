package com.dmplayer.streamaudio.Observer;

import android.widget.ProgressBar;

import java.util.List;

/**
 * Created by Alexvojander on 05.10.2016.
 */

public class SingleObserverContainer implements Observer {
    private static volatile SingleObserverContainer instance;

    //List<ProgressBar> progressBarList=null;

    ProgressBar progressBarClient=null;
    ProgressBar progressBarServer=null;

    private SingleObserverContainer() {}

    public static SingleObserverContainer getInstance() {
        SingleObserverContainer localInstance = instance;
        if (localInstance == null) {
            synchronized (SingleObserverContainer.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SingleObserverContainer();
                }
            }
        }
        return localInstance;
    }

    public void setProgressBarClient(ProgressBar progressBarClient){this.progressBarClient=progressBarClient;}
    public void setProgressBarServer(ProgressBar progressBarServer){this.progressBarServer=progressBarServer;}

    public  ProgressBar getProgressBarClient(){return progressBarClient;}
    public  ProgressBar getProgressBarServer(){return progressBarServer;}
    
    @Override
    public void update(ProgressBar progressBarClient, ProgressBar progressBarServer) {
        // this.progressBarList.add(progressBar);
        this.progressBarClient=progressBarClient;
        this.progressBarServer=progressBarServer;
    }
}
