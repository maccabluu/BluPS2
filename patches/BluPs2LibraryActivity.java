package com.virtualapplications.play;

import android.app.Activity;
import android.app.AlertDialog;
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
    private static final int NAV = Color.rgb(5, 19, 42);
    private static final int NAV_2 = Color.rgb(10, 34, 72);
    private static final int ACTIVE = Color.rgb(32, 119, 255);
    private static final int BLUE = Color.rgb(35, 126, 255);
    private static final int PAGE = Color.rgb(246, 248, 252);
    private static final int CARD = Color.WHITE;
    private static final int TEXT = Color.rgb(25, 32, 45);
    private static final int MUTED = Color.rgb(94, 105, 122);
    private static final int LINE = Color.rgb(220, 226, 236);

    private static final int PICK_GAME_FILE = 5101;
    private static final int PICK_GAME_FOLDER = 5102;
    private boolean gridView = true;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BluPS2 1.5 Alpha");
        getWindow().setStatusBarColor(NAV);
        getWindow().setNavigationBarColor(NAV);
        setContentView(build());
        String action = getIntent().getStringExtra("blups2_action");
        if("add_game".equals(action)) openFilePicker();
        if("add_folder".equals(action)) openFolderPicker();
    }

    private View build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(NAV);
        root.addView(buildSidebar(), new LinearLayout.LayoutParams(dp(238), -1));
        root.addView(buildMain(), new LinearLayout.LayoutParams(0, -1, 1f));
        return root;
    }

    private View buildSidebar() {
        LinearLayout side = new LinearLayout(this);
        side.setOrientation(LinearLayout.VERTICAL);
        side.setPadding(dp(14), dp(18), dp(14), dp(14));
        side.setBackground(sideGradient());

        TextView logo = text("BluPS2", 28, Color.WHITE, true);
        side.addView(logo, new LinearLayout.LayoutParams(-1, dp(42)));
        TextView version = text("1.5 Alpha", 12, Color.rgb(158, 190, 235), false);
        version.setPadding(dp(2), 0, 0, dp(14));
        side.addView(version, new LinearLayout.LayoutParams(-1, dp(34)));

        side.addView(menu("🎮", "Library", true, v -> {}));
        side.addView(menu("▶", "Boot BIOS", false, v -> bootBios()));
        side.addView(menu("🏆", "RetroAchievements", false, v -> retroAchievements()));
        side.addView(menu("⚙", "Settings", false, v -> openSettings()));
        side.addView(menu("📁", "Setup / Change Folders", false, v -> openFolderPicker()));
        side.addView(menu("ⓘ", "About", false, v -> showAbout()));

        View spacer = new View(this);
        side.addView(spacer, new LinearLayout.LayoutParams(-1, 0, 1f));

        DeviceTelemetry.BatteryState battery = DeviceTelemetry.readBattery(this);
        String batteryText = battery.percent < 0 ? "Battery unavailable" : "Battery: " + battery.percent + "%";
        LinearLayout device = new LinearLayout(this);
        device.setOrientation(LinearLayout.VERTICAL);
        device.setPadding(dp(12), dp(10), dp(12), dp(10));
        device.setBackground(round(NAV_2, dp(14), Color.rgb(34, 72, 122)));
        device.setOnClickListener(v -> startActivity(new Intent(this, BluPs2ToolsActivity.class)));
        device.addView(text("●  Android handheld", 12, Color.WHITE, true));
        TextView batteryView = text(batteryText, 11, Color.rgb(186, 208, 238), false);
        batteryView.setPadding(0, dp(5), 0, 0);
        device.addView(batteryView);
        side.addView(device, new LinearLayout.LayoutParams(-1, dp(72)));
        return side;
    }

    private View buildMain() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackgroundColor(PAGE);

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(dp(24), dp(12), dp(18), dp(8));
        TextView title = text("Library", 27, TEXT, true);
        top.addView(title, new LinearLayout.LayoutParams(0, dp(54), 1f));
        top.addView(actionButton("↻", "Refresh", v -> refreshLibrary()));
        top.addView(actionButton("+", "Add Game", v -> openFilePicker()));
        top.addView(actionButton(gridView ? "▦" : "☷", gridView ? "Grid View" : "List View", v -> toggleView()));
        top.addView(actionButton("⋮", "More", v -> showMore()));
        page.addView(top, new LinearLayout.LayoutParams(-1, dp(76)));

        LinearLayout filter = new LinearLayout(this);
        filter.setOrientation(LinearLayout.HORIZONTAL);
        filter.setGravity(Gravity.CENTER_VERTICAL);
        filter.setPadding(dp(24), 0, dp(24), dp(4));
        TextView all = text("All Games", 13, TEXT, true);
        all.setPadding(dp(12), 0, dp(12), 0);
        all.setGravity(Gravity.CENTER_VERTICAL);
        all.setBackground(round(Color.WHITE, dp(10), LINE));
        filter.addView(all, new LinearLayout.LayoutParams(dp(132), dp(38)));
        TextView hint = text("  Your PS2 library", 12, MUTED, false);
        filter.addView(hint, new LinearLayout.LayoutParams(0, dp(38), 1f));
        page.addView(filter, new LinearLayout.LayoutParams(-1, dp(48)));

        page.addView(buildGameArea(), new LinearLayout.LayoutParams(-1, 0, 1f));
        page.addView(buildFooter(), new LinearLayout.LayoutParams(-1, dp(58)));
        return page;
    }

    private View buildGameArea() {
        ScrollView vertical = new ScrollView(this);
        vertical.setFillViewport(true);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(24), dp(10), dp(24), dp(16));
        vertical.addView(content, new ScrollView.LayoutParams(-1, -2));

        HorizontalScrollView covers = new HorizontalScrollView(this);
        covers.setHorizontalScrollBarEnabled(false);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(gameCard("Add a PS2 game", "＋", v -> openFilePicker()));
        row.addView(gameCard("Recent games", "PS2", v -> openCoreLibrary()));
        row.addView(gameCard("Favorites", "★", v -> openCoreLibrary()));
        row.addView(gameCard("Homebrew", "ELF", v -> openCoreLibrary()));
        row.addView(gameCard("Library scan", "↻", v -> refreshLibrary()));
        covers.addView(row);
        content.addView(covers, new LinearLayout.LayoutParams(-1, gridView ? dp(294) : dp(214)));

        TextView empty = text("Game covers and titles appear here after you add your own PS2 backups. Files stay in their original internal storage or microSD location.", 13, MUTED, false);
        empty.setPadding(dp(16), dp(14), dp(16), dp(14));
        empty.setBackground(round(Color.WHITE, dp(12), LINE));
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(-1, -2);
        ep.setMargins(0, dp(14), 0, 0);
        content.addView(empty, ep);
        return vertical;
    }

    private View buildFooter() {
        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setGravity(Gravity.CENTER_VERTICAL);
        footer.setPadding(dp(24), 0, dp(18), 0);
        footer.setBackgroundColor(Color.WHITE);
        footer.addView(hint("✕", "Add Game / Content"));
        footer.addView(hint("△", "Options"));
        footer.addView(hint("◉", "Select"));
        footer.addView(hint("□", "Back"));
        View spacer = new View(this);
        footer.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1f));
        TextView profile = text("M   maccabluu ⌄", 13, TEXT, true);
        profile.setGravity(Gravity.CENTER);
        profile.setPadding(dp(12), 0, dp(12), 0);
        profile.setOnClickListener(v -> startActivity(new Intent(this, BluPs2ProfilesActivity.class)));
        profile.setBackground(round(Color.rgb(232, 239, 250), dp(18), LINE));
        footer.addView(profile, new LinearLayout.LayoutParams(dp(180), dp(38)));
        return footer;
    }

    private View gameCard(String title, String badge, View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(7), dp(7), dp(7), dp(7));
        card.setBackground(round(CARD, dp(10), LINE));
        card.setOnClickListener(listener);

        TextView art = text(badge, badge.length() <= 2 ? 42 : 28, Color.WHITE, true);
        art.setGravity(Gravity.CENTER);
        art.setBackground(coverGradient());
        card.addView(art, new LinearLayout.LayoutParams(-1, gridView ? dp(195) : dp(125)));

        TextView name = text(title, 12, TEXT, true);
        name.setGravity(Gravity.CENTER);
        name.setMaxLines(2);
        name.setPadding(dp(4), dp(7), dp(4), 0);
        card.addView(name, new LinearLayout.LayoutParams(-1, gridView ? dp(58) : dp(46)));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(gridView ? dp(148) : dp(188), gridView ? dp(270) : dp(188));
        lp.setMargins(0, 0, dp(12), 0);
        card.setLayoutParams(lp);
        return card;
    }

    private TextView menu(String icon, String label, boolean active, View.OnClickListener listener) {
        TextView v = text(icon + "   " + label, 14, Color.WHITE, active);
        v.setGravity(Gravity.CENTER_VERTICAL);
        v.setPadding(dp(14), 0, dp(8), 0);
        v.setOnClickListener(listener);
        if(active) v.setBackground(round(ACTIVE, dp(12), Color.rgb(72, 148, 255)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(54));
        lp.setMargins(0, dp(3), 0, dp(3));
        v.setLayoutParams(lp);
        return v;
    }

    private View actionButton(String symbol, String label, View.OnClickListener listener) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(7), dp(3), dp(7), dp(3));
        box.setOnClickListener(listener);
        TextView icon = text(symbol, 24, TEXT, false);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(round(Color.WHITE, dp(10), LINE));
        box.addView(icon, new LinearLayout.LayoutParams(dp(46), dp(40)));
        TextView caption = text(label, 10, TEXT, false);
        caption.setGravity(Gravity.CENTER);
        box.addView(caption, new LinearLayout.LayoutParams(dp(72), dp(22)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(82), dp(64));
        lp.setMargins(dp(3), 0, dp(3), 0);
        box.setLayoutParams(lp);
        return box;
    }

    private View hint(String symbol, String label) {
        TextView v = text(symbol + "  " + label, 11, MUTED, false);
        v.setGravity(Gravity.CENTER_VERTICAL);
        v.setPadding(0, 0, dp(20), 0);
        return v;
    }

    private void refreshLibrary() {
        Toast.makeText(this, "Refreshing BluPS2 library...", Toast.LENGTH_SHORT).show();
        openCoreLibrary();
    }

    private void toggleView() {
        gridView = !gridView;
        setContentView(build());
        Toast.makeText(this, gridView ? "Grid view" : "List view", Toast.LENGTH_SHORT).show();
    }

    private void showMore() {
        String[] options = {"Add Game File", "Add Game Folder", "Scan Library", "Performance Hub", "Profiles"};
        new AlertDialog.Builder(this)
                .setTitle("BluPS2")
                .setItems(options, (d, which) -> {
                    if(which == 0) openFilePicker();
                    else if(which == 1) openFolderPicker();
                    else if(which == 2) refreshLibrary();
                    else if(which == 3) startActivity(new Intent(this, BluPs2ToolsActivity.class));
                    else startActivity(new Intent(this, BluPs2ProfilesActivity.class));
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_GAME_FILE);
    }

    private void openFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_GAME_FOLDER);
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
            Toast.makeText(this, "Folder saved. Starting library scan.", Toast.LENGTH_SHORT).show();
            openCoreLibrary();
        }
    }

    private void bootBios() {
        Toast.makeText(this, "Opening PS2 core for BIOS setup.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void retroAchievements() {
        new AlertDialog.Builder(this)
                .setTitle("RetroAchievements")
                .setMessage("RetroAchievements support is listed in the BluPS2 interface. Account and game integration will depend on core support in a later Alpha update.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
                .setTitle("BluPS2 1.5 Alpha")
                .setMessage("BluPS2 is an experimental ARM64 Android PS2 emulator project using the open-source Play! emulation core.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void openSettings() { startActivity(new Intent(this, BluPs2AppSettingsActivity.class)); }
    private void openCoreLibrary() { startActivity(new Intent(this, MainActivity.class)); }

    private GradientDrawable sideGradient() {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.rgb(8, 35, 82), NAV, Color.rgb(3, 12, 27)});
    }

    private GradientDrawable coverGradient() {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.rgb(20, 108, 235), Color.rgb(13, 61, 150), Color.rgb(7, 28, 76)});
        g.setCornerRadius(dp(7));
        return g;
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
