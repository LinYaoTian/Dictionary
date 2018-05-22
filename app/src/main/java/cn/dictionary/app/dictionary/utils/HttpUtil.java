package cn.dictionary.app.dictionary.utils;


import java.net.MalformedURLException;
import java.net.URL;

import cn.dictionary.app.dictionary.entity.DailySentence;
import cn.dictionary.app.dictionary.https.HttpCallbackListener;
import cn.dictionary.app.dictionary.https.HttpCallbackListenerForImage;
import cn.dictionary.app.dictionary.https.HttpDailySentenceAsyncTask;
import cn.dictionary.app.dictionary.https.HttpDailyImageAsyncTask;
import cn.dictionary.app.dictionary.https.HttpWordVoiceAsyncTask;
import cn.dictionary.app.dictionary.https.HttpWordAsyncTask;

/**
 * 获取网络请求的工具类
 */
public class HttpUtil {

    private URL murl;
    private static HttpUtil httpUtil;

    /**
     * 私有化构造器
     */
    private HttpUtil() {

    }

    /**
     * //获得HttpUtil的单实例方法
     *
     * @return HttpUtil
     */
    public static HttpUtil getHttp() {
        //双重检验锁
        if (httpUtil == null) {
            synchronized (HttpUtil.class) {
                if (null == httpUtil) {
                    httpUtil = new HttpUtil();
                }
            }
        }
        return httpUtil;
    }

    /**
     * 发送查询单词的请求到异步任务中处理耗时操作
     *
     * @param path
     * @param input
     * @param listen
     */
    public void sendHttpRequestForWord(String path, String input, HttpCallbackListener listen) {
        if (null != input) {
            StringBuilder sb = new StringBuilder();
            sb.append(path).append(input);
            try {
                murl = new URL(sb.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        new HttpWordAsyncTask(listen).execute(murl);
    }

    /**
     * 发送获取每日一句的请求到异步任务中处理耗时操作
     *
     * @param path
     * @param listen
     */
    public void sendHttpRequestForDailyEnglish(String path, HttpCallbackListener listen) {

        StringBuilder sb = new StringBuilder();
        sb.append(path);
        try {
            murl = new URL(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new HttpDailySentenceAsyncTask(listen).execute(murl);
    }

    /**
     * 发送获取图片的请求到异步任务中处理耗时操作
     *
     * @param dailySentence
     * @param listener
     */
    public void sendHttpRequestForImage(DailySentence dailySentence, HttpCallbackListenerForImage listener) {
        StringBuilder sb = new StringBuilder();
        sb.append(dailySentence.getPicturePath());
        try {
            murl = new URL(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new HttpDailyImageAsyncTask(dailySentence, listener).execute(murl);
    }

    /**
     * 发送获取网络音频请求到一部任务中
     *
     * @param path 音频的路径
     * @param name 音频的名字
     */
    public void sendHttpRequestForVoice(String name, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        try {
            murl = new URL(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new HttpWordVoiceAsyncTask(name).execute(murl);
    }


}
