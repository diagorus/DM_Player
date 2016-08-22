package com.dmplayer.uicomponent;


import android.content.Context;
import android.widget.LinearLayout;

public class ChildForSwapping extends LinearLayout {
    int layoutId;

    public ChildForSwapping(Context context, int layoutId) {
        super(context);
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
