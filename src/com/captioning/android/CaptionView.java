package com.captioning.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/** Custom class that can modify its features based on the preferences specified in CaptionPreferences */
public class CaptionView extends TextView {

    private final TextPaint mPaint;

    public CaptionView(Context context) {
        this(context, null);
    }

    public CaptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setSubpixelText(true);
        mPaint.setTextAlign(Paint.Align.LEFT);

        setText("[ CAPTION ]");

    }

    /** Handles the shadow/outline of the text */
    private void setShadowLayer(int edgeType) {
        int shadowColor = CaptionPreferences.instance().getTextEdgeColor();

        switch (edgeType) {
            case CaptionPreferences.TEXT_EDGE_DEPRESSED:
                mPaint.setShadowLayer(0.01f, 0, -2, shadowColor);
                break;
            case CaptionPreferences.TEXT_EDGE_RAISED:
                mPaint.setShadowLayer(0.01f, 0, 2, shadowColor);
                break;
            case CaptionPreferences.TEXT_EDGE_DROP_SHADOW:
                mPaint.setShadowLayer(3, 2, 2, shadowColor);
                break;
            case CaptionPreferences.TEXT_EDGE_NONE:
            case CaptionPreferences.TEXT_EDGE_UNIFORM:
            default:
                mPaint.setShadowLayer(0, 0, 0, 0);
                break;
        }
    }

    /** Used to update the CaptionViews when they are initialized, or when the CaptionPreferences have been modified */
    public void applyPreferences() {
        CaptionPreferences prefs = CaptionPreferences.instance();

        setTypeface(prefs.getTypeface());

        int[] colorVals = prefs.getTextARGB();
        setTextColor(Color.argb(colorVals[0], colorVals[1], colorVals[2], colorVals[3]));
        setTextSize(prefs.getTextSize());

        if (prefs.getTextStyle() == CaptionPreferences.TEXT_STYLE_BOLD) {
            setTypeface(Typeface.create(prefs.getTypeface(), Typeface.BOLD));
        } else if (prefs.getTextStyle() == CaptionPreferences.TEXT_STYLE_ITALIC) {
            setTypeface(Typeface.create(prefs.getTypeface(), Typeface.ITALIC));
        }

        setShadowLayer(prefs.getTextEdgeStyle());

        colorVals = prefs.getBgARGB();
        int bgColor = Color.argb(colorVals[0], colorVals[1], colorVals[2], colorVals[3]);
        setBackgroundColor(bgColor);

        invalidate();
    }

    /** The overwritten onDraw() method which displays the caption text according to the defined CaptionPreferences */
    @Override
    protected void onDraw(Canvas canvas) {
        CaptionPreferences prefs = CaptionPreferences.instance();
        float textSize = getTextSize();
        int underlineOffset = (int) (textSize * 0.15f);
        int[] colorVals = prefs.getTextARGB();
        final StaticLayout layout = new StaticLayout(getText(), mPaint, getWidth(), Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);

        final int saveCount = canvas.save();
        canvas.translate(0, 0);

        if (prefs.getTextStyle() == CaptionPreferences.TEXT_STYLE_UNDERLINE) {
            mPaint.setStrokeWidth(textSize * 0.08f);

            for (int i = 0; i < layout.getLineCount(); i++) {
                int offsetY = layout.getLineBaseline(i) + underlineOffset;
                colorVals = prefs.getTextEdgeARGB();
                mPaint.setColor(Color.argb(colorVals[0], colorVals[1], colorVals[2], colorVals[3]));

                if (prefs.getTextEdgeStyle() == CaptionPreferences.TEXT_EDGE_DROP_SHADOW) {
                    mPaint.setColor(Color.argb((int) (colorVals[0] * 0.5), colorVals[1], colorVals[2], colorVals[3]));
                    canvas.drawLine(layout.getLineStart(0), offsetY + 2, layout.getLineWidth(i), offsetY + 2, mPaint);
                } else if (prefs.getTextEdgeStyle() == CaptionPreferences.TEXT_EDGE_RAISED) {
                    canvas.drawLine(layout.getLineStart(0), offsetY + 1, layout.getLineWidth(i), offsetY + 1, mPaint);
                } else if (prefs.getTextEdgeStyle() == CaptionPreferences.TEXT_EDGE_DEPRESSED) {
                    canvas.drawLine(layout.getLineStart(0), offsetY - 1, layout.getLineWidth(i), offsetY - 1, mPaint);
                } else if (prefs.getTextEdgeStyle() == CaptionPreferences.TEXT_EDGE_UNIFORM) {
                    mPaint.setStrokeWidth(textSize * 0.10f);
                    canvas.drawLine(layout.getLineStart(0), offsetY, layout.getLineWidth(i), offsetY, mPaint);
                }

                mPaint.setStrokeWidth(textSize * 0.08f);
                colorVals = prefs.getTextARGB();
                mPaint.setColor(Color.argb(colorVals[0], colorVals[1], colorVals[2], colorVals[3]));
                canvas.drawLine(layout.getLineStart(0), offsetY, layout.getLineWidth(i), offsetY, mPaint);
            }
        }

        if (prefs.getTextStyle() == CaptionPreferences.TEXT_STYLE_BOLD) {
            mPaint.setTypeface(Typeface.create(prefs.getTypeface(), Typeface.BOLD));
        } else if (prefs.getTextStyle() == CaptionPreferences.TEXT_STYLE_ITALIC) {
            mPaint.setTypeface(Typeface.create(prefs.getTypeface(), Typeface.ITALIC));
        } else if (prefs.getTextStyle() == CaptionPreferences.TEXT_STYLE_UNDERLINE) {
            mPaint.setTypeface(prefs.getTypeface());
        } else {
            mPaint.setTypeface(prefs.getTypeface());
        }

        mPaint.setTextSize(textSize);
        mPaint.setStrokeWidth(textSize * 0.05f);

        if (prefs.getTextEdgeStyle() == CaptionPreferences.TEXT_EDGE_UNIFORM) {

            colorVals = prefs.getTextEdgeARGB();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Join.ROUND);
            mPaint.setColor(Color.argb(colorVals[0], colorVals[1], colorVals[2], colorVals[3]));
            mPaint.setFlags(getPaintFlags());

            layout.draw(canvas);
        }

        colorVals = prefs.getTextARGB();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.argb(colorVals[0], colorVals[1], colorVals[2], colorVals[3]));
        mPaint.setFlags(getPaintFlags());

        layout.draw(canvas);
        canvas.restoreToCount(saveCount);

        setText(getText());
    }

    /**
     * Positional offset of the CaptionView from its intended position when changing the text size to larger values
     * 
     * @returns the offset amount as a percentage of the screen size to shift down/over
     */
    public static int getSizeDisplayOffset() {
        switch (CaptionPreferences.instance().getTextSize()) {
            case CaptionPreferences.TEXT_SIZE_LARGE:
                return -5;
            case CaptionPreferences.TEXT_SIZE_HUGE:
                return -10;
            default:
                return 0;
        }
    }

    /*
     * Positional spacing between CaptionViews when stacked atop one another, to prevent overlap.
     * e.g. 
     * [Caption 1] 
     * [Caption 2] 
     * [Caption 3]
     * etc. 
     * 
     * @returns the spacing amount in pixels to be moved downward
     */
    public static int getStackedViewSpacing() {
        switch (CaptionPreferences.instance().getTextSize()) {
            case CaptionPreferences.TEXT_SIZE_SMALL:
                return -13;
            case CaptionPreferences.TEXT_SIZE_MEDIUM:
                return 7;
            case CaptionPreferences.TEXT_SIZE_LARGE:
                return 27;
            case CaptionPreferences.TEXT_SIZE_HUGE:
                return 47;
            default:
                return 7;
        }
    }
}
