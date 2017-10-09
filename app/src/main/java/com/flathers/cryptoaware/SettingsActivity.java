package com.flathers.cryptoaware;

import android.content.Context;
import android.content.Intent;
import android.icu.text.Collator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.content.SharedPreferences;

import static java.lang.Integer.parseInt;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getName();
    final Context mContext = this;
    static final boolean DEFAULT_ALLOW_NOTIFICATIONS = true;
    static final int DEFAULT_UPDATE_TIMER = 5000; //milliseconds
    CheckBox allowNotificationsCheckBox;
    Button frequencyButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static String SUFFIX = " seconds";
    private static int MULTIPLIER = 1000;
    private static String[] times = {3 + SUFFIX, 5 + SUFFIX, 10 + SUFFIX, 15 + SUFFIX, 30 + SUFFIX, 60 + SUFFIX};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPreferences.edit();

        //Initialize default variables
        if(!sharedPreferences.contains("initialized")){
            editor.putBoolean("initialized", true);
            editor.putBoolean("allowNotifications", DEFAULT_ALLOW_NOTIFICATIONS);
            editor.putInt("updateTimer", DEFAULT_UPDATE_TIMER);
            editor.apply();
            Log.i(TAG, "SharedPreferences initialized");
        }

        allowNotificationsCheckBox = (CheckBox) findViewById(R.id.settings_chkbx_notifications);
        allowNotificationsCheckBox.setChecked(sharedPreferences.getBoolean("allowNotifications", DEFAULT_ALLOW_NOTIFICATIONS));
        allowNotificationsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(allowNotificationsCheckBox.isChecked()) {
                    editor.putBoolean("allowNotifications", true);
                    editor.apply();
                    Log.i(TAG, "allowNotifications: true");
                }else{
                    editor.putBoolean("allowNotifications", false);
                    editor.apply();
                    Log.i(TAG, "allowNotifications: false");
                }
            }
        });

        frequencyButton = (Button) findViewById(R.id.settings_btn_frequency);
        frequencyButton.setText(sharedPreferences.getInt("updateTimer", DEFAULT_UPDATE_TIMER) / MULTIPLIER + SUFFIX);
        frequencyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Create a SelectionDialog to add coins
                SelectionDialog coinSelection = new SelectionDialog(mContext, times){
                    @Override
                    public void onSelectListener(){
                        int result = parseInt(mListAvailable[selector.getValue()].replace(SUFFIX, "")) * MULTIPLIER;
                        editor.putInt("updateTimer", result);
                        editor.apply();

                        frequencyButton.setText(sharedPreferences.getInt("updateTimer", DEFAULT_UPDATE_TIMER) / MULTIPLIER + SUFFIX);
                        dismiss();
                    }
                };
                coinSelection.setTitle("Set Update Time (seconds)");
                coinSelection.create();
                coinSelection.hideSearchBar();
                coinSelection.setWrapSelectorWheel(false);
                coinSelection.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_watching, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //Order in manner of importance for performance speed
        if (id == R.id.action_watching) {
            final Intent watchingIntent = new Intent(this, WatchingActivity.class);
            startActivity(watchingIntent);
            return true;
        }
        else if (id == R.id.action_settings) {
            //final Intent settingsIntent = new Intent(this, SettingsActivity.class);
            //startActivity(settingsIntent);
            return true;
        }
        else if (id == R.id.action_about) {
            final Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        allowNotificationsCheckBox.setChecked(sharedPreferences.getBoolean("allowNotifications", DEFAULT_ALLOW_NOTIFICATIONS));
        Log.i(TAG, "onRestoreInstanceState");
    }

    public void toast(Context context, String toastDisplay){
        Toast.makeText(context, toastDisplay, Toast.LENGTH_SHORT).show();
    }
}
