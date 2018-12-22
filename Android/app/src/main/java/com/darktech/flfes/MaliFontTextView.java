package com.darktech.flfes;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MaliFontTextView extends TextView {
    public MaliFontTextView(Context context) {
        super(context);
        setFont();
    }
    public MaliFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public MaliFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Mali-Italic.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}

