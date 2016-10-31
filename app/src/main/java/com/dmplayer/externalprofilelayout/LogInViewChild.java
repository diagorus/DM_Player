package com.dmplayer.externalprofilelayout;

import android.content.Context;
import android.widget.Button;

import com.dmplayer.R;

public class LogInViewChild extends ChildForSwapping {
    private Button logIn;

    public LogInViewChild(Context context, int layoutId) {
        super(context, layoutId);
        init();
    }

    void init() {
        logIn = (Button) findViewById(R.id.external_login);
    }

    public void setOnLogInListener(OnClickListener l) {
        logIn.setOnClickListener(l);
    }
}
