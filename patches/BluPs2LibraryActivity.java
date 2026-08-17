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

public final class BluPs2LibraryActivity extends Activity {
    private static final int BG = Color.rgb(2, 10, 22);
    private static final int PANEL = Color.rgb(8, 28, 52);
    private static final int BLUE = Color.rgb(0, 132, 255);
    private static final int MUTED = Color.rgb(164, 180, 200);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BluPS2");
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

        TextView title = text("BluPS2 Library", 28, Color.WHITE, true);
        root.addView(title);

        TextView sub = text("Add your PS2 game folder, then your games will appear here.", 14, MUTED, false);
        sub.setPadding(0, dp(8), 0, dp(18));
        root.addView(sub);

        LinearLayout empty = new LinearLayout(this);
        empty.setOrientation(LinearLayout.VERTICAL);
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(dp(20), dp(28), dp(20), dp(28));
        empty.setBackground(round(PANEL, dp(14), Color.rgb(27, 76, 126)));

        TextView icon = text("PS2", 40, BLUE, true);
        icon.setGravity(Gravity.CENTER);
        empty.addView(icon);

        TextView emptyTitle = text("No games added yet", 18, Color.WHITE, true);
        emptyTitle.setGravity(Gravity.CENTER);
        emptyTitle.setPadding(0, dp(10), 0, dp(5));
        empty.addView(emptyTitle);

        TextView emptyText = text("Use Add Game Folder to choose the folder containing your own PS2 game files.", 13, MUTED, false);
        emptyText.setGravity(Gravity.CENTER);
        empty.addView(emptyText);
        root.addView(empty, new LinearLayout.LayoutParams(-1, dp(210)));

        Button add = button("Add Game Folder");
        add.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("blups2_brand", true);
            startActivity(i);
        });
        LinearLayout.LayoutParams addLp = new LinearLayout.LayoutParams(-1, dp(50));
        addLp.setMargins(0, dp(16), 0, dp(10));
        root.addView(add, addLp);

        Button back = button("Back to BluPS2 Home");
        back.setOnClickListener(v -> finish());
        root.addView(back, new LinearLayout.LayoutParams(-1, dp(50)));

        return scroll;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(13);
        b.setTextColor(Color.WHITE);
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
