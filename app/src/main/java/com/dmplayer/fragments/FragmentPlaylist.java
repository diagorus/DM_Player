package com.dmplayer.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.asynctask.TaskStateListener;
import com.dmplayer.manager.MediaController;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySingle;
import com.dmplayer.utility.DMPlayerUtility;
import com.dmplayer.utility.DefaultPlaylistTaskFactory;
import com.dmplayer.utility.VkPlaylistTaskFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlaylist extends Fragment {
    private RecyclerView listOfSongs;
    private FrameLayout progressBar;

    private LinearLayout errorLayout;
    private TextView errorInfo;
    private TextView errorReload;

    private SongListAdapter songAdapter;
    private List<SongDetail> songList;

    private static final String ARG_TYPE = "FragmentPlaylist_type";
    private static final String ARG_CATEGORY = "FragmentPlaylist_category";
    private static final String ARG_ID = "FragmentPlaylist_id";
    private static final String ARG_NAME = "FragmentPlaylist_name";

    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_VK = 1;

    private static final String TAG = "FragmentPlaylist";

    public static FragmentPlaylist newInstance(DefaultPlaylistCategorySingle category, int id,
                                               String name) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, TYPE_DEFAULT);
        args.putInt(ARG_CATEGORY, category.ordinal());
        args.putInt(ARG_ID, id);
        args.putString(ARG_NAME, name);

        FragmentPlaylist f = new FragmentPlaylist();
        f.setArguments(args);

        return f;
    }

    public static FragmentPlaylist newInstance(VkPlaylistCategorySingle category, int id,
                                               String name) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, TYPE_VK);
        args.putInt(ARG_CATEGORY, category.ordinal());
        args.putInt(ARG_ID, id);
        args.putString(ARG_NAME, name);

        FragmentPlaylist f = new FragmentPlaylist();
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist, null);
        setupInitialViews(v);
        loadPlaylist();
        return v;
    }

    private void setupInitialViews(View v) {
        songList = new ArrayList<>();
        songAdapter = new SongListAdapter(getActivity(), songList);

        listOfSongs = (RecyclerView) v.findViewById(R.id.list_of_songs);
        listOfSongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOfSongs.setAdapter(songAdapter);

        progressBar = (FrameLayout) v.findViewById(R.id.layout_progress_bar);

        errorLayout = (LinearLayout) v.findViewById(R.id.error_layout);
        errorInfo = (TextView) v.findViewById(R.id.error_info);
        errorReload = (TextView) v.findViewById(R.id.error_reload);
        errorReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                loadPlaylist();
            }
        });
    }

    private void loadPlaylist() {
        TaskStateListener<Playlist> listener =
            new TaskStateListener<Playlist>() {
                @Override
                public void onLoadingStarted() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingSuccessful(Playlist result) {
                    songList.clear();
                    songList.addAll(result.getSongs());
                    songAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);

                    errorInfo.setText("");
                    errorInfo.append(getString(R.string.error) + "\n");
                    errorInfo.append(getString(R.string.check_connection));
                }
        };

        int type = getArguments().getInt(ARG_TYPE);
        int category = getArguments().getInt(ARG_CATEGORY);
        int id = getArguments().getInt(ARG_ID);
        String name = getArguments().getString(ARG_NAME);

        //TODO:think about strategy pattern
        switch (type) {
            case TYPE_DEFAULT:
                loadDefaultPlaylist(category, id, name, listener);
                break;
            case TYPE_VK:
                loadVkPlaylist(category, id, name, listener);
                break;
            default:
                throw new IllegalArgumentException("Default statement reached!");
        }
    }

    private void loadDefaultPlaylist(int category, int id, String name, TaskStateListener<Playlist> l) {
        DefaultPlaylistCategorySingle c = DefaultPlaylistCategorySingle.valueOf(category);

        DefaultPlaylistTaskFactory factory = new DefaultPlaylistTaskFactory(getActivity(), l);
        factory.getLoadPlaylistTask(c, id, name).execute();
    }

    private void loadVkPlaylist(int c, int id, String name, TaskStateListener<Playlist> l) {
        VkPlaylistCategorySingle category = VkPlaylistCategorySingle.valueOf(c);

        VkPlaylistTaskFactory factory = new VkPlaylistTaskFactory(getActivity(), l);
        factory.getTask(category, id, name).execute();
    }

    private class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
        List<SongDetail> songList;

        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public SongListAdapter(Context context, List<SongDetail> songList) {
            this.songList = songList;
            this.options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art)
                    .showImageOnFail(R.drawable.bg_default_album_art)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SongDetail currentSong = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(currentSong.getDuration()));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error getting audio duration:", e);
            }

            holder.artistAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + currentSong.getArtist());
            holder.songName.setText(currentSong.getTitle());
            String contentURI = "content://media/external/audio/media/" + currentSong.getId() + "/albumart";
            imageLoader.displayImage(contentURI, holder.songImage, options);
        }

        @Override
        public int getItemCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView songName;
            ImageView songImage;
            ImageView menuImage;
            TextView artistAndDuration;

            public ViewHolder(View itemView) {
                super(itemView);
                songName = (TextView) itemView.findViewById(R.id.song_name);
                artistAndDuration = (TextView) itemView.findViewById(R.id.song_details);
                songImage = (ImageView) itemView.findViewById(R.id.song_icon_art);
                //TODO: problems with menu icon
                menuImage = (ImageView) itemView.findViewById(R.id.song_icon_option_more);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                SongDetail song = songList.get(getAdapterPosition());
                song.audioProgress = 0.0f;
                song.audioProgressSec = 0;
                ((DMPlayerBaseActivity) getActivity()).loadSongsDetails(song);

                if (MediaController.getInstance().isPlayingAudio(song) && !MediaController.getInstance().isAudioPaused()) {
                    MediaController.getInstance().pauseAudio(song);
                } else {
                    //TODO: 3rd parameter is playlist type, it used to retrieve song when activity closes
                    MediaController.getInstance().setPlaylist(songList, song, -1, -1);
                }
            }
        }
    }
}
