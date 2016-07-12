package com.example.chen.freeparkingusers.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Created by Pants on 2016/7/4.
 */
public class OverScrollView extends ScrollView {

    private View mChild;
    private OnScrollChangeListener onScrollChangeListener;
    private OverScrollStrategy mScrollStrategy = new OverScrollStrategy() {
        // Empty realization~
    };

    public OverScrollView(Context context) {
        this(context, null);
    }

    public OverScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        ViewTreeObserver observer = this.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getChildCount() != 0)
                    mChild = getChildAt(0);
            }
        });
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public void setScrollStrategy(OverScrollStrategy scrollStrategy) {
        if (scrollStrategy != null) {
            mScrollStrategy = scrollStrategy;
        }
    }

    private static final int STATUS_TOP = 0;
    private static final int STATUS_MIDDLE = 1;
    private static final int STATUS_BOTTOM = 2;

    private int status = STATUS_MIDDLE;
    private int curY;
    private int lastY;
    private int position;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 补充功能：scroller 及时停止~
            // 其他实现: 新建内部容器类，利用解决“滑动冲突”方法重写内部类滑动事件...
            position = 0;
            curY = (int) event.getY();
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String a = status == 0 ? "top" : status == 1 ? "middle" : "bottom";
        Log.d("state", "state:" + a + " position:" + position);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                lastY = curY;
                curY = (int) event.getY();

                if (getScrollY() == 0) {
                    // 顶部拉伸
                    if (status != STATUS_TOP) {
                        status = STATUS_TOP;
                        position = 0;
                    }

                    int dy = curY - lastY;
                    if (position + dy <= 0) {
                        status = STATUS_MIDDLE;
                        position = mScrollStrategy.onOverScrollTop(0, 0 - position);
                    } else {
                        position = mScrollStrategy.onOverScrollTop(position + dy, dy);
                    }
                } else if (getScrollY() == mChild.getHeight() - getHeight()) {
                    // 底部拉伸
                    if (status != STATUS_BOTTOM) {
                        status = STATUS_BOTTOM;
                        position = 0;
                    }

                    int dy = lastY - curY;
                    if (position <= 0) {
                        status = STATUS_MIDDLE;
                        position = mScrollStrategy.onOverScrollBottom(0, 0 - position);
                    } else {
                        position = mScrollStrategy.onOverScrollBottom(position + dy, dy);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (status == STATUS_TOP) {
                    mScrollStrategy.onReleaseTop(position);
                } else if (status == STATUS_BOTTOM) {
                    mScrollStrategy.onReleaseBottom(position);
                }
                status = STATUS_MIDDLE;
                break;
            }
        }

        if (status == STATUS_MIDDLE) {
            return super.onTouchEvent(event);
        } else {
            return true;
        }
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldX, int oldY) {
        super.onScrollChanged(scrollX, scrollY, oldX, oldY);

        onScrollChangeListener.onScrollChanged(scrollX, scrollY, oldX, oldY);
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int scrollX, int scrollY, int oldX, int oldY);
    }

    public static abstract class OverScrollStrategy {

        public int onOverScrollTop(int y, int dy) {
            return 0;
        }

        public int onOverScrollBottom(int y, int dy) {
            return 0;
        }

        public void onReleaseTop(int y) {
        }

        public void onReleaseBottom(int y) {
        }
    }
}
