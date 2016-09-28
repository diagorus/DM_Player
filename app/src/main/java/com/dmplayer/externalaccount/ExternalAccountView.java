package com.dmplayer.externalaccount;

public interface ExternalAccountView {
    ExternalAccountViewExternalCallbacks getBehaviorCallbacks();
    void setInternalButtonsCallbacks(ExternalAccountViewInternalCallbacks callbacks);
    void showProfile(ExternalProfileModel profile);
    void showLogIn();
}