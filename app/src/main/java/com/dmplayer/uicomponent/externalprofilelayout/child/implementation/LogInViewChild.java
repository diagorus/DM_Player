package com.dmplayer.uicomponent.externalprofilelayout.child.implementation;

import android.content.Context;
import android.widget.Button;

import com.dmplayer.R;
import com.dmplayer.uicomponent.externalprofilelayout.child.core.ExternalLogInClickable;

public class LogInViewChild extends ChildForSwapping implements ExternalLogInClickable {
    private Button logIn;

    public LogInViewChild(Context context, int layoutId) {
        super(context, layoutId);
        init();
    }

    void init() {
        logIn = (Button) findViewById(R.id.external_login);
    }

    @Override
    public void setOnLogInListener(OnClickListener l) {
        logIn.setOnClickListener(l);
    }
}
