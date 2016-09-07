package com.dmplayer.utility.dialogs;

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
import com.dmplayer.fragments.FragmentSettings;
import com.dmplayer.models.ExternalMusicAccount;
import com.dmplayer.models.VkObjects.VkAccount;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.uicomponent.CircleImageView;
import com.dmplayer.uicomponent.SwappingLayout.ExternalProfileLayout;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProfileDialog extends DialogFragment {
    private Button buttonOK, buttonCancel;
    private EditText nickName;
    private CircleImageView avatar;
    private View view;
    private ExternalProfileLayout vkProfile;

    ExternalMusicAccount vkAccount;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Uri photoFromGallery;

    public final static String PHOTO_DIR_PATH = Environment.getExternalStorageDirectory() + "/.DM_Player/DM_Player_photos";
    public final static String AVATAR_FILE_PATH = PHOTO_DIR_PATH + "/photo_avatar.jpg";
    public final static String AVATAR_TEMP_FILE_PATH = PHOTO_DIR_PATH + "/photo_avatar_temp.jpg";
    public final static String AVATAR_VK_FILE_PATH = PHOTO_DIR_PATH + "/vk_photo.jpg";

    private String TAG = "ProfileDialog_Error";
    public final static String VK_DATA = "VK_DATA";
    public final static String LOGGED_VK = "LOGGED_VK";

    private int where = 0;
    private String initialName;
    private boolean isAvatarChanged = false;

    private boolean isJustLoggedViaVk = false;
    private boolean isVkRefreshed = false;
    private boolean isVkLoggedOut = false;

    private Map<String, String> vkData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        view = inflater.inflate(R.layout.profile_dialog, null);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        init();

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                isJustLoggedViaVk = true;
                isVkLoggedOut = false;

                vkAccount = new VkAccount.Builder()
                        .setToken(res.accessToken)
                        .setUserId(res.userId)
                        .build();
                vkAccount.loadProfile();
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

    void init() {
        buttonOK = (Button) view.findViewById(R.id.buttonOK);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
        nickName = (EditText) view.findViewById(R.id.profile_dialog_name);
        avatar = (CircleImageView) view.findViewById(R.id.profile_dialog_avatar);

        vkProfile = (ExternalProfileLayout) view.findViewById(R.id.vk_profile);

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

        vkProfile.setOnLogInListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.login(ProfileDialog.this, VKScope.AUDIO, VKScope.OFFLINE);
            }
        });

        vkProfile.setOnRefreshListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVkRefreshed = true;
                vkAccount.loadProfile();
            }
        });

        vkProfile.setOnLogOutListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVkLoggedOut = true;
                isJustLoggedViaVk = false;
                isVkRefreshed = false;

                vkProfile.onLoggedOut();
            }
        });

        setDefaultSettings();
    }

    private void setDefaultSettings() {
        setAvatar();
        setName();
        setVk();
    }

    private void setAvatar() {
        String avatarPhoto =  sharedPreferences.getString(FragmentSettings.AVATAR, "");
        Uri avatarPhotoUri = Uri.parse(avatarPhoto);

        if (DMPlayerUtility.isURIExists(avatarPhotoUri)) {
            DMPlayerUtility.settingPicture(avatar, avatarPhotoUri);
        } else {
            DMPlayerUtility.settingPicture(avatar, R.drawable.profile_default_avatar);
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

    private void setVk() {
        boolean isLoggedViaVk = sharedPreferences.getBoolean(LOGGED_VK, false);

        if (isLoggedViaVk) {
            setVkData();

//            vkName.setText(vkData.get("vkname") + " " + vkData.get("vksurname"));
//            vkSongs.setText("Songs: " + vkData.get("vksongscount"));
//            vkAlbums.setText("Albums: " + vkData.get("vkalbumscount"));
//            DMPlayerUtility.settingPicture(vkAvatar, Uri.parse(vkData.get("vkphotouri")));
        }
    }

    private void setVkData() {
        vkData = new HashMap<>();
        vkData.put("vkaccesstoken", sharedPreferences.getString("VKACCESSTOKEN", ""));
        vkData.put("vkuserid", sharedPreferences.getString("VKUSERID", ""));
        vkData.put("vkname", sharedPreferences.getString("VKNAME", ""));
        vkData.put("vksurname", sharedPreferences.getString("VKSURNAME", ""));
        vkData.put("vkphotourl", sharedPreferences.getString("VKPHOTOURL", ""));
        vkData.put("vkphotouri", sharedPreferences.getString("VKPHOTOURI", ""));
        vkData.put("vksongscount", sharedPreferences.getString("VKSONGSCOUNT", ""));
        vkData.put("vkalbumscount", sharedPreferences.getString("VKALBUMSCOUNT", ""));
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

    private void rememberVkUser(String accessToken, String userId) {
        sharedPreferences.edit()
                .putString("VKACCESSTOKEN", accessToken)
                .putString("VKUSERID", userId)
                .apply();
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
            if (isVkLoggedOut) {
                editor.putBoolean(LOGGED_VK, false);

                for (String key : vkData.keySet()) {
                    editor.remove(key.toUpperCase());
                }

                vkData = null;
            }
            if (isJustLoggedViaVk || isVkRefreshed) {
                editor.putBoolean(LOGGED_VK, true);

                vkData.put("vkphotouri", DMPlayerUtility.getUriFromPath(AVATAR_VK_FILE_PATH).toString());

                for (String key : vkData.keySet()) {
                    editor.putString(key.toUpperCase(), vkData.get(key));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isJustLoggedViaVk || isVkRefreshed) {
                String vkAvatarUrl = vkData.get("vkphotourl");
                DMPlayerUtility.downloadImage(context, vkAvatarUrl, PHOTO_DIR_PATH, "vk_photo", "jpg");
            }

            if (isVkLoggedOut) {
                DMPlayerUtility.deleteFile(DMPlayerUtility.getUriFromPath(AVATAR_VK_FILE_PATH).getPath());
            }

            if (isDataChanged() || isVkRefreshed || isVkLoggedOut) {
                editor.apply();
            }
        }

        private boolean isDataChanged() {
            return (!initialName.equals(nickName.getText().toString()))
                    || isAvatarChanged
                    || isJustLoggedViaVk;
        }
    }


    private OnWorkDone OnWorkDone;

    public OnWorkDone getOnWorkDone() {
        return OnWorkDone;
    }

    public void setOnWorkDone(OnWorkDone OnWorkDone) {
        this.OnWorkDone = OnWorkDone;
    }

    public interface OnWorkDone {
        void onPositiveAnswer();
        void onNegativeAnswer();
    }
}
