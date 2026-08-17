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
        setContentView(buildUi());
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(14));
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        TextView logo = text("BluPS2", 28, Color.WHITE, true);
        header.addView(logo, new LinearLayout.LayoutParams(0, dp(52), 1f));
        header.addView(action("⌕", null));
        header.addView(action("▦", null));
        header.addView(action("+", v -> openLibrary()));
        root.addView(header);

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.addView(tab("All Games", true));
        tabs.addView(tab("Recent", false));
        tabs.addView(tab("Favorites", false));
        root.addView(tabs);

        HorizontalScrollView covers = new HorizontalScrollView(this);
        covers.setHorizontalScrollBarEnabled(false);
        LinearLayout coverRow = new LinearLayout(this);
        coverRow.setOrientation(LinearLayout.HORIZONTAL);
        coverRow.setPadding(0, dp(10), 0, dp(10));
        coverRow.addView(gameCard("Add your PS2 games", "ISO / BIN / CSO"));
        coverRow.addView(gameCard("Recently played", "Your latest titles"));
        coverRow.addView(gameCard("Favorites", "Pinned games"));
        coverRow.addView(gameCard("Homebrew", "ELF titles"));
        covers.addView(coverRow);
        root.addView(covers, new LinearLayout.LayoutParams(-1, 0, 1f));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        Button add = button("Add Games");
        add.setOnClickListener(v -> openLibrary());
        Button refresh = button("Refresh");
        refresh.setOnClickListener(v -> openLibrary());
        actions.addView(add, new LinearLayout.LayoutParams(0, dp(48), 1f));
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(0, dp(48), 1f);
        rp.setMargins(dp(10), 0, 0, 0);
        actions.addView(refresh, rp);
        root.addView(actions);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setPadding(0, dp(12), 0, dp(10));
        stats.addView(stat("FPS", "60"), new LinearLayout.LayoutParams(0, dp(76), 1f));
        stats.addView(stat("TEMP", "AUTO"), new LinearLayout.LayoutParams(0, dp(76), 1f));
        stats.addView(stat("BATTERY", "LIVE"), new LinearLayout.LayoutParams(0, dp(76), 1f));
        stats.addView(stat("PROFILE", "MACCA"), new LinearLayout.LayoutParams(0, dp(76), 1f));
        root.addView(stats);

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.addView(nav("Library", true, v -> openLibrary()));
        nav.addView(nav("Games", false, v -> openLibrary()));
        nav.addView(nav("Settings", false, v -> startActivity(new Intent(this, SettingsActivity.class))));
        nav.addView(nav("Profiles", false, v -> startActivity(new Intent(this, BluPs2ProfilesActivity.class))));
        root.addView(nav);
        return root;
    }

    private void openLibrary() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private TextView gameCard(String title, String sub) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14), dp(14), dp(14), dp(14));
        box.setBackground(round(PANEL_2, dp(12), Color.rgb(25, 70, 115)));
        TextView art = text("PS2", 30, BLUE, true);
        art.setGravity(Gravity.CENTER);
        box.addView(art, new LinearLayout.LayoutParams(dp(145), dp(118)));
        TextView t = text(title, 14, Color.WHITE, true);
        box.addView(t);
        TextView s = text(sub, 11, MUTED, false);
        box.addView(s);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(175), dp(190));
        lp.setMargins(0, 0, dp(10), 0);
        box.setLayoutParams(lp);
        box.setOnClickListener(v -> openLibrary());
        return box;
    }

    private TextView stat(String label, String value) {
        TextView v = text(label + "\n" + value, 13, Color.WHITE, true);
        v.setGravity(Gravity.CENTER);
        v.setBackground(round(PANEL, dp(10), Color.rgb(28, 66, 103)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -1, 1f);
        lp.setMargins(dp(3), 0, dp(3), 0);
        v.setLayoutParams(lp);
        return v;
    }

    private TextView tab(String label, boolean active) {
        TextView v = text(label, 13, active ? Color.WHITE : MUTED, active);
        v.setGravity(Gravity.CENTER);
        v.setPadding(dp(18), dp(8), dp(18), dp(8));
        if(active) v.setBackground(round(Color.rgb(0, 67, 135), dp(8), BLUE));
        return v;
    }

    private TextView nav(String label, boolean active, View.OnClickListener listener) {
        TextView v = text(label, 12, active ? BLUE : MUTED, active);
        v.setGravity(Gravity.CENTER);
        v.setOnClickListener(listener);
        v.setPadding(dp(8), dp(12), dp(8), dp(12));
        v.setLayoutParams(new LinearLayout.LayoutParams(0, dp(48), 1f));
        return v;
    }

    private TextView action(String label, View.OnClickListener listener) {
        TextView v = text(label, 24, Color.WHITE, false);
        v.setGravity(Gravity.CENTER);
        v.setPadding(dp(8), 0, dp(8), 0);
        if(listener != null) v.setOnClickListener(listener);
        return v;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setAllCaps(false);
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

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
