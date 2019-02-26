package com.esgi.androidPassword;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class KeepSecureAppWidget extends AppWidgetProvider {
    private static final String APPWIDGET = "APPWIDGET";


    public static void updateWidget(Context context,
                                    AppWidgetManager appWidgetManager,
                                    int appWidgetId,
                                    String text) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.keep_secure_app_widget);
        remoteViews.setTextViewText(R.id.textView, text);

        Intent intent = new Intent(context, KeepSecureConfigureActivity.class); // UPDATE_DATABASE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(APPWIDGET, "onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, "UPDATED");
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.i(APPWIDGET, "onDeleted");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i(APPWIDGET, "onDisabled");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i(APPWIDGET, "onEnabled");
    }
}

