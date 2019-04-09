package com.example.isamorodov.telegramcontest.ui.chart;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.isamorodov.telegramcontest.ui.chart.ChartHorizontalLinesData.s;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;

public class LegendSignatureView extends FrameLayout {

    LinearLayout content;
    Holder[] holdes;
    TextView time;
    Drawable background;

    SimpleDateFormat format = new SimpleDateFormat("E, ");
    SimpleDateFormat format2 = new SimpleDateFormat("MMM dd");

    public LegendSignatureView(Context context) {
        super(context);
        init();
    }

    public LegendSignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LegendSignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.legend_signature, this, true);
        setBackground(background = ContextCompat.getDrawable(getContext(), R.drawable.card_background));
        setPadding(dp(12), dp(8), dp(12), dp(8));
        content = findViewById(R.id.content);

        time = findViewById(R.id.time);
        recolor();
    }

    public void recolor() {
        time.setTextColor(ThemeHelper.getColor(R.attr.text));
        background.setColorFilter(ThemeHelper.getColor(R.attr.popup_background), PorterDuff.Mode.MULTIPLY);
    }

    public void setSize(int n) {
        content.removeAllViews();
        holdes = new Holder[n];
        for (int i = 0; i < n; i++) {
            holdes[i] = new Holder();
            content.addView(holdes[i].root);
        }
    }


    public void setData(int index, long date, ArrayList<LineViewData> lines) {
        int n = holdes.length;
        int j = 0;

        time.setText(formatData(new Date(date)));

        for (int i = 0; i < n; i++) {
            Holder h = holdes[i];

            while (j < n && !lines.get(j).enabled) {
                j++;
            }

            if (j >= n) {
                h.root.setVisibility(View.GONE);
            } else {
                ChartData.Line l = lines.get(j).line;
                h.root.setVisibility(View.VISIBLE);
                h.value.setText(formatWholeNumber(l.y[index]));
                h.signature.setText(lines.get(j).line.name);
                h.value.setTextColor(ThemeHelper.isDark() ? l.colorDark : l.color);
                h.signature.setTextColor(ThemeHelper.getColor(R.attr.text));
            }
            j++;
        }
    }

    private String formatData(Date date) {
        return capitalize(format.format(date)) + capitalize(format2.format(date));
    }

    private String capitalize(String s) {
        if (s.length() > 0)
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        return s;
    }


    public String formatWholeNumber(int v) {
        float num_ = v;
        int count = 0;
        if (v < 10_000) {
            return String.format("%d", v);
        }
        while (num_ >= 10_000 && count < s.length - 1) {
            num_ /= 1000;
            count++;
        }
        return String.format("%.2f", num_) + s[count];
    }


    class Holder {
        final TextView value;
        final TextView signature;
        final LinearLayout root;

        Holder() {
            root = new LinearLayout(getContext());
            root.setPadding(dp(4), dp(2), dp(4), dp(2));
            root.addView(signature = new TextView(getContext()));
            signature.getLayoutParams().width = dp(86);
            root.addView(value = new TextView(getContext()));


            value.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            value.setTextSize(15);

            signature.setTextSize(15);
        }
    }


}
