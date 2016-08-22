package com.dmplayer.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.DialogFragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.fragments.FragmentSettings;
import com.dmplayer.internetservices.APIService;
import com.dmplayer.models.VkAlbumsResp;
import com.dmplayer.models.VkUserDataResp;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.uicomponent.CircleImageView;
import com.dmplayer.uicomponent.SwappingLinearLayout;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProfileDialog extends DialogFragment implements View.OnClickListener {
    private Button buttonOK, buttonCancel;
    private Button buttonLoginVk;
    private TextView vkName;
    private TextView vkSongs;
    private TextView vkAlbums;
    private EditText nickName;
    private CircleImageView avatar;
    private CircleImageView vkAvatar;
    private View view;
    private LinearLayout detector;
    private SwappingLinearLayout vkProfile;
    private ProgressDialog progressDialog;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Uri photoFromGallery;

    private static File photoDirectory;

    private String TAG = "ProfileDialog_Error";
    public final static String VK_DATA = "VK_DATA";
    public final static String LOGGED_VK = "LOGGED_VK";

    private int where = 0;
    private String initialName;
    private boolean isAvatarChanged = false;

    private boolean isLoggedViaVk = false;
    private boolean isJustLoggedViaVk = false;

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
    public void onClick(View v) {

        if (v.getId() != R.id.profile_dialog_name) {
            hideKeys();
            nickName.setCursorVisible(false);
        }

        switch (v.getId()) {
            case R.id.buttonOK:
                new SaveDataTask().execute();
                getDialog().dismiss();
                break;
            case R.id.buttonCancel:
                deletePhoto();
                getDialog().dismiss();
                break;
            case  R.id.profile_dialog_name:
                nickName.setCursorVisible(true);
                break;
            case R.id.profile_dialog_avatar:
                setupAndRunAvatarDialog();
                break;
        }
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
                new VkLoginTask().execute(res.accessToken, res.userId);
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
                    photoFromGallery = data.getData();
                    avatar.setImageURI(null);
                    avatar.setImageURI(photoFromGallery);
                    isAvatarChanged = true;
                }
                break;
            case FragmentSettings.CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    avatar.setImageURI(null);
                    avatar.setImageURI(generateAvatarUri(false));
                    isAvatarChanged = true;
                }
                break;
        }
    }

    void init() {
        buttonOK = (Button) view.findViewById(R.id.buttonOK);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
        nickName = (EditText) view.findViewById(R.id.profile_dialog_name);
        avatar = (CircleImageView) view.findViewById(R.id.profile_dialog_avatar);
        detector = (LinearLayout) view.findViewById(R.id.root_detector);
        vkProfile = (SwappingLinearLayout) view.findViewById(R.id.vk_profile);

        buttonOK.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        nickName.setOnClickListener(this);
        avatar.setOnClickListener(this);
        detector.setOnClickListener(this);

        setDefaultSettings();
    }

    void initForVkProfileFirst() {
        buttonLoginVk = (Button) view.findViewById(R.id.button_login_vk);
        buttonLoginVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.login(ProfileDialog.this, VKScope.AUDIO, VKScope.OFFLINE);
            }
        });
    }

    void initForVkProfileSecond() {
        vkAvatar = (CircleImageView) view.findViewById(R.id.vk_avatar);
        vkName = (TextView) view.findViewById(R.id.vk_name);
        vkSongs = (TextView) view.findViewById(R.id.vk_songsCount);
        vkAlbums = (TextView) view.findViewById(R.id.vk_albumsCount);
    }

    private void setDefaultSettings() {
        String avatarPhoto =  sharedPreferences.getString(FragmentSettings.AVATAR, "");
        String nameText = sharedPreferences.getString(FragmentSettings.NAME, "");
        isLoggedViaVk = sharedPreferences.getBoolean(LOGGED_VK, false);

        Uri avatarPhotoUri = Uri.parse(avatarPhoto);
        if (DMPlayerUtility.isURIExists(avatarPhotoUri)) {
            DMPlayerUtility.settingPicture(avatar, avatarPhotoUri);
        } else {
            DMPlayerUtility.settingPicture(avatar, R.drawable.drawer_default_avatar);
        }

        if (!nameText.equals(""))
            nickName.setText(nameText);
        else
            nickName.setText("Anonymous");

        if (isLoggedViaVk) {
            vkProfile.setSecondLayout();
            initForVkProfileSecond();

            String vkNameFromPrefs = sharedPreferences.getString("VKNAME", "");
            String vkSurnameFromPrefs = sharedPreferences.getString("VKSURNAME", "");
            String vkPhotoFromPrefs = sharedPreferences.getString("VKPHOTOURI", "");
            String vkSongsCountFromPrefs = sharedPreferences.getString("VKSONGSCOUNT", "");
            String vkAlbumsCountFromPrefs = sharedPreferences.getString("VKALBUMSCOUNT", "");

            vkName.setText(vkNameFromPrefs + " " + vkSurnameFromPrefs);
            vkSongs.setText("Songs: " + vkSongsCountFromPrefs);
            vkAlbums.setText("Albums: " + vkAlbumsCountFromPrefs);
            DMPlayerUtility.settingPicture(vkAvatar, Uri.parse(vkPhotoFromPrefs));
        } else {
            vkProfile.setFirstLayout();
            initForVkProfileFirst();
        }

        nickName.setCursorVisible(false);
        initialName = nickName.getText().toString();
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

    private boolean deletePhoto() {
        File photo = new File(generateAvatarUri(false).getPath());
        return photo.delete();
    }

    private void hideKeys() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nickName.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showVkQuestionDialog() {
        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setMessage("Do you want to customize your VK playlists now?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                }).create();
        adb.show();
    }

    private void showSaveQuestionDialog() {
        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setMessage("You have some unsaved changes,\nAre you sure to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProfileDialog.this.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                }).create();
        adb.show();
    }

    private class VkLoginTask extends AsyncTask<String, Void, Void> {
        private APIService service;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            vkData = new HashMap<>();

            createApiService();

            vkProfile.setSecondLayout();
            initForVkProfileSecond();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Wait a bit...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... accessTokenAndUserId) {

            Map<String, String> optionsForUserData = new HashMap<>();
            optionsForUserData.put("fields", "photo_100");
            optionsForUserData.put("access_token", accessTokenAndUserId[0]);
            optionsForUserData.put("v", "5.53");

            Call<VkUserDataResp> callForUserData = service.loadUserData(optionsForUserData);

            Map<String, String> optionsForSongsCount = new HashMap<>();
            optionsForSongsCount.put("owner_id", accessTokenAndUserId[1]);
            optionsForSongsCount.put("access_token", accessTokenAndUserId[0]);
            optionsForSongsCount.put("v", "5.53");

            Call<ResponseBody> callForSongsCount = service.loadSongsCount(optionsForSongsCount);

            Map<String, String> optionsForAlbumsCount = new HashMap<>();
            optionsForAlbumsCount.put("offset", "0");
            optionsForAlbumsCount.put("count", "100");
            optionsForAlbumsCount.put("owner_id", accessTokenAndUserId[1]);
            optionsForAlbumsCount.put("access_token", accessTokenAndUserId[0]);
            optionsForAlbumsCount.put("v", "5.53");

            Call<VkAlbumsResp> callForAlbumsCount = service.loadAlbums(optionsForAlbumsCount);

            try {
                Response<VkUserDataResp> responseUserData = callForUserData.execute();
                Response<ResponseBody> responseSongsCount = callForSongsCount.execute();
                Response<VkAlbumsResp> responseAlbumsCount = callForAlbumsCount.execute();

                String[] userData = responseUserData.body().getStringValues();
                vkData.put("vkName", userData[1]);
                vkData.put("vkSurname", userData[2]);
                vkData.put("vkPhotoURL", userData[3]);

                String json = responseSongsCount.body().string();
                String songCount = json.substring(json.lastIndexOf(":") + 1, json.length() - 1);
                vkData.put("vkSongsCount", songCount);

                String albumsCount = responseAlbumsCount.body().getResponse().getAlbumCount();
                vkData.put("vkAlbumsCount", albumsCount);

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            vkData.put("vkAccessToken", accessTokenAndUserId[0]);
            vkData.put("vkUserId", accessTokenAndUserId[1]);

            return null;
        }

        @Override
        protected void onPostExecute(Void data) {
            super.onPostExecute(data);

            isJustLoggedViaVk = true;

            setVkProfile();

            progressDialog.dismiss();
        }

        private void setVkProfile() {

            vkName.setText(vkData.get("vkName") + " " + vkData.get("vkSurname"));
            Glide.with(ProfileDialog.this).load(vkData.get("vkPhotoURL")).into(vkAvatar);

            vkSongs.setText("Songs: " + vkData.get("vkSongsCount"));
            vkAlbums.setText("Albums: " + vkData.get("vkAlbumsCount"));
        }

        private void createApiService() {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(VkSettings.VK_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(APIService.class);
        }
    }

    private class SaveDataTask extends AsyncTask<Void, Void, Void> {
        boolean isNameChanged;
        String currentName;

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
                File avatarPhoto = new File(generateAvatarUri(true).getPath());
                switch (where) {
                    case FragmentSettings.GALLERY_REQUEST:
                        File pictureToCopy = new File(
                                DMPlayerUtility.getRealPathFromURI(getActivity(), photoFromGallery));
                        try {
                            DMPlayerUtility.copyFile(pictureToCopy, avatarPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }

                        deletePhoto();
                        break;
                    case FragmentSettings.CAMERA_REQUEST:
                        File photoTaken = new File(generateAvatarUri(false).getPath());
                        photoTaken.renameTo(avatarPhoto);

                        break;
                }

                editor.putString(FragmentSettings.AVATAR, avatarPhoto.toURI().toString());
            }

            if (isNameChanged) {
                editor.putString(FragmentSettings.NAME, currentName);
            }

            if (isJustLoggedViaVk) {
                editor.putBoolean(LOGGED_VK, true);

                vkData.put("vkPhotoURI",generateVkAvatarUri().toString());

                for (String key : vkData.keySet()) {
                    editor.putString(key.toUpperCase(), vkData.get(key));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isJustLoggedViaVk) {
                String vkAvatarUrl = vkData.get("vkPhotoURL");
                downloadVkAvatar(vkAvatarUrl);
            }

            if (isDataChanged()) {
                editor.apply();
                startActivity(new Intent(getActivity(), DMPlayerBaseActivity.class));
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }
        }

        private void downloadVkAvatar(String url) {
            Glide.with(getActivity())
                    .load(url)
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 100)
                    .into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onResourceReady(final byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    File dir = new File(checkPhotoDirectory());
                                    File file = new File(checkPhotoDirectory() + "/" + "vk_photo" +".jpg");
                                    try {
                                        if (!dir.mkdirs() && (!dir.exists() || !dir.isDirectory())) {
                                            throw new IOException("Cannot ensure parent directory for file " + file);
                                        }
                                        BufferedOutputStream s = new BufferedOutputStream(new FileOutputStream(file));
                                        s.write(resource);
                                        s.flush();
                                        s.close();
                                    } catch (IOException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                    return null;
                                }
                            }.execute();
                        }
                    });
        }

        private Uri generateVkAvatarUri() {
            File file = new File(checkPhotoDirectory() + "/" + "vk_photo" +".jpg");

            return Uri.fromFile(file);
        }

        private boolean isDataChanged() {
            return (!initialName.equals(nickName.getText().toString()))
                    || isAvatarChanged
                    || isJustLoggedViaVk;
        }

    }
}
