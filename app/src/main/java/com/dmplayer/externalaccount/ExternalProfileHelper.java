package com.dmplayer.externalaccount;

public interface ExternalProfileHelper {
    ExternalProfileModel loadProfileOnline();
    ExternalProfileModel loadProfileOffline();
    void logOut();
}