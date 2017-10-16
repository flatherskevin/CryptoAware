package com.flathers.cryptoaware;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Math.abs;

public class CoinInfoActivity extends AppCompatActivity {
    private static final String TAG = CoinInfoActivity.class.getName();
    final Context mContext = this;
    private static final String BUYSELL_DEFAULT = "BUY";
    private static final double COINQTY_DEFAULT = 0;
    private static final double PRICE_DEFAULT = 0;
    private static final double TOTAL_BTC_DEFAULT = 0;
    private static final String DATE_DEFAULT = "01-01-2017";
    private String buySell = "";
    private static final String BUY_VALUE = "BUY";
    private static final String SELL_VALUE = "SELL";
    private double coinQty = 0;
    private double price = 0;
    private double total_btc = 0;
    private String date = "";
    private Calendar calendar = Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;
    private TransactionsDb transactionsDb;
    SQLiteDatabase dbRead;
    SQLiteDatabase dbWrite;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String currentCoin;
    private ListView transactionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_info);

        //Setup sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        //editor = sharedPreferences.edit();
        currentCoin = sharedPreferences.getString("selectedCoin", "");
        Log.i(TAG, "currentCoin: " + currentCoin);

        setTitle(currentCoin);

        //Make sure database is setup on load
        transactionsDb = new TransactionsDb(mContext);
        dbRead = transactionsDb.getReadableDatabase();
        dbWrite = transactionsDb.getWritableDatabase();

        transactionsListView = (ListView) findViewById(R.id.transaction_lv_transactions);

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);


        final RadioGroup buySellRadioGroup = (RadioGroup) findViewById(R.id.coinInfo_rgrp_buySell);
        final EditText coinQtyEditText = (EditText) findViewById(R.id.coinInfo_edttxt_coinQty);
        final EditText priceEditText = (EditText) findViewById(R.id.coininfo_edttxt_price);
        final Button dateButton = (Button) findViewById(R.id.coinInfo_btn_date);
        ImageButton addButton = (ImageButton) findViewById(R.id.coinInfo_imgbtn_addTransaction);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Get default state of the RadioGroup
        switch(buySellRadioGroup.getCheckedRadioButtonId()){
            case R.id.coinInfo_rbtn_buy:
                buySell = BUY_VALUE;
                break;
            case R.id.coinInfo_rbtn_sell:
                buySell = SELL_VALUE;
                break;
            default:
                buySell = BUY_VALUE;
        }

        //Set a listener for when the RadioGroup changes
        buySellRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId){
                switch(checkedId){
                    case R.id.coinInfo_rbtn_buy:
                        buySell = BUY_VALUE;
                        break;
                    case R.id.coinInfo_rbtn_sell:
                        buySell = SELL_VALUE;
                        break;
                    default:
                        buySell = BUY_VALUE;
                }
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePickerDialog dateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day){

                    }
                }, mYear, mMonth, mDay);

                //Dialog must be created before adding click functionality
                dateDialog.create();

                Button btnPositive = (Button) dateDialog.getButton(dateDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePicker datePicker = (DatePicker) dateDialog.getDatePicker();
                        date = (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth() + "-" + datePicker.getYear();
                        dateButton.setText(date);
                        dateDialog.dismiss();
                    }
                });

                Button btnNegative = (Button) dateDialog.getButton(dateDialog.BUTTON_NEGATIVE);
                btnNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dateDialog.dismiss();
                    }
                });

                dateDialog.show();
            }
        });

        coinQtyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    coinQty = Double.parseDouble(editable.toString());
                } catch(Exception err){
                    Log.i(TAG, err.getStackTrace().toString());
                }
            }
        });

        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    price = Double.parseDouble(editable.toString());
                } catch(Exception err){
                    Log.i(TAG, err.getStackTrace().toString());
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Perform calculations
                coinQty = (buySell.equals("BUY")) ? abs(coinQty) : -abs(coinQty);
                total_btc = price * coinQty;

                //Add values to transactions table
                ContentValues contentValues = new ContentValues();
                contentValues.put(transactionsDb.BUY_SELL, buySell);
                contentValues.put(transactionsDb.TRANSACTION_QTY, abs(coinQty));
                contentValues.put(transactionsDb.MARKET_PRICE, price);
                contentValues.put(transactionsDb.TRANSACTION_DATE, date);
                contentValues.put(transactionsDb.TOTAL_BTC, total_btc);
                contentValues.put(transactionsDb.RAW_FROMSYMBOL, currentCoin);
                int callback = (int) dbWrite.insertWithOnConflict(transactionsDb.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                if(callback == -1){
                    Log.i(TAG, "Error occured while inserting transaction");
                }

                renderTransactionsList();

                coinQty = COINQTY_DEFAULT;
                price = PRICE_DEFAULT;
                total_btc = TOTAL_BTC_DEFAULT;
                date = DATE_DEFAULT;

                coinQtyEditText.getText().clear();
                priceEditText.getText().clear();
                dateButton.setText("DATE");
            }
        });

        renderTransactionsList();

        Log.i(TAG, "onCreate");
    }

    private class TransactionAdapter extends ArrayAdapter<String>{
        private final Context context;
        private final ArrayList<String> transactions;

        public TransactionAdapter(Context context, ArrayList<String> transactions){
            super(context, R.layout.transaction_view, transactions);
            this.context = context;
            this.transactions = transactions;
        }

        public View getView(final int position, View convertView, ViewGroup parent){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.transaction_view, parent, false);

            TextView idTextView = (TextView) rowView.findViewById(R.id.transaction_txt_tableId);
            TextView buySellTextView = (TextView) rowView.findViewById(R.id.transaction_txt_buySell);
            TextView dateTextView = (TextView) rowView.findViewById(R.id.transaction_txt_date);
            TextView qtyTextView = (TextView) rowView.findViewById(R.id.transaction_txt_qty);
            TextView marketPriceTextView = (TextView) rowView.findViewById(R.id.transaction_txt_marketPrice);

            String[] cols = {
                    TransactionsDb.ID,
                    TransactionsDb.BUY_SELL,
                    TransactionsDb.MARKET_PRICE,
                    TransactionsDb.TRANSACTION_DATE,
                    TransactionsDb.TRANSACTION_QTY
            };

            Cursor c = dbRead.query(
                    TransactionsDb.TABLE_NAME,
                    cols,
                    "ID=?",
                    new String[]{transactions.get(position)},
                    null,
                    null,
                    null
            );

            if(c != null && c.moveToFirst()) {
                c.moveToFirst();

                //Log Id at hand
                Log.i(TAG, "Id queried: " + c.getString(c.getColumnIndexOrThrow(TransactionsDb.ID)));

                //Set TextViews from query
                idTextView.setText(c.getString(c.getColumnIndexOrThrow(TransactionsDb.ID)));
                buySellTextView.setText(c.getString(c.getColumnIndexOrThrow(TransactionsDb.BUY_SELL)));
                dateTextView.setText(c.getString(c.getColumnIndexOrThrow(TransactionsDb.TRANSACTION_DATE)));
                qtyTextView.setText(c.getString(c.getColumnIndexOrThrow(TransactionsDb.TRANSACTION_QTY)));
                marketPriceTextView.setText(c.getString(c.getColumnIndexOrThrow(TransactionsDb.MARKET_PRICE)));

                c.close();
            }

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder confirmation = new AlertDialog.Builder(mContext);
                    confirmation.setTitle("Confirmation");
                    confirmation.setMessage("Do you really want to delete this transaction?");
                    confirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(TAG, "Begin coin deletion for: " + transactions.get(position));

                            //Delete transaction entries
                            //Must happen before watching_coins deletion due to FK reference for PK
                            dbWrite.delete(TransactionsDb.TABLE_NAME, TransactionsDb.ID + "=?", new String[]{transactions.get(position)});
                            Log.i(TAG, "Transactions deleted for: " + transactions.get(position));

                            //Delete item from ListView
                            remove(transactions.get(position));

                            //Re-render the coin view
                            notifyDataSetChanged();
                            renderTransactionsList();

                            dialog.dismiss();
                        }
                    });

                    confirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    confirmation.create();
                    confirmation.show();
                    return false;
                }
            });

            return rowView;
        }
    }

    private void renderTransactionsList(){

        //Find all IDs where RAW_FROMSYMBOL is the currentCoin
        ArrayList<String> idList = new ArrayList<String>();

        String[] cols = {transactionsDb.ID};

        Cursor c = dbRead.query(
                transactionsDb.TABLE_NAME,
                cols,
                "RAW_FROMSYMBOL=?",
                new String[]{currentCoin},
                null,
                null,
                null
        );

        while(c.moveToNext() && c != null) {
            String currentId = c.getString(c.getColumnIndexOrThrow(transactionsDb.ID));

            //Prevent coins from being added to the list again
            if (!idList.contains(currentId)){
                idList.add(currentId);
            }
            Log.i(TAG, currentCoin + " transaction ID added: " + c.getString(c.getColumnIndexOrThrow(transactionsDb.ID)));
        }

        Log.i(TAG, "idList: " + idList);

        if(idList.size() > 0) {
            TransactionAdapter transactionAdapter = new TransactionAdapter(mContext, idList);

            int listIndex = transactionsListView.getFirstVisiblePosition();
            View mView = transactionsListView.getChildAt(0);
            int listTop = (mView == null) ? 0 : (mView.getTop() - transactionsListView.getPaddingTop());

            transactionsListView.setAdapter(transactionAdapter);

            //transactionsListView.setSelectionFromTop(listIndex, listTop);

            Log.i(TAG, "transactionsListView rendered");
        }

        c.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //renderTransactionsList();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //renderTransactionsList();
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
        //renderTransactionsList();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbRead.close();
        dbWrite.close();
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
        renderTransactionsList();
        Log.i(TAG, "onRestoreInstanceState");
    }

    public void toast(Context context, String toastDisplay){
        Toast.makeText(context, toastDisplay, Toast.LENGTH_SHORT).show();
    }
}
