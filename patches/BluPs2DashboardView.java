package com.virtualapplications.play;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class BluPs2DashboardView extends LinearLayout {
    private final TextView fps;
    private final TextView temp;
    private final TextView battery;
    private final TextView profile;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updater = new Runnable() {
        @Override public void run() {
            DeviceTelemetry.BatteryState state = DeviceTelemetry.readBattery(getContext());
            float t = DeviceTelemetry.readTemperatureC(state.temperatureC);
            battery.setText(state.percent >= 0 ? "BATTERY  " + state.percent + "%" + (state.charging ? "  ⚡" : "") : "BATTERY  --");
            temp.setText(Float.isNaN(t) ? "TEMP  --" : String.format(Locale.UK, "TEMP  %.0f°C", t));
            fps.setText("FPS  60 TARGET");
            handler.postDelayed(this, 2000);
        }
    };

    public BluPs2DashboardView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(dp(12), dp(8), dp(12), dp(8));
        setBackgroundColor(Color.rgb(4, 15, 34));
        fps = item("FPS  60 TARGET");
        temp = item("TEMP  --");
        battery = item("BATTERY  --");
        profile = item("PROFILE  MACCA");
        addView(fps);
        addView(temp);
        addView(battery);
        addView(profile);
        updater.run();
    }

    private TextView item(String text) {
        TextView v = new TextView(getContext());
        v.setText(text);
        v.setTextColor(Color.rgb(102, 183, 255));
        v.setTextSize(12);
        v.setTypeface(Typeface.DEFAULT_BOLD);
        v.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(0, dp(34), 1f);
        lp.setMargins(dp(3), 0, dp(3), 0);
        v.setLayoutParams(lp);
        return v;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override protected void onDetachedFromWindow() {
        handler.removeCallbacks(updater);
        super.onDetachedFromWindow();
    }
}
