package cn.dictionary.app.dictionary.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.ui.WidgetActivity;

public class NotificationService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intent1 = new Intent(this, WidgetActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("点击进入快速查词")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.iv_widget)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.dictionary))
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
