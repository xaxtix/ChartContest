package com.example.isamorodov.telegramcontest.ui.chart;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    TextView hourTime;
    Drawable background;
    public ImageView chevron;

    SimpleDateFormat format = new SimpleDateFormat("E, ");
    SimpleDateFormat format2 = new SimpleDateFormat("MMM dd");
    SimpleDateFormat hourFormat = new SimpleDateFormat(" HH:mm");


    public boolean useHour = false;
    public boolean showPercentage = false;

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

    protected void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.legend_signature, this, true);
        setBackground(background = ContextCompat.getDrawable(getContext(), R.drawable.card_background));
        setPadding(dp(12), dp(8), dp(12), dp(8));
        content = findViewById(R.id.content);
        chevron = findViewById(R.id.chevron);
        time = findViewById(R.id.time);
        hourTime = findViewById(R.id.hour_time);
        recolor();
    }

    public void recolor() {
        time.setTextColor(ThemeHelper.getColor(R.attr.text));
        hourTime.setTextColor(ThemeHelper.getColor(R.attr.text));
        background.setColorFilter(ThemeHelper.getColor(R.attr.popup_background), PorterDuff.Mode.MULTIPLY);
        chevron.setColorFilter(ThemeHelper.getColor(R.attr.chevron_color), PorterDuff.Mode.SRC_IN);

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
        if (useHour) hourTime.setText(hourFormat.format(date));

        int sum = 0;
        if (showPercentage) {
            for (int i = 0; i < n; i++) {
                if(lines.get(i).enabled) sum += lines.get(i).line.y[index];
            }
        }

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

                if (showPercentage && h.percentage != null) {
                    h.percentage.setVisibility(VISIBLE);
                    h.percentage.setTextColor(ThemeHelper.getColor(R.attr.text));
                    h.percentage.setText(Math.round(100 * lines.get(j).line.y[index] / (float) sum) + "%");
                }
            }
            j++;
        }
    }

    private String formatData(Date date) {
        if (useHour) return capitalize(format2.format(date));
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
        TextView percentage;
        final LinearLayout root;

        Holder() {
            root = new LinearLayout(getContext());
            root.setPadding(dp(4), dp(2), dp(4), dp(2));

            if (showPercentage) {
                root.addView(percentage = new TextView(getContext()));
                percentage.getLayoutParams().width = dp(36);
                percentage.setVisibility(GONE);
                percentage.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                percentage.setTextSize(15);
            }

            root.addView(signature = new TextView(getContext()));
            signature.getLayoutParams().width = showPercentage ? dp(80) : dp(96);
            root.addView(value = new TextView(getContext()));


            value.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            value.setTextSize(15);

            signature.setTextSize(15);
        }
    }
}
