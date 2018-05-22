package cn.dictionary.app.dictionary.view;


import android.app.Activity;

import cn.dictionary.app.dictionary.ui.WidgetActivity;

/**
 * 桌面查词结果
 */

public class WidgetResultFrament extends ResultFragment {
    @Override
    public Activity getCurrentActivity() {
        WidgetActivity activity = (WidgetActivity) getActivity();
        return activity;
    }
}
