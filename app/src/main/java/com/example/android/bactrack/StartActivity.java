package com.example.android.bactrack;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_start);

        Button start = findViewById(R.id.start);
        Button language = findViewById(R.id.language);

        language.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the phrases category is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link PhrasesActivity}
                showChangeLanguageDialog();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the phrases category is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link PhrasesActivity}
                Intent intent = new Intent(StartActivity.this, Settings.class);

                // Start the new activity
                startActivity(intent);
            }
        });

        Button about = findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the phrases category is clicked on.
            @Override
            public void onClick(View view) {
                int LAUNCH_SECOND_ACTIVITY = 1;
                Intent intent = new Intent(StartActivity.this, AboutActivity.class);
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
            }
        });

    }

    private void showChangeLanguageDialog() {
        final String[] LISTITEMS = {"English", "Deutsch", "Русский", "Українська"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(StartActivity.this);
        mBuilder.setTitle("Choose language");
        mBuilder.setSingleChoiceItems(LISTITEMS, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("en");
                    recreate();
                } else if (which == 1) {
                    setLocale("de");
                    recreate();
                } else if (which == 2) {
                    setLocale("ru");
                    recreate();
                } else if (which == 3) {
                    setLocale("uk");
                    recreate();
                }
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }
}
