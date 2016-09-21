package com.dmplayer.utility.ExternalAccount.implementation.SwappingLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dmplayer.R;
import com.dmplayer.utility.ExternalAccount.core.ExternalAccountView;
import com.dmplayer.utility.ExternalAccount.implementation.SwappingLayout.child.implementation.ChildForSwapping;
import com.dmplayer.utility.ExternalAccount.implementation.SwappingLayout.child.implementation.LogInViewChild;
import com.dmplayer.utility.ExternalAccount.implementation.SwappingLayout.child.implementation.ProfileViewChild;

public abstract class ExternalProfileLayout extends LinearLayout implements ExternalProfileView, ExternalAccountView {
    private LogInViewChild startingLayout;
    private ProfileViewChild swappingLayout;
    private ChildForSwapping loadingLayout;

    public LogInViewChild getStartingLayout() {
        return startingLayout;
    }

    public ProfileViewChild getSwappingLayout() {
        return swappingLayout;
    }

    public ChildForSwapping getLoadingLayout() {
        return loadingLayout;
    }

    public ExternalProfileLayout(Context context) {
        super(context);
    }

    public ExternalProfileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExternalProfileLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExternalProfileLayout,
                0, 0);
        int startingLayoutRes;
        int swappingLayoutRes;
        int loadingLayoutRes;
        try {
            startingLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_starting_layout, -1);
            swappingLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_swapping_layout, -1);
            loadingLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_loading_layout, -1);
        } finally {
            a.recycle();
        }

        startingLayout = new LogInViewChild(getContext(), startingLayoutRes);
        loadingLayout = new ChildForSwapping(getContext(), loadingLayoutRes);
        swappingLayout = new ProfileViewChild(getContext(), swappingLayoutRes);
    }

    @Override
    public void setLogInLayout() {
        removeAllViews();
        addView(startingLayout);
    }

    @Override
    public void setLoadingLayout() {
        removeAllViews();
        addView(loadingLayout);
    }

    @Override
    public void setProfileLayout() {
        removeAllViews();
        addView(swappingLayout);
    }
}
