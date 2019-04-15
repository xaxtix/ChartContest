package com.example.isamorodov.telegramcontest.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.ChartProvider;
import com.example.isamorodov.telegramcontest.data.DataController;
import com.example.isamorodov.telegramcontest.data.StackLinearChartData;
import com.example.isamorodov.telegramcontest.ui.StatisticActivity;
import com.example.isamorodov.telegramcontest.ui.chart.ChartHeaderView;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.chart.TransitionParams;
import com.example.isamorodov.telegramcontest.ui.chart.charts.BarChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.BaseChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.DoubleLinearChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.LinearChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.PieChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.StackBarChartView;
import com.example.isamorodov.telegramcontest.ui.chart.charts.StackLinearChartView;
import com.example.isamorodov.telegramcontest.ui.components.FlatCheckBox;
import com.example.isamorodov.telegramcontest.utils.AndroidUtilities;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;
import com.example.isamorodov.telegramcontest.utils.Themed;

import java.util.ArrayList;
import java.util.Arrays;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;

public class ChartFragment extends FrameLayout implements Themed {


    BaseChartView chartView;
    BaseChartView zoomedChartView;
    ChartHeaderView chartHeaderView;

    ViewGroup checkboxContainer;
    ArrayList<CheckBoxHolder> checkBoxes = new ArrayList<>();
    ChartData data;

    long activeZoom = 0;

