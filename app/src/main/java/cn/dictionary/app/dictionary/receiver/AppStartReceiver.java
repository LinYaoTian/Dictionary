package cn.dictionary.app.dictionary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.dictionary.app.dictionary.application.MyApplication;
import cn.dictionary.app.dictionary.service.SettingIntentService;

public class AppStartReceiver extends BroadcastReceiver {
    public AppStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(MyApplication.getContext(), SettingIntentService.class);
        context.startService(intent1);
    }
}
