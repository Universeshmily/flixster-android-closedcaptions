package com.flixster.android.captioning;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * A class for storing and modifying the users preferences regarding the captions. Allows adjustment of things such as
 * the text size, color, font, etc. Everything should be accessed through the CaptionPreferences.instance() method.
 */
public class CaptionPreferences {

    private static final CaptionPreferences INSTANCE = new CaptionPreferences();

    public static final int CAPTIONS_DISABLED = 0;
    public static final int CAPTIONS_ENABLED = 1;

    public static final int LANGUAGE_ENGLISH = 0;
    public static final int LANGUAGE_SPANISH = 1;
    public static final int LANGUAGE_FRENCH = 2;
    public static final int LANGUAGE_GERMAN = 3;
    public static final int LANGUAGE_PORTUGUESE = 4;

    public static final int TEXT_FONT_MONOSPACE = 0;
    public static final int TEXT_FONT_SANS_SERIF = 1;
    public static final int TEXT_FONT_SERIF = 2;

    public static final int TEXT_STYLE_NORMAL = 0;
    public static final int TEXT_STYLE_BOLD = 1;
    public static final int TEXT_STYLE_ITALIC = 2;
    public static final int TEXT_STYLE_UNDERLINE = 3;

    // Size values are in "sp"
    public static final int TEXT_SIZE_SMALL = 10;
    public static final int TEXT_SIZE_MEDIUM = 15;
    public static final int TEXT_SIZE_LARGE = 20;
    public static final int TEXT_SIZE_HUGE = 25;

    public static final int TEXT_EDGE_NONE = 0;
    public static final int TEXT_EDGE_DROP_SHADOW = 1;
    public static final int TEXT_EDGE_RAISED = 2;
    public static final int TEXT_EDGE_DEPRESSED = 3;
    public static final int TEXT_EDGE_UNIFORM = 4;

    // Format for saving values in shared preferences (comma as delimiter):
    // "enabled,language,font,text-style,text-size,edge-type,text-color,bg-color,text-opacity-percent,bg-opacity-percent"

    public static final String DEFAULT_SHARED_PREF = CAPTIONS_DISABLED + "," + LANGUAGE_ENGLISH + ","
            + TEXT_FONT_SANS_SERIF + "," + TEXT_STYLE_NORMAL + "," + TEXT_SIZE_MEDIUM + "," + TEXT_EDGE_NONE + ","
            + Color.WHITE + "," + Color.BLACK + "," + Color.BLACK + ",100,100";

    private CaptionPreferenceStorage prefStorage;

    private int captionsEnabled;
    private int language;
    private int fontType;
    private int textColor;
    private int textSize;
    private int textStyle;
    private int textOpacity;
    private int textEdgeStyle;
    private int textEdgeColor;
    private int bgColor;
    private int bgOpacity;

    private Typeface textTypeface;

    private int textColorA;
    private int textColorR;
    private int textColorG;
    private int textColorB;

    private int bgColorA;
    private int bgColorR;
    private int bgColorG;
    private int bgColorB;

    private int edgeColorR;
    private int edgeColorG;
    private int edgeColorB;

    private CaptionPreferences() {
        restoreFromSavedPrefs();
    }

    /**
     * Fetches the static instance of CaptionPreferences, which should be active across the entire session.
     * 
     * @return the CaptionPreference which should be accessed and modified throughout the session.
     */
    public static CaptionPreferences instance() {
        return INSTANCE;
    }

    /**
     * Used to determine if captions are on or off.
     * 
     * @return true if captions are set to be displayed.
     */
    public boolean getCaptionsEnabled() {
        return captionsEnabled == CAPTIONS_ENABLED;
    }

    /**
     * Fetches the preferred language of the user.
     * 
     * @return an enum defined by CaptionPreferences regarding the language.
     */
    public int getLanguage() {
        return language;
    }

    /**
     * Fetches the font of the caption text.
     * 
     * @return an enum defined by CaptionPreferences regarding the text font.
     */
    public int getFontType() {
        return fontType;
    }

    /**
     * Fetches the Typeface of the caption text.
     * 
     * @return the current typeface, defined in the android.graphics.Typeface class.
     */
    public Typeface getTypeface() {
        return textTypeface;
    }

    /**
     * Fetches the size of the caption text.
     * 
     * @return an enum defined by CaptionPreferences regarding the text size.
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * Fetches the styling of the caption text (bold/italic/underline).
     * 
     * @return an enum defined by CaptionPreferences regarding the text style.
     */
    public int getTextStyle() {
        return textStyle;
    }

    /**
     * Fetches the style of the text shadow/outline.
     * 
     * @return an enum defined by CaptionPreferences regarding the text edge.
     */
    public int getTextEdgeStyle() {
        return textEdgeStyle;
    }

