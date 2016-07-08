package com.example.chen.freeparkingusers.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;

/**
 * Created by chen on 16/7/6.
 */
public class UserInfoDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView ivBack, ivLogout,ivUserImg;

    private EditText evUserName;

    private TextView tvUserid, tvModify, tvModifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initView();
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
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back_userinfo:
                backToMain();
                break;
            case R.id.iv_logout_userinfo:
                Adjustlogout();
                break;
            case R.id.iv_img_userinfo:
                break;
            case R.id.tv_modify_userinfo:
                break;
            case R.id.tv_modifypassword:
                break;
        }
    }

    //按返回键到主页面
    private void backToMain(){
        finish();
    }

    private void Adjustlogout(){
         AlertDialog dialog = new AlertDialog.Builder(getBaseContext()).setTitle("提示")
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
    private void logout(){

    }

    //上传用户图片
    private void updateUserImg(){
//        new NetPostConnection("")
    }
}
