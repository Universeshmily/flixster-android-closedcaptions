package com.captioning.example;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.captioning.R;
import com.captioning.android.CaptionView;
import com.captioning.android.CaptionedPlayer;

/** Creates and plays a simple video with captions */
public class ExamplePlayer extends CaptionedPlayer {

    private static final String VIDEO_FILE = "";
    private static final String CAPTIONS_FILE = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Pass the necessary objects to the CaptionedPlayer
        prepareCaptions(videoView, captionViews, CAPTIONS_FILE);

        // Start the video
        videoView.setVideoPath(VIDEO_FILE);
        videoView.start();

        // Start displaying captions
        rollCaptions();
    }

    @Override
    public void onDestroy() {
        // Stop the thread that displays captions
        stopCaptions();

        super.onDestroy();
    }
}
