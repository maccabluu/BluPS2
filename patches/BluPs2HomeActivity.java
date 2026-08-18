package com.virtualapplications.play;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public final class BluPs2HomeActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BluPS2 1.5 Alpha");
        BluPs2UpdateChecker.automatic(this);
        Intent i = new Intent(this, BluPs2LibraryActivity.class);
        startActivity(i);
        finish();
    }
}
