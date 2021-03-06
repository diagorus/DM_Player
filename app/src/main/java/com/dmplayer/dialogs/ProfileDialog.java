package com.dmplayer.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dmplayer.R;
import com.dmplayer.externalprofilelayout.ExternalProfileLayout;
import com.dmplayer.fragments.FragmentSettings;
import com.dmplayer.phonemedia.DMPlayerUtility;
import com.dmplayer.presenters.VkProfilePresenter;
import com.dmplayer.uicomponent.CircleImageView;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.io.File;
import java.io.IOException;

public class ProfileDialog extends DialogFragment {
    public final static String PHOTO_DIR_PATH = Environment.getExternalStorageDirectory() + "/.DM_Player/DM_Player_photos";
    public final static String AVATAR_FILE_PATH = PHOTO_DIR_PATH + "/photo_avatar.jpg";
    public final static String AVATAR_TEMP_FILE_PATH = PHOTO_DIR_PATH + "/photo_avatar_temp.jpg";
    public final static String AVATAR_VK_FILE_PATH = PHOTO_DIR_PATH + "/vk_photo.jpg";

    private String TAG = "ProfileDialog_Error";

    private Button buttonOK, buttonCancel;
    private EditText nickName;
    private CircleImageView avatar;

    VkProfilePresenter vkProfilePresenter;
    ExternalProfileLayout vkProfileView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Uri photoFromGallery;

    private int where;
    private String initialName;
    private boolean isAvatarChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        View v = inflater.inflate(R.layout.dialog_profile, null);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        init(v);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                vkProfilePresenter.onAccountDataReceived(res.accessToken, res.userId);
            }

            @Override
            public void onError(VKError error) {
                Log.e(TAG, "Login response: OnError");

                Toast.makeText(getActivity(), "An error occurred while login in.",
                        Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        where = requestCode;

        switch (requestCode) {
            case FragmentSettings.GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    isAvatarChanged = true;

                    photoFromGallery = data.getData();
                    DMPlayerUtility.settingPicture(avatar, photoFromGallery);
                }
                break;

            case FragmentSettings.CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    isAvatarChanged = true;

                    Uri tempAvatarUri = DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH);
                    DMPlayerUtility.settingPicture(avatar, tempAvatarUri);
                }
                break;
        }
    }

    void init(View v) {
        buttonOK = (Button) v.findViewById(R.id.buttonOK);
        buttonCancel = (Button) v.findViewById(R.id.buttonCancel);
        nickName = (EditText) v.findViewById(R.id.profile_dialog_name);
        avatar = (CircleImageView) v.findViewById(R.id.profile_dialog_avatar);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveDataTask(getActivity()).execute();

                if (getOnWorkDone() != null) {
                    getOnWorkDone().onPositiveAnswer();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DMPlayerUtility.deleteFile(DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH).getPath());

                if (getOnWorkDone() != null)
                    getOnWorkDone().onNegativeAnswer();

                getDialog().dismiss();
            }
        });

        nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickName.setCursorVisible(true);
                nickName.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        nickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    DMPlayerUtility.hideKeys(ProfileDialog.this.getActivity(), nickName);

                    return true;
                }
                return false;
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAvatarDialog();
            }
        });

        setDefaultSettings();

        setVkProfile(v);
    }

    private void setDefaultSettings() {
        setAvatar();

        setName();
    }

    private  void setVkProfile(View v) {
        vkProfileView = (ExternalProfileLayout) v.findViewById(R.id.vk_profile_view);

        vkProfilePresenter = new VkProfilePresenter(vkProfileView);
        vkProfilePresenter.onCreate(this);
    }

    private void setAvatar() {
        String avatarPhoto =  sharedPreferences.getString(FragmentSettings.AVATAR, "");
        Uri avatarPhotoUri = Uri.parse(avatarPhoto);

        if (DMPlayerUtility.isURIExists(avatarPhotoUri)) {
            DMPlayerUtility.settingPicture(avatar, avatarPhotoUri);
        } else {
            DMPlayerUtility.settingPicture(avatar, R.drawable.avatar_default);
        }
    }

    private void setName() {
        String nameText = sharedPreferences.getString(FragmentSettings.NAME, "");

        if (!nameText.equals(""))
            nickName.setText(nameText);
        else
            nickName.setText(R.string.profile_defult_name);

        initialName = nickName.getText().toString();
    }

    void showAvatarDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Photo");
        String[] titles = {"Take photo from camera", "Choose from gallery"};
        OnClickListener ocl = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        Intent toCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        toCamera.putExtra(MediaStore.EXTRA_OUTPUT, DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH));
                        startActivityForResult(toCamera, FragmentSettings.CAMERA_REQUEST);

                        isAvatarChanged = true;
                        break;
                    case 1:
                        Intent toGallery = new Intent(Intent.ACTION_PICK);
                        toGallery.setType("image/*");
                        startActivityForResult(toGallery, FragmentSettings.GALLERY_REQUEST);

                        isAvatarChanged = true;
                        break;
                }
            }
        };
        adb.setItems(titles, ocl);
        adb.show();
    }

    private class SaveDataTask extends AsyncTask<Void, Void, Void> {
        boolean isNameChanged;
        String currentName;

        Context context;

        private SaveDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            editor = sharedPreferences.edit();
            isNameChanged = !initialName.equals(nickName.getText().toString());
            currentName = nickName.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isAvatarChanged) {
                File avatarPhoto = new File(DMPlayerUtility.getUriFromPath(AVATAR_FILE_PATH).getPath());
                switch (where) {
                    case FragmentSettings.GALLERY_REQUEST:
                        File pictureToCopy = new File(
                                DMPlayerUtility.getRealPathFromURI(context, photoFromGallery));
                        try {
                            DMPlayerUtility.copyFile(pictureToCopy, avatarPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }

                        DMPlayerUtility.deleteFile(DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH).getPath());
                        break;
                    case FragmentSettings.CAMERA_REQUEST:
                        File photoTaken = new File(DMPlayerUtility.getUriFromPath(AVATAR_TEMP_FILE_PATH).getPath());
                        photoTaken.renameTo(avatarPhoto);

                        break;
                }

                editor.putString(FragmentSettings.AVATAR, avatarPhoto.toURI().toString());
            }
            if (isNameChanged) {
                editor.putString(FragmentSettings.NAME, currentName);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isDataChanged()) {
                editor.apply();
            }
        }

        private boolean isDataChanged() {
            return (!initialName.equals(nickName.getText().toString()))
                    || isAvatarChanged;
        }
    }


    private OnWorkDone OnWorkDone;

    public OnWorkDone getOnWorkDone() {
        return OnWorkDone;
    }

    public void setOnWorkDone(OnWorkDone OnWorkDone) {
        this.OnWorkDone = OnWorkDone;
    }
}
