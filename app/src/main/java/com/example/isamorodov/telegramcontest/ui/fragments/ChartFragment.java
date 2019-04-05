package com.example.isamorodov.telegramcontest.ui.fragments;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.DataController;
import com.example.isamorodov.telegramcontest.ui.StatisticActivity;
import com.example.isamorodov.telegramcontest.ui.chart.ChartView;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.components.CheckBox;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;
import com.example.isamorodov.telegramcontest.utils.Themed;

import java.util.ArrayList;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;

public class ChartFragment extends Fragment implements Themed, StatisticChildFragment {


    ChartView chartView;
    // CartViewSurface chartView;
    View cardBackground;
    ViewGroup checkboxContainer;
    NestedScrollView nestedScrollView;
    ArrayList<CheckBoxHolder> checkBoxes = new ArrayList<>();
    ChartData data;
    Bundle savedInstanceState;


    Rect r = new Rect();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View contentView = inflater.inflate(R.layout.chart_fragment, container, false);

        chartView = contentView.findViewById(R.id.chart_view);
        cardBackground = contentView.findViewById(R.id.card_content);
        checkboxContainer = contentView.findViewById(R.id.checkbox_container);
        nestedScrollView = contentView.findViewById(R.id.scroll_view);

        Configuration configuration = getResources().getConfiguration();

        chartView.setLandscape(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE);


        contentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                contentView.getGlobalVisibleRect(r);
                ((StatisticActivity) getActivity()).updateBackground(ChartFragment.this,
                        getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE ? cardBackground.getBottom() + r.top : -1);

                chartView.parentCanScrollVertically = nestedScrollView != null && (nestedScrollView.canScrollVertically(-1) || nestedScrollView.canScrollVertically(1));
            }
        });
        updateData(savedInstanceState);
        return contentView;
    }

    private void updateData(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        if (DataController.Instanse.chartsList == null) {
            data = null;
            return;
        }
        data = DataController.Instanse.chartsList.get(getArguments().getInt("p"));
        if (data != null) {
            chartView.setData(data);
            chartView.restoreState(savedInstanceState);

            checkboxContainer.removeAllViews();
            checkBoxes.clear();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart(dp(16));
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            int n = chartView.lines.size();
            for (int i = 0; i < n; i++) {
                LineViewData l = chartView.lines.get(i);
                new CheckBoxHolder(checkboxContainer, layoutInflater).setData(l, i != n - 1);
            }

            chartView.invalidate();
        }

        recolor();
    }


    @Override
    public void recolor() {
        if (cardBackground == null) return;
        cardBackground.setBackgroundColor(ThemeHelper.getColor(R.attr.card_background));

        chartView.updateColors();
        chartView.invalidate();

        if (data != null) {
            for (int i = 0; i < data.lines.size(); i++) {
                checkBoxes.get(i).recolor(ThemeHelper.isDark() ? data.lines.get(i).colorDark :
                        data.lines.get(i).color);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        chartView.saveState(outState);
    }

    @Override
    public String getTitle() {
        return "Chart " + (getArguments().getInt("p") + 1);
    }

    @Override
    public void onDataLoaded() {
        updateData(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((StatisticActivity) getActivity()).updateBackground(ChartFragment.this,
                getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE ? cardBackground.getBottom() + r.top : -1);
    }

    class CheckBoxHolder {
        final CheckBox checkBox;
        final TextView textView;
        final View itemView;

        CheckBoxHolder(ViewGroup parent, LayoutInflater layoutInflater) {
            itemView = layoutInflater.inflate(R.layout.item_checkbox, parent, false);

            checkBox = itemView.findViewById(R.id.checkBox);
            textView = itemView.findViewById(R.id.text);

            checkBox.setPadding(dp(16), 0, dp(16), 0);
            checkboxContainer.addView(itemView);
            checkBoxes.add(this);
        }

        public void setData(final LineViewData l, boolean showDivider) {
            textView.setText(l.line.name);
            checkBox.setChecked(l.enabled);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.checked);
                    l.enabled = checkBox.checked;
                    chartView.onCheckChanged();
                }
            });
        }

        public void recolor(int c) {
            textView.setTextColor(ThemeHelper.getColor(R.attr.text));
            checkBox.recolor(c);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                itemView.setBackground(ContextCompat.getDrawable(getActivity(),
                        ThemeHelper.isDark() ? R.drawable.highlight_dark :  R.drawable.highlight));
            }
        }
    }
}