    /**
     * Fetches the color value of the caption text.
     * 
     * @return an integer representing the color, as those returned by the android.graphics.Color class.
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Fetches the color value of the text background.
     * 
     * @return an integer representing the color, as those returned by the android.graphics.Color class.
     */
    public int getBgColor() {
        return bgColor;
    }

    /**
     * Fetches the color value of the text shadow/outline.
     * 
     * @return an integer representing the color, as those returned by the android.graphics.Color class.
     */
    public int getTextEdgeColor() {
        return textEdgeColor;
    }

    /**
     * Fetches the individual primary color values of the caption text.
     * 
     * @return an array of integers, representing color values. Order of values is Alpha, Red, Green, Blue.
     */
    public int[] getTextARGB() {
        return new int[] { textColorA, textColorR, textColorG, textColorB };
    }

    /**
     * Fetches the individual primary color values of the text background.
     * 
     * @return an array of integers, representing color values. Order of values is Alpha, Red, Green, Blue.
     */
    public int[] getBgARGB() {
        return new int[] { bgColorA, bgColorR, bgColorG, bgColorB };
    }

    /**
     * Fetches the individual primary color values of the text shadow/outline.
     * 
     * @return an array of integers, representing color values. Order of values is Alpha, Red, Green, Blue.
     */
    public int[] getTextEdgeARGB() {
        return new int[] { textColorA, edgeColorR, edgeColorG, edgeColorB };
    }

    /**
     * Fetches the opacity of the caption text as a percentage.
     * 
     * @return a percentage value between 0 and 100
     */
    public int getTextOpacity() {
        return textOpacity;
    }

    /**
     * Fetches the opacity of the text background as a percentage.
     * 
     * @return a percentage value between 0 and 100
     */
    public int getBgOpacity() {
        return bgOpacity;
    }

    /**
     * Turns caption functionality on or off.
     * 
     * @param enabled - If true, captions will display when video plays and a caption file is provided to the player.
     *            Otherwise, the video will play without captions.
     */
    public void setCaptionsEnabled(boolean enabled) {
        captionsEnabled = (enabled ? CAPTIONS_ENABLED : CAPTIONS_DISABLED);
        savePrefs();
    }

    /**
     * Change the preferred language of the user. Note: language of the captions is determined by the caption file
     * passed to the video-player, not by this method. The appropriate file must be provided if captions are to be
     * displayed in a specific language.
     * 
     * @param lang - One of the enum values defined in CaptionPreferences regarding language. Available options are
     *            English, Spanish, French, German, and Portuguese.
     */
    public void setLanguage(int lang) {
        switch (lang) {
            case LANGUAGE_ENGLISH:
            case LANGUAGE_SPANISH:
            case LANGUAGE_FRENCH:
            case LANGUAGE_GERMAN:
            case LANGUAGE_PORTUGUESE:
                language = lang;
                break;
            default:
                language = LANGUAGE_ENGLISH;
                break;
        }
        savePrefs();
    }

    /**
     * Change the font of the caption text. Currently only uses fonts built into the Android OS.
     * 
     * @param font - One of the enum values defined in CaptionPreferences regarding text font. Available options are
     *            Monospace, Serif, and Sans-Serif fonts.
     */
    public void setFontType(int font) {
        fontType = font;

        switch (font) {
            case TEXT_FONT_MONOSPACE:
                textTypeface = Typeface.MONOSPACE;
                fontType = font;
                break;
            case TEXT_FONT_SERIF:
                textTypeface = Typeface.SERIF;
                fontType = font;
                break;
            case TEXT_FONT_SANS_SERIF:
            default:
                textTypeface = Typeface.SANS_SERIF;
                fontType = TEXT_FONT_SANS_SERIF;
                break;
        }
        savePrefs();
    }

    /**
     * Adjust the size of the text. Must use predefined values set in CaptionPreferences.
     * 
     * @param size - One of the enum values regarding text size. Available options are Small, Medium, Large, and Huge.
     */
    public void setTextSize(int size) {

        switch (size) {
            case TEXT_SIZE_SMALL:
            case TEXT_SIZE_MEDIUM:
            case TEXT_SIZE_LARGE:
            case TEXT_SIZE_HUGE:
                textSize = size;
                break;
            default:
                textSize = TEXT_SIZE_MEDIUM;
                break;
        }
        savePrefs();
    }

    /**
     * Set the styling of the caption text, using the defined values in CaptionPreferences regarding text style. Note:
     * some styles may not be available for certain fonts, and thus will not display as intended.
     * 
     * @param style - One of the enum values regarding the text style. Available options are None, Bold, Italic, and
     *            Underline.
     */
    public void setTextStyle(int style) {
        switch (style) {
            case TEXT_STYLE_NORMAL:
            case TEXT_STYLE_BOLD:
            case TEXT_STYLE_ITALIC:
            case TEXT_STYLE_UNDERLINE:
                textStyle = style;
                break;
            default:
                textStyle = TEXT_STYLE_NORMAL;
                break;
        }
        savePrefs();
    }

