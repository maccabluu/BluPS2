package com.blustudio.blups2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48, 48, 48, 48);
        root.setBackgroundColor(Color.rgb(5, 16, 38));

        TextView title = new TextView(this);
        title.setText("BluPS2");
        title.setTextColor(Color.WHITE);
        title.setTextSize(42);
        title.setGravity(Gravity.CENTER);

        TextView status = new TextView(this);
        status.setText("Android Alpha\nPS2 emulation core integration in development");
        status.setTextColor(Color.rgb(120, 190, 255));
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 24, 0, 0);

        root.addView(title);
        root.addView(status);
        setContentView(root);
    }
}
