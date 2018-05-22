package cn.dictionary.app.dictionary.https;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.dictionary.app.dictionary.entity.DailySentence;

/**
 * 从网络中加载和处理图片以及显示图片
 */

public class HttpDailyImageAsyncTask extends AsyncTask<URL, Void, DailySentence> {

    private int mresultCode;//服务器返回的状态码

    private HttpCallbackListenerForImage mHttpCallbackListenerForImage;
    private DailySentence mDailySentence;
    private String imgName;//图片名

    public HttpDailyImageAsyncTask(DailySentence dailySentence, HttpCallbackListenerForImage listener) {
        mHttpCallbackListenerForImage = listener;
        mDailySentence = dailySentence;
        imgName = dailySentence.getDateline() + ".png";
    }

    @Override
    protected DailySentence doInBackground(URL... urls) {
        InputStream in = null;
        HttpURLConnection conn = null;
        Bitmap bitmap = null;
        try {
            conn = (HttpURLConnection) urls[0].openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(8000);
            conn.setConnectTimeout(8000);
            mresultCode = conn.getResponseCode();
            if (mresultCode == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                mDailySentence.setPicture(bitmap);
                return mDailySentence;
            } else {
                conn.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(DailySentence dailySentence) {
        if (dailySentence != null) {
            mHttpCallbackListenerForImage.onFinish(dailySentence);
        } else {
            mHttpCallbackListenerForImage.onError(mresultCode);
        }

    }

}
