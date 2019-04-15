package com.example.isamorodov.telegramcontest.ui.chart;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import java.util.ArrayList;
import java.util.Date;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;

public class PieLegendView extends LegendSignatureView {

    TextView signature;
    TextView value;

    public PieLegendView(Context context) {
        super(context);
    }

    public PieLegendView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieLegendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init() {
        LinearLayout root = new LinearLayout(getContext());
        root.setPadding(dp(4), dp(2), dp(4), dp(2));
        root.addView(signature = new TextView(getContext()));
        signature.getLayoutParams().width = dp(96);
        root.addView(value = new TextView(getContext()));
        addView(root);
        value.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        setBackground(background = ContextCompat.getDrawable(getContext(), R.drawable.card_background));
        setPadding(dp(12), dp(8), dp(12), dp(8));
    }

    public void recolor() {
        signature.setTextColor(ThemeHelper.getColor(R.attr.text));
        background.setColorFilter(ThemeHelper.getColor(R.attr.popup_background), PorterDuff.Mode.MULTIPLY);


    }


    public void setData(String name, int value, int color) {
        signature.setText(name);
        this.value.setText(Integer.toString(value));
        this.value.setTextColor(color);
    }

    public void setSize(int n) {
    }


    public void setData(int index, long date, ArrayList<LineViewData> lines) {
    }


}
