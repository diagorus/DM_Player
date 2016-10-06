package com.dmplayer.models;

import android.app.Fragment;

public class ExpandableLayoutItem {
    private final Fragment fragment;
    private final String name;
    private final String details;

    public ExpandableLayoutItem(Fragment fragment, String name, String details) {
        this.fragment = fragment;
        this.name = name;
        this.details = details;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}
