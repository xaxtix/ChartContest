package com.example.isamorodov.telegramcontest.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ChartProvider {

    public void getData(final Context context, final DataListener l) {
        new Thread() {
            @Override
            public void run() {
                try {
                    AssetManager assetManager = context.getAssets();

                    final ArrayList<ChartData> rez = new ArrayList<>();

                    for (int k = 0; k < 5; k++) {
                        InputStream is = assetManager.open((k + 1) + "/overview.json");

                        int size;
                        size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        String json = new String(buffer, "UTF-8");


                        switch (k) {
                            case 1:
                                rez.add(new DoubleLinearChartData(new JSONObject(json)));
                                break;
                            case 2:
                                rez.add(new StackBarChartData(new JSONObject(json)));
                                break;
                            case 3:
                                rez.add(new BarChartData(new JSONObject(json)));
                                break;
                            case 4:
                                rez.add(new StackLinearChartData(new JSONObject(json)));
                                break;
                            default:
                                rez.add(new ChartData(new JSONObject(json)));
                                break;
                        }
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            l.onDataReceive(rez);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public interface DataListener {
        void onDataReceive(ArrayList<ChartData> dataList);
    }
}
