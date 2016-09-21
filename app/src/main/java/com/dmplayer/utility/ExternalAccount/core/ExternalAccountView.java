package com.dmplayer.utility.ExternalAccount.core;

import com.dmplayer.utility.ExternalAccount.implementation.VkProfileModel;

public interface ExternalAccountView {
    ExternalAccountViewExternalCallbacks getBehaviorCallbacks();
    void setInternalButtonsCallbacks(ExternalAccountViewInternalCallbacks callbacks);
    void showProfile(VkProfileModel profile);
    void showLogIn();
}