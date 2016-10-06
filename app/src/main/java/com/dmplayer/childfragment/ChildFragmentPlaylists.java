/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.childfragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.PlaylistActivity;
import com.dmplayer.helperservises.VkMusicHelper;
import com.dmplayer.helperservises.VkProfileHelper;
import com.dmplayer.models.Playlist;
import com.dmplayer.utility.LogWriter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ChildFragmentPlaylists extends Fragment {

    private PlaylistsAdapter playlistsAdapter;
    private List<Playlist> playlists = new ArrayList<>();

    private VkMusicHelper vkMusicHelper;

    private static final String TAG = "ChildFragmentPlaylists";

    public static ChildFragmentPlaylists newInstance(int position) {
        return new ChildFragmentPlaylists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_child_playlists, null);
        setupInitialViews(v);

        setupVkMusicHelper();
        loadVkPlaylists();

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

    private void setupVkMusicHelper() {
        SharedPreferences sp = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        vkMusicHelper = new VkMusicHelper.Builder()
                .setLogged(sp.getBoolean(VkProfileHelper.SP_LOGGED, false))
                .setUserId(sp.getString(VkProfileHelper.SP_USER_ID, ""))
                .setToken(sp.getString(VkProfileHelper.SP_ACCESS_TOKEN, ""))
                .build();
    }

    private void loadVkPlaylists() {
        if (vkMusicHelper.isLogged()) {
            new LoadPlaylistsTask().execute();
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

    private class LoadPlaylistsTask extends AsyncTask<Void, Void, List<Playlist>> {

        @Override
        protected List<Playlist> doInBackground(Void... params) {
            return vkMusicHelper.loadMusicListsToShow();
        }

        @Override
        protected void onPostExecute(List<Playlist> playlistsToShow) {
            super.onPostExecute(playlistsToShow);

            playlists.addAll(playlistsToShow);
            playlistsAdapter.notifyDataSetChanged();
        }
    }
}