package com.example.isamorodov.telegramcontest.data;

import com.example.isamorodov.telegramcontest.struct.SegmentTree;
import com.example.isamorodov.telegramcontest.utils.ColorUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class StackBarChartData extends ChartData {

    public int[] ySum;
    public SegmentTree ySumSegmentTree;


    public StackBarChartData(JSONObject jsonObject, long timeStep) throws JSONException {
        super(jsonObject, timeStep);
        init();
    }

    public StackBarChartData(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        init();
    }

    public void init() {
        int n = lines.get(0).y.length;
        int k = lines.size();

        ySum = new int[n];
        for (int i = 0; i < n; i++) {
            ySum[i] = 0;
            for (int j = 0; j < k; j++) {
                ySum[i] += lines.get(j).y[i];
            }
        }

        ySumSegmentTree = new SegmentTree(ySum);
    }

    public int findMax(int start, int end) {
        return ySumSegmentTree.rMaxQ(start, end);
    }

}
