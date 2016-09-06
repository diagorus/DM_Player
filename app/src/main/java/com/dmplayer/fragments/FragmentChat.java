/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.ListView;

import com.dmplayer.R;
import com.dmplayer.chatserver.Server;
import com.dmplayer.models.SongDetail;
import com.dmplayer.utility.LogWriter;

import java.util.ArrayList;

public class FragmentChat extends com.dmplayer.utility.WebViewFragment {

    private static final String TAG = "FragmentChat";
    private Server chatServer;
    private WebView webView;
    private final int serverPort = 65335;

    public static FragmentChat newInstance(int position, Context mContext) {
        return new FragmentChat();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = super.onCreateView(inflater, container, savedInstanceState);
        getWebView().getSettings().setJavaScriptEnabled(true);
        // настройка масштабирования
        getWebView().getSettings().setSupportZoom(false);
        getWebView().getSettings().setBuiltInZoomControls(false);
        getWebView().setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                view.loadUrl(request);
                return true;
            }
        });
        new ServerMaker().execute();
        return rootview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatServer != null) chatServer.stop();
    }


    public class ServerMaker extends AsyncTask<Void, Void, Void> {
        String host;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            webView.getSettings().setSupportZoom(false);
//            webView.getSettings().setDisplayZoomControls(false);
//            webView.getSettings().setBuiltInZoomControls(false);
//            webView.getSettings().setJavaScriptEnabled(true);
            try {
                chatServer = new Server(serverPort, getActivity());
            } catch (Exception ex) {
                ex.printStackTrace();
                LogWriter.info(TAG, ex.toString());
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                chatServer.start();
                host = chatServer.getHostname();
            } catch (Exception ex) {
                if (chatServer != null) chatServer.stop();
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            webView.loadUrl(host+ String.valueOf(serverPort));
            getWebView().loadUrl("http://localhost:" + String.valueOf(serverPort));
        }
    }
}
