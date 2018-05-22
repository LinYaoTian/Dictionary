package cn.dictionary.app.dictionary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.dictionary.app.dictionary.service.NotificationService;

/**
 * 接收打开通知栏查词的广播
 */
public class StartNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent_2 = new Intent(context, NotificationService.class);
        context.startService(intent_2);
    }
}
