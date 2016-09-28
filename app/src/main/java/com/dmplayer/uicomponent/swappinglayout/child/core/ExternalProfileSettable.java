package com.dmplayer.uicomponent.swappinglayout.child.core;

import android.view.View;

import com.dmplayer.externalaccount.ExternalProfileModel;

public interface ExternalProfileSettable {
    void setProfile(ExternalProfileModel profile);
    void setOnRefreshListener(View.OnClickListener l);
    void setOnLogOutListener(View.OnClickListener l);
}
