package com.example.isamorodov.telegramcontest.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

public enum ChartProvider {
    INSTANCE;

    SimpleDateFormat folderDataFormat = new SimpleDateFormat("yyyy-MM/dd");


    AssetManager assetManager;
    DataListener dataListener;
    ChildLoadedListener childLoadedListener;

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public void load(final Context context) {
        assetManager = context.getAssets();
        new Thread() {
            @Override
            public void run() {
                try {
                    final ArrayList<ChartData> rez = new ArrayList<>();

                    long t = System.currentTimeMillis();
                    for (int k = 0; k < 5; k++) {

                        String json = readJson((k + 1) + "/overview.json");


                        switch (k) {
                            case 1:
                                ChartData data = new DoubleLinearChartData(new JSONObject(json));
                                rez.add(data);
                                break;
                            case 2:
                                data = new StackBarChartData(new JSONObject(json));
                                rez.add(data);
                                break;
                            case 3:
                                data = new BarChartData(new JSONObject(json));
                                rez.add(data);
                                break;
                            case 4:
                                data = new StackLinearChartData(new JSONObject(json));
                                rez.add(data);
                                break;
                            default:
                                data = new ChartData(new JSONObject(json));
                                rez.add(data);
                        }
                    }

                    DataController.Instanse.chartsList = rez;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            if (dataListener != null) dataListener.onDataReceive(rez);
                        }
                    });

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (childLoadedListener != null) childLoadedListener.onChildLoaded();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private String readJson(String s) {
        InputStream is;
        try {
            is = assetManager.open(s);
            int size;
            size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    public void readChild(int k, long date) {
        ChartData data = DataController.Instanse.chartsList.get(k);

        if (data.childCharts.get(date, null) != null) {
            return;
        }


        String c = readJson((k + 1) + "/" + folderDataFormat.format(date) + ".json");
        if (c != null) {
            try {
                ChartData chart;
                switch (k) {
                    case 1:
                        chart = new DoubleLinearChartData(new JSONObject(c), k == 3 ? 300000 : 3600000L);
                        break;
                    case 2:
                        chart = new StackBarChartData(new JSONObject(c), k == 3 ? 300000 : 3600000L);
                        break;
                    default:
                        chart = new ChartData(new JSONObject(c), k == 3 ? 300000 : 3600000L);
                }
                data.childCharts.put(date, chart);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface DataListener {
        void onDataReceive(ArrayList<ChartData> dataList);
    }

    public interface ChildLoadedListener {
        void onChildLoaded();
    }
}
