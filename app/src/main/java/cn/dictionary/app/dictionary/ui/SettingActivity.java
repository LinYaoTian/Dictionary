package cn.dictionary.app.dictionary.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import cn.dictionary.app.dictionary.config.Broadcast;
import cn.dictionary.app.dictionary.config.SharedPreference;
import cn.dictionary.app.dictionary.service.NotificationService;
import cn.dictionary.app.dictionary.R;

public class SettingActivity extends AppCompatActivity {

    private Switch sNotificationSwitch;//通知栏开关
    private SharedPreferences.Editor editor;//保存的用户设置
    private SharedPreferences pref;//读取用户数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
        initEvents();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        sNotificationSwitch = (Switch) findViewById(R.id.switch_notification);
        pref = getSharedPreferences(SharedPreference.FILENAME, MODE_PRIVATE);
        //读取设置
        boolean check = pref.getBoolean(SharedPreference.NOTIFICATION, false);
        if (check) {
            sNotificationSwitch.setChecked(check);
        }
    }

    /*
     * 初始化事件
     */
    private void initEvents() {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (sNotificationSwitch.isChecked()) {
                        //发送打开通知栏查词的广播
                        Intent intent = new Intent(Broadcast.STARY_NOTIFICATION);
                        sendBroadcast(intent);
                    }
                    //保存设置
                    editor = getSharedPreferences(SharedPreference.FILENAME, MODE_PRIVATE).edit();
                    editor.putBoolean(SharedPreference.NOTIFICATION, true);
                    editor.apply();
                } else {
                    //关闭通知栏查词
                    Intent stopIntent = new Intent(SettingActivity.this, NotificationService.class);
                    stopService(stopIntent);
                    //保存设置
                    editor = getSharedPreferences(SharedPreference.FILENAME, MODE_PRIVATE).edit();
                    editor.putBoolean(SharedPreference.NOTIFICATION, false);
                    editor.apply();
                }
            }
        };
        sNotificationSwitch.setOnCheckedChangeListener(listener);
    }


}

