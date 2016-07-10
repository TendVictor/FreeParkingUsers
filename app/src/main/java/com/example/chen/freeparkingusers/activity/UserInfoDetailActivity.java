package com.example.chen.freeparkingusers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by chen on 16/7/6.
 */
public class UserInfoDetailActivity extends Activity implements View.OnClickListener {

    public static final int SELECT_PIC_BY_TAKE_PHOTO = 1;

    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    public static final int CROP_BY_CAMERA = 3;

    private int output_X = 480;
    private int output_Y = 480;

    private static final String IMAGE_FILE_NAME = "temp.jpg";

    private Bitmap bitmap;

    private Uri photoUri;
    private Uri cameraUri;

    private String tokens = null;
    private String userName;
    private String userImg;

    private ImageView ivBack, ivLogout, ivUserImg;

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
                    Log.d("0x4",userImg);
                    downloadImg();
                    break;
            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        if(Config.username != null){
            applyForUserInfo(Config.username);
            System.out.println(Config.username+"     lallaallalal");
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bitmap.isRecycled())
            bitmap.recycle();
    }


    //初始化View
    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back_userinfo);
        ivLogout = (ImageView) findViewById(R.id.iv_logout_userinfo);
        ivUserImg = (ImageView) findViewById(R.id.iv_img_userinfo);

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
                applyforToken();
                break;
            case R.id.tv_modifypassword:
                break;
        }
    }

    //选择拍照还是本地图片
    private void choosePhotoType() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("选择图片上传方式").setNeutralButton("选择拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takePhoto();
                    }


                }).setNegativeButton("选择本地图片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pickPhoto();
                    }
                }).create();
        dialog.show();
    }

    //选择拍照
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        String sdState = Environment.getExternalStorageState();
//        if (sdState.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
//            File toFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
//            try {
//                if (!toFile.exists())
//                    toFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        cameraUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                , IMAGE_FILE_NAME));
//            Log.d("cameraUri", cameraUri + "");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
//        }
        Log.d("before", cameraUri + "");
        startActivityForResult(intent, SELECT_PIC_BY_TAKE_PHOTO);
        Log.d("after", cameraUri + "");
//        File file = new File(Environment.getExternalStorageDirectory() + IMAGE_FILE_NAME);
//        cameraUri = Uri.fromFile(file);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
//        startActivityForResult(intent, SELECT_PIC_BY_TAKE_PHOTO);
    }

    //选择本地图片
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("data", data.toString());
        if (data != null) {
            switch (requestCode) {
                case SELECT_PIC_BY_PICK_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        pictureFromLocal(data);
                    }
                    break;
                case SELECT_PIC_BY_TAKE_PHOTO:
//                    Bundle bundle = data.getExtras();
//                    bitmap = data.getParcelableExtra("data");
                    Log.d("bitmap", bitmap.toString());

//                    pictureFromTakingPhoto();
                    break;
                case CROP_BY_CAMERA://裁剪
//                    finishCrop(data);
                    break;
            }
        } else {
//            if(requestCode == SELECT_PIC_BY_TAKE_PHOTO && cameraUri != null){
//                startCropImage(cameraUri);
//            }else if(requestCode == CROP_BY_CAMERA && cameraUri != null){
            Log.d("here", "here");
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), cameraUri);
//                    RoundedBitmapDrawable roundedBitmapDrawable =
//                            RoundedBitmapDrawableFactory.create(getResources(), bitmap);
//                    roundedBitmapDrawable.setCornerRadius(bitmap.getWidth()/2);
//                    ivUserImg.setImageDrawable(roundedBitmapDrawable);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


//                RoundedBitmapDrawable roundedBitmapDrawable =
//                        RoundedBitmapDrawableFactory.create(getResources(), bitmap);
//                roundedBitmapDrawable.setCornerRadius(bitmap.getWidth()/2);
//                ivUserImg.setImageDrawable(roundedBitmapDrawable);
//            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //上传使用七牛云
    private void uploadUsrImg() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里

//            bitmap.recycle();//自由选择是否进行回收


            byte[] result = output.toByteArray();
//            output.flush();
            output.close();

            UploadManager uploadManager = new UploadManager();


            Log.d("tokens", result.length + "  and the tokens is: " + tokens);
            uploadManager.put(result, "user1", tokens, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {

                    Log.d("qiniu", key + ",\r\n " + info + ",\r\n " + response);
                }
            }, new UploadOptions(null, null, false, new UpProgressHandler() {
                @Override
                public void progress(String key, double percent) {

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

    }

    //获取个人信息
    private void applyForUserInfo(String user_id) {
        new NetPostConnection(Config.URL_GET_USERINFO, new NetPostConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) throws JSONException {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    userName = object.getString("user_name");
                    userImg = object.getString("user_img");
                }
                handler.obtainMessage(0x4).sendToTarget();
            }
        }, new NetPostConnection.FailCallback() {
            @Override
            public void onFail() {
                handler.obtainMessage(0x3).sendToTarget();
            }
        }, "user_id", user_id);
    }

    //七牛云下载图片
    private void downloadImg() {
        if(userImg != null){
            userImg.replace("\\","");
            Log.d("downloadImg", userImg);
            ImageLoader.getInstance(this).bindBitmap(userImg,R.drawable.default_img,ivUserImg);
        }
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
            Bitmap bitmap = extras.getParcelable("data");

            RoundedBitmapDrawable roundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCornerRadius(bitmap.getWidth() / 2);
            ivUserImg.setImageDrawable(roundedBitmapDrawable);
        }
    }

    //设置从拍照中获取
    private void pictureFromTakingPhoto() {
        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(tempFile), "image/*");

//        Log.d("tempFile",tempFile.)
        // 设置裁剪
        intent.putExtra("crop", "true");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));


        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", false);

        startActivityForResult(intent, CROP_BY_CAMERA);
    }


    //上传图片
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


//            bitmap = miniBitmapFromUri(picturePath);
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
            // Method 3
        }
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(bitmap.getWidth() / 2);
        ivUserImg.setImageDrawable(roundedBitmapDrawable);


    }

    /**
     * 裁减图片操作
     *
     * @param uri
     */
    private void startCropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 使图片处于可裁剪状态
        intent.putExtra("crop", "true");
        // 裁剪框的比例（根据需要显示的图片比例进行设置）
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 2);
        // 让裁剪框支持缩放
        intent.putExtra("scale", true);
        // 裁剪后图片的大小（注意和上面的裁剪比例保持一致）
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        // 传递原图路径
        File cropFile = new File(Environment.getExternalStorageDirectory() + IMAGE_FILE_NAME);
        Uri cropImageUri = Uri.fromFile(cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        // 设置裁剪区域的形状，默认为矩形，也可设置为原形
//        intent.putExtra("circleCrop", true);
        // 设置图片的输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
        intent.putExtra("return-data", false);
        // 是否需要人脸识别
//        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CROP_BY_CAMERA);
    }


}
