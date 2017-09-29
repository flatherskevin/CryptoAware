package com.flathers.cryptoaware;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
                    //
                    ArrayList<String> newArrayList = coinList.getCoinArrayList();

                    final String[] coinsAvailable = newArrayList.toArray(new String[newArrayList.size()]);

                    //Create a dialog to choose coins from
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.coin_selection);
                    dialog.setTitle("Add Coin");
                    Button btnSelect = (Button) dialog.findViewById(R.id.watching_btn_confirm);
                    Button btnCancel = (Button) dialog.findViewById(R.id.watching_btn_cancel);

                    //Usage of a NumberPicker is satisfactory for now to choose coins
                    //TODO: add a search bar since there are so many coins
                    final NumberPicker selector = (NumberPicker) dialog.findViewById(R.id.watching_numpkr_selector);
                    selector.setMinValue(0);
                    selector.setMaxValue(coinsAvailable.length - 1);
                    Arrays.sort(coinsAvailable);
                    selector.setDisplayedValues(coinsAvailable);
                    selector.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                    //Add selected coin to userCoins on clicking select
                    btnSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userCoins.add(coinsAvailable[selector.getValue()]);
                            renderCoinListView();
                            dialog.dismiss();
                        }
                    });

                    //Dismiss dialog if on clicking cancel
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.create();
                    dialog.show();
                }catch(Exception e){
                    Log.i(TAG, e.toString() + e.getStackTrace());
                }
            }
        });

        final Handler handler = new Handler();
        final int delay = 10000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                renderCoinListView();
                handler.postDelayed(this, delay);
            }
        }, delay);



        Log.i(STATE_TAG, "onCreate");
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

            CoinAdapter watchingAdapter = new CoinAdapter(this, userCoins);

            //Find list from activity_watching and set the adapter
            LinearLayout mainContainer = (LinearLayout) findViewById(R.id.watching_ll_mainContainer);
            ListView coinListView = (ListView) mainContainer.findViewById(R.id.watching_lv_coinList);
            coinListView.setAdapter(watchingAdapter);
        }
    }

    //Custom adapter to turn demoStringValues into a dynamic ListView
    private class CoinAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> coins;

        //TODO: coins string array will be come far more complex will addition of API
        public CoinAdapter(Context context, ArrayList<String> coins) {
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
        renderCoinListView();
        Log.i(STATE_TAG, "onRestoreInstanceState");
    }

    public void toast(Context context, String toastDisplay){
        Toast.makeText(context, toastDisplay, Toast.LENGTH_SHORT).show();
    }
}