package com.dmplayer.uicomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.dmplayer.R;

public class ExpandableLayoutExternalAccount extends ExpandableLayout {
    private int messageLayout;

    public ExpandableLayoutExternalAccount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExpandableLayoutExternalAccount(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ExpandableLayoutExternalAccount, 0, 0);

        try {
            messageLayout = a.getResourceId(R.styleable.ExpandableLayoutExternalAccount_massage_layout, -1);
        } finally {
            a.recycle();
        }

        if (messageLayout == -1) {
            throw new IllegalArgumentException("You must specify \"massage_layout\" attribute to use this layout!");
        }
    }

    public void setUsualLayout() {
        removeAllViews();
        setupHeader();
    }

    public void setMessageLayout() {
        removeAllViews();
        LayoutInflater.from(getContext()).inflate(messageLayout, this, true);
    }
}