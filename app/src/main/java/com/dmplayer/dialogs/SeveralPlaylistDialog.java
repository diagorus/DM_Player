package com.dmplayer.dialogs;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.asynctask.TaskStateListener;
import com.dmplayer.fragments.FragmentPlaylist;
import com.dmplayer.models.PlaylistItemInSeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemInSeveral;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.VkPlaylistItemInSeveral;
import com.dmplayer.utility.DefaultMultiPlaylistTaskFactory;
import com.dmplayer.utility.VkMultiPlaylistTaskFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class SeveralPlaylistDialog extends DialogFragment {
    private RecyclerView listOfPlaylists;
    private PlaylistListAdapter playlistsAdapter;

    private ProgressBar progressBar;

    private LinearLayout errorLayout;
    private TextView errorInfo;
    private TextView errorReload;

    private static final String ARG_TYPE = "SeveralPlaylistDialog_type";
    private static final String ARG_CATEGORY = "SeveralPlaylistDialog_category";
    private static final String ARG_NAME = "SeveralPlaylistDialog_name";

    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_VK = 1;

    public static SeveralPlaylistDialog newInstance(DefaultPlaylistCategorySeveral category,
                                                    String name) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, TYPE_DEFAULT);
        args.putInt(ARG_CATEGORY, category.ordinal());
        args.putString(ARG_NAME, name);

        SeveralPlaylistDialog d = new SeveralPlaylistDialog();
        d.setArguments(args);

        return d;
    }

    public static SeveralPlaylistDialog newInstance(VkPlaylistCategorySeveral category,
                                                    String name) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, TYPE_VK);
        args.putInt(ARG_CATEGORY, category.ordinal());
        args.putString(ARG_NAME, name);

        SeveralPlaylistDialog d = new SeveralPlaylistDialog();
        d.setArguments(args);

        return d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_playlistseveral, null);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        setupInitialViews(v);
        loadPlaylist();
        return v;
    }

    private void setupInitialViews(View v) {
        listOfPlaylists = (RecyclerView) v.findViewById(R.id.recycler_playlists);
        listOfPlaylists.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);

        errorLayout = (LinearLayout) v.findViewById(R.id.error_layout);
        errorInfo = (TextView) v.findViewById(R.id.error_info);
        errorReload = (TextView) v.findViewById(R.id.error_reload);
        errorReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                listOfPlaylists.setVisibility(View.VISIBLE);
                loadPlaylist();
            }
        });

        setupHeader(v);
    }

    private void setupHeader(View v) {
        ImageView buttonClose = (ImageView) v.findViewById(R.id.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        String title = getArguments().getString(ARG_NAME);

        TextView textTitle = (TextView) v.findViewById(R.id.title);
        textTitle.setText(title);
    }

    private void loadPlaylist() {
        final int type = getArguments().getInt(ARG_TYPE);

        TaskStateListener<List<? extends PlaylistItemInSeveral>> listener =
                new TaskStateListener<List<? extends PlaylistItemInSeveral>>() {
                    @Override
                    public void onLoadingStarted() {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingSuccessful(List<? extends PlaylistItemInSeveral> result) {
                        playlistsAdapter = new SeveralPlaylistDialog.PlaylistListAdapter(getActivity(), type,
                                result);
                        listOfPlaylists.setAdapter(playlistsAdapter);

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        listOfPlaylists.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                        errorLayout.setVisibility(View.VISIBLE);

                        errorInfo.setText("");
                        errorInfo.append(getString(R.string.error) + "\n");
                        errorInfo.append(getString(R.string.check_connection));
                    }
                };

        int category = getArguments().getInt(ARG_CATEGORY);

        switch (type) {
            case TYPE_DEFAULT:
                loadPlaylistsFromDefault(category, listener);
                break;
            case TYPE_VK:
                loadPlaylistsFromVk(category, listener);
                break;
            default:
                throw new IllegalArgumentException("Default statement reached!");
        }
    }

    private void loadPlaylistsFromDefault(int category, TaskStateListener<List<? extends PlaylistItemInSeveral>> l) {
        DefaultPlaylistCategorySeveral c = DefaultPlaylistCategorySeveral.valueOf(category);

        DefaultMultiPlaylistTaskFactory factory = new DefaultMultiPlaylistTaskFactory(getActivity(), l);
        factory.getTask(c).execute();
    }

    private void loadPlaylistsFromVk(int category, TaskStateListener<List<? extends PlaylistItemInSeveral>> l) {
        VkPlaylistCategorySeveral c = VkPlaylistCategorySeveral.valueOf(category);

        VkMultiPlaylistTaskFactory factory = new VkMultiPlaylistTaskFactory(getActivity(), l);
        factory.getTask(c).execute();
    }


    private class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.ViewHolder>{
        private List<? extends PlaylistItemInSeveral> items;
        private int type;

        private String unknownPlaylistName;

        private ImageLoader imageLoader = ImageLoader.getInstance();
        private DisplayImageOptions options;

        protected PlaylistListAdapter(Context context, int type,
                                      List<? extends PlaylistItemInSeveral> items) {
            this.items = items;
            this.type = type;

            unknownPlaylistName = context.getString(R.string.unknown_playlist);

            options = new DisplayImageOptions.Builder()
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
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (type) {
                case TYPE_DEFAULT:
                    bindViewForDefault(holder, position);
                    break;
                case TYPE_VK:
                    bindViewForVk(holder, position);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private void bindViewForDefault(ViewHolder holder, int position) {
            DefaultPlaylistItemInSeveral currentItem =
                    (DefaultPlaylistItemInSeveral) items.get(position);

            String playlistName = currentItem.getName();
            String contentURI = "content://media/external/audio/albumart/" + currentItem.getId();

            if ((playlistName == null) || playlistName.equals(MediaStore.UNKNOWN_STRING)) {
                playlistName = unknownPlaylistName;
            }

            holder.title.setText(playlistName);
            imageLoader.displayImage(contentURI, holder.icon, options);
        }

        private void bindViewForVk(ViewHolder holder, int position) {
            VkPlaylistItemInSeveral currentItem = (VkPlaylistItemInSeveral) items.get(position);

            holder.title.setText(currentItem.getName());
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            TextView details;
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                details = (TextView) itemView.findViewById(R.id.details);
                icon = (ImageView) itemView.findViewById(R.id.icon);
                icon.setScaleType(ImageView.ScaleType.CENTER_CROP);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                dismiss();

                FragmentPlaylist f;
                switch(type) {
                    case TYPE_DEFAULT:
                        DefaultPlaylistItemInSeveral defaultItem =
                                (DefaultPlaylistItemInSeveral) items.get(getAdapterPosition());

                        f = FragmentPlaylist
                                .newInstance(defaultItem.getCategory(), defaultItem.getId(), defaultItem.getName());
                        break;
                    case TYPE_VK:
                        VkPlaylistItemInSeveral vkItem =
                                (VkPlaylistItemInSeveral) items.get(getAdapterPosition());

                        f = FragmentPlaylist
                                .newInstance(vkItem.getCategory(), vkItem.getId(), vkItem.getName());

                        break;
                    default:
                        throw new IllegalArgumentException("Default statement reached!");
                }


                FragmentManager fm = getActivity().getFragmentManager();

                fm.beginTransaction()
                        .replace(R.id.fragment, f)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}
