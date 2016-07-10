package com.example.chen.freeparkingusers.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dell-pc on 2015/12/27.
 */
public class Config {

    public static String username = null;

    public static final String URL_USER_LOGIN = "http://139.129.24.127/parking_app/User/user_login.php";
    public static final String URL_USER_GET_SELLER_INFO = "http://139.129.24.127/parking_app/User/user_searchActivityFromsellers.php";
    public static final String URL_SELLER_GET_SELLER_INFO = "http://139.129.24.127/parking_app/Seller/seller_getseller.php";
    public static final String URL_SELLER_SEARCH = "http://139.129.24.127/parking_app/User/user_searchactivity.php";
    public static final String URL_GET_ALLTICKETS = "http://139.129.24.127/parking_app/User/user_getAllTickets.php";
    public static final String URL_GET_TOKENS = "http://139.129.24.127/parking_app/Qiniu/GetToken.php";
    public static final String URL_GET_USERINFO = "http://139.129.24.127/parking_app/User/user_getinformation.php";


    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }



}
