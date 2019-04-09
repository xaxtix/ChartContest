package com.example.isamorodov.telegramcontest.ui.components;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.utils.AndroidUtilities;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;


public class CheckBoxDrawable extends Drawable {

    private RectF rectF;

    private Bitmap drawBitmap;
    private Canvas drawCanvas;

    private float progress;
    private ValueAnimator checkAnimator;

    private boolean attachedToWindow;
    private boolean isChecked;

    private final static float progressBounceDiff = 0.2f;

    Paint checkboxSquare_checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint checkboxSquare_eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint checkboxSquare_backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    int uncheckedColor;
    public int color;
    private int checkColor;

    FlatCheckBox checkBox;


    CheckBoxDrawable(FlatCheckBox checkBox) {
        this.checkBox = checkBox;
        checkboxSquare_checkPaint.setStyle(Paint.Style.STROKE);
        checkboxSquare_checkPaint.setStrokeWidth(dp(2));

        checkboxSquare_eraserPaint.setColor(0);
        checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        rectF = new RectF();
        drawBitmap = Bitmap.createBitmap(dp(18), dp(18), Bitmap.Config.ARGB_4444);
        drawCanvas = new Canvas(drawBitmap);
    }

    public void recolor(int c) {
        uncheckedColor = ThemeHelper.getColor(R.attr.checkbox_inactive);
        checkColor = ThemeHelper.getColor(R.attr.card_background);
        color = c;
        invalidateSelf();
    }

    @Keep
    public void setProgress(float value) {
        if (progress == value) {
            return;
        }
        progress = value;
        invalidateSelf();
    }

    private void cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        checkAnimator = ValueAnimator.ofFloat(progress, newCheckedState ? 1 : 0);
        checkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidateSelf();
            }
        });
        checkAnimator.setDuration(300);
        checkAnimator.start();
    }


    public void setChecked(boolean checked) {
        if (checked == isChecked) {
            return;
        }
        isChecked = checked;
        if (checkBox.attached) {
            animateToCheckedState(checked);
        } else {
            cancelCheckAnimator();
            setProgress(checked ? 1.0f : 0.0f);
        }
    }


    int size = dp(24);
    int height = dp(24);

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public int getIntrinsicWidth() {
        return size;
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    public static boolean hasState(int[] states, int state) {
        if (states == null)
            return false;

        for (int state1 : states)
            if (state1 == state)
                return true;

        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        float checkProgress;
        float bounceProgress;
        if (progress <= 0.5f) {
            bounceProgress = checkProgress = progress / 0.5f;
            int rD = (int) ((Color.red(color) - Color.red(uncheckedColor)) * checkProgress);
            int gD = (int) ((Color.green(color) - Color.green(uncheckedColor)) * checkProgress);
            int bD = (int) ((Color.blue(color) - Color.blue(uncheckedColor)) * checkProgress);
            int c = Color.rgb(Color.red(uncheckedColor) + rD, Color.green(uncheckedColor) + gD, Color.blue(uncheckedColor) + bD);
            checkboxSquare_backgroundPaint.setColor(c);
        } else {
            bounceProgress = 2.0f - progress / 0.5f;
            checkProgress = 1.0f;
            checkboxSquare_backgroundPaint.setColor(color);
        }


        float bounce = dp(1) * bounceProgress;
        rectF.set(bounce, bounce, dp(18) - bounce, dp(18) - bounce);

        drawBitmap.eraseColor(0);
        drawCanvas.drawRoundRect(rectF, dpFloat(1.8f), dpFloat(1.8f), checkboxSquare_backgroundPaint);

        if (checkProgress != 1) {
            float rad = Math.min(dp(7), dp(7) * checkProgress + bounce);
            rectF.set(dp(2) + rad, dp(2) + rad, dp(16) - rad, dp(16) - rad);
            drawCanvas.drawRect(rectF, checkboxSquare_eraserPaint);
        }

        if (progress > 0.5f) {
            checkboxSquare_checkPaint.setColor(checkColor);
            int endX = (int) (dp(7.5f) - dp(5) * (1.0f - bounceProgress));
            int endY = (int) (AndroidUtilities.dpFloat(13.5f) - dp(5) * (1.0f - bounceProgress));
            drawCanvas.drawLine(dp(7.5f), (int) AndroidUtilities.dpFloat(13.5f), endX, endY, checkboxSquare_checkPaint);
            endX = (int) (AndroidUtilities.dpFloat(6.5f) + dp(9) * (1.0f - bounceProgress));
            endY = (int) (AndroidUtilities.dpFloat(13.5f) - dp(9) * (1.0f - bounceProgress));
            drawCanvas.drawLine((int) AndroidUtilities.dpFloat(6.5f), (int) AndroidUtilities.dpFloat(13.5f), endX, endY, checkboxSquare_checkPaint);
        }
        canvas.save();
        canvas.drawBitmap(drawBitmap, 0, 0, null);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

}
