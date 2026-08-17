package com.virtualapplications.play;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

final class DeviceTelemetry {
    static final class BatteryState {
        final int percent;
        final float temperatureC;
        final boolean charging;
        BatteryState(int percent, float temperatureC, boolean charging) {
            this.percent = percent;
            this.temperatureC = temperatureC;
            this.charging = charging;
        }
    }

    private DeviceTelemetry() { }

    static BatteryState readBattery(Context context) {
        int percent = -1;
        float temperatureC = Float.NaN;
        boolean charging = false;
        try {
            Intent battery = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (battery != null) {
                int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) percent = Math.max(0, Math.min(100, Math.round(level * 100f / scale)));
                int raw = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Integer.MIN_VALUE);
                if (raw != Integer.MIN_VALUE) temperatureC = raw / 10f;
                int status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            }
        } catch (Throwable ignored) { }
        return new BatteryState(percent, temperatureC, charging);
    }

    static float readTemperatureC(float fallback) {
        File root = new File("/sys/class/thermal");
        File[] zones = root.listFiles(file -> file != null && file.getName().startsWith("thermal_zone"));
        float best = Float.NaN;
        int bestPriority = -1;
        if (zones != null) {
            for (File zone : zones) {
                try {
                    String type = readLine(new File(zone, "type")).toLowerCase(Locale.ROOT);
                    int priority = priority(type);
                    if (priority < 0) continue;
                    float value = normalize(readLine(new File(zone, "temp")));
                    if (value < 0f || value > 125f) continue;
                    if (priority > bestPriority || (priority == bestPriority && (Float.isNaN(best) || value > best))) {
                        bestPriority = priority;
                        best = value;
                    }
                } catch (Throwable ignored) { }
            }
        }
        return Float.isNaN(best) ? fallback : best;
    }

    private static int priority(String type) {
        if (type.contains("cpu") || type.contains("soc") || type.contains("cluster") || type.contains("tsens") || type.contains("gpu")) return 3;
        if (type.contains("skin") || type.contains("shell")) return 2;
        if (type.contains("battery") || type.contains("batt")) return 1;
        return -1;
    }

    private static float normalize(String raw) {
        double value = Double.parseDouble(raw.trim());
        double a = Math.abs(value);
        if (a >= 1000d) value /= 1000d;
        else if (a > 200d) value /= 10d;
        return (float)value;
    }

    private static String readLine(File file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String value = reader.readLine();
            if (value == null) throw new IllegalStateException("empty sensor");
            return value.trim();
        }
    }
}
