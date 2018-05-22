package cn.dictionary.app.dictionary.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cn.dictionary.app.dictionary.application.MyApplication;

public class NetWorkUtil {

    /**
     * 判断是否有可用网络
     *
     * @return true, 有网络，false,无网络
     */
    public static boolean hasNetwork() {
        //判断是否有网络
        ConnectivityManager connectionManager = (ConnectivityManager) MyApplication.getContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectionManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }
}
