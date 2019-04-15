package com.example.isamorodov.telegramcontest.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.utils.AndroidUtilities;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;


public class FlatCheckBox extends View {

    boolean attached = false;
    public boolean checked;

    String text;
    TextPaint textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint outLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    int colorActive;
    int colorInactive;

    int colorTextActive;

    int MIN_WIDTH = dp(80);
    int HEIGHT = dp(40);
    int INNER_PADDING = dp(24);
    int TRANSLETE_TEXT = dp(8);

    int P = dp(2);

    Path path = new Path();

    RectF rectF = new RectF();

    float progress = 0;

    ValueAnimator checkAnimator;

    public FlatCheckBox(Context context) {
        super(context);
        init();
    }

    public FlatCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlatCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint.setTextSize(dp(16));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        outLinePaint.setStrokeWidth(dpFloat(1.5f));
        outLinePaint.setStyle(Paint.Style.STROKE);

        checkPaint.setStyle(Paint.Style.STROKE);
        checkPaint.setStrokeCap(Paint.Cap.ROUND);
        checkPaint.setStrokeWidth(dp(2));
    }

    public void recolor(int c) {
        colorActive = ThemeHelper.getColor(R.attr.card_background);
        colorTextActive = Color.WHITE;
        colorInactive = c;
        invalidate();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attached = false;
    }

    public void setChecked(boolean enabled) {
        setChecked(enabled, true);
    }

    public void setChecked(boolean enabled, boolean animate) {
        checked = enabled;
        if (!attached || !animate) {
            progress = enabled ? 1f : 0f;
        } else {
            if(checkAnimator != null) {
                checkAnimator.removeAllListeners();
                checkAnimator.cancel();
            }
            checkAnimator = ValueAnimator.ofFloat(progress, enabled ? 1 : 0);
            checkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            checkAnimator.setDuration(300);
            checkAnimator.start();
        }
    }

    public void setText(String text) {
        this.text = text;
        requestLayout();
    }

    int lastW = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textW = text == null ? 0 : (int) textPaint.measureText(text);
        textW += INNER_PADDING << 1;

        if (textW < MIN_WIDTH) textW = MIN_WIDTH;

        setMeasuredDimension(textW + P * 2, HEIGHT + P * 2);

        if (getMeasuredWidth() != lastW) {
            rectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            rectF.inset(P + outLinePaint.getStrokeWidth() / 2, P + outLinePaint.getStrokeWidth() / 2);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);


        float textTranslation = 0f;

        if (progress <= 0.5f) {
            float checkProgress = textTranslation = progress / 0.5f;
            int rD = (int) ((Color.red(colorInactive) - Color.red(colorActive)) * checkProgress);
            int gD = (int) ((Color.green(colorInactive) - Color.green(colorActive)) * checkProgress);
            int bD = (int) ((Color.blue(colorInactive) - Color.blue(colorActive)) * checkProgress);
            int c = Color.rgb(Color.red(colorActive) + rD, Color.green(colorActive) + gD, Color.blue(colorActive) + bD);

            fillPaint.setColor(c);

            rD = (int) ((Color.red(colorTextActive) - Color.red(colorInactive)) * checkProgress);
            gD = (int) ((Color.green(colorTextActive) - Color.green(colorInactive)) * checkProgress);
            bD = (int) ((Color.blue(colorTextActive) - Color.blue(colorInactive)) * checkProgress);
            c = Color.rgb(Color.red(colorInactive) + rD, Color.green(colorInactive) + gD, Color.blue(colorInactive) + bD);

            textPaint.setColor(c);
        } else {
            textTranslation = 1f;
            textPaint.setColor(colorTextActive);
            fillPaint.setColor(colorInactive);
        }


        int heightHalf = (getMeasuredHeight() >> 1);

        outLinePaint.setColor(colorInactive);
        canvas.drawRoundRect(rectF, HEIGHT / 2f, HEIGHT / 2f, fillPaint);
        canvas.drawRoundRect(rectF, HEIGHT / 2f, HEIGHT / 2f, outLinePaint);
        if (text != null) {
            canvas.drawText(text,
                    (getMeasuredWidth() >> 1) + (textTranslation * TRANSLETE_TEXT),
                    heightHalf + (textPaint.getTextSize() * 0.35f),
                    textPaint
            );
        }

        float bounceProgress = 2.0f - progress / 0.5f;
        canvas.save();
        canvas.translate(dp(10), heightHalf - dp(8));
        if (progress > 0.5f) {
            checkPaint.setColor(colorTextActive);
            int endX = (int) (dpFloat(7.5f) - dp(4) * (1.0f - bounceProgress));
            int endY = (int) (AndroidUtilities.dpFloat(13.5f) - dp(4) * (1.0f - bounceProgress));
            canvas.drawLine(dpFloat(7.5f), (int) AndroidUtilities.dpFloat(13.5f), endX, endY, checkPaint);
            endX = (int) (AndroidUtilities.dpFloat(7.5f) + dp(8) * (1.0f - bounceProgress));
            endY = (int) (AndroidUtilities.dpFloat(12f) - dp(8) * (1.0f - bounceProgress));
            canvas.drawLine((int) AndroidUtilities.dpFloat(7.5f), (int) AndroidUtilities.dpFloat(13.5f), endX, endY, checkPaint);
        }
        canvas.restore();


    }

    public void denied() {
        checkAnimator = ValueAnimator.ofFloat(progress, 1f,0.5f);
        checkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        checkAnimator.setDuration(300);
        checkAnimator.start();
        checkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                checkAnimator = ValueAnimator.ofFloat(progress, 0.5f,1f);
                checkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        progress = (float) animation.getAnimatedValue();
                        invalidate();
                    }
                });
                checkAnimator.setDuration(300);
                checkAnimator.start();
            }
        });
    }
}
