package com.dmplayer.utility.ExternalAccount.implementation.SwappingLayout.child.core;

import android.view.View;

import com.dmplayer.utility.ExternalAccount.implementation.VkProfileModel;

public interface ExternalProfileSettable {
    void setProfile(VkProfileModel profile);
    void onRefreshListener(View.OnClickListener l);
    void onLogOutListener(View.OnClickListener l);
}
