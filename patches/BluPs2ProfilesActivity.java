package com.virtualapplications.play;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class BluPs2ProfilesActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(28), dp(28), dp(28), dp(28));
        root.setBackgroundColor(Color.rgb(3, 12, 28));
        TextView title = row("BluPS2 Profiles", 28);
        root.addView(title);
        root.addView(row("Macca\nMain profile", 20));
        root.addView(row("Guest\nTemporary profile", 20));
        root.addView(row("Kids\nFamily profile", 20));
        setContentView(root);
    }

    private TextView row(String value, int size) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(Color.WHITE);
        v.setGravity(Gravity.CENTER_VERTICAL);
        v.setPadding(dp(18), dp(16), dp(18), dp(16));
        v.setBackgroundColor(Color.rgb(8, 27, 54));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(6), 0, dp(6));
        v.setLayoutParams(lp);
        return v;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
