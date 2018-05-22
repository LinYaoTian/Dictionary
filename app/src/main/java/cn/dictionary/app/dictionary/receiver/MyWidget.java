package cn.dictionary.app.dictionary.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.ui.WidgetActivity;

/**
 * 桌面小部件
 */
public class MyWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, WidgetActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_my_widget);
        remoteViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds[0], remoteViews);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}

