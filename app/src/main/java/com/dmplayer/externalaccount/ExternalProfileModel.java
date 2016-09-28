package com.dmplayer.externalaccount;

import android.graphics.Bitmap;

public interface ExternalProfileModel {
    Bitmap getPhoto();
    String getNickname();
    String getSongsCount();
    String getAlbumsCount();
}
