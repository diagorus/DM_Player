package com.dmplayer.externalaccount;

import android.app.Fragment;

public interface ExternalAccountPresenter {
    void onCreate(Fragment fragment);
    void logIn(String token, String userId);
    void refresh();
    void logOut();
    void onDestroy();
}
