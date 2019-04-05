package com.example.isamorodov.telegramcontest.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;

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
                    InputStream is = assetManager.open("chart_data.json");
                    int size = 0;

                    size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    String json = new String(buffer, "UTF-8");


                    JSONArray a = new JSONArray(json);
                    int n = a.length();

                    final ArrayList<ChartData> rez = new ArrayList<>();
                    for (int i = 0; i < n; i++) {
                        rez.add(new ChartData(a.getJSONObject(i)));
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
