package com.gold.egyptrates;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GoldWidgetProvider extends AppWidgetProvider {
    private static final String DATA_URL =
        "https://souq-el-dahb-default-rtdb.firebaseio.com/Market_Price.json";

    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) {
            RemoteViews loading = new RemoteViews(context.getPackageName(), R.layout.gold_widget);
            loading.setTextViewText(R.id.status, "جاري تحديث الأسعار...");
            manager.updateAppWidget(id, loading);
            fetchAndUpdate(context, manager, id);
        }
    }

    private void fetchAndUpdate(Context context, AppWidgetManager manager, int widgetId) {
        new Thread(() -> {
            String buy = "--", sell = "--", stamp = "تعذر تحديث البيانات";
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(DATA_URL).openConnection();
                c.setRequestMethod("GET");
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                c.setRequestProperty("Accept", "application/json");
                int code = c.getResponseCode();
                if (code != 200) throw new Exception("HTTP " + code);
                BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) body.append(line);
                r.close();
                c.disconnect();

                JSONObject data = new JSONObject(body.toString());
                buy = data.optString("buy_price_21k", "--");
                sell = data.optString("sell_price_21k", "--");
                long updated = data.optLong("update_time", data.optLong("updatedAt", 0));
                if (updated > 0) {
                    if (updated < 1000000000000L) updated *= 1000L;
                    stamp = "آخر تحديث: " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(updated));
                } else {
                    stamp = "تم جلب السعر من المصدر";
                }
            } catch (Exception e) {
                stamp = "فشل الاتصال بالمصدر";
            }

            final String finalBuy = buy;
            final String finalSell = sell;
            final String finalStamp = stamp;
            new Handler(Looper.getMainLooper()).post(() -> {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gold_widget);
                views.setTextViewText(R.id.buy_price, finalBuy + " ج");
                views.setTextViewText(R.id.sell_price, finalSell + " ج");
                views.setTextViewText(R.id.status, finalStamp);
                Intent launch = new Intent(context, MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(context, 0, launch,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.widget_root, pi);
                manager.updateAppWidget(widgetId, views);
            });
        }).start();
    }
}
