package com.captioning.android;

import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;

/**
 * In Android 4.4+, the OS provides its own caption preference implementation, which can be accessed through the
 * accessibility settings. The CaptionedPlayer is set to copy and use these preferences from the system when the OS
 * supports it. If preferred, this functionality can be turned off from within the VersionedCaptionHelper class by
 * calling the useSystemCaptionPreferences() method.
 * 
 * For versions 4.3 and lower, this has no effect on the functionality of the player.
 */
public abstract class VersionedCaptionHelper {

    private static VersionedCaptionHelper INSTANCE;
    protected boolean useSystemSettings = true;

    public abstract void setSystemCaptionPreferences(Context context);

    /** Used to turn on or off the use of the OS system preferences for determining caption features */
    public void useSystemCaptionPreferences(boolean useSystem) {
        useSystemSettings = useSystem;
    }

    /** Returns whether the OS system preferences will be used or not */
    public boolean usingSystemCaptionPreferences() {
        return useSystemSettings;
    }

    /** Based on the OS version, returns the appropriate child class */
    public static VersionedCaptionHelper instance() {
        if (INSTANCE == null) {
            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion >= 19) {
                INSTANCE = new KitKatCaptionHelper();
            } else {
                INSTANCE = new DefaultCaptionHelper();
            }
        }
        return INSTANCE;
    }

    /**
     * The version for OS 4.4+, which uses the system preferences if that functionality is enabled.
     */
    private static class KitKatCaptionHelper extends DefaultCaptionHelper {

        /**
         * Fetches the settings from the OS and copies them into the CaptionPreferences. Does nothing if the
         * functionality is disabled.
         */
        @Override
        public void setSystemCaptionPreferences(Context context) {
            if (useSystemSettings) {
                CaptionPreferences prefs = CaptionPreferences.instance();
                prefs.restoreDefaults();

                CaptioningManager capManager = (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);

                prefs.setCaptionsEnabled(capManager.isEnabled());

                float size = capManager.getFontScale();
                if (size <= 0.5f) {
                    prefs.setTextSize(CaptionPreferences.TEXT_SIZE_SMALL);
                } else if (size <= 1.0f) {
                    prefs.setTextSize(CaptionPreferences.TEXT_SIZE_MEDIUM);
                } else if (size <= 1.5f) {
                    prefs.setTextSize(CaptionPreferences.TEXT_SIZE_LARGE);
                } else {
                    prefs.setTextSize(CaptionPreferences.TEXT_SIZE_HUGE);
                }

                Locale loc = capManager.getLocale();
                if (loc != null) {
                    String language = capManager.getLocale().getLanguage();
                    if (language.equals("es") || language.contains("es-")) {
                        prefs.setLanguage(CaptionPreferences.LANGUAGE_SPANISH);
                    } else if (language.equals("fr") || language.contains("fr-")) {
                        prefs.setLanguage(CaptionPreferences.LANGUAGE_FRENCH);
                    } else if (language.equals("de") || language.contains("de-")) {
                        prefs.setLanguage(CaptionPreferences.LANGUAGE_GERMAN);
                    } else if (language.equals("pt") || language.contains("pt-")) {
                        prefs.setLanguage(CaptionPreferences.LANGUAGE_PORTUGUESE);
                    } else { // if (language.equals("en") || language.contains("en-")) {
                        prefs.setLanguage(CaptionPreferences.LANGUAGE_ENGLISH);
                    }
                } else {
                    prefs.setLanguage(CaptionPreferences.LANGUAGE_ENGLISH);
                }

                CaptionStyle capStyle = capManager.getUserStyle();

                int textR = (capStyle.foregroundColor & 0xFF0000) >>> 16;
                int textG = (capStyle.foregroundColor & 0xFF00) >>> 8;
                int textB = capStyle.foregroundColor & 0xFF;
                prefs.setTextColor(Color.rgb(textR, textG, textB));

                int bgR = (capStyle.backgroundColor & 0xFF0000) >>> 16;
                int bgG = (capStyle.backgroundColor & 0xFF00) >>> 8;
                int bgB = capStyle.backgroundColor & 0xFF;
                prefs.setBgColor(Color.rgb(bgR, bgG, bgB));

                prefs.setTextEdgeColor(capStyle.edgeColor);

                int textA = (capStyle.foregroundColor & 0xFF000000) >>> 24;
                prefs.setTextOpacity((int) ((textA / 255.0) * 100));
                int bgA = (capStyle.backgroundColor & 0xFF000000) >>> 24;
                prefs.setBgOpacity((int) ((bgA / 255.0) * 100));

                switch (capStyle.edgeType) {
                    case CaptionStyle.EDGE_TYPE_NONE:
                    default:
                        prefs.setTextEdgeStyle(CaptionPreferences.TEXT_EDGE_NONE);
                        break;
                    case CaptionStyle.EDGE_TYPE_DROP_SHADOW:
                        prefs.setTextEdgeStyle(CaptionPreferences.TEXT_EDGE_DROP_SHADOW);
                        break;
                    case CaptionStyle.EDGE_TYPE_OUTLINE:
                        prefs.setTextEdgeStyle(CaptionPreferences.TEXT_EDGE_UNIFORM);
                        break;
                }

                Typeface font = capStyle.getTypeface();
                if (font != null) {
                    if (font.equals(Typeface.MONOSPACE)) {
                        prefs.setFontType(CaptionPreferences.TEXT_FONT_MONOSPACE);
                    } else if (font.equals(Typeface.SERIF)) {
                        prefs.setFontType(CaptionPreferences.TEXT_FONT_SERIF);
                    } else {
                        prefs.setFontType(CaptionPreferences.TEXT_FONT_SANS_SERIF);
                    }
                } else {
                    prefs.setFontType(CaptionPreferences.TEXT_FONT_SANS_SERIF);
                }
            }
        }
    }

    /**
     * The version for OS 4.3 and lower, which does nothing and uses the normal CaptionPreferences for determining
     * caption features
     */
    private static class DefaultCaptionHelper extends VersionedCaptionHelper {

        @Override
        public void setSystemCaptionPreferences(Context context) {
            // Do Nothing
        }

    }
}
