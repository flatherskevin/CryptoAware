package com.flathers.cryptoaware;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class WatchingActivity extends AppCompatActivity {
    private static final String TAG = WatchingActivity.class.getName();
    final Context mContext = this;
    private static final String STATE_TAG = "StateChange";
    private static final String COIN_VIEW = "CoinView";
    private static final String BUTTON_CLICK = "ButtonClick";
    private String[] userMarkets = new String[] {"Kraken"};
    private ArrayList<String> userCoins = new ArrayList<String>();
    private CoinList coinList;
    private WatchingCoinsDb watchingCoinsDb;
    private boolean allowNotifications; //SharedPreference
    private int updateTimer; //SharedPreference
    private String[] coinsAvailableSelection; //Used for searching coins available to add

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Make sure database is setup on install
        watchingCoinsDb = new WatchingCoinsDb(mContext);

        //Prepare initial coinList
        coinList = new CoinList(mContext);

        //Render current coins
        renderCoinListView();

        ImageButton addCoinButton = (ImageButton) findViewById(R.id.watching_imgbtn_addCoin);

        addCoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final ArrayList<String> newArrayList = coinList.getCoinArrayList();

                    final String[] coinsAvailable = newArrayList.toArray(new String[newArrayList.size()]);

                    Arrays.sort(coinsAvailable);

                    //Create a SelectionDialog to add coins
                    SelectionDialog coinSelection = new SelectionDialog(mContext, coinsAvailable){
                        @Override
                        public void onSelectListener(){
                            userCoins.add(mListAvailable[selector.getValue()]);
                            renderCoinListView();
                            dismiss();
                        }
                    };
                    coinSelection.setTitle("Add Coin");
                    coinSelection.create();
                    coinSelection.show();
                }catch(Exception e){
                    Log.i(TAG, e.toString());
                }
            }
        });

        final Handler handler = new Handler();
        getSharedPreferences();

        handler.postDelayed(new Runnable(){
            public void run(){

                //Render the coin list
                renderCoinListView();

                //Check SharedPreferences in case settings have been changed
                //TODO: clean this up, this doesn't need to happen this repetatively
                getSharedPreferences();

                //Recursive call after updateTimer SharedPreference delay
                handler.postDelayed(this, updateTimer);
            }
        }, updateTimer);

        Log.i(STATE_TAG, "onCreate");
    }

    private void getSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Initialize default variables
        if(!sharedPreferences.contains("initialized")){
            editor.putBoolean("initialized", true);
            editor.putBoolean("allowNotifications", SettingsActivity.DEFAULT_ALLOW_NOTIFICATIONS);
            editor.putInt("updateTimer", SettingsActivity.DEFAULT_UPDATE_TIMER);
            editor.apply();
            Log.i(TAG, "SharedPreferences initialized");
        }

        allowNotifications = sharedPreferences.getBoolean("allowNotifications", SettingsActivity.DEFAULT_ALLOW_NOTIFICATIONS);

        //Used for API update period
        updateTimer = sharedPreferences.getInt("updateTimer", SettingsActivity.DEFAULT_UPDATE_TIMER);
    }

    public void renderCoinListView(){
        //Update puser coins based on db
        SQLiteDatabase db = watchingCoinsDb.getReadableDatabase();
        String[] cols = {watchingCoinsDb.RAW_FROMSYMBOL};
        Cursor c = db.query(
                watchingCoinsDb.TABLE_NAME,
                cols,
                null,
                null,
                null,
                null,
                null
        );
        while(c.moveToNext() && c != null) {
            String currentCoin = c.getString(c.getColumnIndexOrThrow(watchingCoinsDb.RAW_FROMSYMBOL));

            //Prevent coins from being added to the list again
            if (!userCoins.contains(currentCoin)){
                userCoins.add(currentCoin);
            }
            Log.i(TAG, "userCoin added: " + c.getString(c.getColumnIndexOrThrow(watchingCoinsDb.RAW_FROMSYMBOL)));
        }

        if(userCoins.size() > 0) {
            //Update current information in db
            PriceMultiFull priceMultiFull = new PriceMultiFull(mContext, userCoins);
            priceMultiFull.sendRequest();

            CoinAdapter watchingAdapter = new CoinAdapter(mContext, userCoins);

            //Find list from activity_watching and set the adapter
            LinearLayout mainContainer = (LinearLayout) findViewById(R.id.watching_ll_mainContainer);
            ListView coinListView = (ListView) mainContainer.findViewById(R.id.watching_lv_coinList);

            int listIndex = coinListView.getFirstVisiblePosition();
            View mView = coinListView.getChildAt(0);
            int listTop = (mView == null) ? 0 : (mView.getTop() - coinListView.getPaddingTop());

            coinListView.setAdapter(watchingAdapter);

            coinListView.setSelectionFromTop(listIndex, listTop);
        }
    }

    //Custom adapter to turn demoStringValues into a dynamic ListView
    private class CoinAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> coins;

        public CoinAdapter(Context context, ArrayList<String> coins) {
            super(context, R.layout.coin_view, coins);
            this.context = context;
            this.coins = coins;
        }

        //Generates a custom ListView item for each item in userCoins
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.coin_view, parent, false);

            //Find all items within the coin_view...these will all eventually be needed
            RelativeLayout coinContainer = (RelativeLayout) rowView.findViewById(R.id.watching_rl_coinContainer);
            TableLayout coinTable = (TableLayout) coinContainer.findViewById(R.id.watching_tl_coinTable);
            TableRow row1 = (TableRow) coinTable.findViewById(R.id.watching_tr_row1);
            TableRow row2 = (TableRow) coinTable.findViewById(R.id.watching_tr_row2);
            TableRow row3 = (TableRow) coinTable.findViewById(R.id.watching_tr_row3);
            TextView nameValue = (TextView) row1.findViewById(R.id.watching_txt_nameValue);
            LinearLayout changeLayout = (LinearLayout) row1.findViewById(R.id.watching_ll_changeLayout);
            FrameLayout arrowLayout = (FrameLayout) changeLayout.findViewById(R.id.watching_rl_arrowLayout);
            ImageView upArrow = (ImageView) changeLayout.findViewById(R.id.watching_upArrow);
            ImageView downArrow = (ImageView) changeLayout.findViewById(R.id.watching_downArrow);
            TextView changeValue = (TextView) changeLayout.findViewById(R.id.watching_txt_changeValue);
            ImageButton editProperties = (ImageButton) row1.findViewById(R.id.watching_imgbtn_editProperties);
            LinearLayout walletLayout = (LinearLayout) row2.findViewById(R.id.watching_ll_walletLayout);
            TextView walletValue = (TextView) walletLayout.findViewById(R.id.watching_txt_walletValue);
            LinearLayout profitLayout = (LinearLayout) row2.findViewById(R.id.watching_ll_profitLayout);
            TextView profitValue = (TextView) profitLayout.findViewById(R.id.watching_txt_profitValue);
            LinearLayout priceLayout = (LinearLayout) row3.findViewById(R.id.watching_ll_priceLayout);
            TextView priceValue = (TextView) priceLayout.findViewById(R.id.watching_txt_priceValue);
            LinearLayout lastMarketLayout = (LinearLayout) row3.findViewById(R.id.watching_ll_lastMarketLayout);
            TextView lastMarketValue = (TextView) lastMarketLayout.findViewById(R.id.watching_txt_lastMarketValue);

            //Set onClickListener to open a generic alert window
            editProperties.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //Log the button that was clicked
                    Log.i(BUTTON_CLICK, (coins.get(position) + " edit properties"));

                    //TODO: Remove the alert dialog, and replace with properties fragment or activity
                    //Create the alert dialog builder
                    //This entire dialog will be removed and replaced with a fragment
                    //It currently serves as a POC of OnClickListener
                    final AlertDialog.Builder futureDevelopment = new AlertDialog.Builder(context);
                    futureDevelopment.setTitle("AlertDialog");
                    futureDevelopment.setMessage("Future development space for " + coins.get(position));
                    futureDevelopment.setCancelable(true);

                    AlertDialog alert = futureDevelopment.create();
                    alert.show();
                }
            });

            //Change text to that of the current coin
            nameValue.setText(coins.get(position));

            //load states of coins from SQL database
            SQLiteDatabase db = watchingCoinsDb.getReadableDatabase();
            String[] cols = {watchingCoinsDb.DISPLAY_CHANGEPCT24HOUR,
                    watchingCoinsDb.DISPLAY_PRICE,
                    watchingCoinsDb.RAW_LASTMARKET};
            Cursor c = db.query(
                    watchingCoinsDb.TABLE_NAME,
                    cols,
                    "RAW_FROMSYMBOL=?",
                    new String[]{coins.get(position)},
                    null,
                    null,
                    null
            );
            if(c != null && c.moveToFirst()) {
                c.moveToFirst();
                double display_changePct24Hour = c.getDouble(c.getColumnIndexOrThrow(watchingCoinsDb.DISPLAY_CHANGEPCT24HOUR));
                if(display_changePct24Hour >= 0){
                    changeValue.setTextColor(getResources().getColor(R.color.positive));
                    downArrow.setVisibility(View.INVISIBLE);
                    upArrow.setVisibility(View.VISIBLE);
                } else{
                    changeValue.setTextColor(getResources().getColor(R.color.negative));
                    upArrow.setVisibility(View.INVISIBLE);
                    downArrow.setVisibility(View.VISIBLE);
                }

                changeValue.setText(display_changePct24Hour + "%");
                priceValue.setText(c.getString(c.getColumnIndexOrThrow(watchingCoinsDb.DISPLAY_PRICE)));
                lastMarketValue.setText(c.getString(c.getColumnIndexOrThrow(watchingCoinsDb.RAW_LASTMARKET)));

                c.close();
            }
            Log.i(COIN_VIEW, "Coin view created for " + coins.get(position));

            return rowView;
        }
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
            //final Intent watchingIntent = new Intent(this, WatchingActivity.class);
            //startActivity(watchingIntent);
            return true;
        }
        else if (id == R.id.action_settings) {
            final Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
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
        Log.i(STATE_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(STATE_TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(STATE_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(STATE_TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(STATE_TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(STATE_TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(STATE_TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        renderCoinListView();
        Log.i(STATE_TAG, "onRestoreInstanceState");
    }

    public void toast(Context context, String toastDisplay){
        Toast.makeText(context, toastDisplay, Toast.LENGTH_SHORT).show();
    }
}