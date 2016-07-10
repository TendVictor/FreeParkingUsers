package com.example.chen.freeparkingusers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.net.NetPostConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chen on 16/7/10.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvToLogin;
    private EditText evInputUserId,evInputUserName,evInputUserPassword,evInputSurePassword;
    private Button btnRegister;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0://账号注册成功：
                    Toast.makeText(RegisterActivity.this,"账号注册成功",Toast.LENGTH_SHORT).show();
                    goToLogin();
                    break;
                case 0x1://账号重复
                    Toast.makeText(RegisterActivity.this,"账号已存在",Toast.LENGTH_SHORT).show();
                    break;
                case 0x2://网络原因
                    Toast.makeText(RegisterActivity.this,"网络原因，注册失败",Toast.LENGTH_SHORT).show();
                    break;
                case 0x3://信息不正确
                    Toast.makeText(RegisterActivity.this,"信息不完全或密码不正确",Toast.LENGTH_SHORT).show();
                    break;
            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }


    private void initView() {
        tvToLogin = (TextView) findViewById(R.id.tv_gotologin);
        evInputUserId = (EditText) findViewById(R.id.ev_userid_register);
        evInputUserName = (EditText) findViewById(R.id.ev_username_register);
        evInputUserPassword = (EditText) findViewById(R.id.ev_password_register);
        evInputSurePassword = (EditText) findViewById(R.id.ev_surepassword_register);

        btnRegister = (Button) findViewById(R.id.btn_register);

        tvToLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_gotologin:
                goToLogin();
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }

    //注册新用户
    private void register() {
        String user_id = evInputUserId.getText().toString();
        String user_name = evInputUserName.getText().toString();
        String user_password = evInputUserPassword.getText().toString();
        int num = (int) (Math.random()*2+1);
        String user_img = Config.IMG_PREFIX +"default"+ num + ".jpg";
        boolean correct = user_password.equalsIgnoreCase(evInputSurePassword.getText().toString());
        Log.d("user_img", user_img);
        if(!checkInfoIsNull(user_id,user_name,user_password,user_img) && correct){
            new NetPostConnection(Config.URL_REGISTER_USER, new NetPostConnection.SuccessCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    JSONObject object = new JSONObject(result);
                    int res = object.getInt("state");
                    if(res == 1){
                        handler.obtainMessage(0x1).sendToTarget();
                    }else{
                        handler.obtainMessage(0x0).sendToTarget();
                    }
                }
            }, new NetPostConnection.FailCallback() {
                @Override
                public void onFail() {
                        handler.obtainMessage(0x2).sendToTarget();
                }
            }, "user_id",user_id,"user_name",user_name,"user_password",user_password , "user_img", user_img);
        }else{
                        handler.obtainMessage(0x3).sendToTarget();
        }
    }


    //前往登录界面
    private void goToLogin() {
        Intent intent  = new Intent(RegisterActivity.this , LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkInfoIsNull(Object ... keys){
        boolean someisNull = false;
        for (int i = 0; i < keys.length; i++){
            if(keys[i] == null)
                someisNull = true;
        }
        Log.d("someisNull", someisNull + "");
        return someisNull;
    }

}
