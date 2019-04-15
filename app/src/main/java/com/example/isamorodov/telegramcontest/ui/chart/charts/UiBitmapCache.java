package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;

public class UiBitmapCache {

    private static Bitmap pickerBitmam;
    private static Canvas canvas;


    static RectF rectF = new RectF();

    static Paint xRefP = new Paint(Paint.ANTI_ALIAS_FLAG);

    static {
        xRefP.setColor(0);
        xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    static int k = 0;

    static private boolean invalidate = true;

    static Bitmap getPickerMaskBitmap(int h, int w) {
        if (h + w << 10 != k || invalidate) {
            invalidate = false;
            k = h + w << 10;
            pickerBitmam = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(pickerBitmam);

            rectF.set(0, 0, w, h);
            canvas.drawColor(ThemeHelper.getColor(R.attr.card_background));
            canvas.drawRoundRect(rectF, dp(4), dp(4), xRefP);
        }


        return pickerBitmam;
    }

    public static void invalidate(){
        invalidate = true;
    }
}
