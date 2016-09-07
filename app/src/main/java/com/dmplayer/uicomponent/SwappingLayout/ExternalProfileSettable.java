package com.dmplayer.uicomponent.SwappingLayout;

import android.view.View;

import com.dmplayer.models.ExternalProfileObject;

public interface ExternalProfileSettable {
    void setProfile(ExternalProfileObject profile);
    void setOnRefreshListener(View.OnClickListener l);
    void setOnLogOutListener(View.OnClickListener l);
}
