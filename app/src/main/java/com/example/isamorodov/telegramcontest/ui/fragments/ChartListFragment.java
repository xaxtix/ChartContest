package com.example.isamorodov.telegramcontest.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.DataController;
import com.example.isamorodov.telegramcontest.ui.StatisticActivity;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;
import com.example.isamorodov.telegramcontest.utils.Themed;

import java.util.ArrayList;

public class ChartListFragment extends Fragment implements Themed, StatisticChildFragment {


    ArrayList<ChartData> data;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        data = DataController.Instanse.chartsList;
        listView = new ListView(getActivity());
        listView.setDivider(null);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return data == null ? 0 : data.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.data_chart_item, parent, false);

                }

                convertView.setBackgroundColor(ThemeHelper.getColor(R.attr.card_background));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    convertView.setForeground(ContextCompat.getDrawable(getActivity(),
                            ThemeHelper.isDark() ? R.drawable.highlight_dark :  R.drawable.highlight));
                }
                TextView t = convertView.findViewById(R.id.text);
                t.setTextColor(ThemeHelper.getColor(R.attr.text));
                t.setText("Chart " + (position + 1));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment f = new ChartFragment();
                        Bundle b = new Bundle();
                        b.putInt("p", position);
                        f.setArguments(b);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, f)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                return convertView;
            }
        });
        recolor();

        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ((StatisticActivity) getActivity()).updateBackground(ChartListFragment.this, 0);
            }
        });
        return listView;
    }

    public void onDataLoaded() {
        data = DataController.Instanse.chartsList;
        if(listView != null) {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void recolor() {
        if (listView != null)
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public String getTitle() {
        return getString(R.string.title);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((StatisticActivity) getActivity()).updateBackground(this, 0);
    }
}
