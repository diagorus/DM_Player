package com.dmplayer.utility.ExternalAccount.core;

public interface ExternalProfileHelper {
    Object loadProfileOnline();
    Object loadProfileOffline();
    void logOut();
}