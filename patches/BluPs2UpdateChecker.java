package com.virtualapplications.play;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BluPs2UpdateChecker {
    private static final String CURRENT = "1.3";
    private static final String API = "https://api.github.com/repos/maccabluu/BluPS2/releases/latest";
    private static final long COOLDOWN = 15L * 60L * 1000L;
    private static final Pattern VERSION = Pattern.compile("^v?(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:-public-alpha)?$", Pattern.CASE_INSENSITIVE);

    private BluPs2UpdateChecker() {}

    static void automatic(Activity a) {
        SharedPreferences p = a.getSharedPreferences("blups2_updates", Context.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        long last = p.getLong("last_check", 0L);
        if(now - last < COOLDOWN) return;
        p.edit().putLong("last_check", now).apply();
        check(a, false);
    }

    static void manual(Activity a) { check(a, true); }

    private static void check(Activity a, boolean manual) {
        new Thread(() -> {
            try {
                HttpURLConnection c = (HttpURLConnection)new URL(API).openConnection();
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                c.setRequestProperty("Accept", "application/vnd.github+json");
                c.setRequestProperty("User-Agent", "BluPS2-Updater");
                if(c.getResponseCode() != 200) throw new Exception("GitHub HTTP " + c.getResponseCode());
                BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder b = new StringBuilder();
                String line;
                while((line = r.readLine()) != null) b.append(line);
                r.close();
                JSONObject release = new JSONObject(b.toString());
                if(release.optBoolean("draft", false)) return;
                String tag = release.optString("tag_name", "");
                if(!VERSION.matcher(tag).matches() || compare(tag, CURRENT) <= 0) {
                    if(manual) toast(a, "BluPS2 1.3 is up to date.");
                    return;
                }
                String notes = release.optString("body", "No release notes supplied.");
                String page = release.optString("html_url", "https://github.com/maccabluu/BluPS2/releases/latest");
                JSONArray assets = release.optJSONArray("assets");
                String apk = null;
                if(assets != null) for(int i = 0; i < assets.length(); i++) {
                    JSONObject x = assets.getJSONObject(i);
                    if(x.optString("name", "").toLowerCase().endsWith(".apk")) {
                        apk = x.optString("browser_download_url", null);
                        break;
                    }
                }
                String finalApk = apk;
                run(() -> new AlertDialog.Builder(a)
                        .setTitle("BluPS2 update available")
                        .setMessage(tag)
                        .setPositiveButton("Update now", (d,w) -> {
                            Uri u = Uri.parse(finalApk == null ? page : finalApk);
                            a.startActivity(new Intent(Intent.ACTION_VIEW, u));
                        })
                        .setNeutralButton("What's new", (d,w) -> new AlertDialog.Builder(a).setTitle(tag).setMessage(notes).setPositiveButton("OK", null).show())
                        .setNegativeButton("Later", null)
                        .show());
            } catch(Exception e) {
                if(manual) toast(a, "Update check failed: " + e.getMessage());
            }
        }).start();
    }

    private static int compare(String a, String b) {
        int[] x = parts(a), y = parts(b);
        for(int i = 0; i < 3; i++) if(x[i] != y[i]) return Integer.compare(x[i], y[i]);
        return 0;
    }

    private static int[] parts(String s) {
        Matcher m = VERSION.matcher(s);
        if(!m.matches()) m = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+))?$").matcher(s);
        if(!m.matches()) return new int[]{0,0,0};
        return new int[]{Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), m.group(3) == null ? 0 : Integer.parseInt(m.group(3))};
    }

    private static void toast(Activity a, String s) { run(() -> Toast.makeText(a, s, Toast.LENGTH_LONG).show()); }
    private static void run(Runnable r) { new Handler(Looper.getMainLooper()).post(r); }
}
