package com.dmplayer.utility.ExternalAccount.implementation.SwappingLayout.child.implementation;

import android.content.Context;
import android.widget.Button;

import com.dmplayer.R;
import com.dmplayer.utility.ExternalAccount.implementation.SwappingLayout.child.core.ExternalLogInClickable;

public class LogInViewChild extends ChildForSwapping implements ExternalLogInClickable {

    Button logIn;

    public LogInViewChild(Context context, int layoutId) {
        super(context, layoutId);
        init();
    }

    void init() {
        logIn = (Button) findViewById(R.id.external_login);
    }

    @Override
    public void onLogInListener(OnClickListener l) {
        logIn.setOnClickListener(l);
    }
}
