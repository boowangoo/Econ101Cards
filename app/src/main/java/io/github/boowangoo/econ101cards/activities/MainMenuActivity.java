package io.github.boowangoo.econ101cards.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

import io.github.boowangoo.econ101cards.R;
import io.github.boowangoo.econ101cards.data.DataService;
import io.realm.Realm;

public class MainMenuActivity extends AppCompatActivity {

    Realm realm;

    DataService dataService = new DataService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        realm = Realm.getDefaultInstance();

        try {
            dataService.loadData(getAssets(), realm);
        } catch (IOException e) {
            e.printStackTrace();
        }

        realm.close();
    }


    public void startPracticeActivity(View v) {
        Intent intent = new Intent(this, PracticeActivity.class);
        startActivity(intent);
    }

    public void startStatsActivity(View v) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

    public void startSettingsActivity(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startAboutActivity(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
