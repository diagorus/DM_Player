package com.dmplayer.uicomponent;


import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmplayer.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Alexvojander on 28.11.2016.
 */

public class GoogleMapWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater=null;
    Bitmap img_bmp=null;
    public void setImg_res(Bitmap img_bmp){
        this.img_bmp=img_bmp;
    }

    public GoogleMapWindowAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return (null);

    }

    @Override
    public View getInfoContents(Marker marker) {

        View popup=inflater.inflate(R.layout.google_map_window_adapter, null);
        ImageView im=(ImageView)popup.findViewById(R.id.icon) ;


        if(img_bmp==null){
            im.setImageResource(R.drawable.default_avatar);
        }else{
            im.setImageBitmap(img_bmp);
        }

        TextView tv=(TextView)popup.findViewById(R.id.title);

        tv.setText(marker.getTitle());
        tv=(TextView)popup.findViewById(R.id.snippet);
        tv.setText(marker.getSnippet());

        return(popup);
    }
}
