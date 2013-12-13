package com.captioning.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.VideoView;

/**
 * Activity which handles the display of captions. Should be extended by the activity which handles video playback.
 */
public class CaptionedPlayer extends Activity {

    private static final String FILE_PROTOCOL = "file://";
    private static final int CAPTION_MONITOR_INTERVAL_MS = 300;
    private static final int TIMEOUT_CONNECTION = 4000;
    private static final int TIMEOUT_READ = 60000;

    private boolean captionsActive = false;
    private boolean threadActive = false;
    private VideoView videoview;
    private CaptionView[] captionViews;
    private List<TimedTextElement> captions;
    private String captionUrl;
    private DisplayMetrics outMetrics;

    /**
     * Initializes the provided CaptionViews and prepares to fetch the captions from the url. Should be called within
     * the onCreate() method of the child class.
     * 
     * @param vv - The main VideoView which will hold the media going alongside the captions. Necessary to ensure
     *            captions are synced to the video.
     * @param cvs - The array of CaptionsViews which will used to display the caption text. Position of the CaptionView
     *            will correspond to the region of the TimedTextElement when determining where to display the text.
     * @param url - The file path or url from which the captions will be fetched. Captions must be in SMPTE-TT/TTML
     *            format.
     */
    protected void prepareCaptions(VideoView vv, CaptionView[] cvs, String url) {
        videoview = vv;
        captionViews = cvs;
        captionUrl = url;

        outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        if (CaptionPreferences.instance().getCaptionsEnabled() && captionUrl != null && !captionUrl.equals("")) {
            VersionedCaptionHelper.instance().setSystemCaptionPreferences(this);
            for (CaptionView cv : captionViews) {
                cv.applyPreferences();
                cv.setVisibility(View.INVISIBLE);
            }
            fetchCaptions(successHandler, errorHandler, captionUrl);
        }
    }

    /**
     * Initiates the thread which updates captions. Will continue to run until paused.
     */
    protected void rollCaptions() {
        if (CaptionPreferences.instance().getCaptionsEnabled() && captionUrl != null && !captionUrl.equals("")) {
            captionsActive = true;

            if (!threadActive) {
                Thread captionMonitorThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadActive = true;
                        while (captionsActive) {
                            captionHandler.sendEmptyMessage(0);
                            try {
                                Thread.sleep(CAPTION_MONITOR_INTERVAL_MS);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        threadActive = false;
                    }
                });
                captionMonitorThread.start();
            }
            CaptionLogger.d("CaptionedPlayer.rollCaptions caption display initiated");
        }
    }

    /**
     * Pauses the thread which updates captions, and hides currently visible text.
     */
    protected void stopCaptions() {
        for (CaptionView cv : captionViews) {
            cv.setVisibility(View.INVISIBLE);
        }
        captionsActive = false;
        CaptionLogger.d("CaptionedPlayer.stopCaptions caption display paused");
    }

    /**
     * The main logic of displaying captions. Shows the appropriate captions according to the position of the provided
     * VideoView
     */
    private final Handler captionHandler = new Handler() {
        int ttIndex;
        int currentPosition;

        @Override
        public void handleMessage(Message msg) {
            if (captions != null) {
                int width = outMetrics.widthPixels;
                int height = outMetrics.heightPixels;
                int textSizeOffset = CaptionView.getSizeDisplayOffset();
                int stackedViewSpacing = CaptionView.getStackedViewSpacing();

                if (videoview.getCurrentPosition() < currentPosition) {
                    ttIndex = 0;
                    for (int i = 0; i < captionViews.length; i++) {
                        captionViews[i].setVisibility(View.INVISIBLE);
                    }
                }
                currentPosition = videoview.getCurrentPosition();
                for (int i = ttIndex; i < captions.size(); i++) {
                    TimedTextElement ttElement = captions.get(i);
                    if (ttElement.end <= currentPosition) {
                        if (captionViews[ttElement.region].getVisibility() == View.VISIBLE) {
                            captionViews[ttElement.region].setVisibility(View.INVISIBLE);
                            CaptionLogger.d("hiding index " + i + ", text " + ttElement.text);
                        }
                        ttIndex++;
                    } else if (ttElement.begin <= currentPosition) {
                        if (captionViews[ttElement.region].getVisibility() != View.VISIBLE) {
                            CaptionLogger.d("showing index " + i + ", text " + ttElement.text);
                            MarginLayoutParams cvParams = (MarginLayoutParams) captionViews[ttElement.region]
                                    .getLayoutParams();
                            float xOrigin = (ttElement.originX + textSizeOffset) / 100.0f;
                            float yOrigin = (ttElement.originY + textSizeOffset) / 100.0f;
                            cvParams.setMargins((int) (width * xOrigin), (int) (height * yOrigin)
                                    + (stackedViewSpacing * ttElement.region), 0, 5);
                            captionViews[ttElement.region].setText(ttElement.text);
                            captionViews[ttElement.region].setVisibility(View.VISIBLE);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    };

    /** Called upon the success of fetchCaptions(), and initiates the display of captions */
    private final Handler successHandler = new Handler() {
        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            captions = (List<TimedTextElement>) msg.obj;
            CaptionLogger.d("CaptionedPlayer.successHandler fetch captions succeeded");
        }
    };

    /** Called upon the failure of fetchCaptions() */
    private final Handler errorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CaptionLogger.d("CaptionedPlayer.errorHandler fetch captions failed");
        }
    };

    /** Parses the captions provided through the urlString, and notifies of success or failure */
    private static void fetchCaptions(final Handler successHandler, final Handler errorHandler, final String urlString) {
        CaptionLogger.d("CaptionedPlayer.fetchCaptions " + urlString);
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    if (urlString.startsWith(FILE_PROTOCOL)) {
                        File file = new File(urlString.replace(FILE_PROTOCOL, ""));
                        is = new FileInputStream(file);
                    } else {
                        HttpURLConnection connection = (HttpURLConnection) (new URL(urlString)).openConnection();
                        connection.setConnectTimeout(TIMEOUT_CONNECTION);
                        connection.setReadTimeout(TIMEOUT_READ);
                        connection.connect();
                        is = connection.getInputStream();
                    }
                    if (is != null) {
                        CaptionsXmlParser parser = new CaptionsXmlParser();
                        List<TimedTextElement> ttElements = parser.parse(is);
                        successHandler.sendMessage(Message.obtain(null, 0, Collections.unmodifiableList(ttElements)));
                    } else {
                        errorHandler.sendEmptyMessage(0);
                        CaptionLogger.w("CaptionedPlayer.fetchCaptions InputStream null");
                    }
                } catch (IOException e) {
                    errorHandler.sendEmptyMessage(0);
                    CaptionLogger.w("CaptionPlayer.fetchCaptions IOException on accessing InputStream", e);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            CaptionLogger.w("CaptionPlayer.fetchCaptions IOException on closing InputStream", e);

                        }
                    }
                }
            }
        }).start();
    }
}