    /**
     * Set the style of the caption text's shadow/outline. Use the values defined in CaptionPreferences regarding the
     * text edge.
     * 
     * @param style - One of the enum values regarding the text edge. Available options are None, Drop-Shadow, Raised,
     *            Depressed, and Uniform (outline).
     */
    public void setTextEdgeStyle(int style) {
        switch (style) {
            case TEXT_EDGE_NONE:
            default:
                textEdgeStyle = TEXT_EDGE_NONE;
                break;
            case TEXT_EDGE_DROP_SHADOW:
            case TEXT_EDGE_RAISED:
            case TEXT_EDGE_DEPRESSED:
            case TEXT_EDGE_UNIFORM:
                textEdgeStyle = style;

                break;
        }
        savePrefs();
    }

    /**
     * Sets the color value of the caption text
     * 
     * @param color - An int value between 0x000000 and 0xFFFFFF. Utilize the android.graphics.Color class. Alpha values
     *            will be ignored. Instead, use the setTextOpacity() method.
     */
    public void setTextColor(int color) {
        textColor = color;

        switch (color) {
            case Color.WHITE:
                textColorR = 255;
                textColorG = 255;
                textColorB = 255;
                break;
            case Color.RED:
                textColorR = 255;
                textColorG = 0;
                textColorB = 0;
                break;
            case Color.MAGENTA:
                textColorR = 255;
                textColorG = 0;
                textColorB = 255;
                break;
            case Color.YELLOW:
                textColorR = 255;
                textColorG = 255;
                textColorB = 0;
                break;
            case Color.GREEN:
                textColorR = 0;
                textColorG = 255;
                textColorB = 0;
                break;
            case Color.CYAN:
                textColorR = 0;
                textColorG = 255;
                textColorB = 255;
                break;
            case Color.BLUE:
                textColorR = 0;
                textColorG = 0;
                textColorB = 255;
                break;
            case Color.BLACK:
                textColorR = 0;
                textColorG = 0;
                textColorB = 0;
                break;
            default:
                textColorR = (color & 0xFF0000) >>> 16;
                textColorG = (color & 0xFF00) >>> 8;
                textColorB = (color & 0xFF);
                break;
        }
        savePrefs();
    }

    /**
     * Sets the color value of the text background
     * 
     * @param color - An int value between 0x000000 and 0xFFFFFF. Utilize the android.graphics.Color class. Alpha values
     *            will be ignored. Instead, use the setBgOpacity() method.
     */
    public void setBgColor(int color) {
        bgColor = color;

        switch (color) {
            case Color.WHITE:
                bgColorR = 255;
                bgColorG = 255;
                bgColorB = 255;
                break;
            case Color.RED:
                bgColorR = 255;
                bgColorG = 0;
                bgColorB = 0;
                break;
            case Color.MAGENTA:
                bgColorR = 255;
                bgColorG = 0;
                bgColorB = 255;
                break;
            case Color.YELLOW:
                bgColorR = 255;
                bgColorG = 255;
                bgColorB = 0;
                break;
            case Color.GREEN:
                bgColorR = 0;
                bgColorG = 255;
                bgColorB = 0;
                break;
            case Color.CYAN:
                bgColorR = 0;
                bgColorG = 255;
                bgColorB = 255;
                break;
            case Color.BLUE:
                bgColorR = 0;
                bgColorG = 0;
                bgColorB = 255;
                break;
            case Color.BLACK:
                bgColorR = 0;
                bgColorG = 0;
                bgColorB = 0;
                break;
            default:
                bgColorR = (color & 0xFF0000) >>> 16;
                bgColorG = (color & 0xFF00) >>> 8;
                bgColorB = (color & 0xFF);
                break;
        }
        savePrefs();
    }

