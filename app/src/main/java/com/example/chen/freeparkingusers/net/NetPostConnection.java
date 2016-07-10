package com.example.chen.freeparkingusers.net;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Post方式的网络连接基类
 */
public class NetPostConnection {

    public NetPostConnection(final String url, final SuccessCallback successCallback, final FailCallback failCallback, final Object... kvs) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... arg0) {
                StringBuffer paramsStr = new StringBuffer();
                for (int i = 0; i < kvs.length; i += 2) {
                    paramsStr.append(kvs[i]).append("=").append(kvs[i + 1]).append("&");
                }

                //利用POST方法访问
                try {
                    URLConnection uc;
                    uc = new URL(url).openConnection();
                    uc.setConnectTimeout(5000);
                    uc.setDoOutput(true);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(uc.getOutputStream(), "utf-8"));
                    bw.write(paramsStr.toString());
                    bw.flush();

                    System.out.println("Request url:" + uc.getURL());
                    System.out.println("Request data:" + paramsStr);

                    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream(), "utf-8"));
                    String line = null;
                    // StringBuffer result = new StringBuffer();
                    StringBuilder result = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }

                    System.out.println("Result:" + result);
                    return result.toString();

                } catch (MalformedURLException e) {
                    failCallback.onFail();
                    e.printStackTrace();
                } catch (IOException e) {
                    failCallback.onFail();
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                if (result != null) {
                    if (successCallback != null) {
                        try {
                            successCallback.onSuccess(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (failCallback != null) {
                        failCallback.onFail();
                    }
                }

                super.onPostExecute(result);
            }
        }.execute();

    }


    public static interface SuccessCallback {
        void onSuccess(String result) throws JSONException;
    }

    public static interface FailCallback {
        void onFail();
    }
}
