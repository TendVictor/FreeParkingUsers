package com.example.chen.freeparkingusers.activity;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.item.CampaignInfo;
import com.example.chen.freeparkingusers.net.ImageLoader;
import com.example.chen.freeparkingusers.tool.ImageUtil;
import com.example.chen.freeparkingusers.tool.SystemBarTintManager;
import com.example.chen.freeparkingusers.view.OverScrollView;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pants on 2016/7/5.
 */
public class CampaignDetailActivity extends Activity {
    private OverScrollView overScrollView;
    private ImageView titleBackground;
    private LinearLayout imageScrollView;
    private RelativeLayout.LayoutParams scrollLayoutParams;

    private CampaignInfo campaignInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);
        setStatusBarAlpha();
        configureTitleLayout();
        configureImageScroll();

        campaignInfo = getIntent().getParcelableExtra("campaign_info");

        ((TextView) findViewById(R.id.tvCampaignName)).setText(campaignInfo.getCampaignName());
        ((TextView) findViewById(R.id.tvCampaignTime))
                .setText(getInterval(campaignInfo.getCampaignStartTime(), campaignInfo.getCampaignEndTime()));
        ((TextView) findViewById(R.id.tvCampaignDetail)).setText(campaignInfo.getCampaignDetail()
                + "\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>"
                + "\n<br/>\n<br/> \n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>\n<br/>");
        ImageLoader.getInstance(this).bindBitmap(campaignInfo.getCampaignImage(),
                R.drawable.default_img, (ImageView) findViewById(R.id.ivCampaignImage), new ImageLoader.BindStrategy() {
                    @Override
                    public void bindBitmapToTarget(ImageView imageView, Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);

                        int ivWidth = getResources().getDisplayMetrics().widthPixels;
                        int ivHeight = imageHeight;
                        int bWidth = bitmap.getWidth();
                        int bHeight = bitmap.getHeight();
                        float ratioWidth = 1f * ivWidth / bWidth;
                        float ratioHeight = 1f * ivHeight / bHeight;
                        float ratioClipHeight = 1f * titleLayoutHeight / ivHeight;

                        Bitmap titleBitmap;
                        int x, y, width, height;
                        if (ratioWidth < ratioHeight) {
                            width = (int) (ivWidth / ratioHeight);
                            x = (bWidth - width) / 2;
                            height = (int) (bHeight * ratioClipHeight);
                            y = bHeight - height;
                        } else {
                            width = bWidth;
                            x = 0;
                            height = (int) (ivHeight / ratioWidth * ratioClipHeight);
                            y = (int) ((bHeight + ivHeight / ratioWidth) / 2) - height;
                        }

                        titleBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
                        titleBitmap = ImageUtil.createDarkBitmap(titleBitmap);
                        titleBackground.setImageBitmap(titleBitmap);
                    }
                });
        overScrollView = (OverScrollView) findViewById(R.id.overScrollView);
        overScrollView.setScrollStrategy(new OverScrollView.OverScrollStrategy() {
            @Override
            public int onOverScrollTop(int y, int dy) {
                int scrollY = Math.min(y, imageHeight - imageScrollHeight);
                imageScrollView.scrollTo(0, (imageHeight - imageScrollHeight - scrollY) / 2);
                scrollLayoutParams.height = imageScrollHeight + scrollY;
                imageScrollView.requestLayout();
                return scrollY;
            }

            @Override
            public void onReleaseTop(int y) {
                ValueAnimator animator = ValueAnimator.ofInt(y, 0);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int scrollY = (int) animation.getAnimatedValue();
                        imageScrollView.scrollTo(0, (imageHeight - imageScrollHeight - scrollY) / 2);
                        scrollLayoutParams.height = imageScrollHeight + scrollY;
                        imageScrollView.requestLayout();
                    }
                });
                animator.setDuration(150);
                animator.start();
            }
        });
        overScrollView.setOnScrollChangeListener(new OverScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(int scrollX, int scrollY, int oldX, int oldY) {
                float alpha = 1.0f * scrollY / (imageScrollHeight - titleLayoutHeight);
                alpha = Math.min(alpha, 1);
                titleBackground.setAlpha(alpha);
            }
        });
    }

    int imageHeight;
    int imageScrollHeight;
    int titleLayoutHeight;

    private void configureImageScroll() {
        imageScrollView = (LinearLayout) findViewById(R.id.imageScrollView);

        imageHeight = getResources().getDisplayMetrics().widthPixels;
        imageScrollHeight = (int) (imageHeight * 0.8f);

        findViewById(R.id.ivCampaignImage).setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, imageHeight));
        scrollLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageScrollHeight);
        imageScrollView.setLayoutParams(scrollLayoutParams);

        final ViewTreeObserver observer = imageScrollView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                imageScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                imageScrollView.scrollTo(0, (imageHeight - imageScrollHeight) / 2);
            }
        });
    }

    private void configureTitleLayout() {
        int statusBarHeight = getStatusBarHeight();
        int titleBarHeight = (int) (50 * getResources().getDisplayMetrics().density);
        titleLayoutHeight = statusBarHeight + titleBarHeight;

        findViewById(R.id.statusBar).setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight));
        findViewById(R.id.titleBar).setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleBarHeight));

        titleBackground = (ImageView) findViewById(R.id.titleBackground);
        titleBackground.setLayoutParams(
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, titleLayoutHeight));
        titleBackground.setBackgroundColor(0xff404040);
        titleBackground.setAlpha(0f);
    }

    private String getInterval(String campaignStartTime, String campaignEndTime) {
        SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatOut = new SimpleDateFormat("yyyy.MM.dd");

        try {
            Date startDatetime = formatIn.parse(campaignStartTime);
            Date endDatetime = formatIn.parse(campaignEndTime);
            String startDate = formatOut.format(startDatetime);
            String endDate = formatOut.format(endDatetime);
            if (startDate.substring(0, 3).contentEquals(endDate.substring(0, 3)))
                return startDate.concat("-").concat(endDate.substring(5));
            else
                return startDate.concat("-").concat(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnPrevious) {
            finish();
        }
    }

    private int getStatusBarHeight() {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;
        }
        return statusBarHeight;
    }

    public void setStatusBarAlpha() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //状态栏透明 需要在创建SystemBarTintManager 之前调用。
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        //使StatusBarTintView 和 actionbar的颜色保持一致，风格统一。
        tintManager.setStatusBarTintResource(R.color.colorAlpha);

    }

    //    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
