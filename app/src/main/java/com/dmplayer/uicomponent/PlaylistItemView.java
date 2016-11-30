package com.dmplayer.uicomponent;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.models.PlaylistItem;

import butterknife.ButterKnife;

public class PlaylistItemView extends RelativeLayout {
    public PlaylistItemView(Context context, PlaylistItem playlistItem) {
        super(context);
        init(playlistItem);
    }

    private void init(PlaylistItem playlistItem) {
        inflate(getContext(), R.layout.expandable_item_playlist, this);

        ((TextView) ButterKnife.findById(this, R.id.name)).setText(playlistItem.getName());
        ((TextView) ButterKnife.findById(this, R.id.details)).setText(playlistItem.getDetails());
        ((ImageView) ButterKnife.findById(this, R.id.icon)).setImageResource(playlistItem.getImageResourceId());
    }
}