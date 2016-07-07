package com.example.chen.freeparkingusers.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;

import java.util.List;

/**
 * Created by chen on 16/7/5.
 */
public class ViewPagerIndicator extends LinearLayout {

    private Paint mPaint;

    private Path mPath;

    private int mLineWidth;

    private int mLineHeight;

    private static final float RADIO_LINE = 1.0f / 5;

    //初始时，指示器偏移量
    private int mLineTranslationX;

    //手指滑动的偏移量
    private float mTranslationX;

    private static final int COUNT_DEFAULT_TAB = 2;

    private int mTabVisibleCount = COUNT_DEFAULT_TAB;

    private List<String> mTabTitles;

    private ViewPager mViewPager;

    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_TEXT_NORMAL = 0x1b1b1bFF;
    /**
     * 标题选中时的颜色
     */
    private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFFFFFFFF;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabIndicator);
        mTabVisibleCount = a.getInt(R.styleable.TabIndicator_item_count, COUNT_DEFAULT_TAB);

        if (mTabVisibleCount < 0)
            mTabVisibleCount = COUNT_DEFAULT_TAB;

        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#2CBEC5"));
        mPaint.setStrokeWidth((float) 3.0);
//        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));

    }

    //绘制指示器
    @Override
    public void dispatchDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(mLineTranslationX + mTranslationX, getHeight() + 1);//?
//        Log.d("mLineHeight", mLineHeight + "");
//        Log.d("mLineWidth" ,mLineWidth+"");
//        Log.d("mLineTranslationX" ,mLineTranslationX+"");
//        Log.d("mTranslationX", mTranslationX + "");
        canvas.drawRect(0, -mLineHeight, mLineWidth,0, mPaint);
//        canvas.drawPath(mPath, mPaint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    //初始化线条宽度
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLineWidth = (int) (w / mTabVisibleCount * RADIO_LINE);

//        mLineWidth = Math.min(Dimension)

        //初始化线条
        initLine();

        mLineTranslationX = getWidth() / mTabVisibleCount / 2 - mLineWidth / 2;
    }

    /**
     * 设置可见的tab的数量
     *
     * @param count
     */
    public void setTabVisibleCount(int count) {
        this.mTabVisibleCount = count;
    }


    /**
     * 设置tab的标题内容 可选，可以自己在布局文件中写死
     *
     * @param datas
     */
    public void setTabItemTitles(List<String> datas) {
        if (datas != null && datas.size() > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (String title : mTabTitles) {
                addView(generateTextView(title));
            }

            //设置item点击事件
            setItemClickEvent();

        }
    }

    /**
     * 对外的ViewPager的回调接口
     *
     * @author chen
     */
    public interface PageChangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    //对外的ViewPager回调接口
    private PageChangeListener onPageChangeListener;

    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }

    //设置关联的ViewPager
    public void setViewPager(ViewPager mViewPager, int position) {
        this.mViewPager = mViewPager;

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 滚动
                scroll(position, positionOffset);

                //回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset,
                            positionOffsetPixels);
                }
            }


            @Override
            public void onPageSelected(int position) {
                //处理一些操作
//                resetTextViewColor();
//                highLightTextView(position);
                //回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        // 设置当前页
        mViewPager.setCurrentItem(position);
        //高亮
//        highLightTextView(position);
    }

    //设置原先的颜色
    protected void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    protected void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHTCOLOR);
        }

    }


    /**
     * 指示器跟随手指滚动，以及容器滚动
     *
     * @param position
     * @param positionOffset
     */
    private void scroll(int position, float positionOffset) {
        mTranslationX = getWidth() / mTabVisibleCount * (position + positionOffset);
        int tabWidth = getScreenWidth() / mTabVisibleCount;

        if(positionOffset > 0  && position >= (mTabVisibleCount-2)
                && getChildCount() > mTabVisibleCount){
            if(mTabVisibleCount != 1){
                this.scrollTo((position - (mTabVisibleCount-2)) * tabWidth
                        + (int)(tabWidth * positionOffset),0);
            }else{
                this.scrollTo(position*tabWidth+ (int)(tabWidth*positionOffset),0);
            }
        }
        invalidate();
    }


    /**
     * 设置布局中view的一些必要属性；如果设置了setTabTitles，布局中view则无效
     */
    @Override
    protected void onFinishInflate(){
        Log.e("TAG", "onFinishInflate");
        super.onFinishInflate();

        int cCount = getChildCount();

        if(cCount == 0)
             return;

        for(int i = 0; i< cCount; i++){
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            view.setLayoutParams(lp);
        }

        setItemClickEvent();
    }

    /**
     * 设置点击事件
     */
    private void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;//?
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }

    }

    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */
    private TextView generateTextView(String text) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setGravity(Gravity.CENTER|Gravity.TOP);
        tv.setPadding(0, 8, 0, 0);
        tv.setTextColor(Color.parseColor("#1b1b1b"));
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//调整字体大小
        tv.setLayoutParams(lp);
        return tv;
    }

    //初始化线条
    private void initLine() {
        mPath = new Path();

        mLineHeight = 8;
        mPath.moveTo(0, 0);
        mPath.lineTo(mLineWidth, 0);
        mPath.lineTo(0,-mLineHeight);
        mPath.lineTo(-mLineWidth,-mLineHeight);
        mPath.close();

    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
