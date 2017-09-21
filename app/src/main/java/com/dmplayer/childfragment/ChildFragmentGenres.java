/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.childfragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.PlaylistActivity;
import com.dmplayer.models.Genre;
import com.dmplayer.models.SongDetail;
import com.dmplayer.phonemedia.DMPlayerUtility;
import com.dmplayer.phonemedia.MusicAlphabetIndexer;
import com.dmplayer.phonemedia.PhoneMediaControl;

import java.util.ArrayList;
import java.util.List;

public class ChildFragmentGenres extends Fragment {

    private static final String TAG = "ChildFragmentArtists";
    private static final String DELETE_GENRE_TAG = "<*EMPTY*>";
    private RecyclerView recyclerView;
    private GenresRecyclerAdapter adapter;
    private Cursor mGenreCursor;


    public static ChildFragmentGenres newInstance(int position, Context mContext) {
        return new ChildFragmentGenres();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_album, null);
        setupView(view);
        return view;
    }

    private void setupView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_playlists);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        populateData();
    }

    private List<Genre> getUsableGenres() {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();

        Cursor c = getGenresCursor();

        List<Genre> genres = new ArrayList<>();
        List<SongDetail> songsList;

        int genreId;
        String genreName;

        while (c.moveToNext()) {
            genreId = c.getInt(0);
            genreName = c.getString(1);

            songsList = mPhoneMediaControl.getList(getActivity(), genreId, PhoneMediaControl.SongsLoadFor.GENRE, "");

            if (songsList.size() > 0) {
                genres.add(new Genre(genreId, genreName));
            }
        }

        return genres;
    }

    private void populateData() {
        if (adapter == null) {
            List<Genre> genres = getUsableGenres();
            adapter = new GenresRecyclerAdapter(getActivity(), genres);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setAdapter(adapter);
        }
    }

    public void resetView() {
        recyclerView.scrollToPosition(0);
    }

    private Cursor getGenresCursor(AsyncQueryHandler async, String filter) {
        String[] cols = new String[]{MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME};

        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        if (!TextUtils.isEmpty(filter)) {
            uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
        }

        Cursor ret = null;
        if (async != null) {
            async.startQuery(0, null, uri, cols, null, null, null);
        } else {
            ret = DMPlayerUtility.query(getActivity(), uri, cols, null, null, null);
        }

        return ret;
    }

    private Cursor getGenresCursor() {
        String[] cols = new String[] {MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres._COUNT,
                MediaStore.Audio.Genres.NAME};
        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

        return DMPlayerUtility.query(getActivity(), uri, cols, null, null, null);
    }

    private class GenresRecyclerAdapter extends RecyclerView.Adapter<GenresRecyclerAdapter.ViewHolder> {
        private final Context context;
        private final List<Genre> genres;

        private final BitmapDrawable mDefaultAlbumIcon;

        private final Resources mResources;
        private final String mUnknownArtist;
        private MusicAlphabetIndexer mIndexer;
        private String mConstraint = null;
        private boolean mConstraintIsValid = false;

        protected GenresRecyclerAdapter(Context context, List<Genre> genres) {
            this.context = context;
            this.genres = genres;

            Resources r = context.getResources();
            mDefaultAlbumIcon = (BitmapDrawable) r.getDrawable(R.drawable.bg_default_album_art);
            // no filter or dither, it's a lot faster and we can't tell the difference

            mDefaultAlbumIcon.setFilterBitmap(false);
            mDefaultAlbumIcon.setDither(false);

            mResources = context.getResources();
            mUnknownArtist = context.getString(R.string.unknown_artist_name);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GenresRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String genreName = genres.get(position).getName();

            if ((genreName == null) || genreName.equals(MediaStore.UNKNOWN_STRING)) {
                genreName = mUnknownArtist;
            }

            holder.topLine.setText(genreName);
            holder.bottomLine.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return genres.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView topLine;
            TextView bottomLine;
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                topLine = (TextView) itemView.findViewById(R.id.title);
                bottomLine = (TextView) itemView.findViewById(R.id.details);
                icon = (ImageView) itemView.findViewById(R.id.icon);
                icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                try {
                    long genreId = genres.get(getAdapterPosition()).getId();
                    Context context = getActivity();
                    Intent mIntent = new Intent(context, PlaylistActivity.class);

                    Bundle mBundle = new Bundle();
                    mBundle.putLong("id", genreId);
                    mBundle.putLong("tagfor", PhoneMediaControl.SongsLoadFor.GENRE.ordinal());
                    mBundle.putString("albumname", ((TextView) view.findViewById(R.id.title)).getText().toString().trim());
                    mBundle.putString("title_one", "All my songs");
                    mBundle.putString("title_sec", ((TextView) view.findViewById(R.id.details)).getText().toString().trim());

                    mIntent.putExtras(mBundle);

                    context.startActivity(mIntent);
                    ((Activity) context).overridePendingTransition(0, 0);
                } catch (Exception e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }
}