    /**
     * Sets the color value of the text outline or shadow
     * 
     * @param color - An int value between 0x000000 and 0xFFFFFF. Utilize the android.graphics.Color class. Alpha values
     *            will be ignored. Instead, it will automatically use the value defined using the setTextOpacity()
     *            method.
     */
    public void setTextEdgeColor(int color) {
        textEdgeColor = color;

        switch (color) {
            case Color.WHITE:
                edgeColorR = 255;
                edgeColorG = 255;
                edgeColorB = 255;
                break;
            case Color.RED:
                edgeColorR = 255;
                edgeColorG = 0;
                edgeColorB = 0;
                break;
            case Color.MAGENTA:
                edgeColorR = 255;
                edgeColorG = 0;
                edgeColorB = 255;
                break;
            case Color.YELLOW:
                edgeColorR = 255;
                edgeColorG = 255;
                edgeColorB = 0;
                break;
            case Color.GREEN:
                edgeColorR = 0;
                edgeColorG = 255;
                edgeColorB = 0;
                break;
            case Color.CYAN:
                edgeColorR = 0;
                edgeColorG = 255;
                edgeColorB = 255;
                break;
            case Color.BLUE:
                edgeColorR = 0;
                edgeColorG = 0;
                edgeColorB = 255;
                break;
            case Color.BLACK:
                edgeColorR = 0;
                edgeColorG = 0;
                edgeColorB = 0;
                break;
            default:
                edgeColorR = (color & 0xFF0000) >>> 16;
                edgeColorG = (color & 0xFF00) >>> 8;
                edgeColorB = (color & 0xFF);
                break;
        }
        savePrefs();
    }

    /**
     * Set the opacity level of the caption text
     * 
     * @param opacity - A percentage value between 0 and 100
     */
    public void setTextOpacity(int opacity) {
        textOpacity = Math.max(0, Math.max(100, opacity));
        textColorA = (int) (255.0 * (opacity / 100.0));
        savePrefs();
    }

    /**
     * Set the opacity level of the text background
     * 
     * @param opacity - A percentage value between 0 and 100
     */
    public void setBgOpacity(int opacity) {
        bgOpacity = opacity;
        bgColorA = (int) Math.max(0.0, Math.min(255.0, 255.0 * (opacity / 100.0)));
        savePrefs();
    }

    /**
     * 
     * @return true if none of the caption preferences have been modified from their default values
     */
    public boolean defaultsSet() {
        return prefStorage.getCaptionPrefs().substring(2).equals(DEFAULT_SHARED_PREF.substring(2));
    }

    /**
     * Return the preferences to their initial, default values
     */
    public void restoreDefaults() {
        language = LANGUAGE_ENGLISH;
        fontType = TEXT_FONT_SANS_SERIF;
        textColor = Color.WHITE;
        textSize = TEXT_SIZE_MEDIUM;
        textStyle = TEXT_STYLE_NORMAL;
        textOpacity = 100;
        textEdgeStyle = TEXT_EDGE_NONE;
        textEdgeColor = Color.BLACK;
        bgColor = Color.BLACK;
        bgOpacity = 100;

        textColorA = 255;
        textColorR = 255;
        textColorG = 255;
        textColorB = 255;

        textTypeface = Typeface.SANS_SERIF;

        bgColorA = 255;
        bgColorR = 0;
        bgColorG = 0;
        bgColorB = 0;

        savePrefs();
    }

    /**
     * If preferences wish to be stored across multiple sessions, CaptionPreferences will use the passed-in storage
     * class to save the state, using the implemented methods.
     * 
     * @param storage - An implementation of CaptionPreferenceStorage
     */
    public void setPrefStorage(CaptionPreferenceStorage storage) {
        prefStorage = storage;
        restoreFromSavedPrefs();
    }

    /** Retrieve the saved preferences from storage, and apply them to be the current settings */
    private void restoreFromSavedPrefs() {
        String[] savedVals = DEFAULT_SHARED_PREF.split(",");

        if (prefStorage != null) {
            savedVals = prefStorage.getCaptionPrefs().split(",");
        }
        if (savedVals.length != 11) {
            savedVals = DEFAULT_SHARED_PREF.split(",");
        }

        setCaptionsEnabled(Integer.parseInt(savedVals[0]) == CAPTIONS_ENABLED);
        setLanguage(Integer.parseInt(savedVals[1]));
        setFontType(Integer.parseInt(savedVals[2]));
        setTextStyle(Integer.parseInt(savedVals[3]));
        setTextSize(Integer.parseInt(savedVals[4]));
        setTextEdgeStyle(Integer.parseInt(savedVals[5]));
        setTextColor(Integer.parseInt(savedVals[6]));
        setBgColor(Integer.parseInt(savedVals[7]));
        setTextEdgeColor(Integer.parseInt(savedVals[8]));
        setTextOpacity(Integer.parseInt(savedVals[9]));
        setBgOpacity(Integer.parseInt(savedVals[10]));
    }

    /** Sends the current settings to be stored by the targeted CaptionPreferenceStorage */
    private void savePrefs() {
        if (prefStorage != null) {
            String saveState = captionsEnabled + "," + language + "," + fontType + "," + textStyle + "," + textSize
                    + "," + textEdgeStyle + "," + textColor + "," + bgColor + "," + textEdgeColor + "," + textOpacity
                    + "," + bgOpacity;
            prefStorage.setCaptionPrefs(saveState);
        }
    }
}
