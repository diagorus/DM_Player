package com.dmplayer.uicomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dmplayer.R;

public class SwappingLinearLayout extends LinearLayout {

    private ChildForSwapping startingLayout;
    private ChildForSwapping swappingLayout;

    private boolean wasSwapped = false;

    public SwappingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public SwappingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    public SwappingLinearLayout(Context context) {
        super(context);
    }

    public void setSecondLayout() {
        removeAllViews();
        wasSwapped = true;
        addView(swappingLayout);
    }

    public void setFirstLayout() {
        removeAllViews();
        wasSwapped = false;
        addView(startingLayout);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SwappingLinearLayout,
                0, 0);
        int startingLayoutRes;
        int swappingLayoutRes;
        try {
            startingLayoutRes = a.getResourceId(R.styleable.SwappingLinearLayout_starting_layout, -1);
            swappingLayoutRes = a.getResourceId(R.styleable.SwappingLinearLayout_swapping_layout, -1);
        } finally {
            a.recycle();
        }

        startingLayout = new ChildForSwapping(getContext(), startingLayoutRes);
        swappingLayout = new ChildForSwapping(getContext(), swappingLayoutRes);

        if (!wasSwapped)
            setFirstLayout();
        else
            setSecondLayout();

    }
}
