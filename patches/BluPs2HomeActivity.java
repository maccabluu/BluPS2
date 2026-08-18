package com.virtualapplications.play;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class BluPs2HomeActivity extends Activity {
    private static final int BG = Color.rgb(2, 10, 22);
    private static final int PANEL = Color.rgb(7, 22, 42);
    private static final int PANEL_2 = Color.rgb(10, 31, 57);
    private static final int BLUE = Color.rgb(0, 132, 255);
    private static final int MUTED = Color.rgb(160, 177, 198);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        setTitle("BluPS2");
        setContentView(buildUi());
        BluPs2UpdateChecker.automatic(this);
    }

    private View buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(10), dp(16), dp(16));
        root.setBackgroundColor(BG);
        scroll.addView(root, new ScrollView.LayoutParams(-1, -2));

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        TextView logo = text("BluPS2", 27, Color.WHITE, true);
        header.addView(logo, new LinearLayout.LayoutParams(0, dp(48), 1f));
        header.addView(action("⌕", v -> openLibrary()));
        header.addView(action("▦", v -> openTools()));
        header.addView(action("+", v -> openLibrary()));
        root.addView(header, new LinearLayout.LayoutParams(-1, dp(48)));

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setGravity(Gravity.CENTER_VERTICAL);
        tabs.addView(tab("All Games", true));
        tabs.addView(tab("Recent", false));
        tabs.addView(tab("Favorites", false));
        root.addView(tabs, new LinearLayout.LayoutParams(-1, dp(44)));

        HorizontalScrollView covers = new HorizontalScrollView(this);
        covers.setHorizontalScrollBarEnabled(false);
        LinearLayout coverRow = new LinearLayout(this);
        coverRow.setOrientation(LinearLayout.HORIZONTAL);
        coverRow.setPadding(0, dp(8), 0, dp(8));
        coverRow.addView(gameCard("Add PS2 games", "ISO, BIN, CSO"));
        coverRow.addView(gameCard("Recently played", "Latest titles"));
        coverRow.addView(gameCard("Favorites", "Pinned titles"));
        coverRow.addView(gameCard("Homebrew", "ELF titles"));
        covers.addView(coverRow);
        root.addView(covers, new LinearLayout.LayoutParams(-1, dp(202)));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        Button add = button("Add Games");
        add.setOnClickListener(v -> openLibrary());
        Button tools = button("Performance Hub");
        tools.setOnClickListener(v -> openTools());
        actions.addView(add, new LinearLayout.LayoutParams(0, dp(46), 1f));
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(0, dp(46), 1f);
        rp.setMargins(dp(8), 0, 0, 0);
        actions.addView(tools, rp);
        root.addView(actions, new LinearLayout.LayoutParams(-1, dp(46)));

        DeviceTelemetry.BatteryState battery = DeviceTelemetry.readBattery(this);
        float temp = DeviceTelemetry.readTemperatureC(battery.temperatureC);
        int target = getSharedPreferences("blups2_tools", MODE_PRIVATE).getInt("target_fps", 60);
        String batteryText = battery.percent < 0 ? "?" : battery.percent + "%";
        String tempText = Float.isNaN(temp) ? "?" : String.format(java.util.Locale.UK, "%.1f°C", temp);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setPadding(0, dp(10), 0, dp(8));
        stats.addView(stat("TARGET", target + " FPS"), statParams());
        stats.addView(stat("TEMP", tempText), statParams());
        stats.addView(stat("BATTERY", batteryText), statParams());
        stats.addView(stat("PROFILE", "MACCA"), statParams());
        root.addView(stats, new LinearLayout.LayoutParams(-1, dp(88)));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.addView(nav("Library", true, v -> openLibrary()));
        nav.addView(nav("Tools", false, v -> openTools()));
        nav.addView(nav("Settings", false, v -> startActivity(new Intent(this, BluPs2AppSettingsActivity.class))));
        nav.addView(nav("Profiles", false, v -> startActivity(new Intent(this, BluPs2ProfilesActivity.class))));
        root.addView(nav, new LinearLayout.LayoutParams(-1, dp(50)));

        return scroll;
    }

    private LinearLayout.LayoutParams statParams() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(70), 1f);
        lp.setMargins(dp(3), 0, dp(3), 0);
        return lp;
    }

    private void openLibrary() { startActivity(new Intent(this, BluPs2LibraryActivity.class)); }
    private void openTools() { startActivity(new Intent(this, BluPs2ToolsActivity.class)); }

    private View gameCard(String title, String sub) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(12), dp(10), dp(12), dp(10));
        box.setBackground(round(PANEL_2, dp(12), Color.rgb(25, 70, 115)));

        TextView art = text("PS2", 28, BLUE, true);
        art.setGravity(Gravity.CENTER);
        box.addView(art, new LinearLayout.LayoutParams(-1, dp(105)));

        TextView titleView = text(title, 13, Color.WHITE, true);
        titleView.setMaxLines(1);
        box.addView(titleView, new LinearLayout.LayoutParams(-1, dp(28)));

        TextView subView = text(sub, 10, MUTED, false);
        subView.setMaxLines(1);
        box.addView(subView, new LinearLayout.LayoutParams(-1, dp(24)));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(165), dp(176));
        lp.setMargins(0, 0, dp(9), 0);
        box.setLayoutParams(lp);
        box.setOnClickListener(v -> openLibrary());
        return box;
    }

    private TextView stat(String label, String value) {
        TextView v = text(label + "\n" + value, 12, Color.WHITE, true);
        v.setGravity(Gravity.CENTER);
        v.setMaxLines(2);
        v.setBackground(round(PANEL, dp(10), Color.rgb(28, 66, 103)));
        return v;
    }

    private TextView tab(String label, boolean active) {
        TextView v = text(label, 12, active ? Color.WHITE : MUTED, active);
        v.setGravity(Gravity.CENTER);
        v.setPadding(dp(14), dp(6), dp(14), dp(6));
        if(active) v.setBackground(round(Color.rgb(0, 67, 135), dp(8), BLUE));
        return v;
    }

    private TextView nav(String label, boolean active, View.OnClickListener listener) {
        TextView v = text(label, 11, active ? BLUE : MUTED, active);
        v.setGravity(Gravity.CENTER);
        v.setOnClickListener(listener);
        v.setPadding(dp(4), dp(8), dp(4), dp(8));
        v.setLayoutParams(new LinearLayout.LayoutParams(0, dp(48), 1f));
        return v;
    }

    private TextView action(String label, View.OnClickListener listener) {
        TextView v = text(label, 22, Color.WHITE, false);
        v.setGravity(Gravity.CENTER);
        v.setPadding(dp(7), 0, dp(7), 0);
        v.setOnClickListener(listener);
        return v;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(12);
        b.setAllCaps(false);
        b.setPadding(dp(6), 0, dp(6), 0);
        b.setBackground(round(PANEL_2, dp(10), Color.rgb(29, 72, 116)));
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
