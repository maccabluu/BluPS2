package com.virtualapplications.play;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BluPs2UpdateChecker {
    private static final String CURRENT = "1.4-alpha";
    private static final String API = "https://api.github.com/repos/maccabluu/BluPS2/releases/latest";
    private static final long COOLDOWN = 15L * 60L * 1000L;
    private static final Pattern VERSION = Pattern.compile("^v?(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:-(?:public-)?alpha)?$", Pattern.CASE_INSENSITIVE);

    private BluPs2UpdateChecker() {}

    static void automatic(Activity a) {
        SharedPreferences p = a.getSharedPreferences("blups2_updates", Context.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        if(now - p.getLong("last_check", 0L) < COOLDOWN) return;
        p.edit().putLong("last_check", now).apply();
        check(a, false);
    }

    static void manual(Activity a) { check(a, true); }

    private static void check(Activity a, boolean manual) {
        new Thread(() -> {
            try {
                JSONObject release = getJson(API);
                if(release.optBoolean("draft", false)) return;
                String tag = release.optString("tag_name", "");
                if(!VERSION.matcher(tag).matches() || compare(tag, CURRENT) <= 0) {
                    if(manual) toast(a, "BluPS2 1.4 Alpha is up to date.");
                    return;
                }
                String notes = release.optString("body", "No release notes supplied.");
                JSONArray assets = release.optJSONArray("assets");
                String apk = null, apkName = "BluPS2-update.apk", sha = null;
                if(assets != null) for(int i = 0; i < assets.length(); i++) {
                    JSONObject x = assets.getJSONObject(i);
                    String name = x.optString("name", "");
                    String lower = name.toLowerCase(Locale.ROOT);
                    if(apk == null && lower.endsWith(".apk")) {
                        apk = x.optString("browser_download_url", null);
                        apkName = name;
                    }
                    if(sha == null && (lower.endsWith(".sha256") || lower.endsWith("sha256.txt"))) {
                        sha = x.optString("browser_download_url", null);
                    }
                }
                if(apk == null) {
                    if(manual) toast(a, "A newer release exists, but no APK was attached.");
                    return;
                }
                String finalApk = apk, finalName = apkName, finalSha = sha;
                run(() -> new AlertDialog.Builder(a)
                        .setTitle("BluPS2 update available")
                        .setMessage(tag)
                        .setPositiveButton("Update now", (d,w) -> download(a, finalApk, finalName, finalSha))
                        .setNeutralButton("What's new", (d,w) -> new AlertDialog.Builder(a).setTitle(tag).setMessage(notes).setPositiveButton("OK", null).show())
                        .setNegativeButton("Later", null)
                        .show());
            } catch(Exception e) {
                if(manual) toast(a, "Update check failed: " + e.getMessage());
            }
        }).start();
    }

    private static void download(Activity a, String url, String name, String shaUrl) {
        try {
            DownloadManager dm = (DownloadManager)a.getSystemService(Context.DOWNLOAD_SERVICE);
            File dir = a.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if(dir == null) throw new Exception("Download folder unavailable");
            File target = new File(dir, "BluPS2-update.apk");
            if(target.exists()) target.delete();
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));
            r.setTitle("BluPS2 update");
            r.setDescription(name);
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            r.setDestinationUri(Uri.fromFile(target));
            long id = dm.enqueue(r);
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override public void onReceive(Context context, Intent intent) {
                    if(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) != id) return;
                    try { a.unregisterReceiver(this); } catch(Exception ignored) {}
                    new Thread(() -> finishDownload(a, dm, id, target, shaUrl)).start();
                }
            };
            a.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
            toast(a, "BluPS2 update download started.");
        } catch(Exception e) {
            toast(a, "Update download failed: " + e.getMessage());
        }
    }

    private static void finishDownload(Activity a, DownloadManager dm, long id, File apk, String shaUrl) {
        try {
            DownloadManager.Query q = new DownloadManager.Query().setFilterById(id);
            try(Cursor c = dm.query(q)) {
                if(c == null || !c.moveToFirst() || c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) != DownloadManager.STATUS_SUCCESSFUL)
                    throw new Exception("Android download failed");
            }
            if(shaUrl != null) {
                String body = getText(shaUrl);
                Matcher m = Pattern.compile("(?i)\\b([a-f0-9]{64})\\b").matcher(body);
                if(!m.find() || !m.group(1).equalsIgnoreCase(sha256(apk))) {
                    apk.delete();
                    toast(a, "Update blocked. SHA-256 verification failed.");
                    return;
                }
            }
            run(() -> openInstaller(a, apk));
        } catch(Exception e) {
            toast(a, "Update verification failed: " + e.getMessage());
        }
    }

    private static void openInstaller(Activity a, File apk) {
        try {
            Uri u = FileProvider.getUriForFile(a, a.getPackageName() + ".blups2files", apk);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(u, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            a.startActivity(i);
        } catch(Exception e) {
            toast(a, "Android Package Installer could not open: " + e.getMessage());
        }
    }

    private static JSONObject getJson(String url) throws Exception { return new JSONObject(getText(url)); }

    private static String getText(String url) throws Exception {
        HttpURLConnection c = (HttpURLConnection)new URL(url).openConnection();
        c.setConnectTimeout(10000); c.setReadTimeout(10000);
        c.setRequestProperty("Accept", "application/vnd.github+json");
        c.setRequestProperty("User-Agent", "BluPS2-Updater");
        int code = c.getResponseCode();
        if(code < 200 || code >= 300) throw new Exception("GitHub HTTP " + code);
        StringBuilder b = new StringBuilder();
        try(BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
            String line; while((line = r.readLine()) != null) b.append(line).append('\n');
        }
        return b.toString();
    }

    private static String sha256(File f) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try(InputStream in = new FileInputStream(f)) {
            byte[] buf = new byte[65536]; int n;
            while((n = in.read(buf)) > 0) md.update(buf, 0, n);
        }
        StringBuilder s = new StringBuilder();
        for(byte b : md.digest()) s.append(String.format(Locale.ROOT, "%02x", b));
        return s.toString();
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
