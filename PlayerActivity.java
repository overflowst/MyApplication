package com.example.rahul.myapplication;

import android.content.ComponentName;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.rahul.myapplication.service.MyMediaBrowserService;

public class PlayerActivity extends AppCompatActivity {

    MediaBrowserCompat mMediaBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // initialize media browser & media connection callback
        mMediaBrowser = new MediaBrowserCompat(PlayerActivity.this,
                new ComponentName(PlayerActivity.this, MyMediaBrowserService.class), mMediaConnectionCallback, null);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister media controller
        if (MediaControllerCompat.getMediaController(PlayerActivity.this) != null) {
            MediaControllerCompat.getMediaController(PlayerActivity.this).unregisterCallback(controllerCallback);
        }
        mMediaBrowser.disconnect();
    }

    private MediaBrowserCompat.ConnectionCallback mMediaConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();

            // Get the token for the MediaSession
            MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

            // Create a MediaControllerCompat using token
            MediaControllerCompat mediaController = null;
            try {
                mediaController = new MediaControllerCompat(PlayerActivity.this, token);
            } catch (RemoteException e) {
                Log.e(this.toString(), e.getMessage());
            }
            // Save the controller
            MediaControllerCompat.setMediaController(PlayerActivity.this, mediaController);

            // Finish building the UI
            buildTransportControls();
        }

        @Override
        public void onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnectsl
        }

        @Override
        public void onConnectionFailed() {
            // The Service has refused our connection
        }
    };

    private void buildTransportControls() {
        // TODO
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(PlayerActivity.this);

        // Display the initial state
        MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);
    }

    MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                }
            };
}
