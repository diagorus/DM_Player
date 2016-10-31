package com.dmplayer.externalprofile;

public interface ExternalProfileHelper {
    ExternalProfileModel loadProfileOnline();
    ExternalProfileModel loadProfileOffline();
    void logOut();
    boolean isLogged();
}