/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.utility.ColorChooserDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FragmentSettings extends Fragment implements View.OnClickListener {

    final static int GALLERY_REQUEST = 1;

    public final static String HEADER_PICTURE = "HEADER_PICTURE";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public FragmentSettings() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_settings, null);
        setupInitialViews(rootview);
        return rootview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setupInitialViews(View rootview) {
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        ((RelativeLayout) rootview.findViewById(R.id.relativeLayoutChooseTheme)).setOnClickListener(this);
        ((RelativeLayout) rootview.findViewById(R.id.relativeLayoutChangeHeaderBackground)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeLayoutChooseTheme:
                showColorChooseDialog();
                break;

            case R.id.relativeLayoutCustomizeProfile:

                break;

            case R.id.relativeLayoutChangeHeaderBackground:
                Intent picturePickerIntent = new Intent(Intent.ACTION_PICK);
                picturePickerIntent.setType("image/*");
                startActivityForResult(picturePickerIntent, GALLERY_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = returnedIntent.getData();

                    setHeaderPicture(selectedImage);

                    startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                }
                break;
        }
    }

    public void setThemeFragment(int theme) {
        editor = sharedPreferences.edit();
        editor.putInt("THEME", theme).apply();
    }

    public void setHeaderPicture(Uri picture) {
        editor = sharedPreferences.edit();
        editor.putString(HEADER_PICTURE, picture.toString()).apply();
    }

    private void showColorChooseDialog() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ColorChooserDialog dialog = new ColorChooserDialog();
        dialog.setOnItemChoose(new ColorChooserDialog.OnItemChoose() {
            @Override
            public void onClick(int position) {
                setThemeFragment(position);
            }

            @Override
            public void onSaveChange() {
                startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }
        });
        dialog.show(fragmentManager, "fragment_color_chooser");
    }
}