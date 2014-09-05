package com.example.eterverda.playless;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import io.github.eterverda.playless.AutoUpdateService;

public class SampleActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sample);

        startService(new Intent(Intent.ACTION_MAIN).setClass(this, AutoUpdateService.class));
    }
}
