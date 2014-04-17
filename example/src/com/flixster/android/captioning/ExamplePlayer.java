package com.flixster.android.captioning;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.captioning.R;
import com.flixster.android.captioning.CaptionView;
import com.flixster.android.captioning.CaptionedPlayer;

/** Creates and plays a simple video with captions */
public class ExamplePlayer extends CaptionedPlayer {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View the video in fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.example_player_layout);

        VideoView videoView = (VideoView) findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Create and group the CaptionViews for the CaptionedPlayer
        CaptionView[] captionViews = new CaptionView[4];
        captionViews[0] = (CaptionView) findViewById(R.id.caption1);
        captionViews[1] = (CaptionView) findViewById(R.id.caption2);
        captionViews[2] = (CaptionView) findViewById(R.id.caption3);
        captionViews[3] = (CaptionView) findViewById(R.id.caption4);

        Bundle b = getIntent().getExtras();

        // Pass the necessary objects to the CaptionedPlayer
        prepareCaptions(videoView, captionViews, b.getString("captions"), captionHandler);

        // Start the video
        videoView.setVideoPath(b.getString("video"));
        videoView.start();
    }

    // Notified upon the success or failure of prepareCaptions()
    private final Handler captionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            // Start displaying captions on successful load
                case NOTIFY_FETCH_SUCCESS:
                    rollCaptions();
                    break;

                // Notify user if encountered error while trying to fetch captions
                case NOTIFY_FETCH_CREATE_STREAM_FAILED:
                case NOTIFY_FETCH_IO_EXCEPTION:
                    Toast.makeText(ExamplePlayer.this,
                            "Unable to display captions.  Please ensure the provided URL is correct.",
                            Toast.LENGTH_LONG).show();
                    break;

                // Captions are always enabled in this example, so this will never be reached
                case NOTIFY_FETCH_DISABLED:
                    Toast.makeText(ExamplePlayer.this, "Captions are currently disabled.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        // Stop the thread that displays captions
        stopCaptions();

        super.onDestroy();
    }
}
