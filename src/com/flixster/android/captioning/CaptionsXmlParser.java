package com.flixster.android.captioning;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/** Class used to retrieve the data from SMPTE-TT/TTML files */
public class CaptionsXmlParser {

    /**
     * Fetches the data from the file inputstream and converts into a list of TimedTextElement objects which can be more
     * easily accessed
     */
    public List<TimedTextElement> parse(InputStream is) {
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(is, null);
            return readTtml(parser);
        } catch (XmlPullParserException e) {
            CaptionLogger.w("CaptionsXmlParser.parse", e);
            return null;
        }
    }

    /** Does the work of retrieving the data and creating the TimedTextElements */
    private List<TimedTextElement> readTtml(XmlPullParser parser) {
        List<TimedTextElement> ttElements = null;
        try {
            String dropMode = null, frameRate = null, frameRateMultiplier = null;
            String begin = null, end = null, origin = null, text = null;
            int region = 0;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        ttElements = new ArrayList<TimedTextElement>();
                        break;
                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if (tagName.equals(TimedTextElement.TAG_TT)) {
                            dropMode = parser.getAttributeValue(null, TimedTextElement.ATTR_DROP_MODE);
                            frameRate = parser.getAttributeValue(null, TimedTextElement.ATTR_FRAME_RATE);
                            frameRateMultiplier = parser.getAttributeValue(null,
                                    TimedTextElement.ATTR_FRAME_RATE_MULTIPLIER);
                            CaptionLogger.d("CaptionsXmlParser.readTtml: dropMode " + dropMode + ", frameRate "
                                    + frameRate + ", frameRateMultiplier " + frameRateMultiplier);
                        } else if (tagName.equals(TimedTextElement.TAG_P)) {
                            String newBegin = parser.getAttributeValue(null, TimedTextElement.ATTR_BEGIN);
                            if (newBegin.equals(begin)) {
                                region++;
                            } else {
                                region = 0;
                            }
                            begin = newBegin;
                            end = parser.getAttributeValue(null, TimedTextElement.ATTR_END);
                            origin = parser.getAttributeValue(null, TimedTextElement.ATTR_ORIGIN);
                            text = null;
                        } else if (tagName.equals(TimedTextElement.TAG_BR)) {
                            text = text + '\n';
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (!parser.isWhitespace()) {
                            String nextText = parser.getText();
                            if (text == null) {
                                text = nextText;
                            } else {
                                text = text + nextText;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        if (tagName.equals(TimedTextElement.TAG_P)) {
                            ttElements.add(new TimedTextElement(begin, end, region, origin, text, dropMode, frameRate,
                                    frameRateMultiplier));
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            CaptionLogger.w("CaptionsXmlParser.readTtml", e);
        } catch (IOException e) {
            CaptionLogger.w("CaptionsXmlParser.readTtml", e);
        }
        return ttElements;
    }
}
