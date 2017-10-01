package com.flathers.cryptoaware;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.content.SharedPreferences;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getName();
    final Context mContext = this;
    static final boolean DEFAULT_ALLOW_NOTIFICATIONS = true;
    static final int DEFAULT_UPDATE_TIMER = 7000; //milliseconds
    CheckBox allowNotificationsCheckBox;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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
