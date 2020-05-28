package com.example.android.bactrack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    //weight in kilograms or pounds
    public static boolean pounds = false;
    EditText userWeightET;
    //weight
    String strWeight;
    public double userWeight;
    double distributionRatio;
    RadioButton radioMale;
    RadioButton radioButton0;
    TextView weightType;
    RadioGroup BodyType;
    RadioGroup gender;

    public static final String SHARED_PREFS_SETTINGS = "sharedPrefsSettings";
    public static final String WEIGHT = "userWeight";
    public static final String GENDER = "gender";
    public static final String BODYTYPE = "bodytype";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button language = findViewById(R.id.languageSettings);
        Button about = findViewById(R.id.about);
        radioButton0 = findViewById(R.id.radioButton0);
        userWeightET = findViewById(R.id.userWeight);
        radioMale = findViewById(R.id.radioMale);
        BodyType = findViewById(R.id.BodyType);
        weightType = findViewById(R.id.weightType);
        Button changeWeight = findViewById(R.id.changeWeight);
        Button submit = findViewById(R.id.submit);
        gender = findViewById(R.id.gender);
        BodyType = findViewById(R.id.BodyType);

        //initialize RadioGroups
        gender.check(R.id.radioMale);
        BodyType.check(R.id.radioButton0);

        //load saved data if Settings is opened within MainAdd
        Bundle settings = getIntent().getExtras();
        if (settings != null) {
            loadData();
        }

        //set locale
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });

        //go to about page
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int LAUNCH_SECOND_ACTIVITY = 1;
                Intent intent = new Intent(Settings.this, AboutActivity.class);
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
            }
        });

        //set imperial/metric
        changeWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imperialToMetric();
            }
        });

        //submit settings to main activity
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strWeight = userWeightET.getText().toString();
                if (TextUtils.isEmpty(strWeight)) {
                    Toast.makeText(Settings.this, R.string.Please_enter_weight, Toast.LENGTH_SHORT).show();
                } else {
                    saveData();

                    userWeight = Double.parseDouble(strWeight);
                    Intent intent = new Intent(Settings.this, MainAdd.class);
                    intent.putExtra("distributionRatio", radioButtons());
                    intent.putExtra("userWeight", userWeightKGPD());

                    startActivity(intent);
                }
            }
        });
    }

    public void imperialToMetric() {
        if (pounds) {
            ((TextView) findViewById(R.id.weightType)).setText(R.string.kilograms);
            ((TextView) findViewById(R.id.changeWeight)).setText(R.string.set_to_imperial);
        } else { // currently accepting kgs
            ((TextView) findViewById(R.id.weightType)).setText(R.string.pounds);
            ((TextView) findViewById(R.id.changeWeight)).setText(R.string.set_to_metric);
        }
        pounds = !pounds;
    }

    public double userWeightKGPD() {
        //checks weight (kg/pd)
        if (pounds)
            userWeight *= 0.45;
        return userWeight;
    }

    public double radioButtons() {
        if (radioMale.isChecked())
            distributionRatio = 0.68;
        else distributionRatio = 0.55;
        if (BodyType.getCheckedRadioButtonId() == findViewById(R.id.radioButtonNeg2).getId())
            distributionRatio -= 0.1;
        if (BodyType.getCheckedRadioButtonId() == findViewById(R.id.radioButtonNeg1).getId())
            distributionRatio -= 0.05;
        if (BodyType.getCheckedRadioButtonId() == findViewById(R.id.radioButton1).getId())
            distributionRatio += 0.05;
        if (BodyType.getCheckedRadioButtonId() == findViewById(R.id.radioButton2).getId())
            distributionRatio += 0.1;
        return distributionRatio;
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(WEIGHT, strWeight);
        editor.putInt(GENDER, gender.getCheckedRadioButtonId());
        editor.putInt(BODYTYPE, BodyType.getCheckedRadioButtonId());

        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        int savedRadioIndex1 = sharedPreferences.getInt(GENDER, 0);
        int savedRadioIndex2 = sharedPreferences.getInt(BODYTYPE, 0);

        userWeightET.setText(sharedPreferences.getString(WEIGHT, null));
        gender.check(savedRadioIndex1);
        BodyType.check(savedRadioIndex2);

    }

    private void showChangeLanguageDialog() {
        final String[] LISTITEMS = {"English", "Deutsch", "Русский", "Українська"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this);
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
