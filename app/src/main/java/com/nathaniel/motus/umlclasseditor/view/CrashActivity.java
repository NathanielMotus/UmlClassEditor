package com.nathaniel.motus.umlclasseditor.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.databinding.ActivityCrashBinding;

public class CrashActivity extends AppCompatActivity {
    private ActivityCrashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logs.setText(getIntent().getExtras().containsKey("ex") ? getIntent().getStringExtra("ex") : "");

        binding.restartBtn.setOnClickListener(v->triggerRebirth(this));
    }

    void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        mainIntent.setPackage(context.getPackageName());
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}