    Bundle savedInstanceState;
    int p;
    StatisticActivity activity;

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
        chartHeaderView = findViewById(R.id.header);
        chartHeaderView.back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOut();
            }
        });
    }


    public void onCreateView(final StatisticActivity activity, final int p, Bundle savedInstanceState) {

        this.savedInstanceState = savedInstanceState;

        FrameLayout frameLayout = findViewById(R.id.cart_frame);

        switch (p) {
            case 1:
                chartView = new DoubleLinearChartView(getContext());
                zoomedChartView = new DoubleLinearChartView(getContext());
                zoomedChartView.legendSignatureView.useHour = true;
                zoomedChartView.legendSignatureView.chevron.setVisibility(View.GONE);
                break;
            case 2:
                chartView = new StackBarChartView(getContext());
                zoomedChartView = new StackBarChartView(getContext());
                zoomedChartView.legendSignatureView.useHour = true;
                zoomedChartView.legendSignatureView.chevron.setVisibility(View.GONE);
                break;
            case 3:
                chartView = new BarChartView(getContext());
                zoomedChartView = new LinearChartView(getContext());
                zoomedChartView.legendSignatureView.useHour = true;
                zoomedChartView.legendSignatureView.chevron.setVisibility(View.GONE);
                break;
            case 4:
                chartView = new StackLinearChartView(getContext());
                chartView.legendSignatureView.showPercentage = true;
                zoomedChartView = new PieChartView(getContext());
                break;
            default:
                chartView = new LinearChartView(getContext());
                zoomedChartView = new LinearChartView(getContext());
                zoomedChartView.legendSignatureView.useHour = true;
                zoomedChartView.legendSignatureView.chevron.setVisibility(View.GONE);
                break;
        }

        frameLayout.addView(chartView);
        frameLayout.addView(chartView.legendSignatureView, WRAP_CONTENT, WRAP_CONTENT);
        frameLayout.addView(zoomedChartView);
        frameLayout.addView(zoomedChartView.legendSignatureView, WRAP_CONTENT, WRAP_CONTENT);


        chartView.legendSignatureView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomChart(false);
            }
        });
        chartView.setVisibility(VISIBLE);
        zoomedChartView.setVisibility(INVISIBLE);

        Configuration configuration = getContext().getResources().getConfiguration();

        boolean land = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
        chartView.setLandscape(land);
        chartView.setHeader(chartHeaderView);
        chartHeaderView.setTitle("Chart " + (p + 1));
        zoomedChartView.setLandscape(land);

        this.p = p;
        this.activity = activity;


        updateData(savedInstanceState, p);
    }

    private void zoomChart(boolean skipTransition) {
        long d = chartView.getSelectedDate();

        ChartData childData = null;
        if (p == 4) {
            childData = new StackLinearChartData(data, d);
        } else {
            ChartProvider.INSTANCE.readChild(p, d);
            childData = data.childCharts.get(d);
        }
        if (childData == null) return;

        activeZoom = d;
        if (p == 3) {
            zoomedChartView.updatePicker5m();
        } else {
            zoomedChartView.updatePicker(childData, d);
        }
        zoomedChartView.setData(childData);

        if (p == 3) {

            checkboxContainer.removeAllViews();
            checkBoxes.clear();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT);
            lp.setMarginStart(dp(16));
            int n = childData.lines.size();
            if (n > 1) {
                for (int i = 0; i < n; i++) {
                    LineViewData l = (LineViewData) zoomedChartView.lines.get(i);
                    CheckBoxHolder checkBoxHolder = new CheckBoxHolder(i);
                    checkBoxHolder.setData(l);
                    checkBoxHolder.recolor(ThemeHelper.isDark() ? childData.lines.get(i).colorDark :
                            childData.lines.get(i).color);

                }
            }
        }

        chartView.legendSignatureView.setAlpha(0f);
        chartView.selectionA = 0;
        chartView.legendShowing = false;
        chartView.animateLegentTo = false;


        if (p == 0 || p == 1 || p == 2 || p == 4) {
            for (int i = 0; i < childData.lines.size(); i++) {
                boolean check = checkBoxes.get(i).checkBox.checked;
                ((LineViewData) zoomedChartView.lines.get(i)).enabled = check;
                ((LineViewData) zoomedChartView.lines.get(i)).alpha = check ? 255 : 0;
                if (p == 2) {
                    zoomedChartView.onCheckChanged();
                }
            }
        }
        zoomedChartView.updateColors();
        zoomedChartView.clearSelection();

        chartHeaderView.zoomTo(zoomedChartView, d);

        if (skipTransition) {
            chartView.setVisibility(INVISIBLE);
            zoomedChartView.setVisibility(VISIBLE);
            zoomedChartView.setHeader(chartHeaderView);

            chartView.transitionMode = BaseChartView.TRANSITION_MODE_NONE;
            zoomedChartView.transitionMode = BaseChartView.TRANSITION_MODE_NONE;
        } else {
            ValueAnimator animator = createTransitionAnimator(d, true);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    chartView.setVisibility(INVISIBLE);
                    zoomedChartView.setHeader(chartHeaderView);

                    chartView.enabled = true;
                    zoomedChartView.enabled = true;
                    chartView.transitionMode = BaseChartView.TRANSITION_MODE_NONE;
                    zoomedChartView.transitionMode = BaseChartView.TRANSITION_MODE_NONE;
                    ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
            animator.start();
        }


    }

    private void zoomOut() {
        chartHeaderView.zoomOut(chartView);
        zoomedChartView.setHeader(null);

        long d = chartView.getSelectedDate();
        activeZoom = 0;

        chartView.setVisibility(VISIBLE);
        zoomedChartView.clearSelection();

        if (p == 3) {
            checkboxContainer.removeAllViews();
            checkBoxes.clear();
        }


        ValueAnimator animator = createTransitionAnimator(d, false);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                zoomedChartView.setVisibility(INVISIBLE);

                chartView.transitionMode = BaseChartView.TRANSITION_MODE_NONE;
                zoomedChartView.transitionMode = BaseChartView.TRANSITION_MODE_NONE;

                chartView.enabled = true;
                zoomedChartView.enabled = true;

                chartView.legendShowing = true;
                chartView.moveLegend();
                chartView.animateLegend(true);
                chartView.invalidate();
                ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        });

        animator.start();
    }

    private ValueAnimator createTransitionAnimator(long d, boolean in) {
        ((Activity) getContext()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        chartView.enabled = false;
        zoomedChartView.enabled = false;
        chartView.transitionMode = BaseChartView.TRANSITION_MODE_PARENT;
        zoomedChartView.transitionMode = BaseChartView.TRANSITION_MODE_CHILD;

        final TransitionParams params = new TransitionParams();
        params.pickerEndOut = chartView.pickerDelegate.pickerEnd;
        params.pickerStartOut = chartView.pickerDelegate.pickerStart;


        if (p == 3) params.needScaleY = false;


        params.date = d;

        int dateIndex = Arrays.binarySearch(data.x, d);
        params.xPercentage = data.xPercentage[dateIndex];


        zoomedChartView.setVisibility(VISIBLE);
        zoomedChartView.transitionParams = params;
        chartView.transitionParams = params;

        int max = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < data.lines.size(); i++) {
            if (data.lines.get(i).y[dateIndex] > max) max = data.lines.get(i).y[dateIndex];
            if (data.lines.get(i).y[dateIndex] < min) min = data.lines.get(i).y[dateIndex];
        }
        final float pYPercentage = (((float) min + (max - min)) - chartView.currentMinHeight) / (chartView.currentMaxHeight - chartView.currentMinHeight);


        ValueAnimator animator = ValueAnimator.ofFloat(in ? 0f : 1f, in ? 1f : 0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fullWidth = (chartView.viewSizes.chartWidth / (chartView.pickerDelegate.pickerEnd - chartView.pickerDelegate.pickerStart));
                float offset = fullWidth * (chartView.pickerDelegate.pickerStart) - chartView.HORIZONTAL_PADDING;

                params.pY = chartView.viewSizes.chartArea.top + (1f - pYPercentage) * chartView.viewSizes.chartArea.height();
                params.pX = chartView.viewSizes.chartFullWidth * params.xPercentage - offset;

                params.progress = (float) animation.getAnimatedValue();
                zoomedChartView.invalidate();
                chartView.invalidate();
            }
        });

        animator.setDuration(p == 4 ? 600 : 400);
        animator.setInterpolator(AndroidUtilities.INTERPOLATOR);

        return animator;
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

            int n = chartView.lines.size();
            if (n > 1) {
                for (int i = 0; i < n; i++) {
                    LineViewData l = (LineViewData) chartView.lines.get(i);
                    new CheckBoxHolder(i).setData(l);
                }
            }

            activeZoom = savedInstanceState == null ? 0 : savedInstanceState.getLong("zoom");
            if (activeZoom > 0) {
                chartView.selectDate(activeZoom);
                zoomChart(true);
                zoomedChartView.restoreState(savedInstanceState.getBundle("zoomed"));
            } else {
                chartView.invalidate();
            }


        }


        recolor();
    }


    @Override
    public void recolor() {
        this.setBackgroundColor(ThemeHelper.getColor(R.attr.card_background));

        chartView.updateColors();
        chartView.invalidate();

        zoomedChartView.updateColors();
        zoomedChartView.invalidate();

        if (data != null && data.lines.size() > 1) {
            for (int i = 0; i < data.lines.size(); i++) {
                checkBoxes.get(i).recolor(ThemeHelper.isDark() ? data.lines.get(i).colorDark :
                        data.lines.get(i).color);
            }
        }

        if (p == 3 && activeZoom != 0) {
            ChartData childData = data.childCharts.get(activeZoom);
            if (childData != null) {
                for (int i = 0; i < childData.lines.size(); i++) {
                    checkBoxes.get(i).recolor(ThemeHelper.isDark() ? childData.lines.get(i).colorDark :
                            childData.lines.get(i).color);
                }
            }
        }

        chartHeaderView.recolor();
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        chartView.saveState(outState);
        outState.putLong("zoom", activeZoom);
        Bundle b = new Bundle();
        zoomedChartView.saveState(b);
        outState.putBundle("zoomed", b);
    }

    public void onDataLoaded() {
        updateData(savedInstanceState, p);
    }


    class CheckBoxHolder {
        final FlatCheckBox checkBox;
        LineViewData line;
        final int position;

        CheckBoxHolder(int position) {
            this.position = position;
            checkBox = new FlatCheckBox(getContext());

            checkBox.setPadding(dp(16), 0, dp(16), 0);
            checkboxContainer.addView(checkBox);
            checkBoxes.add(this);
        }

        public void setData(final LineViewData l) {
            this.line = l;
            checkBox.setText(l.line.name);
            checkBox.setChecked(l.enabled, false);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean allDisabled = true;
                    int n = checkBoxes.size();
                    for (int i = 0; i < n; i++) {
                        if (i != position && checkBoxes.get(i).checkBox.checked) {
                            allDisabled = false;
                            break;
                        }
                    }
                    if (allDisabled) {
                        checkBox.denied();
                        return;
                    }
                    checkBox.setChecked(!checkBox.checked);
                    l.enabled = checkBox.checked;
                    chartView.onCheckChanged();

                    if (activeZoom > 0) {
                        ((LineViewData) zoomedChartView.lines.get(position)).enabled = checkBox.checked;
                        zoomedChartView.onCheckChanged();
                    }
                }
            });

            checkBox.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    int n = checkBoxes.size();
                    for (int i = 0; i < n; i++) {
                        checkBoxes.get(i).checkBox.setChecked(false);
                        checkBoxes.get(i).line.enabled = false;

                        if (activeZoom > 0) {
                            ((LineViewData) zoomedChartView.lines.get(i)).enabled = false;
                        }
                    }

                    checkBox.setChecked(true);
                    l.enabled = true;
                    chartView.onCheckChanged();

                    if (activeZoom > 0) {
                        ((LineViewData) zoomedChartView.lines.get(position)).enabled = true;
                        zoomedChartView.onCheckChanged();
                    }
                    return true;
                }
            });
        }

        public void recolor(int c) {
            checkBox.recolor(c);
        }
    }
}
