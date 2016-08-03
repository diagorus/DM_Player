package com.dmplayer.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmplayer.R;

/**
 * Created by Алексей on 03.08.2016.
 */
public class FragmentVK extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_vk, null);
        setupInitialViews(rootview);
        return rootview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupInitialViews(View inflatreView) {

    }
}
