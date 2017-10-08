package com.flathers.cryptoaware;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by flath on 10/8/2017.
 */

public class TransactionsDb extends SQLiteOpenHelper {
    private static final String TAG = TransactionsDb.class.getName();
    public static final String DB_NAME = "CryptoAware.db";
    public static final String TABLE_NAME = "transactions";
    public static final int DB_VERSION = 1;
    public static final String RAW_FROMSYMBOL = WatchingCoinsDb.RAW_FROMSYMBOL;
    public static final String TRANSACTION_PRICE = "TRANSACTION_PRICE";
    public static final String MARKET_PRICE = "MARKET_PRICE";
    public static final String MARKET = "MARKET";
    public static final String TRANSACTION_DATE = "TRANSACTION_DATE";

    TransactionsDb(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        //db.execSQL(CREATE_TABLE);
        Log.i(TAG, "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
