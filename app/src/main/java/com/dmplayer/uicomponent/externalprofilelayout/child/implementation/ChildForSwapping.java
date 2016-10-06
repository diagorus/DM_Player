package com.dmplayer.uicomponent.externalprofilelayout.child.implementation;


import android.content.Context;
import android.widget.LinearLayout;

public class ChildForSwapping extends LinearLayout {
    private int layoutId;
    private Context context;

    public ChildForSwapping(Context context, int layoutId) {
        super(context);

        this.context = context;
        this.layoutId = layoutId;

        setLayoutId(layoutId);
        inflate(getContext(), layoutId, this);
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
