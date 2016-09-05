package com.dmplayer.utility.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dmplayer.R;
import com.dmplayer.fragments.FragmentSettings;
import com.dmplayer.internetservices.VkAPIService;
import com.dmplayer.models.ExternalMusicAccount;
import com.dmplayer.models.VkAccount;
import com.dmplayer.models.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkProfileUserDataResponse.VkUserDataCollection;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.uicomponent.CircleImageView;
import com.dmplayer.uicomponent.SwappingLayout.ExternalProfileLayout;
import com.dmplayer.utility.VkSettings;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProfileDialog extends DialogFragment implements View.OnClickListener {
    private Button buttonOK, buttonCancel;
    private Button buttonLoginVk;
    private ImageView imageViewLogOut, imageViewRefresh;
    private TextView vkName;
    private TextView vkSongs;
    private TextView vkAlbums;
    private EditText nickName;
    private CircleImageView avatar;
    private CircleImageView vkAvatar;
    private View view;
    private ExternalProfileLayout vkProfile;

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOK:
                new SaveDataTask(getActivity()).execute();
                if (getOnWorkDone() != null)
                    getOnWorkDone().onPositiveAnswer();
                break;
            case R.id.buttonCancel:
                deletePhoto(generateAvatarUri(getActivity(), true).getPath());
                if (getOnWorkDone() != null)
                    getOnWorkDone().onNegativeAnswer();
                getDialog().dismiss();
                break;
            case  R.id.profile_dialog_name:
                nickName.setCursorVisible(true);
                nickName.setInputType(InputType.TYPE_CLASS_TEXT);
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
                isJustLoggedViaVk = true;
                isVkLoggedOut = false;
                ExternalMusicAccount vkAccount = new VkAccount.Builder()
                        .setToken(res.accessToken)
                        .setUserId(res.userId)
                        .setSwappingLayoutController(vkProfile)
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

                    Uri tempAvatarUri = generateAvatarUri(getActivity(), true);
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

        buttonOK.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        nickName.setOnClickListener(this);
        nickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeys();

                    return true;
                }
                return false;
            }
        });

        avatar.setOnClickListener(this);

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
        vkAvatar = (CircleImageView) view.findViewById(R.id.external_avatar);
        vkName = (TextView) view.findViewById(R.id.external_nickname);
        vkSongs = (TextView) view.findViewById(R.id.external_songsCount);
        vkAlbums = (TextView) view.findViewById(R.id.external_albumsCount);
        imageViewLogOut = (ImageView) view.findViewById(R.id.external_logout);
        imageViewRefresh = (ImageView) view.findViewById(R.id.external_refresh);

        imageViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVkRefreshed = true;

                String accessToken = sharedPreferences.getString("VKACCESSTOKEN", "");
                String userId = sharedPreferences.getString("VKUSERID", "");
            }
        });
        imageViewLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVkLoggedOut = true;
                isJustLoggedViaVk = false;
                isVkRefreshed = false;

                vkProfile.setFirstLayout();
                initForVkProfileFirst();
            }
        });
    }

    private void setDefaultSettings() {
        String avatarPhoto =  sharedPreferences.getString(FragmentSettings.AVATAR, "");
        String nameText = sharedPreferences.getString(FragmentSettings.NAME, "");
        boolean isLoggedViaVk = sharedPreferences.getBoolean(LOGGED_VK, false);

        Uri avatarPhotoUri = Uri.parse(avatarPhoto);
        if (DMPlayerUtility.isURIExists(avatarPhotoUri)) {
            DMPlayerUtility.settingPicture(avatar, avatarPhotoUri);
        } else {
            DMPlayerUtility.settingPicture(avatar, R.drawable.profile_default_avatar);
        }

        if (!nameText.equals(""))
            nickName.setText(nameText);
        else
            nickName.setText(R.string.profile_defult_name);

        if (isLoggedViaVk) {
            vkProfile.setSecondLayout();
            initForVkProfileSecond();

            setVkData();

            vkName.setText(vkData.get("vkname") + " " + vkData.get("vksurname"));
            vkSongs.setText("Songs: " + vkData.get("vksongscount"));
            vkAlbums.setText("Albums: " + vkData.get("vkalbumscount"));
            DMPlayerUtility.settingPicture(vkAvatar, Uri.parse(vkData.get("vkphotouri")));
        } else {
            vkProfile.setFirstLayout();
            initForVkProfileFirst();
        }

        initialName = nickName.getText().toString();
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
                        toCamera.putExtra(MediaStore.EXTRA_OUTPUT, generateAvatarUri(getActivity(), true));
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

    @NonNull
    public static String checkPhotoDirectory(Context context) {
        photoDirectory = new File(
                context.getExternalCacheDir() + "/DMPlayer/",
                "DMPlayer_photos");
        if (!photoDirectory.exists())
            photoDirectory.mkdirs();
        return photoDirectory.getPath();
    }

    private Uri generateAvatarUri(Context context, boolean isTemp) {
        File file = new File(checkPhotoDirectory(context) + "/" + "photo_avatar" + ((isTemp)? "_new" : "") +".jpg");

        return Uri.fromFile(file);
    }

    private Uri generateVkAvatarUri() {
        File file = new File(checkPhotoDirectory(getActivity()) + "/" + "vk_photo" +".jpg");

        return Uri.fromFile(file);
    }

    private boolean deletePhoto(String path) {
        File photo = new File(path);
        return photo.delete();
    }

    private void hideKeys() {
        nickName.setCursorVisible(false);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nickName.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
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
                File avatarPhoto = new File(generateAvatarUri(context ,false).getPath());
                switch (where) {
                    case FragmentSettings.GALLERY_REQUEST:
                        File pictureToCopy = new File(
                                DMPlayerUtility.getRealPathFromURI(context, photoFromGallery));
                        try {
                            DMPlayerUtility.copyFile(pictureToCopy, avatarPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }

                        deletePhoto(generateAvatarUri(context, true).getPath());
                        break;
                    case FragmentSettings.CAMERA_REQUEST:
                        File photoTaken = new File(generateAvatarUri(context, true).getPath());
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

                vkData.put("vkphotouri", generateVkAvatarUri().toString());

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
                downloadVkAvatar(context, vkAvatarUrl);
            }

            if (isVkLoggedOut) {
                deletePhoto(generateVkAvatarUri().getPath());
            }

            if (isDataChanged() || isVkRefreshed || isVkLoggedOut) {
                editor.apply();
            }
        }

        private void downloadVkAvatar(final Context context, String url) {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 100)
                    .into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onResourceReady(final byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    File dir = new File(checkPhotoDirectory(context));
                                    File file = new File(checkPhotoDirectory(context) + "/" + "vk_photo" +".jpg");
                                    try {
                                        if (!dir.mkdirs() && (!dir.exists() || !dir.isDirectory())) {
                                            throw new IOException("Cannot ensure parent directory for file " + file);
                                        }
                                        BufferedOutputStream s = new BufferedOutputStream(new FileOutputStream(file));
                                        s.write(resource);
                                        s.flush();
                                        s.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            }.execute();
                        }
                    });
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
