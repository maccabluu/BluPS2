package com.virtualapplications.play;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public final class BluPs2LibraryActivity extends Activity {
    private static final int BG = Color.rgb(3, 16, 38);
    private static final int SIDEBAR = Color.rgb(12, 72, 178);
    private static final int SIDEBAR_ACTIVE = Color.rgb(38, 119, 255);
    private static final int PANEL = Color.rgb(12, 66, 158);
    private static final int PANEL_DARK = Color.rgb(5, 40, 105);
    private static final int BLUE = Color.rgb(70, 151, 255);
    private static final int MUTED = Color.rgb(190, 210, 240);

    private static final int PICK_GAME_FILE = 4101;
    private static final int PICK_GAME_FOLDER = 4102;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BluPS2 1.4 Alpha");
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        setContentView(build());
    }

    private View build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(BG);

        root.addView(buildSidebar(), new LinearLayout.LayoutParams(dp(220), -1));
        root.addView(buildLibrary(), new LinearLayout.LayoutParams(0, -1, 1f));
        return root;
    }

    private View buildSidebar() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(SIDEBAR);
        scroll.setFillViewport(true);

        LinearLayout side = new LinearLayout(this);
        side.setOrientation(LinearLayout.VERTICAL);
        side.setPadding(dp(12), dp(16), dp(12), dp(16));
        scroll.addView(side, new ScrollView.LayoutParams(-1, -2));

        TextView logo = text("BluPS2", 27, Color.WHITE, true);
        logo.setPadding(dp(12), 0, 0, dp(18));
        side.addView(logo, new LinearLayout.LayoutParams(-1, dp(58)));

        side.addView(section("LIBRARY"));
        side.addView(menu("🎮", "Library", true, v -> {}));
        side.addView(menu("▶", "Boot BIOS", false, v -> bootBios()));
        side.addView(menu("🏆", "RetroAchievements", false, v -> retroAchievements()));
        side.addView(menu("⚙", "Settings", false, v -> openSettings()));

        TextView options = section("OPTIONS");
        LinearLayout.LayoutParams olp = new LinearLayout.LayoutParams(-1, dp(36));
        olp.setMargins(0, dp(14), 0, 0);
        side.addView(options, olp);
        side.addView(menu("📁", "Setup / Change Folders", false, v -> openFolderPicker()));

        TextView version = text("BluPS2 1.4 Alpha", 11, Color.argb(190, 255, 255, 255), false);
        version.setPadding(dp(12), dp(24), 0, 0);
        side.addView(version);
        return scroll;
    }

    private View buildLibrary() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(12), dp(18), dp(16));
        page.setBackground(gradient());

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = text("Library", 26, Color.WHITE, true);
        top.addView(title, new LinearLayout.LayoutParams(0, dp(48), 1f));
        top.addView(topAction("↻", v -> openCoreLibrary()));
        top.addView(topAction("▦", v -> Toast.makeText(this, "Grid view selected", Toast.LENGTH_SHORT).show()));
        top.addView(topAction("⋮", v -> showMore()));
        page.addView(top, new LinearLayout.LayoutParams(-1, dp(50)));

        TextView status = text("PS2 Games   •   1.4 Alpha", 12, MUTED, false);
        status.setPadding(0, 0, 0, dp(8));
        page.addView(status, new LinearLayout.LayoutParams(-1, dp(34)));

        HorizontalScrollView covers = new HorizontalScrollView(this);
        covers.setHorizontalScrollBarEnabled(false);
        covers.setFillViewport(false);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(8), dp(6), dp(8));
        row.addView(gameCard("Add Game", "+", v -> openFilePicker()));
        row.addView(gameCard("Recent", "PS2", v -> openCoreLibrary()));
        row.addView(gameCard("Favorites", "PS2", v -> openCoreLibrary()));
        row.addView(gameCard("Homebrew", "ELF", v -> openCoreLibrary()));
        row.addView(gameCard("Library Scan", "↻", v -> openCoreLibrary()));
        covers.addView(row);
        page.addView(covers, new LinearLayout.LayoutParams(-1, dp(300)));

        TextView help = text("Choose + to add a PS2 game file. Use Setup / Change Folders for internal storage or microSD folders. Games stay in their original storage location.", 13, Color.WHITE, false);
        help.setPadding(dp(14), dp(12), dp(14), dp(12));
        help.setBackground(round(PANEL_DARK, dp(12), Color.rgb(45, 110, 220)));
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(-1, -2);
        hlp.setMargins(0, dp(8), 0, 0);
        page.addView(help, hlp);
        return page;
    }

    private View gameCard(String name, String badge, View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(8), dp(8), dp(8), dp(8));
        card.setBackground(round(PANEL, dp(12), BLUE));
        card.setOnClickListener(listener);

        TextView art = text(badge, badge.length() <= 2 ? 38 : 28, Color.WHITE, true);
        art.setGravity(Gravity.CENTER);
        art.setBackground(round(PANEL_DARK, dp(8), Color.rgb(80, 145, 245)));
        card.addView(art, new LinearLayout.LayoutParams(-1, dp(210)));

        TextView label = text(name, 13, Color.WHITE, true);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setMaxLines(1);
        label.setPadding(dp(4), dp(7), dp(4), 0);
        card.addView(label, new LinearLayout.LayoutParams(-1, dp(42)));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(160), dp(270));
        lp.setMargins(0, 0, dp(12), 0);
        card.setLayoutParams(lp);
        return card;
    }

    private TextView menu(String icon, String label, boolean active, View.OnClickListener listener) {
        TextView v = text(icon + "   " + label, 15, Color.WHITE, active);
        v.setGravity(Gravity.CENTER_VERTICAL);
        v.setPadding(dp(14), 0, dp(8), 0);
        v.setOnClickListener(listener);
        if(active) v.setBackground(round(SIDEBAR_ACTIVE, dp(16), Color.rgb(95, 160, 255)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(58));
        lp.setMargins(0, dp(3), 0, dp(3));
        v.setLayoutParams(lp);
        return v;
    }

    private TextView section(String label) {
        TextView v = text(label, 11, Color.argb(200, 255, 255, 255), true);
        v.setGravity(Gravity.CENTER_VERTICAL);
        v.setPadding(dp(12), 0, 0, 0);
        return v;
    }

    private TextView topAction(String label, View.OnClickListener listener) {
        TextView v = text(label, 24, Color.WHITE, false);
        v.setGravity(Gravity.CENTER);
        v.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(48), dp(48));
        lp.setMargins(dp(4), 0, 0, 0);
        v.setLayoutParams(lp);
        return v;
    }

    private void openFilePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, PICK_GAME_FILE);
        } catch(Exception e) {
            Toast.makeText(this, "Unable to open the Android file picker.", Toast.LENGTH_LONG).show();
        }
    }

    private void openFolderPicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, PICK_GAME_FOLDER);
        } catch(Exception e) {
            Toast.makeText(this, "Unable to open the Android folder picker.", Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        try { getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch(Exception ignored) {}

        if(requestCode == PICK_GAME_FILE) {
            Intent launch = new Intent(this, MainActivity.class);
            launch.setAction(Intent.ACTION_VIEW);
            launch.setData(uri);
            launch.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(launch);
        } else if(requestCode == PICK_GAME_FOLDER) {
            Toast.makeText(this, "Folder saved. Opening library scan.", Toast.LENGTH_SHORT).show();
            openCoreLibrary();
        }
    }

    private void bootBios() {
        Toast.makeText(this, "Opening BluPS2 settings for BIOS setup.", Toast.LENGTH_SHORT).show();
        openSettings();
    }

    private void retroAchievements() {
        Toast.makeText(this, "RetroAchievements integration is being prepared for BluPS2 1.4 Alpha.", Toast.LENGTH_LONG).show();
    }

    private void openSettings() { startActivity(new Intent(this, BluPs2AppSettingsActivity.class)); }
    private void openCoreLibrary() { startActivity(new Intent(this, MainActivity.class)); }

    private void showMore() {
        Toast.makeText(this, "More: Add Game, Add Folder, Scan Library", Toast.LENGTH_SHORT).show();
    }

    private GradientDrawable gradient() {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.rgb(7, 79, 190), Color.rgb(3, 31, 88), BG});
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
