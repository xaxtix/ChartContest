package com.example.isamorodov.telegramcontest.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.TelegramContestApp;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.ChartProvider;
import com.example.isamorodov.telegramcontest.data.DataController;
import com.example.isamorodov.telegramcontest.ui.fragments.ChartListFragment;
import com.example.isamorodov.telegramcontest.ui.fragments.StatisticChildFragment;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;
import com.example.isamorodov.telegramcontest.utils.Themed;

import java.util.ArrayList;


public class StatisticActivity extends FragmentActivity {

    private FrameLayout toolbar;

    private TextView title;
    private View back;
    private ChartProvider provider = new ChartProvider();

    ArrayList<ChartData> charts;

    ChartListFragment chartListFragmet;

    Fragment currentFragment;
    BackgroundDrawable backgroundDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);

        back = toolbar.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.findViewById(R.id.theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeHelper.switchTheme(StatisticActivity.this);
                recolor();
            }
        });
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                StatisticChildFragment f = (StatisticChildFragment) getSupportFragmentManager().findFragmentById(R.id.content);
                if (f != null) {
                    title.setText(f.getTitle());
                }

                currentFragment = (Fragment) f;

                boolean showBackButton = getSupportFragmentManager().getBackStackEntryCount() > 0;
                back.setVisibility(showBackButton ? View.VISIBLE : View.GONE);
            }
        });


        if (DataController.Instanse.chartsList == null) {
            provider.getData(TelegramContestApp.context, new ChartProvider.DataListener() {
                @Override
                public void onDataReceive(ArrayList<ChartData> data) {
                    DataController.Instanse.chartsList = data;
                    if (chartListFragmet != null) {
                        chartListFragmet.onDataLoaded();
                    }

                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.content);
                    if (f != null) ((StatisticChildFragment) f).onDataLoaded();
                }
            });
        }


        if (f == null) {
            chartListFragmet = new ChartListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, chartListFragmet, "list")
                    .commit();

            title.setText(getString(R.string.title));
            back.setVisibility(View.GONE);
        } else {
            chartListFragmet = (ChartListFragment) getSupportFragmentManager().findFragmentByTag("list");
            title.setText(((StatisticChildFragment) f).getTitle());

            boolean showBackButton = getSupportFragmentManager().getBackStackEntryCount() > 0;
            back.setVisibility(showBackButton ? View.VISIBLE : View.GONE);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
//            getSupportActionBar().setHomeButtonEnabled(showBackButton);
        }

        recolor();
    }


    private void recolor() {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ThemeHelper.getColor(R.attr.primary_dark));
        }

        toolbar.setBackgroundColor(ThemeHelper.getColor(R.attr.primary));
//        toolbar.setTitleTextColor(Color.WHITE);

        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f instanceof Themed) ((Themed) f).recolor();
        }

        getWindow().setBackgroundDrawable(backgroundDrawable = new BackgroundDrawable(
                ThemeHelper.getColor(R.attr.window_background)
        ));

    }

    public void updateBackground(Fragment f, int i) {
        if (f == currentFragment) BackgroundDrawable.offset = i;
        if (backgroundDrawable != null) backgroundDrawable.invalidateSelf();

    }
}
