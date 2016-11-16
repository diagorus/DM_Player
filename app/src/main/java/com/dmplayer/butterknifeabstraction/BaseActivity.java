package com.dmplayer.butterknifeabstraction;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
    }

    private void bindViews() {
        ButterKnife.bind(this);
    }
}
