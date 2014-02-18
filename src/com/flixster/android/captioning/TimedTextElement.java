package com.flixster.android.captioning;

/** Object representation of individual SMPTE-TT/TTML timed text data */
public class TimedTextElement {
    public static final String TEST_URL = "http://us1cc.res.com.edgesuite.net/p/prometheus_19cf6ad4_cc_lid_0_1.xml";
    public static final String LOCAL_FILE_EXTENSION = ".ttml";

    protected static final String TAG_TT = "tt";
    protected static final String TAG_P = "p";
    protected static final String TAG_BR = "br";

    protected static final String ATTR_DROP_MODE = "ttp:dropMode";
    protected static final String ATTR_FRAME_RATE = "ttp:frameRate";
    protected static final String ATTR_FRAME_RATE_MULTIPLIER = "ttp:frameRateMultiplier";
    protected static final String ATTR_BEGIN = "begin";
    protected static final String ATTR_END = "end";
    protected static final String ATTR_ORIGIN = "tts:origin";

    private enum SmpteDropMode {
        DROP_NTSC, DROP_PAL, NON_DROP;

        protected static SmpteDropMode match(String dropMode) {
            if ("dropNTSC".equals(dropMode)) {
                return DROP_NTSC;
            } else if ("dropPAL".equals(dropMode)) {
                return DROP_PAL;
            } else if ("nonDrop".equals(dropMode)) {
                return NON_DROP;
            } else {
                return NON_DROP;
            }
        }
    }

    private enum SmpteFrameRate {
        SMPTE_2398, // 23.98 fps (Film Sync)
        SMPTE_24, // 24 fps
        SMPTE_25, // 25 fps (PAL)
        SMPTE_2997_DROP, // 29.97 fps drop frame (NTSC)
        SMPTE_2997_NONDROP, // 29.97 fps non drop frame (NTSC)
        SMPTE_30; // 30 fps

        protected static SmpteFrameRate match(double frameRate) {
            int rateFloored = (int) Math.floor(frameRate);
            switch (rateFloored) {
                case 23:
                    return SMPTE_2398;
                case 24:
                    return SMPTE_24;
                case 25:
                    return SMPTE_25;
                case 29:
                    return SMPTE_2997_NONDROP;
                case 30:
                    return SMPTE_30;
                case 50:
                    return SMPTE_25;
                case 60:
                    return SMPTE_30;
                case 59:
                    return SMPTE_2997_NONDROP;
                default:
                    return SMPTE_30;
            }
        }
    }

    public final int begin;
    public final int end;
    public final int region;
    public final int originX, originY;
    public final String text;

    protected TimedTextElement(String begin, String end, int region, String origin, String text, String dropMode,
            String frameRate, String frameRateMultiplier) {
        double frameRateValue = 0;
        if (frameRateMultiplier != null) {
            String[] multiplierStr = frameRateMultiplier.split(" ");
            int frameRateNumerator = Integer.parseInt(multiplierStr[0]);
            int frameRateDenominator = Integer.parseInt(multiplierStr[1]);
            frameRateValue = (double) Integer.parseInt(frameRate) * frameRateNumerator / frameRateDenominator;
        }
        SmpteDropMode mode = SmpteDropMode.match(dropMode);
        SmpteFrameRate rate = SmpteFrameRate.SMPTE_30;
        switch (mode) {
            case DROP_NTSC:
            case DROP_PAL:
                rate = SmpteFrameRate.SMPTE_2997_DROP;
                break;
            case NON_DROP:
                rate = SmpteFrameRate.match(frameRateValue);
                break;
        }

        this.begin = (int) convertSmpteToAbsoluteTime(begin, rate);
        this.end = (int) convertSmpteToAbsoluteTime(end, rate);
        this.region = region;
        if (origin != null) {
            String[] originStr = origin.replace("%", "").split(" ");
            this.originX = (int) Float.parseFloat(originStr[0]);
            this.originY = (int) Float.parseFloat(originStr[1]);
        } else {
            this.originX = 15;
            this.originY = 80;
        }
        this.text = text;
    }

