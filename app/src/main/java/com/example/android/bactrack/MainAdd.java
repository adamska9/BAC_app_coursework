package com.example.android.bactrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainAdd extends AppCompatActivity implements Dialog.DialogListener, EditDialog.DialogListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NAME = "name";
    public static final String PERCENT = "percent";
    public static final String ML = "ml";
    static ArrayList<Drink> drinks;
    double distributionRatio;
    double userWeight;
    int ethanolGrams = 0;
    double BAC = 0;
    double hours = 0;
    TextView bloodAlcoholContent;
    TextView hoursText;
    Button addButton;
    Button calculateButton;
    Button settingsButton;
    //Variables declaration:
    private RVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add);

        loadData();

        bloodAlcoholContent = findViewById(R.id.bloodAlcoholContent);
        hoursText = findViewById(R.id.hoursText);


        //Get settings from SettingsActivity
        Bundle settings = getIntent().getExtras();
        if (settings != null) {
            distributionRatio = settings.getDouble("distributionRatio");
            userWeight = settings.getDouble("userWeight");
        }

        buildRecyclerView();
        setButtons();

    }


    //Add a drink dialog
    public void openDialog() {
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(), "example");
    }

    public void openEditDialog(int position) {
        EditDialog editDialog = new EditDialog(position, drinks.get(position).getName(), drinks.get(position).getPercent(), drinks.get(position).getMl());
        editDialog.show(getSupportFragmentManager(), "example");
    }

    @Override
    public void applyTexts(String name, int percent, int ml) {
        drinks.add(new Drink(name, percent, ml));
        adapter.notifyItemInserted(drinks.size() - 1);
        saveData();
    }

    @Override
    public void editApplyTexts(int position, String name, double percent, double ml) {
        drinks.get(position).changeName(name);
        drinks.get(position).changePercent(percent);
        drinks.get(position).changeMl(ml);
        adapter.notifyItemChanged(position);
        saveData();
    }

    //Calculate BAC formula
    public double calculateBAC() {
        BAC = 0;
        ethanolGrams = 0;
        for (Drink drink : drinks) {
            ethanolGrams += (drink.getMl() * drink.getPercent());
        }
        ethanolGrams /= 100;
        BAC = ((0.79) * ethanolGrams * 100) / (userWeight * 1000 * distributionRatio);
        hours = (BAC - 0.02) / 0.016;
        if (hours < 0)
            hours = 0;
        return BAC;
    }


    public void buildRecyclerView() {
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        adapter = new RVAdapter(drinks);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new RVAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openEditDialog(position);
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(drinks);
        editor.putString("drinks list", json);

        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("drinks list", null);
        Type type = new TypeToken<ArrayList<Drink>>() {
        }.getType();
        drinks = gson.fromJson(json, type);

        if (drinks == null) {
            drinks = new ArrayList<>();
        }
    }

    private void removeItem(int position) {
        drinks.remove(position);
        adapter.notifyItemRemoved(position);
        saveData();
    }

    //Buttons
    public void setButtons() {

        //Add a drink
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        //Set BAC and hours texts
        calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BAC = calculateBAC();
                bloodAlcoholContent.setText(String.format("%.3f%%", BAC));
                hoursText.setText(String.format("Hours left until sober: %.2f", hours));
                saveData();
            }
        });

        //Go to Settings
        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                Intent intent = new Intent(MainAdd.this, Settings.class);
                intent.putExtra("From Main", true);
                startActivity(intent);
            }
        });

    }
}
