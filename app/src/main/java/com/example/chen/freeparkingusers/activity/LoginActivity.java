package com.example.chen.freeparkingusers.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.freeparkingusers.MainActivity;
import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.net.NetPostConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername = null;
    private EditText edtPasswd = null;
    private Button btnLogin = null;
    private ImageView ivBack = null;
    private TextView tvRegister = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvents();
    }

    private void initEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        edtUsername = $(R.id.edt_username);
        edtPasswd = $(R.id.edt_passwd);
        btnLogin = $(R.id.btn_login);
        tvRegister = $(R.id.tv_to_register);
        ivBack = $(R.id.iv_back_login);
    }

    private String username = null;
    private ProgressDialog pd = null;

    private void Login(){

        username = edtUsername.getText().toString();
        String passwd = edtPasswd.getText().toString();
        if(checkVaild(username) && checkVaild(passwd)){

            pd = ProgressDialog.show(this,"","正在登陆");

            new NetPostConnection(Config.URL_USER_LOGIN, new NetPostConnection.SuccessCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = NET_SUCCESS;
                    handler.sendMessage(msg);
                }
            }, new NetPostConnection.FailCallback() {
                @Override
                public void onFail() {
                    handler.sendEmptyMessage(NET_FAILURE);
                }
            }, new Object[]{
              "user_id",username,"user_password",passwd
            });
        }else{
            Toast.makeText(this,"请填写完整",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkVaild(String username) {
        return !username.equals("");
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NET_SUCCESS:
                    Log.e("TAG",msg.obj.toString());
                    parseJsonResult(msg.obj.toString());
                    pd.cancel();
                    break;
                case NET_FAILURE:
                    Toast.makeText(LoginActivity.this,"网络异常,请重试",Toast.LENGTH_SHORT).show();
                    pd.cancel();
                    break;
            }
        }
    };

    private void parseJsonResult(String s) {
        try {
            JSONObject result = new JSONObject(s);
            int flag = (int) result.get("state");

            if(flag == 0){
                //保存username
                Config.username =  username;

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(this,"信息错误，请重试",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final int NET_SUCCESS = 0x123;
    public static final int NET_FAILURE = 0x110;

    private <T extends View > T  $(int id){
        return (T)findViewById(id);
    }
}
