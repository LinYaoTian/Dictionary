package cn.dictionary.app.dictionary.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import cn.dictionary.app.dictionary.config.Broadcast;
import cn.dictionary.app.dictionary.config.SharedPreference;


public class SettingIntentService extends IntentService {

    private SharedPreferences pref;//读取用户数据

    public SettingIntentService() {
        super("SettingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //通知栏开关
        pref = getSharedPreferences(SharedPreference.FILENAME, MODE_PRIVATE);
        boolean check = pref.getBoolean(SharedPreference.NOTIFICATION, false);
        if (check) {
            //发送打开通知栏查词的广播
            Intent intent_1 = new Intent(Broadcast.STARY_NOTIFICATION);
            sendBroadcast(intent_1);
        } else {
            //关闭通知栏查词
            Intent stopIntent = new Intent(SettingIntentService.this, NotificationService.class);
            stopService(stopIntent);
        }

    }
}
