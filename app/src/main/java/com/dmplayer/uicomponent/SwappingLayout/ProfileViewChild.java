package com.dmplayer.uicomponent.SwappingLayout;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dmplayer.R;
import com.dmplayer.models.ExternalProfileObject;
import com.dmplayer.uicomponent.CircleImageView;

public class ProfileViewChild extends ChildForSwapping implements ExternalProfileSettable {
    CircleImageView avatar;
    TextView nickname;
    TextView songsCount;
    TextView albumsCount;
    ImageView refresh;
    ImageView logOut;

    public ProfileViewChild(Context context, int layoutId) {
        super(context, layoutId);
        init();
    }

    void init() {
        avatar = (CircleImageView) findViewById(R.id.external_avatar);
        nickname = (TextView) findViewById(R.id.external_nickname);
        songsCount = (TextView) findViewById(R.id.external_songsCount);
        albumsCount = (TextView) findViewById(R.id.external_albumsCount);
        refresh = (ImageView) findViewById(R.id.external_refresh);
        logOut = (ImageView) findViewById(R.id.external_logout);

        if(isNotInitialized()) {
            throw new IllegalArgumentException("Your profile layout views wasn't initialized," +
                    " check their names");
        }
    }

    private boolean isNotInitialized() {
        return (avatar == null || nickname == null ||
                songsCount == null || albumsCount == null ||
                refresh == null || logOut == null);
    }

    @Override
    public void setProfile(ExternalProfileObject profile) {
        nickname.setText(profile.getNickname());
        Glide.with(getContext()).load(profile.getPhotoUrl()).into(avatar);
        songsCount.setText("Songs: " + profile.getSongsCount());
        albumsCount.setText("Albums: " + profile.getAlbumsCount());
    }

    @Override
    public void setOnRefreshListener(OnClickListener l) {
        refresh.setOnClickListener(l);
    }

    @Override
    public void setOnLogOutListener(OnClickListener l) {
        logOut.setOnClickListener(l);
    }
}
