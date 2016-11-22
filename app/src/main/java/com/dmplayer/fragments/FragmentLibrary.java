package com.dmplayer.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.butterknifeabstraction.BaseFragment;
import com.dmplayer.dialogs.OnWorkDone;
import com.dmplayer.dialogs.ProfileDialog;
import com.dmplayer.dialogs.SeveralPlaylistDialog;
import com.dmplayer.helperservises.VkProfileHelper;
import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSingle;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.VkPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.VkPlaylistItemSingle;
import com.dmplayer.uicomponent.ExpandableLayout;
import com.dmplayer.uicomponent.ExpandableLayoutExternalAccount;
import com.dmplayer.uicomponent.ExpandableLayoutManager;
import com.dmplayer.uicomponent.PlaylistItemView;
import com.dmplayer.utility.PlaylistProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FragmentLibrary extends BaseFragment {
    private static final String TAG = "FragmentLibrary";

    private ExpandableLayoutManager expandableManager;

    @BindView(R.id.local_playlists)
    ExpandableLayout expandableLocal;
    @BindView(R.id.default_playlists)
    ExpandableLayout expandableDefault;
    @BindView(R.id.vk_playlists)
    ExpandableLayoutExternalAccount expandableVk;

    @BindView(R.id.button_add_local_playlist)
    ImageView buttonAddLocalPlaylist;

    private List<PlaylistItem> localPlaylists;
    private List<PlaylistItem> defaultPlaylists;
    private List<PlaylistItem> vkPlaylists;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_library;
    }

    private void init() {
        expandableLocal.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void OnExpand(ExpandableLayout v) {
                expandableManager.collapseOthers(v);

                setupLocalPlaylists();
            }
        });


        expandableDefault.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void OnExpand(ExpandableLayout v) {
                expandableManager.collapseOthers(v);

                setupDefaultPlaylists();
            }
        });

        expandableVk.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void OnExpand(ExpandableLayout v) {
                expandableManager.collapseOthers(v);

                setupVkPlaylists();
            }
        });
        checkVkLogged();

        expandableManager = new ExpandableLayoutManager();
        expandableManager.register(expandableLocal);
        expandableManager.register(expandableDefault);
        expandableManager.register(expandableVk);
    }



    private void checkVkLogged() {
        VkProfileHelper vkHelper = new VkProfileHelper.Builder(getActivity()).build();
        if (!vkHelper.isLogged()) {
            expandableVk.setMessageLayout();

            TextView infoText = (TextView) expandableVk.findViewById(R.id.title_not_logged);
            infoText.setText(getString(R.string.not_logged, "Vk"));

            ImageView toProfile = (ImageView) expandableVk.findViewById(R.id.button_to_profile);
            toProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getActivity().getFragmentManager();

                    final ProfileDialog dialog = new ProfileDialog();
                    dialog.setOnWorkDone(new OnWorkDone() {
                        @Override
                        public void onAgree() {
                            checkVkLogged();
                        }

                        @Override
                        public void onRefuse() {}
                    });
                    dialog.show(fragmentManager, "fragment_profile");
                }
            });
        } else {
            expandableVk.setUsualLayout();
        }
    }

    private void setupLocalPlaylists() {
        if(localPlaylists == null) {

        }
    }

    private void setupDefaultPlaylists() {
        if (defaultPlaylists == null) {
            defaultPlaylists = PlaylistProvider.getDefaultPlaylists();
        }

        if (expandableDefault.getContentAmount() <= 0) {
            for (final PlaylistItem playlistItem : defaultPlaylists) {
                PlaylistItemView itemView = new PlaylistItemView(getActivity(), playlistItem);

                if (playlistItem instanceof DefaultPlaylistItemSingle) {
                    final DefaultPlaylistItemSingle defaultPlaylistItemSingle =
                            (DefaultPlaylistItemSingle) playlistItem;

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            replaceFragmentToPlaylist(defaultPlaylistItemSingle.getCategory(),
                                    defaultPlaylistItemSingle.getName());
                        }
                    });
                }
                if (playlistItem instanceof DefaultPlaylistItemSeveral) {
                    final DefaultPlaylistItemSeveral defaultPlaylistItemSeveral =
                            (DefaultPlaylistItemSeveral) playlistItem;

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSeveralPlaylistDialog(defaultPlaylistItemSeveral.getCategory(),
                                    defaultPlaylistItemSeveral.getName());
                        }
                    });
                }

                expandableDefault.addContent(itemView);
            }
        }
    }

    private void setupVkPlaylists() {
        if (vkPlaylists == null) {
            vkPlaylists = PlaylistProvider.getVkPlaylists();
        }

        if (expandableVk.getContentAmount() <= 0) {
            for (final PlaylistItem playlistItem : vkPlaylists) {
                PlaylistItemView itemView = new PlaylistItemView(getActivity(), playlistItem);

                if (playlistItem instanceof VkPlaylistItemSingle) {
                    final VkPlaylistItemSingle vkPlaylistItemSingle =
                            (VkPlaylistItemSingle) playlistItem;

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            replaceFragmentToPlaylist(vkPlaylistItemSingle.getCategory(),
                                    vkPlaylistItemSingle.getName());
                        }
                    });
                }
                if (playlistItem instanceof VkPlaylistItemSeveral) {
                    final VkPlaylistItemSeveral vkPlaylistItemSeveral =
                            (VkPlaylistItemSeveral) playlistItem;

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSeveralPlaylistDialog(vkPlaylistItemSeveral.getCategory(),
                                    vkPlaylistItemSeveral.getName());
                        }
                    });
                }

                expandableVk.addContent(itemView);
            }
        }
    }

    @OnClick({R.id.local_playlists, R.id.default_playlists, R.id.vk_playlists})
    public void changeExpandableState(RelativeLayout v) {
        try {
            ExpandableLayout expandable = (ExpandableLayout) v.getParent();

            if(expandable.isExpanded()) {
                expandable.collapse();
            } else {
                expandable.expand();
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "Unable to cast to ExpandableLayout", e);
        }
    }

    @OnClick(R.id.button_add_local_playlist)
    public void showMusicChooserFragment() {
        FragmentManager fm = getFragmentManager();

        FragmentMusicChooser f = new FragmentMusicChooser();

        fm.beginTransaction()
                .replace(R.id.fragment, f)
                .addToBackStack(null)
                .commit();
    }

    private void replaceFragmentToPlaylist(DefaultPlaylistCategorySingle category, String name) {
        FragmentManager fm = getFragmentManager();

        FragmentPlaylist f = FragmentPlaylist.newInstance(category, -1, name);

        fm.beginTransaction()
                .replace(R.id.fragment, f)
                .addToBackStack(null)
                .commit();
    }

    private void replaceFragmentToPlaylist(VkPlaylistCategorySingle category, String name) {
        FragmentManager fm = getFragmentManager();

        FragmentPlaylist f = FragmentPlaylist.newInstance(category, -1, name);

        fm.beginTransaction()
                .replace(R.id.fragment, f)
                .addToBackStack(null)
                .commit();
    }

    private void showSeveralPlaylistDialog(DefaultPlaylistCategorySeveral category, String name) {
        FragmentManager fm = getActivity().getFragmentManager();

        SeveralPlaylistDialog dialog = SeveralPlaylistDialog.newInstance(category, name);

        dialog.show(fm, "fragment_playlistseveral");
    }

    private void showSeveralPlaylistDialog(VkPlaylistCategorySeveral category, String name) {
        FragmentManager fm = getActivity().getFragmentManager();

        SeveralPlaylistDialog dialog = SeveralPlaylistDialog.newInstance(category, name);

        dialog.show(fm, "fragment_playlistseveral");
    }
}