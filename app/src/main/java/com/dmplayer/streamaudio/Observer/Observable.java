package com.dmplayer.streamaudio.Observer;

/**
 * Created by Alexvojander on 05.10.2016.
 */

interface Observable {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}