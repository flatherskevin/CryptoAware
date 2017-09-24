package com.flathers.cryptoaware;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WatchingActivity extends AppCompatActivity {
    private static final String TAG = WatchingActivity.class.getName();
    final Context mContext = this;
    private static final String STATE_TAG = "StateChange";
    private static final String COIN_VIEW = "CoinView";
    private static final String BUTTON_CLICK = "ButtonClick";
    String[] demoMarkets = new String[] {"Yobit", "Kraken"};
    String[] demoStringValues = new String[] {"LTC","DODGE"};
    CoinList coinList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: this loading onCreate is a rough and dirty way to make it work. Fix the async issue
        coinList = new CoinList(mContext);

        ImageButton addCoinButton = (ImageButton) findViewById(R.id.watching_imgbtn_addCoin);

        addCoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    ArrayList<String> newArrayList = coinList.getCoinArrayList();
                    Log.i(TAG, "coinList: " + newArrayList.toString());

                    String[] coinsAvailable = newArrayList.toArray(new String[newArrayList.size()]);

                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.coin_selection);
                    dialog.setTitle("Add Coin");

                    NumberPicker selector = (NumberPicker) dialog.findViewById(R.id.watching_numpkr_selector);
                    selector.setMinValue(0);
                    //Log.i(TAG, coinsAvailable.toString());
                    selector.setMaxValue(coinsAvailable.length - 1);
                    Arrays.sort(coinsAvailable);
                    selector.setDisplayedValues(coinsAvailable);
                    selector.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                    dialog.create();
                    dialog.show();
                }catch(Exception e){
                    e.printStackTrace();
                    Log.i(TAG, e.getStackTrace().toString());
                    Log.i(TAG, e.toString());
                }
            }
        });

        //This is a demo of generating a list of coin information
        //This will be replaced with saved states of user-added favorites
        //List items will be more complex than just text in the future
        CoinAdapter watchingAdapter = new CoinAdapter(this, demoStringValues);

        //Find list from activity_watching and set the adapter
        LinearLayout mainContainer = (LinearLayout) findViewById(R.id.watching_ll_mainContainer);
        ListView coinListView = (ListView) mainContainer.findViewById(R.id.watching_lv_coinList);
        coinListView.setAdapter(watchingAdapter);

        Log.i(STATE_TAG, "onCreate");
    }

    //Custom adapter to turn demoStringValues into a dynamic ListView
    private class CoinAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] coins;

        //TODO: coins string array will be come far more complex will addition of API
        public CoinAdapter(Context context, String[] coins) {
            super(context, R.layout.coin_view, coins);
            this.context = context;
            this.coins = coins;
        }

        //Generates a custoom ListView item for each item in coins
        //TODO: customize for full extent of API parameters
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
            LinearLayout askLayout = (LinearLayout) row3.findViewById(R.id.watching_ll_askLayout);
            TextView askValue = (TextView) askLayout.findViewById(R.id.watching_txt_askValue);
            LinearLayout bidLayout = (LinearLayout) row3.findViewById(R.id.watching_ll_bidLayout);
            TextView bidValue = (TextView) bidLayout.findViewById(R.id.watching_txt_bidValue);

            //Set onClickListener to open a generic alert window
            editProperties.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //Log the button that was clicked
                    Log.i(BUTTON_CLICK, (coins[position] + " edit properties"));

                    //TODO: Remove the alert dialog, and replace with properties fragment or activity
                    //Create the alert dialog builder
                    //This entire dialog will be removed and replaced with a fragment
                    //It currently serves as a POC of OnClickListener
                    final AlertDialog.Builder futureDevelopment = new AlertDialog.Builder(context);
                    futureDevelopment.setTitle("AlertDialog");
                    futureDevelopment.setMessage("Future development space for " + coins[position]);
                    futureDevelopment.setCancelable(true);

                    AlertDialog alert = futureDevelopment.create();
                    alert.show();


                }
            });

            //Change text to that of the current coin
            nameValue.setText(coins[position]);

            //TODO: load / save states of checkboxes from / to SQL database

            Log.i(COIN_VIEW, "Coin view created for " + coins[position]);

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
            return true;
        }
        else if (id == R.id.action_settings) {
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
        Log.i(STATE_TAG, "onRestoreInstanceState");
    }

}
