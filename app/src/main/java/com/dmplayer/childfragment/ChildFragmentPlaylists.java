/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.childfragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.PlaylistActivity;
import com.dmplayer.internetservices.VkAPIService;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.VkAlbumObject;
import com.dmplayer.models.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkPlaylist;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.phonemidea.PhoneMediaControl;
import com.dmplayer.utility.LogWriter;
import com.dmplayer.utility.VkSettings;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChildFragmentPlaylists extends Fragment {

    private PlaylistsAdapter playlistsAdapter;
    private ArrayList<Playlist> playlists = new ArrayList<>();

    private static Context context;

    private static VkAPIService service;
    boolean isLoggedViaVk = false;

    private SharedPreferences sharedPreferences;

    private static final String TAG = "ChildFragmentPlaylists";


    public static ChildFragmentPlaylists newInstance(int position, Context mContext) {
        ChildFragmentPlaylists f = new ChildFragmentPlaylists();
        context = mContext;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_child_playlists, null);
        setupInitialViews(v);
        new LoadPlaylistsTask().execute();
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupInitialViews(View inflaterView) {
        RecyclerView recyclerView = (RecyclerView) inflaterView.findViewById(R.id.recyclerView_playlists);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        playlistsAdapter = new PlaylistsAdapter(getActivity());
        recyclerView.setAdapter(playlistsAdapter);
    }

//    private class LoadPlaylistsTask1 extends AsyncTask<Void, Void, Void> {
//
//        ArrayList<File> playlistsPaths = new ArrayList<>();
//        ProgressDialog progressDialog;
//        boolean isLoggedViaVk = false;
//        String vkUserId;
//        String vkAccessToken;
//        String vkSongsCount;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
//            isLoggedViaVk = sharedPreferences.getBoolean("LOGGED_VK", false);
//
//            if (isLoggedViaVk) {
//                vkUserId = sharedPreferences.getString("VKUSERID", "");
//                vkAccessToken = sharedPreferences.getString("VKACCESSTOKEN", "");
//                vkSongsCount = sharedPreferences.getString("VKSONGSCOUNT", "");
//            }
//
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setMessage("Wait a bit...");
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                loadLocalPlaylists(context);
//            } catch (Exception e) {
//                Log.e(TAG, e.getCause().toString());
//            }
//
//            loadVkSongs();
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            progressDialog.dismiss();
//
//            playlistsAdapter.notifyDataSetChanged();
//        }
//
//        private void loadLocalPlaylists(Context context) throws Exception{
//            File parentDir = new File(
//                    context.getExternalCacheDir() + "/DMPlayer/", "DMPlayer_playlists");
//            if (parentDir.exists()) {
//                for (File file : parentDir.listFiles()) {
//                    if (file.getName().endsWith(".dpl"))
//                        playlistsPaths.add(file);
//                }
//
//                for (File file : playlistsPaths){
//                    ObjectInputStream fin = new ObjectInputStream(new FileInputStream (file));
//                    Playlist current = (Playlist) fin.readObject();
//                    current.setPath(file.getPath());
//                    playlists.add(current);
//                }
//            }
//        }
//
//        private void loadVkSongs() {
//            if (DMPlayerUtility.hasConnection(getActivity()) && isLoggedViaVk) {
//                createApiService();
//                try {
//                    loadAlbum(0, 24, "", "My audios");
//                    loadUserAlbums();
//                    loadPopular(0, 24);
//                    loadRecommended(0, 24);
//                } catch (IOException e) {
//                    Log.e(TAG, e.getCause().toString());
//                }
//            }
//        }
//
//        private void loadAlbum(int offset, int count, String albumId, String albumName) throws IOException {
//            Map<String, String> optionsForAlbum = new HashMap<>();
//            optionsForAlbum.put("owner_id", vkUserId);
//            optionsForAlbum.put("album_id", albumId);
//            optionsForAlbum.put("need_user", "0");
//            optionsForAlbum.put("offset", String.valueOf(offset));
//            optionsForAlbum.put("count", String.valueOf(count));
//            optionsForAlbum.put("access_token", vkAccessToken);
//            optionsForAlbum.put("v", "5.53");
//
//            Call<VkAudioWrapper> callForAlbum = service.loadAudio(optionsForAlbum);
//
//            Response<VkAudioWrapper> responseAlbum = callForAlbum.execute();
//
//            ArrayList<VkAudioObject> vkAlbum = new ArrayList<>(
//                    Arrays.asList(responseAlbum.body().getResponse().getItems()));
//
//            Playlist playlistAlbum = new Playlist();
//            playlistAlbum.setName(albumName);
//            VkToSongDetailConverter converter = new VkToSongDetailConverter();
//            for (VkAudioObject vkSong : vkAlbum) {
//                playlistAlbum.addSong(converter.convert(vkSong));
//            }
//            playlists.add(playlistAlbum);
//        }
//
//        private void loadUserAlbums() throws IOException {
//            Map<String, String> optionsForAlbums = new HashMap<>();
//            optionsForAlbums.put("offset", "0");
//            optionsForAlbums.put("count", "100");
//            optionsForAlbums.put("owner_id", vkUserId);
//            optionsForAlbums.put("access_token", vkAccessToken);
//            optionsForAlbums.put("v", "5.53");
//
//            Call<VkAlbumsWrapper> callForAlbums = service.loadAlbums(optionsForAlbums);
//
//            Response<VkAlbumsWrapper> responseAlbums = callForAlbums.execute();
//
//            ArrayList<VkAlbumObject> vkAllAlbums = new ArrayList<>(
//                    Arrays.asList(responseAlbums.body().getResponse().getItems()));
//
//            for (VkAlbumObject vkAlbum : vkAllAlbums) {
//                loadAlbum(0, 24, vkAlbum.getId(), vkAlbum.getTitle());
//            }
//        }
//
//        private void loadPopular(int offset, int count) throws IOException {
//            Map<String, String> optionsForPopular = new HashMap<>();
//            optionsForPopular.put("only_eng", "1");
//            optionsForPopular.put("genre_id", "");
//            optionsForPopular.put("offset", String.valueOf(offset));
//            optionsForPopular.put("count", String.valueOf(count));
//            optionsForPopular.put("access_token", vkAccessToken);
//            optionsForPopular.put("v", "5.53");
//
//            Call<VkPopularCollection> callForPopular = service.loadPopularAudio(optionsForPopular);
//
//            Response<VkPopularCollection> responsePopular = callForPopular.execute();
//
//            ArrayList<VkAudioObject> vkPopular = new ArrayList<>(
//                    Arrays.asList(responsePopular.body().getResponse()));
//
//            Playlist playlistPopular = new Playlist();
//            playlistPopular.setName("Popular");
//            VkToSongDetailConverter converter = new VkToSongDetailConverter();
//            for (VkAudioObject vkSong : vkPopular) {
//                playlistPopular.addSong(converter.convert(vkSong));
//            }
//            playlists.add(playlistPopular);
//        }
//
//        private void loadRecommended(int offset, int count) throws IOException {
//            Map<String, String> optionsForRecommended = new HashMap<>();
//            optionsForRecommended.put("target_audio", "");
//            optionsForRecommended.put("user_id", vkUserId);
//            optionsForRecommended.put("offset", String.valueOf(offset));
//            optionsForRecommended.put("count", String.valueOf(count));
//            optionsForRecommended.put("shuffle", "1");
//            optionsForRecommended.put("access_token", vkAccessToken);
//            optionsForRecommended.put("v", "5.53");
//
//            Call<VkAudioWrapper> callForRecommended = service.loadRecommendedAudio(optionsForRecommended);
//
//            Response<VkAudioWrapper> responseRecommended = callForRecommended.execute();
//
//            ArrayList<VkAudioObject> vkRecommended = new ArrayList<>(
//                    Arrays.asList(responseRecommended.body().getResponse().getItems()));
//
//            Playlist playlistRecommended = new Playlist();
//            playlistRecommended.setName("Recommended");
//            VkToSongDetailConverter converter = new VkToSongDetailConverter();
//            for (VkAudioObject vkSong : vkRecommended) {
//                playlistRecommended.addSong(converter.convert(vkSong));
//            }
//            playlists.add(playlistRecommended);
//        }
//
//        private void createApiService() {
//
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(VkSettings.VK_API_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            service = retrofit.create(VkAPIService.class);
//        }
//    }

    private class LoadPlaylistsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        boolean isLoggedViaVk = false;
        String vkUserId;
        String vkAccessToken;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
            isLoggedViaVk = sharedPreferences.getBoolean("LOGGED_VK", false);

            if (isLoggedViaVk) {
                vkUserId = sharedPreferences.getString("VKUSERID", "");
                vkAccessToken = sharedPreferences.getString("VKACCESSTOKEN", "");
            }

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading_message));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                showLocalPlaylists(context);
            } catch (Exception e) {
                Log.e(TAG, e.getCause().toString());
            }

            showVkPlaylists();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            playlistsAdapter.notifyDataSetChanged();
        }

        private void showLocalPlaylists(Context context) throws Exception{
            File parentDir = new File(
                    context.getExternalCacheDir() + "/DMPlayer/", "DMPlayer_playlists");
            if (parentDir.exists()) {
                for (File file : parentDir.listFiles()) {
                    String playlistName = file.getName();
                    if (playlistName.endsWith(".dpl")) {
                        String playlistNameNoEx = playlistName.substring(0, playlistName.lastIndexOf("."));
                        playlists.add(new Playlist(playlistNameNoEx));
                    }
                }
            }
        }

        private void showVkPlaylists() {
            if (DMPlayerUtility.hasConnection(getActivity()) && isLoggedViaVk) {
                createApiService();

                try {
                    playlists.add(new VkPlaylist("My Audios", VkPlaylist.ALL));
                    loadVkUserAlbums();
                    playlists.add(new VkPlaylist("Popular", VkPlaylist.POPULAR));
                    playlists.add(new VkPlaylist("Recommended", VkPlaylist.RECOMMENDED));
                } catch (IOException e) {
                    Log.e(TAG, e.getCause().toString());
                }
            }
        }

        private void loadVkUserAlbums() throws IOException {
            Map<String, String> optionsForAlbums = new HashMap<>();
            optionsForAlbums.put("offset", "0");
            optionsForAlbums.put("count", "100");
            optionsForAlbums.put("owner_id", vkUserId);
            optionsForAlbums.put("access_token", vkAccessToken);
            optionsForAlbums.put("v", "5.53");

            Call<VkAlbumsWrapper> callForAlbums = service.loadAlbums(optionsForAlbums);

            Response<VkAlbumsWrapper> responseAlbums = callForAlbums.execute();

            ArrayList<VkAlbumObject> vkAllAlbums = new ArrayList<>(
                    Arrays.asList(responseAlbums.body().getResponse().getItems()));

            for (VkAlbumObject vkAlbum : vkAllAlbums) {
                playlists.add(new VkPlaylist(vkAlbum.getTitle(), VkPlaylist.ALBUM, vkAlbum.getId()));
            }
        }

        private void createApiService() {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(VkSettings.VK_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(VkAPIService.class);
        }
    }

    public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ViewHolder> {
        private Context context = null;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public PlaylistsAdapter(Context mContext) {
            this.context = mContext;
            this.options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art)
                    .showImageOnFail(R.drawable.bg_default_album_art)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Playlist playlist = playlists.get(position);
            holder.topLine.setText(playlist.getName());
            holder.bottomLine.setVisibility(View.GONE);
            imageLoader.displayImage("", holder.icon, options);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return (playlists != null) ? playlists.size() : 0;
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView topLine;
            TextView bottomLine;
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                topLine = (TextView) itemView.findViewById(R.id.line_1);
                bottomLine = (TextView) itemView.findViewById(R.id.line_2);
                icon = (ImageView) itemView.findViewById(R.id.icon);
                icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                try {
                    Intent toPlaylistView = new Intent(context, PlaylistActivity.class);
                    Playlist current = playlists.get(getAdapterPosition());
                    toPlaylistView.putExtras(current.getBundle());

                    context.startActivity(toPlaylistView);
                    ((Activity) context).overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogWriter.info(TAG, e.toString());
                }
            }
        }
    }
}
