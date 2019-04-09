package com.example.isamorodov.telegramcontest.data;

import com.example.isamorodov.telegramcontest.struct.SegmentTree;

import org.json.JSONException;
import org.json.JSONObject;

public class StackLinearChartData extends ChartData {

    int[] ySum;
    SegmentTree ySumSegmentTree;

    public StackLinearChartData(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

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
