/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.activities.MusicChooserActivity;
import com.dmplayer.dialogs.OnWorkDone;
import com.dmplayer.dialogs.ProfileDialog;
import com.dmplayer.dialogs.ThemeDialog;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;
import com.dmplayer.utility.DMPlayerUtility;
import com.dmplayer.utility.LogWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentSettings extends Fragment implements View.OnClickListener {
    public final static int GALLERY_REQUEST = 1;
    public final static int CAMERA_REQUEST = 2;
    public final static int PICKER_REQUEST = 3;

    public final static String HEADER_BACKGROUND = "HEADER_BACKGROUND";
    public final static String AVATAR = "AVATAR";
    public final static String NAME = "NAME";

    private SharedPreferences sharedPreferences;

    private String TAG = "FragmentSettings";

    private ArrayList<Uri> mSongUri = new ArrayList<>();
    private ArrayList<SongDetail> songList = new ArrayList<>();
    private String MIXING_MODE="mixing_mode";
    private TextView textViewMixingMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);
        setupInitialViews(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setupInitialViews(View view) {
        sharedPreferences = getActivity().getSharedPreferences("VALUES", MODE_PRIVATE);

        view.findViewById(R.id.relativeLayout_choose_theme).setOnClickListener(this);
        view.findViewById(R.id.relativeLayout_customize_profile).setOnClickListener(this);
        view.findViewById(R.id.relativeLayout_change_header_back).setOnClickListener(this);
        view.findViewById(R.id.relativeLayoutCreatePlaylist).setOnClickListener(this);
        view.findViewById(R.id.relativeLayoutMusicChooser).setOnClickListener(this);
        view.findViewById(R.id.relativeLayoutMusicMixEnabled).setOnClickListener(this);
        textViewMixingMode = (TextView) view.findViewById(R.id.textViewMusicMixEnabledDescription);
        setMixingModeInTextView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeLayout_choose_theme:
                showThemeDialog();
                break;

            case R.id.relativeLayout_customize_profile:
                showColorProfileDialog();
                break;

            case R.id.relativeLayout_change_header_back:
                Intent toGallery = new Intent(Intent.ACTION_PICK);
                toGallery.setType("image/*");
                startActivityForResult(toGallery, GALLERY_REQUEST);
                break;

            case R.id.relativeLayoutCreatePlaylist:
                try {
                    final Playlist a = new Playlist();
//                    if(mSongUri!=null) {
//                        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
//                        PhoneMediaControl.setPhoneMediaControlInterface(new PhoneMediaControl.PhoneMediaControlInterface() {
//
//                            @Override
//                            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
//                                songList = songsList_;
//                                a.addSong(songList.get(0));
//                            }
//                        });
//                        for (Uri uri :
//                                mSongUri) {
//                            mPhoneMediaControl.loadMusicList(getActivity(), -1, PhoneMediaControl
//                                            .SongsLoadFor.Playlist,
//                                    uri.getPath());
//
//                        }
//                    }

                    if (songList!=null) {
                        for (SongDetail song:
                             songList) {
                            a.addSong(song);
                        }
                        a.setName("2cyka blyat idi nahuy pidoras");
                        try {
                            File b = new File(Environment
                                    .getExternalStorageDirectory() + "/DMPlayer/DMPlayer_playlists");
                            if (!b.exists())
                                b.mkdirs();
                            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(Environment
                                    .getExternalStorageDirectory() + "/DMPlayer/DMPlayer_playlists/a.dpl"));
                            os.writeObject(a);
                            os.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        LogWriter.info(TAG, "OK");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case R.id.relativeLayoutMusicChooser:
                Intent picker = new Intent(getActivity(), MusicChooserActivity.class);
                startActivityForResult(picker,PICKER_REQUEST);
                break;
            case R.id.relativeLayoutMusicMixEnabled:
                setMixingMode();
                setMixingModeInTextView();
                break;
        }
    }

    private void setMixingMode() {
        String mixing_mode = sharedPreferences.getString(MIXING_MODE, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(mixing_mode.equals("ON")){
            editor.putString(MIXING_MODE, "OFF");
        } else if(mixing_mode.equals("OFF")){
            editor.putString(MIXING_MODE, "ON");
        } else if(mixing_mode.equals("")){
            editor.putString(MIXING_MODE, "OFF");
        }
        editor.apply();
    }

    void setMixingModeInTextView() {
        String savedText = sharedPreferences.getString(MIXING_MODE, "");

        if (savedText.equals("ON")) {
            textViewMixingMode.setText("Mixing mode on");
        } else if(savedText.equals("OFF")) {
            textViewMixingMode.setText("Mixing mode off");
        } else if(savedText.equals("")) {
            textViewMixingMode.setText("Mixing mode off");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = returnedIntent.getData();

                    String pathToBackground = copyBackgroundToStorage(selectedImage);
                    setHeaderBackground(pathToBackground);

                    startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                }
                break;

            case PICKER_REQUEST:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        songList = (ArrayList<SongDetail>) returnedIntent.getExtras()
                                .getSerializable("songs");
                        LogWriter.info(TAG, "Got songs " + mSongUri.get(mSongUri.size()));
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                        LogWriter.info(TAG,ex.toString());
                    }
                }
                break;
        }
    }

    public void setHeaderBackground(String picture) {
        sharedPreferences.edit()
                .putString(HEADER_BACKGROUND, picture)
                .apply();
    }

    private void showThemeDialog() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        ThemeDialog dialog = new ThemeDialog();
        dialog.setOnItemChoose(new OnWorkDone() {
            @Override
            public void onAgree() {
                startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }

            @Override
            public void onRefuse() {}
        });
        dialog.show(fragmentManager, "fragment_theme");
    }

    private void showColorProfileDialog() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        ProfileDialog dialog = new ProfileDialog();
        dialog.setOnWorkDone(new OnWorkDone() {
            @Override
            public void onAgree() {
                startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }

            @Override
            public void onRefuse() {
            }
        });
        dialog.setCancelable(false);
        dialog.show(fragmentManager, "fragment_profile");
    }

    private String copyBackgroundToStorage(Uri picture) {
        File backgroundSource = new File(DMPlayerUtility.getRealPathFromURI(getActivity(), picture));
        File backgroundDest = new File(ProfileDialog.PHOTO_DIR_PATH + "/" + "header_background" +
                backgroundSource
                        .getPath()
                        .substring(backgroundSource
                                .getPath()
                                .lastIndexOf(".")));
        try {
            DMPlayerUtility.copyFile(backgroundSource, backgroundDest);
        } catch (IOException ioex) {
            Log.e(TAG, "Error occurred while coping background");
        }
        return backgroundDest.toURI().toString();
    }
}