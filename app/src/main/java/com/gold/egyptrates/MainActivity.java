package com.gold.egyptrates;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(9,9,9));
        TextView title = new TextView(this);
        title.setText("✦ ذهب مصر");
        title.setTextColor(Color.rgb(242,205,105));
        title.setTextSize(25);
        title.setGravity(Gravity.CENTER);
        title.setPadding(12,20,12,12);
        root.addView(title);
        TextView source = new TextView(this);
        source.setText("مصدر الأسعار: قاعدة بيانات سوق الدهب\nاضغط تحديث داخل صفحة المصدر لمراجعة البيانات الأصلية.");
        source.setTextColor(Color.rgb(220,205,165));
        source.setGravity(Gravity.CENTER);
        source.setPadding(12,8,12,12);
        root.addView(source);
        WebView web = new WebView(this);
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.loadUrl("https://souq-el-dahb-default-rtdb.firebaseio.com/Market_Price.json");
        root.addView(web, new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }
}
