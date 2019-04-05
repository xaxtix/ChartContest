package com.example.isamorodov.telegramcontest.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;


public class CheckBox extends View {

    boolean attached = false;
    public boolean checked;

    CheckBoxDrawable drawable;
    public CheckBox(Context context) {
        super(context);
        init();
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackground(drawable = new CheckBoxDrawable(this));
    }

    public void recolor(int c){
        drawable.recolor(c);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attached = false;
    }

    public void setChecked(boolean enabled) {
        checked = enabled;
        drawable.setChecked(checked);
    }
}
