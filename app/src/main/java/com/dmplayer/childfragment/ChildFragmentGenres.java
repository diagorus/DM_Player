/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.childfragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.PlaylistActivity;
import com.dmplayer.adapter.CursorRecyclerViewAdapter;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.phonemidea.MusicAlphabetIndexer;
import com.dmplayer.phonemidea.PhoneMediaControl;
import com.dmplayer.utility.LogWriter;

public class ChildFragmentGenres extends Fragment {

    private static final String TAG = "ChildFragmentArtists";
    private static final String DELETE_GENRE_TAG = "<*EMPTY*>";
    private static Context context;
    private RecyclerView recyclerView;
    private GenresRecyclerAdapter mAdapter;
    private Cursor mGenreCursor;

    public static ChildFragmentGenres newInstance(int position, Context mContext) {
        ChildFragmentGenres f = new ChildFragmentGenres();
        context = mContext;
        return f;
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

    private void populateData() {
        mAdapter = (GenresRecyclerAdapter) getActivity().getLastNonConfigurationInstance();
        if (mAdapter == null) {
            mAdapter = new GenresRecyclerAdapter(getActivity(), null);
            recyclerView.setAdapter(mAdapter);
            getGenresCursor(mAdapter.getQueryHandler(), null);
        } else {
            recyclerView.setAdapter(mAdapter);
            mGenreCursor = mAdapter.getCursor();
            if (mGenreCursor != null) {
                init(mGenreCursor);
            } else {
                getGenresCursor(mAdapter.getQueryHandler(), null);
            }
        }
    }

    public void resetView() {
        recyclerView.scrollToPosition(0);
    }

    public void init(Cursor c) {
        if (mAdapter == null) {
            return;
        }

        mAdapter.changeCursor(c); // also sets mGenreCursor
        if (mGenreCursor == null) {
            DMPlayerUtility.displayDatabaseError(getActivity());
            mReScanHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private Handler mReScanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAdapter != null) {
                getGenresCursor(mAdapter.getQueryHandler(), null);
            }
        }
    };

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

    public class GenresRecyclerAdapter extends CursorRecyclerViewAdapter<GenresRecyclerAdapter.ViewHolder> {

        private final BitmapDrawable mDefaultAlbumIcon;
        private int genresIdIdx;
        private int genresIdx;

        private final Context mContext;
        private final Resources mResources;
        private final String mUnknownArtist;
        private MusicAlphabetIndexer mIndexer;
        private AsyncQueryHandler mQueryHandler;
        private String mConstraint = null;
        private boolean mConstraintIsValid = false;

        protected GenresRecyclerAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            mQueryHandler = new QueryHandler(context.getContentResolver());

            Resources r = context.getResources();
            mDefaultAlbumIcon = (BitmapDrawable) r.getDrawable(R.drawable.bg_default_album_art);
            // no filter or dither, it's a lot faster and we can't tell the
            // difference
            mDefaultAlbumIcon.setFilterBitmap(false);
            mDefaultAlbumIcon.setDither(false);

            mContext = context;
            getColumnIndices(cursor);
            mResources = context.getResources();
            mUnknownArtist = context.getString(R.string.unknown_artist_name);
        }



        public AsyncQueryHandler getQueryHandler() {
            return mQueryHandler;
        }

        @Override
        public void changeCursor(Cursor cursor) {
            if (getActivity().isFinishing() && cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (cursor != mGenreCursor) {
                mGenreCursor = cursor;
                getColumnIndices(cursor);
                super.changeCursor(cursor);
            }
        }

        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            String s = constraint.toString();
            if (mConstraintIsValid && ((s == null && mConstraint == null) || (s != null && s.equals(mConstraint)))) {
                return getCursor();
            }
            Cursor c = getGenresCursor(null, s);
            mConstraint = s;
            mConstraintIsValid = true;
            return c;
        }

        private void getColumnIndices(Cursor cursor) {
            if (cursor != null) {
                genresIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
                genresIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                if (mIndexer != null) {
                    mIndexer.setCursor(cursor);
                } else {
                    mIndexer = new MusicAlphabetIndexer(cursor, genresIdx, mResources.getString(R.string.fast_scroll_alphabet));
                }
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

            String displayGenres = cursor.getString(genresIdx);
            int genreId = cursor.getInt(genresIdIdx);

            boolean unknown = ((displayGenres == null) || displayGenres.equals(MediaStore.UNKNOWN_STRING));
            if (unknown) {
                displayGenres = mUnknownArtist;
            }
            viewHolder.topLine.setText(displayGenres);
            viewHolder.bottomLine.setVisibility(View.GONE);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int position) {
            return new ViewHolder(LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.inflate_grid_item, viewgroup, false));
        }

        private long getGenreID(int position) {
            return getItemId(position);
        }

        class QueryHandler extends AsyncQueryHandler {
            QueryHandler(ContentResolver res) {
                super(res);
            }

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//        int genreId = c.getInt(0);
//        ArrayList<SongDetail> genreSongsCollection = PhoneMediaControl.getInstance()
//                .getList(context, genreId, PhoneMediaControl.SongsLoadFor.Genre, "");
//        if(genreSongsCollection.isEmpty())
//            return;
                init(cursor);
            }
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
                    long genreId = getGenreID(getAdapterPosition());
                    Intent mIntent = new Intent(context, PlaylistActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putLong("id", genreId);
                    mBundle.putLong("tagfor", PhoneMediaControl.SongsLoadFor.Genre.ordinal());
                    mBundle.putString("albumname", ((TextView) view.findViewById(R.id.line_1)).getText().toString().trim());
                    mBundle.putString("title_one", "All my songs");
                    mBundle.putString("title_sec", ((TextView) view.findViewById(R.id.line_2)).getText().toString().trim());
                    mIntent.putExtras(mBundle);
                    context.startActivity(mIntent);
                    ((Activity) context).overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogWriter.info(TAG, e.toString());
                }
            }
        }
    }
}
