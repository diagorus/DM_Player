package com.dmplayer.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface.OnClickListener;
import android.widget.LinearLayout;

import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.fragments.FragmentSettings;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.uicomponent.CircleImageView;

import java.io.File;
import java.io.IOException;


public class ProfileDialog extends DialogFragment implements View.OnClickListener {
    private Button buttonOK, buttonCancel;
    private EditText name;
    private CircleImageView avatar;
    private View view;
    private SharedPreferences sharedPreferences;
    private Uri photoFromGallery;
    private boolean wasAvatarChanged = false;

    private static File photoDirectory;

    private String TAG = "ProfileDialog";

    private int where = 0;
    private String initialName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        view = inflater.inflate(R.layout.profile_dialog, container);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        init();

        return view;
    }

    void init() {
        buttonOK = (Button) view.findViewById(R.id.buttonOK);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
        name = (EditText) view.findViewById(R.id.profile_dialog_name);
        avatar = (CircleImageView) view.findViewById(R.id.profile_dialog_avatar);
        LinearLayout detector = (LinearLayout) view.findViewById(R.id.root_detector);
        setDefaultSettings();

        buttonOK.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        name.setOnClickListener(this);
        avatar.setOnClickListener(this);
        detector.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() != R.id.profile_dialog_name) {
            hideKeys();
            name.setCursorVisible(false);
        }

        switch (v.getId()) {
            case R.id.buttonOK:
                saveChanges();
                getDialog().dismiss();
                break;
            case R.id.buttonCancel:
                deletePhoto();
                getDialog().dismiss();
                break;
            case  R.id.profile_dialog_name:
                name.setCursorVisible(true);
                break;
            case R.id.profile_dialog_avatar:
                setupAndRunAvatarDialog();
                break;
        }

    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        where = requestCode;
        switch (requestCode) {
            case FragmentSettings.GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    photoFromGallery = returnedIntent.getData();
                    avatar.setImageURI(null);
                    avatar.setImageURI(photoFromGallery);
                    wasAvatarChanged = true;
                }
                break;
            case FragmentSettings.CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    avatar.setImageURI(null);
                    avatar.setImageURI(generateAvatarUri(false));
                    wasAvatarChanged = true;
                }
                break;
        }
    }

    void setupAndRunAvatarDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Photo");
        String[] titles = {"Take photo from camera", "Choose from gallery"};
        OnClickListener ocl = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        Intent toCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        toCamera.putExtra(MediaStore.EXTRA_OUTPUT, generateAvatarUri(false));
                        startActivityForResult(toCamera, FragmentSettings.CAMERA_REQUEST);

                        wasAvatarChanged = true;
                        break;
                    case 1:
                        Intent toGallery = new Intent(Intent.ACTION_PICK);
                        toGallery.setType("image/*");
                        startActivityForResult(toGallery, FragmentSettings.GALLERY_REQUEST);

                        wasAvatarChanged = true;
                        break;
                }
            }
        };
        adb.setItems(titles, ocl);
        adb.show();
    }

    @NonNull
    public static String checkPhotoDirectory() {
        photoDirectory = new File(
                Environment.getExternalStorageDirectory() + "/DMPlayer/",
                "DMPlayer_photos");
        if (!photoDirectory.exists())
            photoDirectory.mkdirs();
        return photoDirectory.getPath();
    }

    private Uri generateAvatarUri(boolean conf) {
        File file = new File(checkPhotoDirectory() + "/" + "photo_avatar" + ((conf)? "" : "_new") +".jpg");

        return Uri.fromFile(file);
    }

    private void saveChanges() {
        if (wasAvatarChanged) {
            File avatarPhoto = new File(generateAvatarUri(true).getPath());
            switch (where) {
                case FragmentSettings.GALLERY_REQUEST:
                    File pictureToCopy = new File(
                            DMPlayerUtility.getRealPathFromURI(getActivity(), photoFromGallery));
                    try {
                        DMPlayerUtility.copyFile(pictureToCopy, avatarPhoto);
                    } catch (IOException ioex) {
                        Log.e(TAG, "Error occurred while coping photo");
                    }

                    deletePhoto();
                    break;
                case FragmentSettings.CAMERA_REQUEST:
                    File photoTaken = new File(generateAvatarUri(false).getPath());
                    photoTaken.renameTo(avatarPhoto);

                    break;

            }
            sharedPreferences
                    .edit()
                    .putString(FragmentSettings.AVATAR, avatarPhoto.toURI().toString())
                    .apply();
        }

        boolean wasNameChaged = !initialName.equals(name.getText().toString());
        if (wasNameChaged) {
            sharedPreferences
                    .edit()
                    .putString(FragmentSettings.NAME, name.getText().toString())
                    .apply();
        }
        if (wasAvatarChanged || wasNameChaged) {
            startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
            getActivity().finish();
            getActivity().overridePendingTransition(0, 0);
        }
    }

    private boolean deletePhoto() {
        File photo = new File(generateAvatarUri(false).getPath());
        return photo.delete();
    }

    private void setDefaultSettings() {
        String avatarPhoto =  sharedPreferences.getString(FragmentSettings.AVATAR, "");
        String nameText = sharedPreferences.getString(FragmentSettings.NAME, "");

        Uri avatarPhotoUri = Uri.parse(avatarPhoto);
        if (DMPlayerUtility.isURIExists(avatarPhotoUri)) {
            DMPlayerUtility.settingPicture(avatar, avatarPhotoUri);
        } else {
            DMPlayerUtility.settingPicture(avatar, R.drawable.drawer_default_avatar);
        }

        if (!nameText.equals(""))
            name.setText(nameText);
        else
            name.setText("Anonymous");

        name.setCursorVisible(false);
        initialName = name.getText().toString();
    }

    private void hideKeys() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
