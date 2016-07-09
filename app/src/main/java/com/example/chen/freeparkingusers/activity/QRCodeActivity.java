package com.example.chen.freeparkingusers.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

public class QRCodeActivity extends AppCompatActivity {

    private static  final String TAG = "QRCAct";
    private String ticket_id = null;
    private String seller_name = null;
    private String ticket_deadline = null;

    private TextView tvAddress = null;
    private TextView tvDeadline = null;
    private ImageView ivQRCode = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ticket_id = getIntent().getStringExtra("ticket_id");
        seller_name = getIntent().getStringExtra("seller_name");
        ticket_deadline = getIntent().getStringExtra("ticket_deadline");
        setContentView(R.layout.activity_qrcode);
        initView();
    }

    private void initView() {
        tvAddress = $(R.id.tv_address_detail);
        tvDeadline = $(R.id.tv_deadline_detail);
        ivQRCode = $(R.id.iv_qrcode);


        tvAddress.setText(seller_name);
        tvDeadline.setText(ticket_deadline);

        ivQRCode.setBackgroundDrawable(null);
        ivQRCode.setImageBitmap(generateQRCode());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Bitmap generateQRCode(){

        Bitmap logoBm = BitmapFactory.decodeResource(getResources(),R.drawable.xing_logo);
        Bitmap qrcode = EncodingUtils.createQRCode(this,ticket_id,230,230,logoBm);
        return qrcode;
    }
    public void exitQrCode(View view){
        this.finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private <T extends View> T $(int id){
        return (T)findViewById(id);
    }
}
