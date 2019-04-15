package com.example.isamorodov.telegramcontest.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.ChartProvider;
import com.example.isamorodov.telegramcontest.data.DataController;
import com.example.isamorodov.telegramcontest.ui.chart.charts.UiBitmapCache;
import com.example.isamorodov.telegramcontest.ui.fragments.ChartFragment;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import java.util.ArrayList;


public class StatisticActivity extends Activity {

    private FrameLayout toolbar;

    private TextView title;
    private ChartProvider provider = ChartProvider.INSTANCE;
    private ImageView themeIcon;

    private ChartFragment chart1;
    private ChartFragment chart2;
    private ChartFragment chart3;
    private ChartFragment chart4;
    private ChartFragment chart5;
    private ScrollView scrollView;
    private ViewGroup contentInsideScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        scrollView = findViewById(R.id.scroll);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        contentInsideScroll = findViewById(R.id.lnl);

        themeIcon = toolbar.findViewById(R.id.theme);

        themeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeHelper.switchTheme(StatisticActivity.this);
                recolor();
            }
        });


        scrollView.setVisibility(View.GONE);
        if (DataController.Instanse.chartsList == null) {
            provider.setDataListener(new ChartProvider.DataListener() {
                @Override
                public void onDataReceive(ArrayList<ChartData> data) {
                    DataController.Instanse.chartsList = data;
                    chart1.onDataLoaded();
                    chart2.onDataLoaded();
                    chart3.onDataLoaded();
                    chart4.onDataLoaded();
                    chart5.onDataLoaded();

                    scrollView.setVisibility(View.VISIBLE);
                    scrollView.setAlpha(0);
                    scrollView.animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            super.onAnimationEnd(animation);
                            getWindow().setBackgroundDrawable(null);
                        }
                    }).setDuration(400).start();
                }
            });
        } else {
            scrollView.setVisibility(View.VISIBLE);
            getWindow().setBackgroundDrawable(null);
        }

        chart1 = findViewById(R.id.chart1);
        chart1.onCreateView(this, 0, savedInstanceState == null ? null : savedInstanceState.getBundle("c0"));

        chart2 = findViewById(R.id.chart2);
        chart2.onCreateView(this, 1, savedInstanceState == null ? null : savedInstanceState.getBundle("c1"));

        chart3 = findViewById(R.id.chart3);
        chart3.onCreateView(this, 2, savedInstanceState == null ? null : savedInstanceState.getBundle("c2"));

        chart4 = findViewById(R.id.chart4);
        chart4.onCreateView(this, 3, savedInstanceState == null ? null : savedInstanceState.getBundle("c3"));

        chart5 = findViewById(R.id.chart5);
        chart5.onCreateView(this, 4, savedInstanceState == null ? null : savedInstanceState.getBundle("c4"));

        recolor();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle b = new Bundle();
        chart1.onSaveInstanceState(b);
        outState.putBundle("c0", b);

        b = new Bundle();
        chart2.onSaveInstanceState(b);
        outState.putBundle("c1", b);

        b = new Bundle();
        chart3.onSaveInstanceState(b);
        outState.putBundle("c2", b);

        b = new Bundle();
        chart4.onSaveInstanceState(b);
        outState.putBundle("c3", b);

        b = new Bundle();
        chart5.onSaveInstanceState(b);
        outState.putBundle("c4", b);


    }

    private void recolor() {
        Window window = getWindow();
        UiBitmapCache.invalidate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ThemeHelper.getColor(R.attr.primary_dark));
            if (ThemeHelper.isDark()) {
                int flags = getWindow().getDecorView().getSystemUiVisibility();
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                getWindow().getDecorView().setSystemUiVisibility(flags);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        toolbar.setBackgroundColor(ThemeHelper.getColor(R.attr.primary));
        title.setTextColor(ThemeHelper.getColor(R.attr.text));
        themeIcon.setColorFilter(ThemeHelper.getColor(R.attr.night_icon_color));


        chart1.recolor();
        chart2.recolor();
        chart3.recolor();
        chart4.recolor();
        chart5.recolor();

        for (int i = 0; i < contentInsideScroll.getChildCount(); i++) {
            contentInsideScroll.getChildAt(i).invalidate();
        }

        if( scrollView.getVisibility() ==View.VISIBLE) getWindow().setBackgroundDrawable(null);
    }
}
