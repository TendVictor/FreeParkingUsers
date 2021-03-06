/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xys.libzxing.zxing.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.Result;
import com.xys.libzxing.R;
import com.xys.libzxing.zxing.camera.CameraManager;
import com.xys.libzxing.zxing.decode.DecodeThread;
import com.xys.libzxing.zxing.utils.BeepManager;
import com.xys.libzxing.zxing.utils.CaptureActivityHandler;
import com.xys.libzxing.zxing.utils.InactivityTimer;
import com.xys.libzxing.zxing.utils.NetPostConnection;
import com.xys.libzxing.zxing.view.ScanView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private SurfaceView scanPreview = null;
    private ScanView scanCropView;

    private Rect mCropRect = null;
    private boolean isHasSurface = false;

    private static final String SCAN_QRCode_URL = "http://139.129.24.127/parking_app/User/user_scanQRcode.php";

    private String username = null;
    private ProgressDialog pd = null;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        username = getIntent().getStringExtra("user_id");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);

        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        //扫描视图
        scanCropView = (ScanView) findViewById(R.id.capture_crop_view);


        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

    }

    @Override
    protected void onResume() {

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.

        cameraManager = new CameraManager(getApplication());
        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            getCameraPermissionAndInitCamera(scanPreview.getHolder());

        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            Log.e("TAG","here");

            scanPreview.getHolder().addCallback(this);


        }

        inactivityTimer.onResume();

        super.onResume();
    }


    //判断系统是否为6.0且获取权限(拍照)
    public void getCameraPermissionAndInitCamera(SurfaceHolder holder){

        int  currentapiVersion = android.os.Build.VERSION.SDK_INT;

        //若是大于6.0，则需代码获取权限
        if(currentapiVersion >= 23){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
            }else{
                initCamera(holder);
            }
        }else{//版本低于6.0
            initCamera(holder);
        }

    }

    private  static  final int REQUEST_CAMERA = 0x110;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == REQUEST_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Log.e("TAG","onRequestPermissionsResult");
                initCamera(scanPreview.getHolder());

            }else{//拒绝了
                Toast.makeText(CaptureActivity.this,"摄像头权限拒绝",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }

        if (!isHasSurface) {

            Log.e("TAG","OnCreat");
            isHasSurface = true;
            getCameraPermissionAndInitCamera(holder);

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void onback(View view){
        this.finish();
    }
    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

         String ticket_id = rawResult.getText();

         // just for test
         Toast.makeText(CaptureActivity.this,ticket_id,Toast.LENGTH_SHORT).show();
         AddTicket(ticket_id);

    }

    public void AddTicket(String ticket_id) {

        pd  = ProgressDialog.show(this,"","正在添加,请稍候");

        new NetPostConnection(SCAN_QRCode_URL, new NetPostConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) throws JSONException {
                Message msg = new Message();
                msg.obj = result;
                msg.what = NET_SUCCESS;
                nethandler.sendMessage(msg);
            }
        }, new NetPostConnection.FailCallback() {
            @Override
            public void onFail() {
                nethandler.sendEmptyMessage(NET_FAILURE);
            }
        }, new Object[]{
                "user_id",username,"ticket_id",ticket_id
        });
    }


    private static  final int NET_SUCCESS = 0x123;
    private static  final int NET_FAILURE = 0x110;
    private Handler nethandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
             switch (msg.what){
                 case NET_SUCCESS:
                     pd.cancel();
                     String result = (String) msg.obj;
                     Log.e("TAG",result);
                     if(parseJsonResult(result)){
                         new AlertDialog.Builder(CaptureActivity.this).setMessage("添加成功").setPositiveButton("点击返回", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 finish();
                                 overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                             }
                         }).setCancelable(false).show();
                     }else{
                         new AlertDialog.Builder(CaptureActivity.this).setMessage("不合法的ticket信息,请商家检查停车券").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 finish();
                                 overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                             }
                         }).setCancelable(false).show();
                     }
                     break;
                 case NET_FAILURE:
                     pd.cancel();
                     Toast.makeText(CaptureActivity.this,"网络异常,请重试",Toast.LENGTH_SHORT).show();
                     break;
             }
        }
    };

    private boolean parseJsonResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            int flag = jsonObject.getInt("state");
            if(flag == 0){
                return true;
            }else if(flag == 1){
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }
            initCrop();

        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.e(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Camera error");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        mCropRect = CameraManager.getFramingRect();
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}