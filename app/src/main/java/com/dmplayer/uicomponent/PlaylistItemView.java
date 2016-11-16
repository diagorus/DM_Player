package com.dmplayer.uicomponent;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.models.PlaylistItem;

public class PlaylistItemView extends RelativeLayout {
    public PlaylistItemView(Context context, PlaylistItem playlistItem) {
        super(context);
        init(playlistItem);
    }

    private void init(PlaylistItem playlistItem) {
        inflate(getContext(), R.layout.expandable_item_playlist, this);

        ((TextView) findViewById(R.id.name)).setText(playlistItem.getName());
        ((TextView) findViewById(R.id.details)).setText(playlistItem.getDetails());
        ((ImageView) findViewById(R.id.icon)).setImageResource(playlistItem.getImageResourceId());
    }
}