package com.darktech.flfes;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomFontButton extends androidx.appcompat.widget.AppCompatButton {
    public CustomFontButton(Context context) {
        super(context);
        setFont();
    }
    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public CustomFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Mali-Italic.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
