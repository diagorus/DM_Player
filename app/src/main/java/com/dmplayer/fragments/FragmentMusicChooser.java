package com.dmplayer.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.asynctask.TaskStateListener;
import com.dmplayer.butterknifeabstraction.BaseFragment;
import com.dmplayer.dbhandler.PlaylistSongsTableHelper;
import com.dmplayer.dbhandler.PlaylistTableHelper;
import com.dmplayer.dbhandler.SongsTableHelper;
import com.dmplayer.dialogs.InputDialog;
import com.dmplayer.dialogs.OnWorkDoneWithResult;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.utility.DMPlayerUtility;
import com.dmplayer.utility.DefaultPlaylistTaskFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentMusicChooser extends BaseFragment {
    private static final String TAG = FragmentMusicChooser.class.getSimpleName();

    @BindView(R.id.list_of_songs)
    ListView listOfSongs;

    private List<SongDetail> songList;
    private MusicChooserAdapter songAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        loadSongList();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music_chooser;
    }

    private void init() {
        songList = new ArrayList<>();
        songAdapter = new MusicChooserAdapter(getActivity(), songList);

        listOfSongs.setAdapter(songAdapter);
        listOfSongs.setFastScrollEnabled(true);
        listOfSongs.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listOfSongs.setMultiChoiceModeListener(new MultiSongsChooser(listOfSongs));
    }

    private void loadSongList() {
        TaskStateListener<Playlist> listener =
                new TaskStateListener<Playlist>() {
                    @Override
                    public void onLoadingStarted() {}

                    @Override
                    public void onLoadingSuccessful(Playlist result) {
                        songList.clear();
                        songList.addAll(result.getSongs());
                        songAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {}
                };

        DefaultPlaylistTaskFactory factory = new DefaultPlaylistTaskFactory(getActivity(), listener);
        factory.getTask(DefaultPlaylistCategorySingle.ALL_SONGS, -1, "ALL songs").execute();
    }

    public void storeLocalPlaylist(final Context context, final Playlist playlist) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    SongsTableHelper.getInstance(context).insertSongs(playlist);
                    PlaylistTableHelper.getInstance(context).insertPlaylist(playlist);
                    PlaylistSongsTableHelper.getInstance(context).insertPlaylistSongs(playlist);
                } catch (Exception e) {
                    Log.e(TAG, "Error inserting playlist:", e);
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class MusicChooserAdapter extends BaseAdapter {
        private Context context;
        private List<SongDetail> songList;

        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public MusicChooserAdapter(Context context, List<SongDetail> songList) {
            this.context = context;
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
        public Object getItem(int position) {
            return songList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_song, null);
                viewHolder.song_row = ButterKnife.findById(convertView, R.id.song_row);
                viewHolder.songName = ButterKnife.findById(convertView, R.id.song_name);
                viewHolder.songDetails = ButterKnife.findById(convertView, R.id.song_details);
                viewHolder.songIcon = ButterKnife.findById(convertView, R.id.song_icon_art);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SongDetail currentSong = songList.get(position);

            String currentSongDuration = "";
            try {
                currentSongDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(currentSong.getDuration()));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error getting audio duration:", e);
            }
            //TODO: resolve this using string resource
            viewHolder.songDetails.setText((currentSongDuration.isEmpty() ? "" : currentSongDuration + " | ") + currentSong.getArtist());
            viewHolder.songName.setText(currentSong.getTitle());
            String contentURI = "content://media/external/audio/media/" + currentSong.getId() + "/albumart";
            imageLoader.displayImage(contentURI, viewHolder.songIcon, options);

            return convertView;
        }

        @Override
        public int getCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder {
            LinearLayout song_row;
            ImageView songIcon;
            TextView songName;
            TextView songDetails;
        }
    }

    public class MultiSongsChooser implements AbsListView.MultiChoiceModeListener{
        private AbsListView listView;

        public MultiSongsChooser(AbsListView listView) {
            this.listView = listView;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            actionMode.setTitle(String.valueOf(listView.getCheckedItemCount()) + " selected");
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.contextbar_multi_select, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.cab_add) {
                FragmentManager fm = getFragmentManager();
                OnWorkDoneWithResult l = new OnWorkDoneWithResult() {
                    @Override
                    public void onAgree(Bundle result) {
                        String newPlaylistName = getString(R.string.new_playlist_name);
                        if (result != null) {
                            newPlaylistName = result.getString(InputDialog.RESULT_INPUT,
                                    getString(R.string.new_playlist_name));
                        }

                        List<SongDetail> selectedSongList = new ArrayList<>();
                        SparseBooleanArray selectedSongs = listView.getCheckedItemPositions();
                        for(int i = 0; i < selectedSongs.size(); i++) {
                            if(selectedSongs.valueAt(i)) {
                                selectedSongList.add((SongDetail) listView.getItemAtPosition(i));
                            }
                        }

                        Playlist newPlaylist = new Playlist(newPlaylistName, selectedSongList);
                        storeLocalPlaylist(getActivity(), newPlaylist);
                    }

                    @Override
                    public void onRefuse() {}
                };

                showInputDialog(fm, l);
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {}

        private void showInputDialog(FragmentManager fm, OnWorkDoneWithResult l) {
            InputDialog d = InputDialog.newInstance(getString(R.string.new_playlist_title),
                    getString(R.string.new_playlist_invitation));

            d.setOnWorkDoneWithResult(l);
            d.show(fm, "dialog_playlist_name");
        }
    }
}
