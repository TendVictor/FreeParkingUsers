package com.example.chen.freeparkingusers.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.item.CampaignInfo;
import com.example.chen.freeparkingusers.item.SellerInfo;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.net.ImageLoader;
import com.example.chen.freeparkingusers.net.NetPostConnection;
import com.example.chen.freeparkingusers.tool.ImageUtil;
import com.example.chen.freeparkingusers.tool.SystemBarTintManager;
import com.example.chen.freeparkingusers.view.OverScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Pants on 2016/7/5.
 */
public class SellerDetailActivity extends Activity {

    private OverScrollView overScrollView;
    private ImageView titleBackground;
    private LinearLayout layoutContent;
    private ScrollView imageScrollView;
    private RelativeLayout.LayoutParams scrollLayoutParams;

    private SellerInfo sellerInfo;
    private List<CampaignInfo> campaignInfoList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x1) {
                AlertDialog dialog = new AlertDialog.Builder(getBaseContext())
                        .setTitle("提示").setMessage("获取商家活动信息失败")
                        .setNeutralButton("回到主页面", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("重新获取", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateCampaignInfo(sellerInfo.getSellerId());
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            } else if (msg.what == 0x0) {
                updateCampaignUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_detail);
        setStatusBarAlpha();
        configureTitleLayout();
        configureImageScroll();


        sellerInfo = getIntent().getParcelableExtra("seller_info");
        ((TextView) findViewById(R.id.tvSellerName)).setText(sellerInfo.getSellerName());
        ((TextView) findViewById(R.id.tvSellerAddress)).setText(sellerInfo.getSellerAddress());
        ((TextView) findViewById(R.id.tvSellerContact)).setText(sellerInfo.getSellerContact());
        ImageLoader.getInstance(this).bindBitmap(sellerInfo.getSellerImage(),
                R.drawable.default_user_icon_1, (ImageView) findViewById(R.id.ivSellerImage), new ImageLoader.BindStrategy() {
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

        layoutContent = (LinearLayout) findViewById(R.id.layoutContent);
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
                animator.setDuration(200);
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
        imageScrollView = (ScrollView) findViewById(R.id.imageScrollView);

        imageHeight = getResources().getDisplayMetrics().widthPixels;
        imageScrollHeight = (int) (imageHeight * 0.8f);

        findViewById(R.id.ivSellerImage).setLayoutParams(
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageHeight));
        scrollLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageScrollHeight);
        imageScrollView.setLayoutParams(scrollLayoutParams);
        imageScrollView.requestLayout();

        imageScrollView.scrollTo(0, (imageHeight - imageScrollHeight) / 2);
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

    @Override
    protected void onResume() {
        super.onResume();

        if (campaignInfoList == null) {
            updateCampaignInfo(sellerInfo.getSellerId());
        }
    }

    private void updateCampaignInfo(String sellerId) {
        new NetPostConnection(Config.URL_USER_GET_SELLER_INFO,
                new NetPostConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) throws JSONException {
                        JSONArray jsonArray = new JSONArray(result);
                        int count = jsonArray.length();
                        campaignInfoList = new ArrayList<>(count);
                        for (int i = 0; i < count; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CampaignInfo info = new CampaignInfo(
                                    jsonObject.getInt("activity_id"),
                                    jsonObject.getString("activity_img"),
                                    jsonObject.getString("activity_name"),
                                    jsonObject.getString("activity_endtime"),
                                    jsonObject.getString("activity_starttime"),
                                    jsonObject.getString("activity_detail")
                            );
                            campaignInfoList.add(info);
                        }
                        mHandler.obtainMessage(0x0).sendToTarget();
                    }
                },
                new NetPostConnection.FailCallback() {
                    @Override
                    public void onFail() {
                        mHandler.obtainMessage(0x1).sendToTarget();
                    }
                }, "seller_id", sellerId);
    }

    private void updateCampaignUI() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.line_height_larger));
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.bottomMargin = 16;

        int size = campaignInfoList.size();
        if (size == 0) {
            View view = View.inflate(this, R.layout.layout_campaign_none_tip, null);
            layoutContent.addView(view, params);
        } else {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CampaignInfo info = (CampaignInfo) v.getTag(R.id.tag_campaign_info);
                    Intent intent = new Intent(SellerDetailActivity.this, CampaignDetailActivity.class);
                    intent.putExtra("campaign_info", info);
                    startActivity(intent);
                }
            };

            for (int i = 0; i < size; i++) {
                CampaignInfo info = campaignInfoList.get(i);
                View view = View.inflate(this, R.layout.layout_campaign_item, null);
                ((TextView) view.findViewById(R.id.tvCampaignNumber))
                        .setText("活动" + getChineseNumber(i + 1));
                ((TextView) view.findViewById(R.id.tvCampaignName))
                        .setText(info.getCampaignName());
                ((TextView) view.findViewById(R.id.tvCampaignDetail))
                        .setText(info.getCampaignDetail());
                layoutContent.addView(view, params2);

                view.setTag(R.id.tag_campaign_info, campaignInfoList.get(i));
                view.setOnClickListener(onClickListener);
            }
        }
    }

    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnPrevious) {
            finish();
        }
    }

    // assert (i<100);
    private String getChineseNumber(int i) {
        char[] nums = new char[]{
                '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'
        };
        StringBuilder sb = new StringBuilder();
        int a = i / 10;
        int b = i % 10;
        if (a >= 2)
            sb.append(nums[a - 1]);
        if (a >= 1)
            sb.append(nums[9]);
        if (b != 0)
            sb.append(nums[b - 1]);
        return sb.toString();
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
