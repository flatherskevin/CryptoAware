package com.flathers.cryptoaware;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.IDNA;
import android.util.Log;

/**
 * Created by flath on 10/8/2017.
 */

public class TransactionsDb extends SQLiteOpenHelper {
    private static final String TAG = TransactionsDb.class.getName();
    public static final String DB_NAME = "CryptoAware.db";
    public static final String TABLE_NAME = "transactions";
    public static final int DB_VERSION = 1;
    public static final String ID = "ID";
    public static final String RAW_FROMSYMBOL = WatchingCoinsDb.RAW_FROMSYMBOL;
    public static final String TRANSACTION_QTY = "TRANSACTION_QTY";
    public static final String MARKET_PRICE = "MARKET_PRICE_BTC";
    public static final String TRANSACTION_DATE = "TRANSACTION_DATE";
    public static final String TOTAL_BTC = "TOTAL_BTC";
    public static final String BUY_SELL = "BUY_SELL";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME                +   " ("
            + ID                        +   " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RAW_FROMSYMBOL            +   " TEXT,"
            + BUY_SELL                  +   " TEXT,"
            + TRANSACTION_QTY           +   " REAL,"
            + TRANSACTION_DATE          +   " TEXT,"
            + MARKET_PRICE              +   " REAL,"
            + TOTAL_BTC                 +   " REAL,"
            + "FOREIGN KEY (" + RAW_FROMSYMBOL +") REFERENCES " + WatchingCoinsDb.TABLE_NAME + "(" + WatchingCoinsDb.RAW_FROMSYMBOL + "));";

    TransactionsDb(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
        Log.i(TAG, "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
