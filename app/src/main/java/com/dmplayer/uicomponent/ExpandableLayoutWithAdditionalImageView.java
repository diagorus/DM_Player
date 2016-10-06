package com.dmplayer.uicomponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.dmplayer.R;

public class ExpandableLayoutWithAdditionalImageView extends ExpandableLayout {
    private ImageView additionalImageView;

    public ExpandableLayoutWithAdditionalImageView(Context context) {
        super(context);
    }

    public ExpandableLayoutWithAdditionalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableLayoutWithAdditionalImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init()
    {
        additionalImageView = (ImageView) findViewById(R.id.ad_imageView);
        additionalImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
