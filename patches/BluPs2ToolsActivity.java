package com.virtualapplications.play;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class BluPs2ToolsActivity extends Activity {
    private static final int BG = Color.rgb(2, 10, 22);
    private static final int PANEL = Color.rgb(8, 27, 50);
    private static final int BLUE = Color.rgb(0, 132, 255);
    private static final int MUTED = Color.rgb(158, 177, 200);
    private static final String PREFS = "blups2_tools";
    private TextView telemetry;
    private final Runnable refreshTask = new Runnable() {
        @Override public void run() {
            refreshTelemetry();
            if (telemetry != null) telemetry.postDelayed(this, 1500);
        }
    };

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        setContentView(buildUi());
    }

    @Override protected void onResume() {
        super.onResume();
        if (telemetry != null) telemetry.post(refreshTask);
    }

    @Override protected void onPause() {
        if (telemetry != null) telemetry.removeCallbacks(refreshTask);
        super.onPause();
    }

    private View buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(18), dp(20), dp(24));
        scroll.addView(root);

        root.addView(label("BluPS2 Performance Hub", 26, Color.WHITE, true));
        root.addView(label("Xbox-style device, controller and library shortcuts", 12, MUTED, false));

        LinearLayout device = card();
        device.addView(label("DEVICE MONITOR", 12, BLUE, true));
        telemetry = label("Reading device...", 16, Color.WHITE, true);
        telemetry.setPadding(0, dp(8), 0, 0);
        device.addView(telemetry);
        device.addView(label("Smart Heat Guard reports Android thermal pressure. PS2 speed still depends on the Play core and each game.", 11, MUTED, false));
        root.addView(device, marginTop(12));

        LinearLayout performance = card();
        performance.addView(label("PERFORMANCE PRESET", 12, BLUE, true));
        performance.addView(label("Save your preferred BluPS2 target for the dashboard.", 11, MUTED, false));
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Button balanced = button("Balanced 30");
        balanced.setOnClickListener(v -> saveTarget(30));
        Button smooth = button("Smooth 60");
        smooth.setOnClickListener(v -> saveTarget(60));
        row.addView(balanced, new LinearLayout.LayoutParams(0, dp(50), 1f));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, dp(50), 1f);
        sp.setMargins(dp(8), 0, 0, 0);
        row.addView(smooth, sp);
        performance.addView(row, marginTop(10));
        root.addView(performance, marginTop(12));

        LinearLayout controller = card();
        controller.addView(label("CONTROLLER", 12, BLUE, true));
        controller.addView(label("Open Play controller settings or test controller vibration.", 11, MUTED, false));
        Button settings = button("Controller Settings");
        settings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        controller.addView(settings, marginTop(10));
        Button haptic = button("Test Haptics");
        haptic.setOnClickListener(v -> testHaptics());
        controller.addView(haptic, marginTop(8));
        root.addView(controller, marginTop(12));

        LinearLayout library = card();
        library.addView(label("LIBRARY & SAVES", 12, BLUE, true));
        library.addView(label("Jump straight to the Play library. Game images and save data stay handled by the PS2 core.", 11, MUTED, false));
        Button games = button("Open Game Library");
        games.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        library.addView(games, marginTop(10));
        Button profiles = button("Profiles");
        profiles.setOnClickListener(v -> startActivity(new Intent(this, BluPs2ProfilesActivity.class)));
        library.addView(profiles, marginTop(8));
        root.addView(library, marginTop(12));

        Button back = button("Back to BluPS2 Home");
        back.setOnClickListener(v -> finish());
        root.addView(back, marginTop(14));
        return scroll;
    }

    private void refreshTelemetry() {
        if (telemetry == null) return;
        DeviceTelemetry.BatteryState b = DeviceTelemetry.readBattery(this);
        float temp = DeviceTelemetry.readTemperatureC(b.temperatureC);
        String battery = b.percent < 0 ? "?" : b.percent + "%";
        String tempText = Float.isNaN(temp) ? "?" : String.format(java.util.Locale.UK, "%.1f°C", temp);
        int thermal = thermalStatus();
        String thermalText = thermalName(thermal);
        int target = getSharedPreferences(PREFS, MODE_PRIVATE).getInt("target_fps", 60);
        telemetry.setText("Target " + target + " FPS   •   Battery " + battery + (b.charging ? " ⚡" : "") + "\nTemperature " + tempText + "   •   Heat Guard " + thermalText);
    }

    private int thermalStatus() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return -1;
        try {
            PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
            return pm == null ? -1 : pm.getCurrentThermalStatus();
        } catch (Throwable ignored) {
            return -1;
        }
    }

    private String thermalName(int status) {
        if (status < 0) return "Unavailable";
        switch (status) {
            case PowerManager.THERMAL_STATUS_NONE: return "Cool";
            case PowerManager.THERMAL_STATUS_LIGHT: return "Light";
            case PowerManager.THERMAL_STATUS_MODERATE: return "Moderate";
            case PowerManager.THERMAL_STATUS_SEVERE: return "Severe";
            case PowerManager.THERMAL_STATUS_CRITICAL: return "Critical";
            case PowerManager.THERMAL_STATUS_EMERGENCY: return "Emergency";
            case PowerManager.THERMAL_STATUS_SHUTDOWN: return "Shutdown";
            default: return "Unknown";
        }
    }

    private void saveTarget(int fps) {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putInt("target_fps", fps).apply();
        refreshTelemetry();
    }

    private void testHaptics() {
        try {
            Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            if (vibrator == null || !vibrator.hasVibrator()) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE));
            else vibrator.vibrate(120);
        } catch (Throwable ignored) { }
    }

    private LinearLayout card() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(14), dp(16), dp(14));
        box.setBackground(round(PANEL, dp(12), Color.rgb(29, 71, 113)));
        return box;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setBackground(round(Color.rgb(10, 47, 83), dp(9), Color.rgb(25, 93, 150)));
        return b;
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private LinearLayout.LayoutParams marginTop(int top) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(top);
        return p;
    }

    private GradientDrawable round(int fill, int radius, int stroke) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(radius);
        g.setStroke(dp(1), stroke);
        return g;
    }

    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }
}
