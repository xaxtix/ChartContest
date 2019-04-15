package com.example.isamorodov.telegramcontest.ui.chart;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.ui.chart.charts.BaseChartView;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.bgHandler;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;

public class ChartHeaderView extends FrameLayout {

    TextView title;
    TextView dates;
    TextView datesTmp;
    public TextView back;

    Drawable zoomIcon;

    Handler handler = new Handler();

    UpdateDatesRunnable updateDatesRunnable = new UpdateDatesRunnable();

    SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");


    public ChartHeaderView(@NonNull Context context) {
        super(context);
        init();
    }

    public ChartHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.chart_header, this, true);

        title = findViewById(R.id.title);
        dates = findViewById(R.id.date);
        back = findViewById(R.id.back);
        datesTmp = findViewById(R.id.dateTmp);
        datesTmp.setVisibility(View.GONE);


        back.setVisibility(View.GONE);
        back.setText("Zoom out");
        zoomIcon = ContextCompat.getDrawable(getContext(),R.drawable.ic_magnify_minus_outline_black_24dp);
        back.setCompoundDrawablesWithIntrinsicBounds(zoomIcon,null,null,null);
        back.setCompoundDrawablePadding(dp(4));

        datesTmp.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                datesTmp.setPivotX(datesTmp.getMeasuredWidth() * 0.7f);
                dates.setPivotX(dates.getMeasuredWidth() * 0.7f);
            }
        });
        recolor();
    }

    public void recolor() {
        title.setTextColor(ThemeHelper.getColor(R.attr.text));
        dates.setTextColor(ThemeHelper.getColor(R.attr.text));
        datesTmp.setTextColor(ThemeHelper.getColor(R.attr.text));
        back.setTextColor(ThemeHelper.getColor(R.attr.back_zoom_color));
        zoomIcon.setColorFilter(ThemeHelper.getColor(R.attr.back_zoom_color),PorterDuff.Mode.SRC_IN);
    }

    public void setDates(long start, long end) {
        if (updateDatesRunnable.start  / 86400000L != start / 86400000L ||
                updateDatesRunnable.end / 86400000L != end / 86400000L) {
            bgHandler.removeCallbacks(updateDatesRunnable);
            updateDatesRunnable.setDates(start, end);
            bgHandler.postDelayed(updateDatesRunnable, 200);
        }
    }

    public void setTitle(String s) {
        title.setText(s);
    }

    public void zoomTo(BaseChartView chartView, long d) {
        updateDatesRunnable.setDates(d, d);
        handler.removeCallbacks(updateDatesRunnable);
        handler.post(updateDatesRunnable);

        back.setVisibility(View.VISIBLE);

        back.setAlpha(0);
        back.setScaleX(0.3f);
        back.setScaleY(0.3f);
        back.setPivotX(0);
        back.setPivotY(dp(40));
        back.animate().alpha(1f)
                .scaleY(1f)
                .scaleX(1f)
                .setDuration(200)
                .start();

        title.setAlpha(1f);
        title.setTranslationX(0);
        title.setTranslationY(0);
        title.setScaleX(1f);
        title.setScaleY(1f);
        title.setPivotX(0);
        title.setPivotY(0);
        title.animate()
                .alpha(0f)
                .scaleY(0.3f)
                .scaleX(0.3f)
                .setDuration(200)
                .start();


    }

    public void zoomOut(BaseChartView chartView) {
        updateDatesRunnable.setDates(chartView.getStartDate(), chartView.getEndDate());
        handler.removeCallbacks(updateDatesRunnable);
        handler.post(updateDatesRunnable);
        title.setAlpha(0);
        title.setScaleX(0.3f);
        title.setScaleY(0.3f);
        title.setPivotX(0);
        title.setPivotY(0);
        title.animate().alpha(1f)
                .scaleY(1f)
                .scaleX(1f)
                .setDuration(200)
                .start();

        back.setAlpha(1f);
        back.setTranslationX(0);
        back.setTranslationY(0);
        back.setScaleX(1f);
        back.setScaleY(1f);
        back.setPivotY(dp(40));
        back.animate()
                .alpha(0f)
                .scaleY(0.3f)
                .scaleX(0.3f)
                .setDuration(200)
                .start();
    }

    class UpdateDatesRunnable implements Runnable {

        long start;
        long end;

        public void setDates(long start, long end) {
            this.start = start;
            this.end = end;
        }
        @Override
        public void run() {
            final String newText;
            if (end - start >= 86400000L) {
                newText = formatter.format(new Date(start)) + " — " + formatter.format(new Date(end));
            } else {
                newText = formatter.format(new Date(start));
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isAttachedToWindow() && !TextUtils.isEmpty(dates.getText())) {
                        datesTmp.setText(dates.getText());
                        dates.setText(newText);

                        datesTmp.setVisibility(View.VISIBLE);
                        datesTmp.setAlpha(1f);


                        datesTmp.setPivotY(dp(20));
                        datesTmp.setScaleX(1f);
                        datesTmp.setScaleY(1f);
                        datesTmp.animate()
                                .alpha(0f)
                                .scaleY(0.3f)
                                .scaleX(0.3f)
                                .setDuration(200)
                                .start();

                        dates.setAlpha(0);

                        dates.setScaleX(0.3f);
                        dates.setScaleY(0.3f);
                        dates.animate().alpha(1f)
                                .scaleY(1f)
                                .scaleX(1f)
                                .setDuration(200)
                                .start();

                    } else {
                        dates.setText(newText);
                        datesTmp.setVisibility(View.GONE);
                        dates.setAlpha(1f);
                    }
                }
            });
        }
    }
}
