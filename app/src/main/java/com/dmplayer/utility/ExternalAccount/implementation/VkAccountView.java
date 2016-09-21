package com.dmplayer.utility.ExternalAccount.implementation;


import android.content.Context;
import android.util.AttributeSet;

import com.dmplayer.utility.ExternalAccount.core.ExternalAccountViewExternalCallbacks;
import com.dmplayer.utility.ExternalAccount.core.ExternalAccountViewInternalCallbacks;
import com.dmplayer.utility.ExternalAccount.implementation.SwappingLayout.ExternalProfileLayout;

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
    //TODO: change this bullshit!!
    @Override
    public void setInternalButtonsCallbacks(ExternalAccountViewInternalCallbacks callbacks) {
        getStartingLayout().onLogInListener(callbacks.onLogInListener());
        getSwappingLayout().onRefreshListener(callbacks.onRefreshListener());
        getSwappingLayout().onLogOutListener(callbacks.onLogOutListener());
    }

    @Override
    public void showProfile(VkProfileModel profile) {
        setProfileLayout();
        getSwappingLayout().setProfile(profile);
    }

    @Override
    public void showLogIn() {
        setLogInLayout();
    }
}