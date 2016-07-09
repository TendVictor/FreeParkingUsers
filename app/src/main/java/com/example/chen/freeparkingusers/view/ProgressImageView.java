package com.example.chen.freeparkingusers.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Pants on 2016/7/9.
 */
public class ProgressImageView extends RelativeLayout {
    private ImageView mImageView;
    private ProgressShadowView mShadowView;


    public ProgressImageView(Context context) {
        this(context, null);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView = new ImageView(context);
        mShadowView = new ProgressShadowView(context);
        addView(mImageView, params1);
        addView(mShadowView, params1);

    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setProgress(int progress) {
        if (progress < 0 || progress > 100)
            throw new RuntimeException("Method setProgress: OutOfBoundsException");

        mShadowView.setProgress(progress);
    }

    public void setProgressEnable(boolean enable) {
        mShadowView.setProgressEnable(enable);
    }

    static class ProgressShadowView extends RelativeLayout {

        private TextView mTextView;
        private int mProgress;
        private boolean mEnable;

        private Paint paint;
        private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        public ProgressShadowView(Context context) {
            super(context);
            setWillNotDraw(false);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.CENTER_IN_PARENT);
            mTextView = new TextView(context);
            mTextView.setTextColor(0xff404040);
            mTextView.setTextSize(20);
            addView(mTextView, params2);
        }

        private void setProgress(int progress) {
            mProgress = progress;

            mTextView.setText(mProgress + "%");
            invalidate();
        }

        public void setProgressEnable(boolean enable) {
            mEnable = enable;

            if (mEnable)
                mTextView.setAlpha(1);
            else
                mTextView.setAlpha(0);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (!mEnable)
                return;

            int progressY = (int) (getHeight() * (1 - mProgress / 100f));

            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();
            int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);

            RectF rectF = new RectF(0, 0, getWidth(), getHeight());
            paint.setColor(0xeeeeeeee);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawOval(rectF, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            paint.setColor(0xffffffff);
            canvas.drawRect(0, progressY, getWidth(), getHeight(), paint);
            paint.setXfermode(null);
            canvas.restoreToCount(layerId);
        }
    }
}
