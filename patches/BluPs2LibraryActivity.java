package com.virtualapplications.play;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public final class BluPs2LibraryActivity extends Activity {
    private static final int BG = Color.rgb(2, 10, 22);
    private static final int PANEL = Color.rgb(8, 28, 52);
    private static final int BLUE = Color.rgb(0, 132, 255);
    private static final int MUTED = Color.rgb(164, 180, 200);

    private static final int PICK_GAME_FILE = 4101;
    private static final int PICK_GAME_FOLDER = 4102;

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

        TextView sub = text("Add games from internal storage or microSD. BluPS2 keeps large game images in their original location and reads them through Android storage access.", 14, MUTED, false);
        sub.setPadding(0, dp(8), 0, dp(18));
        root.addView(sub);

        LinearLayout empty = new LinearLayout(this);
        empty.setOrientation(LinearLayout.VERTICAL);
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(dp(20), dp(24), dp(20), dp(24));
        empty.setBackground(round(PANEL, dp(14), Color.rgb(27, 76, 126)));

        TextView icon = text("PS2", 40, BLUE, true);
        icon.setGravity(Gravity.CENTER);
        empty.addView(icon);

        TextView emptyTitle = text("Add your PS2 games", 18, Color.WHITE, true);
        emptyTitle.setGravity(Gravity.CENTER);
        emptyTitle.setPadding(0, dp(10), 0, dp(5));
        empty.addView(emptyTitle);

        TextView emptyText = text("Choose one supported PS2 game file, or choose a folder containing legal backups you own. ISO disc images and ELF homebrew are supported by the core, with additional formats depending on core support.", 13, MUTED, false);
        emptyText.setGravity(Gravity.CENTER);
        empty.addView(emptyText);
        root.addView(empty, new LinearLayout.LayoutParams(-1, dp(220)));

        Button addFile = button("+ Add Game File");
        addFile.setOnClickListener(v -> openFilePicker());
        LinearLayout.LayoutParams fileLp = new LinearLayout.LayoutParams(-1, dp(50));
        fileLp.setMargins(0, dp(16), 0, dp(10));
        root.addView(addFile, fileLp);

        Button addFolder = button("Add Game Folder");
        addFolder.setOnClickListener(v -> openFolderPicker());
        LinearLayout.LayoutParams folderLp = new LinearLayout.LayoutParams(-1, dp(50));
        folderLp.setMargins(0, 0, 0, dp(10));
        root.addView(addFolder, folderLp);

        Button scan = button("Scan Library");
        scan.setOnClickListener(v -> openCoreLibrary());
        LinearLayout.LayoutParams scanLp = new LinearLayout.LayoutParams(-1, dp(50));
        scanLp.setMargins(0, 0, 0, dp(10));
        root.addView(scan, scanLp);

        Button back = button("Back to BluPS2 Home");
        back.setOnClickListener(v -> finish());
        root.addView(back, new LinearLayout.LayoutParams(-1, dp(50)));

        return scroll;
    }

    private void openFilePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, PICK_GAME_FILE);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open the Android file picker.", Toast.LENGTH_LONG).show();
        }
    }

    private void openFolderPicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, PICK_GAME_FOLDER);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open the Android folder picker.", Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;

        Uri uri = data.getData();
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ignored) { }

        if (requestCode == PICK_GAME_FILE) {
            Intent launch = new Intent(this, MainActivity.class);
            launch.setAction(Intent.ACTION_VIEW);
            launch.setData(uri);
            launch.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(launch);
        } else if (requestCode == PICK_GAME_FOLDER) {
            Toast.makeText(this, "Game folder added. Scanning library...", Toast.LENGTH_SHORT).show();
            openCoreLibrary();
        }
    }

    private void openCoreLibrary() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("blups2_brand", true);
        startActivity(i);
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
