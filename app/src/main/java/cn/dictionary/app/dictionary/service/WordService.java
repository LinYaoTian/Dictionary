package cn.dictionary.app.dictionary.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import cn.dictionary.app.dictionary.config.Broadcast;
import cn.dictionary.app.dictionary.config.HttpPath;
import cn.dictionary.app.dictionary.db.SearchRecordDao;
import cn.dictionary.app.dictionary.entity.Words;
import cn.dictionary.app.dictionary.https.HttpCallbackListener;
import cn.dictionary.app.dictionary.utils.HttpUtil;
import cn.dictionary.app.dictionary.application.MyApplication;
import cn.dictionary.app.dictionary.utils.ParserUtil;

public class WordService extends Service {

    public static Words sWord;

    public WordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("input");
        //发起查词网络请求并根据结果判断处理
        HttpUtil.getHttp().sendHttpRequestForWord(HttpPath.PATH_WORD, input, mhttpCallbackListenForWord);
        Toast.makeText(MyApplication.getContext(), "正在联网查词", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    private HttpCallbackListener mhttpCallbackListenForWord = new HttpCallbackListener() {

        @Override
        public void onFinish(String response) {
            //解析数据
            sWord = ParserUtil.ParseJSONForWord(response);
            //若有数据
            if (sWord != null) {
                //保存数据
                SearchRecordDao.getInstance().addWord(sWord);
                //进行音频解析和保存
                String uk_voiceName = "uk_" + sWord.getQuery() + ".mp3";
                String us_voiceName = "us_" + sWord.getQuery() + ".mp3";
                //英式发音
                if (sWord.getUk_speech() != null) {
                    HttpUtil.getHttp().sendHttpRequestForVoice(uk_voiceName, sWord.getUk_speech());
                }
                //美式发音
                if (sWord.getUs_speech() != null) {
                    HttpUtil.getHttp().sendHttpRequestForVoice(us_voiceName, sWord.getUs_speech());
                }
                //发送广播提示成功完成查询单词
                Intent intent = new Intent(Broadcast.WORDSERVICECOMPLETE);
                sendBroadcast(intent);
            } else {
                //提示用户无查询结果
                Intent intent = new Intent(Broadcast.WORDSERVICEISNULL);
                sendBroadcast(intent);
            }
        }

        @Override
        public void onError(int resultCode) {
            Toast.makeText(MyApplication.getContext(), "获取单词错误：" + resultCode, Toast.LENGTH_SHORT).show();
        }
    };
}
