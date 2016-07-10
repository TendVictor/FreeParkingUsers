package com.example.chen.freeparkingusers.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.net.NetPostConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chen on 16/7/10.
 */
public class ModifyPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText ev_oldPassword;
    private EditText ev_newPassword;
    private EditText ev_surePassword;

    private boolean isSurePassword = false;

    private ImageView iv_back_modifyPassword;

    private Button modifyPassword;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x0://修改成功
                    Toast.makeText(ModifyPasswordActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x1://修改失败
                    Toast.makeText(ModifyPasswordActivity.this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
                    break;
                case 0x2://网络原因
                    Toast.makeText(ModifyPasswordActivity.this,"网络原因。。。",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypassword);
        initView();
    }


    private void initView() {
        ev_oldPassword = (EditText) findViewById(R.id.ev_oldpassword);
        ev_newPassword = (EditText) findViewById(R.id.ev_newpassword);
        ev_surePassword = (EditText) findViewById(R.id.ev_surepassword);

        iv_back_modifyPassword = (ImageView) findViewById(R.id.iv_back_modifypassword);

        modifyPassword = (Button) findViewById(R.id.btn_modifypassword);

        ev_surePassword.addTextChangedListener(new TextWatcher() {
            String newPw = null, surePw = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                surePw = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                newPw = ev_surePassword.getText().toString();
                if (!newPw.equalsIgnoreCase(surePw)) {
                    Toast.makeText(ModifyPasswordActivity.this,"两次密码不匹配",Toast.LENGTH_SHORT).show();
                    isSurePassword = false;
                }else{
                    isSurePassword = true;
                }
            }
        });

        iv_back_modifyPassword.setOnClickListener(this);
        modifyPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back_modifypassword://返回个人中心
                finish();
                break;
            case R.id.btn_modifypassword: //确认修改密码
                modifyPassword();
                break;
        }
    }

    //检查密码 且确认修改
    private void modifyPassword() {
        if(isSurePassword){

            String oldPassword = ev_oldPassword.getText().toString();
            String newPassword = ev_surePassword.getText().toString();

            new NetPostConnection(Config.URL_MODIFY_PASSWORD, new NetPostConnection.SuccessCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    JSONObject object= new JSONObject(result);
                    int res = object.getInt("state");
                    if(res == 0){
                        handler.obtainMessage(0x0).sendToTarget();
                    }else{
                        handler.obtainMessage(0x1).sendToTarget();
                    }
                }
            }, new NetPostConnection.FailCallback() {
                @Override
                public void onFail() {
                    handler.obtainMessage(0x2).sendToTarget();
                }
            },"user_id",Config.username,"user_oldpassword",oldPassword,"user_newpassword",newPassword);
        }
    }
}
