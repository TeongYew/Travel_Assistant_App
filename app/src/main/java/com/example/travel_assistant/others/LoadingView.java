package com.example.travel_assistant.others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import com.example.travel_assistant.R;

public class LoadingView extends View {
    private Paint paint;
    private Drawable icon;
    private ValueAnimator animator;

    public LoadingView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(0xff000000);
        icon = context.getDrawable(R.drawable.flight_departure);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());

        animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float rotation = (Float) animator.getAnimatedValue();
        canvas.rotate(rotation, getWidth() / 2, getHeight() / 2);
        int left = (getWidth() - icon.getIntrinsicWidth()) / 2;
        int top = (getHeight() - icon.getIntrinsicHeight()) / 2;
        icon.setBounds(left, top, left + icon.getIntrinsicWidth(), top + icon.getIntrinsicHeight());
        icon.draw(canvas);

        canvas.rotate(-rotation, getWidth() / 2, getHeight() / 2);
        paint.setColor(0xff000000);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - icon.getIntrinsicWidth() / 2, paint);
    }

    public void setIcon(int resId) {
        icon = getContext().getDrawable(resId);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        invalidate();
    }

    public void startAnimation() {
        animator.start();
    }

    public void stopAnimation() {
        animator.cancel();
    }
}