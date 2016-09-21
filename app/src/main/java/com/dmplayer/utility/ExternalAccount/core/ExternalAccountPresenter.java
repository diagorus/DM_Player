package com.dmplayer.utility.ExternalAccount.core;

import android.app.Fragment;

public interface ExternalAccountPresenter {
    void onCreate(Fragment fragment);
    void onLogIn(String token, String userId);
    void onRefresh();
    void onLogOut();
    void onDestroy();
}
