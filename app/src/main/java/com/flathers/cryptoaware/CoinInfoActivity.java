package com.flathers.cryptoaware;

import android.app.DatePickerDialog;
import android.content.Context;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.sql.BatchUpdateException;
import java.util.Calendar;

public class CoinInfoActivity extends AppCompatActivity {
    private static final String TAG = CoinInfoActivity.class.getName();
    final Context mContext = this;
    private String buySell = "";
    private static final String BUY_VALUE = "BUY";
    private static final String SELL_VALUE = "SELL";
    private double coinQty = 0;
    private double price = 0;
    private String date = "";
    private Calendar calendar = Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_info);

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        RadioGroup buySellRadioGroup = (RadioGroup) findViewById(R.id.coinInfo_rgrp_buySell);
        EditText coinQtyEditText = (EditText) findViewById(R.id.coinInfo_edttxt_coinQty);
        EditText priceEditText = (EditText) findViewById(R.id.coininfo_edttxt_price);
        final Button dateButton = (Button) findViewById(R.id.coinInfo_btn_date);
        ImageButton addButton = (ImageButton) findViewById(R.id.coinInfo_imgbtn_addTransaction);

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
        Log.i(TAG, "onRestoreInstanceState");
    }

    public void toast(Context context, String toastDisplay){
        Toast.makeText(context, toastDisplay, Toast.LENGTH_SHORT).show();
    }
}
