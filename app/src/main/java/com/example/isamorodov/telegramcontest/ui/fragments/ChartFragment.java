package com.example.isamorodov.telegramcontest.ui.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.DataController;
import com.example.isamorodov.telegramcontest.ui.StatisticActivity;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.chart.charts.BarChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.BaseChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.DoubleLinearChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.LinearChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.StackBarChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.StackLinearChartView;
import com.example.isamorodov.telegramcontest.ui.components.FlatCheckBox;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;
import com.example.isamorodov.telegramcontest.utils.Themed;

import java.util.ArrayList;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;

public class ChartFragment extends FrameLayout implements Themed {


    BaseChartView chartView;

    ViewGroup checkboxContainer;
    ArrayList<CheckBoxHolder> checkBoxes = new ArrayList<>();
    ChartData data;

    Bundle savedInstanceState;
    int p;
    StatisticActivity activity;

    Rect r = new Rect();

    public ChartFragment(@NonNull Context context) {
        super(context);
        init();
    }

    public ChartFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartFragment(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.chart_fragment, this, true);
        checkboxContainer = findViewById(R.id.checkbox_container);

    }


    public void onCreateView(final StatisticActivity activity, int p) {

       // this.savedInstanceState = savedInstanceState;

        FrameLayout frameLayout = findViewById(R.id.cart_frame);

        switch (p){
            case 1:
                chartView = new DoubleLinearChartView(getContext());
                break;
            case 2:
                chartView = new StackBarChartView(getContext());
                break;
            case 3:
                chartView = new BarChartView(getContext());
                break;
            case 4:
                chartView = new StackLinearChartView(getContext());
                break;
            default:
                chartView = new LinearChartView(getContext());
                break;
        }

        frameLayout.addView(chartView);

        Configuration configuration = getContext().getResources().getConfiguration();

        boolean land = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
        chartView.setLandscape(land);

        this.p = p;
        this.activity = activity;


        updateData(savedInstanceState, p);
    }

    private void updateData(@Nullable Bundle savedInstanceState, int p) {
        this.savedInstanceState = savedInstanceState;
        if (DataController.Instanse.chartsList == null) {
            data = null;
            return;
        }
        data = DataController.Instanse.chartsList.get(p);
        if (data != null) {
            chartView.setData(data);
            chartView.restoreState(savedInstanceState);

            checkboxContainer.removeAllViews();
            checkBoxes.clear();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart(dp(16));
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            int n = chartView.lines.size();
            if(n > 1) {
                for (int i = 0; i < n; i++) {
                    LineViewData l = (LineViewData) chartView.lines.get(i);
                    new CheckBoxHolder(checkboxContainer, layoutInflater).setData(l);
                }
            }

            chartView.invalidate();
        }

        recolor();
    }


    @Override
    public void recolor() {
        this.setBackgroundColor(ThemeHelper.getColor(R.attr.card_background));

        chartView.updateColors();
        chartView.invalidate();

        if (data != null &&  data.lines.size() > 1) {
            for (int i = 0; i < data.lines.size(); i++) {
                checkBoxes.get(i).recolor(ThemeHelper.isDark() ? data.lines.get(i).colorDark :
                        data.lines.get(i).color);
            }
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        chartView.saveState(outState);
    }

    public void onDataLoaded() {
        updateData(savedInstanceState, p);
    }


    class CheckBoxHolder {
        final FlatCheckBox checkBox;


        CheckBoxHolder(ViewGroup parent, LayoutInflater layoutInflater) {


            checkBox = new FlatCheckBox(getContext());

            checkBox.setPadding(dp(16), 0, dp(16), 0);
            checkboxContainer.addView(checkBox);
            checkBoxes.add(this);
        }

        public void setData(final LineViewData l) {
            checkBox.setText(l.line.name);
            checkBox.setChecked(l.enabled,false);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.checked);
                    l.enabled = checkBox.checked;
                    chartView.onCheckChanged();
                }
            });
        }

        public void recolor(int c) {
            checkBox.recolor(c);
        }
    }
}
