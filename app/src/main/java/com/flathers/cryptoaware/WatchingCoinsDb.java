package com.flathers.cryptoaware;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by flath on 9/21/2017.
 */

public class WatchingCoinsDb extends SQLiteOpenHelper{
    private static final String TAG = WatchingCoinsDb.class.getName();
    public static final String DB_NAME = "CryptoAware.db";
    public static final String TABLE_NAME = "watching_coins";
    public static final int DB_VERSION = 1;
    public static final String RAW_TYPE = "RAW_TYPE";
    public static final String RAW_MARKET = "RAW_MARKET";
    public static final String RAW_FROMSYMBOL = "RAW_FROMSYMBOL";
    public static final String RAW_TOSYMBOL = "RAW_TOSYMBOL";
    public static final String RAW_FLAGS = "RAW_FLAGS";
    public static final String RAW_PRICE = "RAW_PRICE";
    public static final String RAW_LASTUPDATE = "RAW_LASTUPDATE";
    public static final String RAW_LASTVOLUME = "RAW_LASTVOLUME";
    public static final String RAW_LASTVOLUMETO = "RAW_LASTVOLUMETO";
    public static final String RAW_LASTTRADEID = "RAW_LASTTRADEID";
    public static final String RAW_VOLUME24HOUR = "RAW_VOLUME24HOUR";
    public static final String RAW_VOLUME24HOURTO = "RAW_VOLUME24HOURTO";
    public static final String RAW_OPEN24HOUR = "RAW_OPEN24HOUR";
    public static final String RAW_HIGH24HOUR = "RAW_HIGH24HOUR";
    public static final String RAW_LOW24HOUR = "RAW_LOW24HOUR";
    public static final String RAW_LASTMARKET = "RAW_LASTMARKET";
    public static final String RAW_CHANGE24HOUR = "RAW_CHANGE24HOUR";
    public static final String RAW_CHANGEPCT24HOUR = "RAW_CHANGEPCT24HOUR";
    public static final String RAW_SUPPLY = "RAW_SUPPLY";
    public static final String RAW_MKTCAP = "RAW_MKTCAP";
    public static final String DISPLAY_MARKET = "DISPLAY_MARKET";
    public static final String DISPLAY_FROMSYMBOL = "DISPLAY_FROMSYMBOL";
    public static final String DISPLAY_TOSYMBOL = "DISPLAY_TOSYMBOL";
    public static final String DISPLAY_PRICE = "DISPLAY_PRICE";
    public static final String DISPLAY_LASTUPDATE = "DISPLAY_LASTUPDATE";
    public static final String DISPLAY_LASTVOLUME = "DISPLAY_LASTVOLUME";
    public static final String DISPLAY_LASTVOLUMETO = "DISPLAY_LASTVOLUMETO";
    public static final String DISPLAY_LASTTRADEID = "DISPLAY_LASTTRADEID";
    public static final String DISPLAY_VOLUME24HOUR = "DISPLAY_VOLUME24HOUR";
    public static final String DISPLAY_VOLUME24HOURTO = "DISPLAY_VOLUME24HOURTO";
    public static final String DISPLAY_OPEN24HOUR = "DISPLAY_OPEN24HOUR";
    public static final String DISPLAY_HIGH24HOUR = "DISPLAY_HIGH24HOUR";
    public static final String DISPLAY_LOW24HOUR = "DISPLAY_LOW24HOUR";
    public static final String DISPLAY_LASTMARKET = "DISPLAY_LASTMARKET";
    public static final String DISPLAY_CHANGE24HOUR = "DISPLAY_CHANGE24HOUR";
    public static final String DISPLAY_CHANGEPCT24HOUR = "DISPLAY_CHANGEPCT24HOUR";
    public static final String DISPLAY_SUPPLY = "DISPLAY_SUPPLY";
    public static final String DISPLAY_MKTCAP = "DISPLAY_MKTCAP";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME                +   " ("
            + RAW_TYPE                  +   " INTEGER,"
            + RAW_MARKET                +   " TEXT,"
            + RAW_FROMSYMBOL            +   " TEXT,"
            + RAW_TOSYMBOL              +   " TEXT,"
            + RAW_FLAGS                 +   " INTEGER,"
            + RAW_PRICE                 +   " REAL,"
            + RAW_LASTUPDATE            +   " INTEGER,"
            + RAW_LASTVOLUME            +   " REAL,"
            + RAW_LASTVOLUMETO          +   " REAL,"
            + RAW_LASTTRADEID           +   " INTEGER,"
            + RAW_VOLUME24HOUR          +   " REAL,"
            + RAW_VOLUME24HOURTO        +   " REAL,"
            + RAW_OPEN24HOUR            +   " REAL,"
            + RAW_HIGH24HOUR            +   " REAL,"
            + RAW_LOW24HOUR             +   " REAL,"
            + RAW_LASTMARKET            +   " TEXT,"
            + RAW_CHANGE24HOUR          +   " REAL,"
            + RAW_CHANGEPCT24HOUR       +   " REAL,"
            + RAW_SUPPLY                +   " REAL,"
            + RAW_MKTCAP                +   " REAL,"
            + DISPLAY_MARKET            +   " TEXT,"
            + DISPLAY_FROMSYMBOL        +   " TEXT,"
            + DISPLAY_TOSYMBOL          +   " TEXT,"
            + DISPLAY_PRICE             +   " REAL,"
            + DISPLAY_LASTUPDATE        +   " INTEGER,"
            + DISPLAY_LASTVOLUME        +   " REAL,"
            + DISPLAY_LASTVOLUMETO      +   " REAL,"
            + DISPLAY_LASTTRADEID       +   " INTEGER,"
            + DISPLAY_VOLUME24HOUR      +   " REAL,"
            + DISPLAY_VOLUME24HOURTO    +   " REAL,"
            + DISPLAY_OPEN24HOUR        +   " REAL,"
            + DISPLAY_HIGH24HOUR        +   " REAL,"
            + DISPLAY_LOW24HOUR         +   " REAL,"
            + DISPLAY_LASTMARKET        +   " TEXT,"
            + DISPLAY_CHANGE24HOUR      +   " REAL,"
            + DISPLAY_CHANGEPCT24HOUR   +   " REAL,"
            + DISPLAY_SUPPLY            +   " REAL,"
            + DISPLAY_MKTCAP            +   " REAL,"
            + "PRIMARY KEY(" + RAW_FROMSYMBOL + "));";



    WatchingCoinsDb(Context context){
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
