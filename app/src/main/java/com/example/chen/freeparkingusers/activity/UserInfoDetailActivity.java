package com.example.chen.freeparkingusers.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.net.ImageLoader;
import com.example.chen.freeparkingusers.net.NetPostConnection;
import com.example.chen.freeparkingusers.view.ProgressImageView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by chen on 16/7/6.
 */
public class UserInfoDetailActivity extends Activity implements View.OnClickListener {

    public static final int REQUEST_CAMERA = 0;

    public static final int SELECT_PIC_BY_TAKE_PHOTO = 1;

    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    public static final int CROP_BY_CAMERA = 3;

    private int output_X = 200;
    private int output_Y = 200;

    private static final String IMAGE_FILE_NAME = "temp.JPG";

    private Bitmap bitmap;

    private Uri photoUri;
    private Uri cameraUri;

    private boolean isImgChange = false;
    private String tokens = null;
    private String userName;
    private String userImg;
    private String key = null;


    private ImageView ivBack, ivLogout;
    private ProgressImageView ivUserImg;

    private ProgressBar pb_img_loader;

    private EditText evUserName;

    private TextView tvUserid, tvModify, tvModifyPassword;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1://获取token失败
                    Toast.makeText(UserInfoDetailActivity.this, "获取token失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0x2://获取token成功
                    Log.d("tokens获取成功", tokens);
                    uploadUsrImg();
                    break;
                case 0x3://网络原因
                    Toast.makeText(UserInfoDetailActivity.this, "网络原因获取失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0x4://获取个人信息成功
                    Log.d("0x4", userImg);
                    updateUserInfo();
                    downloadImg();
                    break;
                case 0x5://修改个人信息成功：
                    Toast.makeText(UserInfoDetailActivity.this, "修改个人信息成功", Toast.LENGTH_SHORT).show();
                    break;
                case 0x6://修改失败
                    Toast.makeText(UserInfoDetailActivity.this, "修改个人信息失败，请重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        if (Config.username != null) {
            applyForUserInfo(Config.username);
            System.out.println(Config.username + "     lallaallalal");
        }
        initView();
    }

    //确保Destroy处理掉bitmap等占内存
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null)
            if (!bitmap.isRecycled())
                bitmap.recycle();
    }


    //初始化View
    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back_userinfo);
        ivLogout = (ImageView) findViewById(R.id.iv_logout_userinfo);
        ivUserImg = (ProgressImageView) findViewById(R.id.iv_img_userinfo);

        evUserName = (EditText) findViewById(R.id.ev_showname_userinfo);

        tvUserid = (TextView) findViewById(R.id.tv_showid_userinfo);
        tvModify = (TextView) findViewById(R.id.tv_modify_userinfo);
        tvModifyPassword = (TextView) findViewById(R.id.tv_modifypassword);

        ivBack.setOnClickListener(this);
        ivLogout.setOnClickListener(this);
        ivUserImg.setOnClickListener(this);

        tvModify.setOnClickListener(this);
        tvModifyPassword.setOnClickListener(this);
    }


    //监听点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_userinfo:
                backToMain();
                break;
            case R.id.iv_logout_userinfo:
                Adjustlogout();
                break;
            case R.id.iv_img_userinfo:
                choosePhotoType();
                break;
            case R.id.tv_modify_userinfo:
                modifyUserInfo();
                break;
            case R.id.tv_modifypassword:
                moveToModifyActivity();
                break;
        }
    }

    //移动至修改密码界面
    private void moveToModifyActivity() {
        Intent intent = new Intent(UserInfoDetailActivity.this, ModifyPasswordActivity.class);
        startActivity(intent);
    }

    //选择拍照还是本地图片
    private void choosePhotoType() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("选择图片上传方式").setNeutralButton("选择拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAppVersionAndTakePermission();
                    }


                }).setNegativeButton("选择本地图片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pickPhoto();
                    }
                }).create();
        dialog.show();
    }

    //判断系统是否为6.0且获取权限(拍照)
    public void getAppVersionAndTakePermission(){

        int  currentapiVersion=android.os.Build.VERSION.SDK_INT;
        //若是大于6.0，则需代码获取权限
        if(currentapiVersion >= 23){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
            }else{
                takePhoto();
            }
        }else{//版本低于6.0
                takePhoto();
        }
    }

    //选择拍照
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                , IMAGE_FILE_NAME));
        Log.d("before", cameraUri + "");
        startActivityForResult(intent, SELECT_PIC_BY_TAKE_PHOTO);
        Log.d("after", cameraUri + "");
    }

    //选择本地图片
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    //回调在获取权限后的处理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == REQUEST_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            }else{//拒绝了
                Toast.makeText(UserInfoDetailActivity.this,"摄像头权限拒绝",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //使用startActivityForResult后，返回信息的处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null || requestCode == SELECT_PIC_BY_TAKE_PHOTO) {
            switch (requestCode) {
                case SELECT_PIC_BY_PICK_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        isImgChange = true;
                        pictureFromLocal(data);
                    }
                    break;
                case SELECT_PIC_BY_TAKE_PHOTO:
                    if(data != null && resultCode == RESULT_OK){
                        isImgChange = true;
                        finishCrop(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //使用七牛云上传图片
    private void uploadUsrImg() {
        ivUserImg.setProgressEnable(true);
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里

            byte[] result = output.toByteArray();
            output.flush();
            output.close();

            UploadManager uploadManager = new UploadManager();


            Log.d("tokens", result.length + "  and the tokens is: " + tokens);
            UpCompletionHandler upHandler = new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    Log.d("responseqiniu", key + "   ,\r\n " + info + ",\r\n" + response);
                    ivUserImg.setProgressEnable(false);
                    isImgChange = false;
                    userImg = Config.IMG_PREFIX + key;
                    Log.d("userImgComplete",userImg);
                }
            };


            uploadManager.put(result, key, tokens, upHandler, new UploadOptions(null, null, false,
                    new UpProgressHandler() {
                        @Override
                        public void progress(String key, double percent) {
                            ivUserImg.setProgress((int) (percent * 100));
                            Log.d("qiniu", key + " : " + percent);
                        }
                    }, null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //按返回键到主页面
    private void backToMain() {
        finish();
    }

    //判断是否退出登录
    private void Adjustlogout() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("确定退出登录吗？").setNeutralButton("退出登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        finish();
                    }
                }).setNegativeButton("不退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    //退出登录
    private void logout() {
        Config.username = null;
    }

    //修改个人信息
    private void modifyUserInfo() {
        userName = evUserName.getText().toString();
        if (!checkInfoIsNull(userName, Config.username, userImg)) {
            new NetPostConnection(Config.URL_MODIFY_USER, new NetPostConnection.SuccessCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    JSONObject object = new JSONObject(result);
                    int res = object.getInt("state");
                    if (res == 0) {
                        handler.obtainMessage(0x5).sendToTarget();
                    } else {
                        handler.obtainMessage(0x6).sendToTarget();
                    }
                }
            }, new NetPostConnection.FailCallback() {
                @Override
                public void onFail() {
                    handler.obtainMessage(0x3).sendToTarget();
                }
            }, "user_id", Config.username, "user_name", userName, "user_img", userImg);
        }
    }

    //获取个人信息
    private void applyForUserInfo(String user_id) {
        new NetPostConnection(Config.URL_GET_USERINFO, new NetPostConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONObject object = new JSONObject(result);
                userName = object.getString("user_name");
                userImg = object.getString("user_img");
                handler.obtainMessage(0x4).sendToTarget();
            }
        }, new NetPostConnection.FailCallback() {
            @Override
            public void onFail() {
                handler.obtainMessage(0x3).sendToTarget();
            }
        }, "user_id", user_id);
    }

    //登录成功后，更新信息
    private void updateUserInfo() {
        evUserName.setText(userName);
        if (Config.username != null)
            tvUserid.setText(Config.username);
    }

    //七牛云下载图片
    private void downloadImg() {
        if (userImg != null) {
            userImg.replace("\\", "");
            String getUserImg = userImg += "?v=" + new Random().nextInt(10000);
            Log.d("getUserImg", getUserImg);
            ImageLoader.getInstance(this).bindBitmap(getUserImg, R.drawable.default_img,
                    ivUserImg.getImageView(), ImageLoader.getInstance(this).roundedBindStrategy);

        }
    }

    //
    private void getTokenIfChanged(String key){
        Log.d("key",key);
        new NetPostConnection(Config.URL_PICTURE_UPLOAD, new NetPostConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) throws JSONException {
                if(result.contains("\"state\":1")){
                    handler.obtainMessage(0x1).sendToTarget();
                    return;
                }
                JSONObject object = new JSONObject(result);
                tokens = object.get("uptoken").toString();
                Log.d("tokens", tokens + "");
                if (tokens != null) {
                    handler.obtainMessage(0x2).sendToTarget();
                }
            }
        }, new NetPostConnection.FailCallback() {
            @Override
            public void onFail() {
                handler.obtainMessage(0x3).sendToTarget();
            }
        },"key",key);
    }

    //请求七牛云token
    private void applyforToken() {
        new NetPostConnection(Config.URL_GET_TOKENS, new NetPostConnection.SuccessCallback() {

            @Override
            public void onSuccess(String result) throws JSONException {
//                Log.d("onSuccess", result + "");
                if (result.equalsIgnoreCase("1")) {
                    handler.obtainMessage(0x1).sendToTarget();
                    return;
                }
                JSONObject object = new JSONObject(result);
                tokens = object.get("uptoken").toString();
                Log.d("tokens", tokens + "");
                if (tokens != null) {
                    handler.obtainMessage(0x2).sendToTarget();
                }
            }
        }, new NetPostConnection.FailCallback() {
            @Override
            public void onFail() {
                handler.obtainMessage(0x3).sendToTarget();
            }
        }, "", "");
    }

    //裁剪图片
    private void finishCrop(Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            bitmap = extras.getParcelable("data");

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int wh = w > h ? h : w;

            int retX =  w > h ? (w - h ) /2 : 0;
            int retY =  w > h ? 0 : (h - w)/2;

            Log.d("bitmap params", "w: " + w +" h: " + h + " retX: "  + retX + " retY:" + retY);
            bitmap = Bitmap.createBitmap(bitmap, retX, retY, wh,wh,null, false);


            RoundedBitmapDrawable roundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCornerRadius(bitmap.getWidth() / 2);
            ivUserImg.getImageView().setImageDrawable(roundedBitmapDrawable);

            if(isImgChange){
                key = tvUserid.getText().toString();
                getTokenIfChanged(key);
            }
        }
    }


    //上传图片从本地
    private void pictureFromLocal(Intent data) {
        if (data == null) {
            Toast.makeText(this, "选择图片文件出错1", Toast.LENGTH_SHORT).show();
            return;
        } else {
            photoUri = data.getData();
            if (photoUri == null) {
                Toast.makeText(this, "选择图片文件出错2", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Log.i("photoUri", "photoUri= " + photoUri);

        RoundedBitmapDrawable roundedBitmapDrawable = null;

        // Method 1
        String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.SIZE, MediaStore
                .Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(photoUri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

        } else {
            // Method 2
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                // 大小压缩
                bitmap = Bitmap.createScaledBitmap(bitmap, 320, 320, false);
                // 质量压缩
                int quality = 80;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                bitmap = BitmapFactory.decodeStream(bais);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(bitmap.getWidth() / 2);
        ivUserImg.getImageView().setImageDrawable(roundedBitmapDrawable);

        if (isImgChange){
            key = tvUserid.getText().toString();
            getTokenIfChanged(key);
        }

    }

    //判断参数是否都存在
    private boolean checkInfoIsNull(Object... keys) {
        boolean someisNull = false;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null)
                someisNull = true;
        }
        Log.d("someisNull", someisNull + "");
        return someisNull;
    }
}
