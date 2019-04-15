package com.example.isamorodov.telegramcontest.ui.chart;

public class ChartHorizontalLinesData {

    final public int[] values = new int[6];
    final public String[] valuesStr = new String[6];
    public String[] valuesStr2;
    public int alpha;

    public int fixedAlpha = 255;

    public ChartHorizontalLinesData(int newMaxHeight, int newMinHeight, boolean useMinHeight) {
        this(newMaxHeight, newMinHeight, useMinHeight, 0);
    }


    public ChartHorizontalLinesData(int newMaxHeight, int newMinHeight, boolean useMinHeight, float k) {
        if (!useMinHeight) {
            int v = newMaxHeight;
            if (newMaxHeight > 100) {
                v = round(newMaxHeight);
            }

            int step = (int) Math.ceil(v / 5f);
            for (int i = 1; i < 6; i++) {
                values[i] = i * step;
                valuesStr[i] = formatWholeNumber(values[i]);
            }
        } else {
            int step = (newMaxHeight - newMinHeight) / 5;
            if (k > 0) valuesStr2 = new String[6];
            for (int i = 0; i < 6; i++) {
                values[i] = newMinHeight + i * step;
                valuesStr[i] = formatWholeNumber(values[i]);
                if(k > 0) valuesStr2[i] = formatWholeNumber((int) (values[i] / k));
            }
        }
    }

    public static int lookupHeight(int maxValue) {
        int v = maxValue;
        if (maxValue > 100) {
            v = round(maxValue);
        }

        int step = (int) Math.ceil(v / 5f);
        return step * 5;
    }

    public static final String[] s = {"", "K", "M", "G", "T", "P"};

    public static String formatWholeNumber(int v) {
        float num_ = v;
        int count = 0;
        if (v < 1000) {
            return String.format("%d", v);
        }
        while (num_ >= 100 && count < s.length - 1) {
            num_ /= 1000;
            count++;
        }

        return String.format("%.1f", num_) + s[count];
    }

    private static int round(int maxValue) {
        float k = maxValue / 5;
        if (k % 10 == 0) return maxValue;
        else return ((maxValue / 10 + 1) * 10);
    }


}
