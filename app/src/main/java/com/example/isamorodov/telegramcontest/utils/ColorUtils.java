package com.example.isamorodov.telegramcontest.utils;

import android.graphics.Color;

public class ColorUtils {

    public static int blend(int color1, int color2, float amount) {
        final byte ALPHA_CHANNEL = 24;
        final byte RED_CHANNEL = 16;
        final byte GREEN_CHANNEL = 8;
        final byte BLUE_CHANNEL = 0;

        final float inverseAmount = 1.0f - amount;

        int a = ((int) (((float) (color1 >> ALPHA_CHANNEL & 0xff) * amount) +
                ((float) (color2 >> ALPHA_CHANNEL & 0xff) * inverseAmount))) & 0xff;
        int r = ((int) (((float) (color1 >> RED_CHANNEL & 0xff) * amount) +
                ((float) (color2 >> RED_CHANNEL & 0xff) * inverseAmount))) & 0xff;
        int g = ((int) (((float) (color1 >> GREEN_CHANNEL & 0xff) * amount) +
                ((float) (color2 >> GREEN_CHANNEL & 0xff) * inverseAmount))) & 0xff;
        int b = ((int) (((float) (color1 & 0xff) * amount) +
                ((float) (color2 & 0xff) * inverseAmount))) & 0xff;

        return a << ALPHA_CHANNEL | r << RED_CHANNEL | g << GREEN_CHANNEL | b << BLUE_CHANNEL;
    }

    public static int transformColor(int fromColor, int toColor, float a) {
        int rD = (int) ((Color.red(fromColor) - Color.red(toColor)) * a);
        int gD = (int) ((Color.green(fromColor) - Color.green(toColor)) * a);
        int bD = (int) ((Color.blue(fromColor) - Color.blue(toColor)) * a);
        return Color.rgb(Color.red(toColor) + rD, Color.green(toColor) + gD, Color.blue(toColor) + bD);
    }

    public static int grayBlending(int color) {
        return blend(color, 0xFF7A7A7A, 0.75f);
    }
}
