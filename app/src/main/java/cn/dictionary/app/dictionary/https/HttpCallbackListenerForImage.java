package cn.dictionary.app.dictionary.https;


import android.graphics.Bitmap;

import java.io.InputStream;

import cn.dictionary.app.dictionary.entity.DailySentence;

/**
 * 对获取图片网络请求的失败或成功进行相应的逻辑处理
 */

public interface HttpCallbackListenerForImage {

    void onFinish(DailySentence dailySentence);

    void onError(int resultCode);
}
