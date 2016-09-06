package com.dmplayer.uicomponent.SwappingLayout;

import com.dmplayer.models.ExternalProfileObject;

public interface SwappingLayoutController {
    void onLogInStarted();
    void onLogInFinished(ExternalProfileObject profile);
    void onLoggedOut();
}
