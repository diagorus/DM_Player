package com.dmplayer.uicomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmplayer.R;

public class ExpandableLayout extends LinearLayout {
    private static final String ANDROID_SCHEME = "http://schemas.android.com/apk/res/android";

    private static final String SAVE_STATE_EXPANDED = "IS_EXPANDED";
    private static final String SAVE_STATE_SUPER = "STATE_SUPER";

    RelativeLayout header;
    LinearLayout content;

    String titleText;
    String detailsText;
    int imageResource;

    int originalOrientation;

    boolean isExpanded;

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    public LinearLayout getContent() {
        return content;
    }

    private void init(Context context, AttributeSet attrs) {
        originalOrientation = Integer.parseInt(attrs.getAttributeValue(ANDROID_SCHEME, "orientation"));
        setOrientation(VERTICAL);

        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ExpandableLayout, 0, 0);

        try {
            titleText = a.getString(R.styleable.ExpandableLayout_text_title);
            detailsText = a.getString(R.styleable.ExpandableLayout_text_details);
            imageResource = a.getResourceId(R.styleable.ExpandableLayout_src_image,
                    android.R.color.transparent);
        } finally {
            a.recycle();
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    protected void setupHeader() {
        LayoutInflater.from(getContext()).inflate(R.layout.expandable_layout, this, true);

        header = (RelativeLayout) findViewById(R.id.header);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(titleText);

        TextView details = (TextView) findViewById(R.id.details);
        details.setText(detailsText);

        ImageView image = (ImageView) findViewById(R.id.image);
        image.setImageResource(imageResource);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle savedState = (Bundle) state;
            isExpanded = savedState.getBoolean(SAVE_STATE_EXPANDED, false);
            state = savedState.getParcelable(SAVE_STATE_SUPER);
        }
        setExpandedState(isExpanded);
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle outState = new Bundle();
        outState.putParcelable(SAVE_STATE_SUPER, super.onSaveInstanceState());
        outState.putBoolean(SAVE_STATE_EXPANDED, isExpanded());

        return outState;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        content = new LinearLayout(getContext());
        content.setOrientation(originalOrientation);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(0);
            removeViewAt(0);
            content.addView(child);
        }

        setupHeader();

        content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        addView(content);
        content.setVisibility(GONE);

        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        header.setOnClickListener(l);
    }

    public void changeExpandState() {
        setExpandedState(!isExpanded());
    }

    public void setExpandedState(boolean isExpand) {
        if (isExpand) {
            content.setVisibility(VISIBLE);
            isExpanded = true;

            onExpandListener.OnExpand();
        } else {
            content.setVisibility(GONE);
            isExpanded = false;
        }
    }

    private OnExpandListener onExpandListener;

    public void setOnExpandListener(OnExpandListener onExpandListener) {
        this.onExpandListener = onExpandListener;
    }

    public interface OnExpandListener {
        void OnExpand();
    }
}
