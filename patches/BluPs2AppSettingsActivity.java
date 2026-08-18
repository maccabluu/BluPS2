package com.virtualapplications.play;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class BluPs2AppSettingsActivity extends Activity {
    private static final int BG = Color.rgb(2, 10, 22);
    private static final int PANEL = Color.rgb(8, 28, 52);
    private static final int BLUE = Color.rgb(0, 132, 255);
    private static final int MUTED = Color.rgb(164, 180, 200);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BluPS2 Settings");
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        setContentView(build());
    }

    private ScrollView build() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(16), dp(18), dp(18));
        scroll.addView(root, new ScrollView.LayoutParams(-1, -2));

        root.addView(text("Settings", 28, Color.WHITE, true));
        TextView app = text("App", 18, BLUE, true);
        app.setPadding(0, dp(18), 0, dp(8));
        root.addView(app);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(round(PANEL, dp(12), Color.rgb(27, 76, 126)));
        card.addView(text("BluPS2 1.3", 17, Color.WHITE, true));
        TextView info = text("Automatic update checks run after launch with a 15-minute cooldown.", 13, MUTED, false);
        info.setPadding(0, dp(6), 0, dp(12));
        card.addView(info);

        Button check = button("Check for updates");
        check.setOnClickListener(v -> BluPs2UpdateChecker.manual(this));
        card.addView(check, new LinearLayout.LayoutParams(-1, dp(50)));
        root.addView(card);

        Button emulator = button("Emulator settings");
        emulator.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(50));
        lp.setMargins(0, dp(14), 0, 0);
        root.addView(emulator, lp);
        return scroll;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setGravity(Gravity.CENTER);
        b.setBackground(round(PANEL, dp(10), BLUE));
        return b;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);
        if(bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private GradientDrawable round(int fill, int radius, int stroke) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(radius);
        g.setStroke(dp(1), stroke);
        return g;
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
