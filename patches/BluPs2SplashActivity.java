package com.virtualapplications.play;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class BluPs2SplashActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.rgb(0, 7, 18), Color.BLACK});
        root.setBackground(bg);

        TextView logo = text("BluPS2", 54, Color.WHITE, true);
        root.addView(logo);
        TextView sub = text("P L A Y S T A T I O N ® 2   E M U L A T O R", 12, Color.LTGRAY, false);
        sub.setPadding(0, dp(6), 0, dp(24));
        root.addView(sub);

        TextView line = new TextView(this);
        GradientDrawable lineBg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.TRANSPARENT, Color.rgb(0, 120, 255), Color.TRANSPARENT});
        line.setBackground(lineBg);
        root.addView(line, new LinearLayout.LayoutParams(dp(280), dp(2)));

        TextView tag = text("Relive. Play. Blu.", 20, Color.rgb(30, 145, 255), false);
        tag.setPadding(0, dp(36), 0, dp(8));
        root.addView(tag);
        root.addView(text("Your PS2. Anywhere.", 19, Color.WHITE, false));

        setContentView(root);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }, 1800);
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);
        v.setGravity(Gravity.CENTER);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
        return v;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
