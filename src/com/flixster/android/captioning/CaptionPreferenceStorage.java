package com.flixster.android.captioning;

/**
 * Abstract interface to be used if a user wants their closed caption preferences to persist across multiple sessions.
 * You can set the target storage class by calling CaptionPreferences.instance().setPrefStorage(<storage>)
 */
public interface CaptionPreferenceStorage {

    /**
     * Called by CaptionPreferences whenever a new storage is targeted, to grab any previously stored preferences. If
     * none are found, CaptionPreferences will use its default preference settings.
     */
    public abstract String getCaptionPrefs();

    /**
     * Called by CaptionPreferences whenever a field is modified, should be used if user wishes their state to be saved
     * across multiple sessions.
     */
    public abstract void setCaptionPrefs(String prefs);
}