    /**
     * Converts a given time-expression and frame-rate into milliseconds
     * 
     * @param timeExpression - String with format defined at
     *            <http://www.w3.org/TR/ttaf1-dfxp/#timing-value-timeExpression>
     * @param rate - Specified frame-rate
     * @return - Long time in milliseconds
     */
    private long convertSmpteToAbsoluteTime(String timeExpression, SmpteFrameRate rate) {
        String[] timeVals = timeExpression.split(":");
        try {
            if (timeVals.length == 1) {
                double val;
                if (timeExpression.substring(timeExpression.length() - 2).equals("ms")) {
                    val = Double.parseDouble(timeExpression.substring(0, timeExpression.length() - 2)) / 1000.0;
                } else {
                    val = Double.parseDouble(timeExpression.substring(0, timeExpression.length() - 1));
                }
                switch (timeExpression.charAt(timeExpression.length() - 1)) {
                    case 'h': // hours
                        return convertSmpteToAbsoluteTime(val, 0, 0, 0, rate);
                    case 'm': // minutes
                        return convertSmpteToAbsoluteTime(0, val, 0, 0, rate);
                    case 's': // seconds or milliseconds
                        return convertSmpteToAbsoluteTime(0, 0, val, 0, rate);
                    case 'f': // frames
                        return convertSmpteToAbsoluteTime(0, 0, 0, val, rate);
                    default:
                        CaptionLogger.w("Could not parse timeExpression \"" + timeExpression + "\"");
                        return 0;
                }
            } else if (timeVals.length == 3) {
                return convertSmpteToAbsoluteTime(Double.parseDouble(timeVals[0]), Double.parseDouble(timeVals[1]),
                        Double.parseDouble(timeVals[2]), 0, rate);
            } else if (timeVals.length == 4) {
                return convertSmpteToAbsoluteTime(Double.parseDouble(timeVals[0]), Double.parseDouble(timeVals[1]),
                        Double.parseDouble(timeVals[2]), Double.parseDouble(timeVals[3]), rate);
            }
        } catch (NumberFormatException e) {
            CaptionLogger.w("Could not parse timeExpression \"" + timeExpression + "\"");
        }
        return 0;
    }

    private long convertSmpteToAbsoluteTime(double hours, double minutes, double seconds, double frames,
            SmpteFrameRate rate) {
        switch (rate) {
            case SMPTE_2398:
                return smpte23_98_ToAbsoluteTime(hours, minutes, seconds, frames);
            case SMPTE_24:
                return smpte24_ToAbsoluteTime(hours, minutes, seconds, frames);
            case SMPTE_25:
                return smpte25_ToAbsoluteTime(hours, minutes, seconds, frames);
            case SMPTE_2997_DROP:
                return smpte29_97_Drop_ToAbsoluteTime(hours, minutes, seconds, frames);
            case SMPTE_2997_NONDROP:
                return smpte29_97_NonDrop_ToAbsoluteTime(hours, minutes, seconds, frames);
            case SMPTE_30:
                return smpte30_ToAbsoluteTime(hours, minutes, seconds, frames);
            default:
                return 0;
        }
    }

    private long smpte23_98_ToAbsoluteTime(double hours, double minutes, double seconds, double frames) {
        return (long) (Math.ceil(3753.75 * frames) + 90090l * (long) (seconds + 60 * (minutes + 60 * hours))) * 1000 / 90000;
    }

    private long smpte24_ToAbsoluteTime(double hours, double minutes, double seconds, double frames) {
        return (long) (3750 * frames + 90000l * (long) (seconds + 60 * (minutes + 60 * hours))) * 1000 / 90000;
    }

    private long smpte25_ToAbsoluteTime(double hours, double minutes, double seconds, double frames) {
        return (long) (3600 * frames + 90000l * (long) (seconds + 60 * (minutes + 60 * hours))) * 1000 / 90000;
    }

    private long smpte29_97_Drop_ToAbsoluteTime(double hours, double minutes, double seconds, double frames) {
        return (long) (3003 * frames + 90090l * seconds + 26999973l * minutes / 5 + 323999676l * hours) * 1000 / 90000;
    }

    private long smpte29_97_NonDrop_ToAbsoluteTime(double hours, double minutes, double seconds, double frames) {
        return (long) (3003 * frames + 90090l * (seconds + 60 * (minutes + 60 * hours))) * 1000 / 90000;
    }

    private long smpte30_ToAbsoluteTime(double hours, double minutes, double seconds, double frames) {
        return (long) (3000 * frames + 90000l * (seconds + 60 * (minutes + 60 * hours))) * 1000 / 90000;
    }

}
