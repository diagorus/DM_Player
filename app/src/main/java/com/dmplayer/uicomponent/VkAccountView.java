package com.dmplayer.uicomponent;


import android.content.Context;
import android.util.AttributeSet;

import com.dmplayer.externalaccount.ExternalAccountViewExternalCallbacks;
import com.dmplayer.externalaccount.ExternalAccountViewInternalCallbacks;
import com.dmplayer.externalaccount.ExternalProfileModel;
import com.dmplayer.uicomponent.externalprofilelayout.ExternalProfileLayout;

public class VkAccountView extends ExternalProfileLayout {
    private ExternalAccountViewExternalCallbacks behaviorCallbacks;

    public VkAccountView(Context context) {
        super(context);
    }

    public VkAccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBehaviorCallbacks();
    }

    public VkAccountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBehaviorCallbacks();
    }

    private void setBehaviorCallbacks() {
        behaviorCallbacks = new ExternalAccountViewExternalCallbacks() {
            @Override
            public void onLoadingStarted() {
                setLoadingLayout();
            }

            @Override
            public void onLoadingFinished() {
                setProfileLayout();
            }

            @Override
            public void onLoggedOut() {
                setLogInLayout();
            }
        };
    }

    @Override
    public ExternalAccountViewExternalCallbacks getBehaviorCallbacks() {
        return behaviorCallbacks;
    }

    @Override
    public void setInternalButtonsCallbacks(ExternalAccountViewInternalCallbacks callbacks) {
        getStartingLayout().setOnLogInListener(callbacks.onLogInListener());
        getSwappingLayout().setOnRefreshListener(callbacks.onRefreshListener());
        getSwappingLayout().setOnLogOutListener(callbacks.onLogOutListener());
    }

    @Override
    public void showProfile(ExternalProfileModel profile) {
        setProfileLayout();
        getSwappingLayout().setProfile(profile);
    }

    @Override
    public void showLogIn() {
        setLogInLayout();
    }
}