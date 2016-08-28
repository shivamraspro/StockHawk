package com.example.shivam.stockr.rest;

import com.example.shivam.stockr.data.QuoteColumns;

/**
 * Created by shivam on 13/08/16.
 */
public class Constants {

    public final static String PERIODIC_TAG = "periodic";
    public final static String INSTANT_TAG = "instant";
    public final static String TAG_INIT = "init";
    public final static String TAG_ADD = "add";
    public final static String BASE_URL = "https://query.yahooapis.com/v1/public/yql?q=";
    public final static String END_URL = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    public final static String CHAR_SET_NAME = "UTF-8";

    //Names of entities in json input
    public final static String QUOTE_SYMBOL = "symbol";
    public final static String QUOTE_PERCENT_CHANGE = "ChangeinPercent";
    public final static String QUOTE_CHANGE = "Change";
    public final static String QUOTE_BID_PRICE = "Bid";
    public final static String QUOTE_NAME = "Name";
    public final static String QUOTE_YEARLOW = "YearLow";
    public final static String QUOTE_YEARHIGH = "YearHigh";

    public final static String QUOTE_DAY_CLOSING_PRICE = "Close";


    //columns for DetailsActivity
    public final static String[] QUOTE_DETAILS_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.NAME,
            QuoteColumns.BIDPRICE,
            QuoteColumns.YEARLOW,
            QuoteColumns.YEARHIGH
    };

    public static final int INDEX_DETAILS_SYMBOL = 0;
    public static final int INDEX_DETAILS_NAME = 1;
    public static final int INDEX_DETAILS_BIDPRICE = 2;
    public static final int INDEX_DETAILS_YEARLOW = 3;
    public static final int INDEX_DETAILS_YEARHIGH = 4;


    //columns for MainActivity
    public final static String[] QUOTE_MAIN_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.ISUP
    };

    public static final int INDEX_MAIN_ID = 0;
    public static final int INDEX_MAIN_SYMBOL = 1;
    public static final int INDEX_MAIN_BIDPRICE = 2;
    public static final int INDEX_MAIN_PERCENT_CHANGE = 3;
    public static final int INDEX_MAIN_CHANGE = 4;
    public static final int INDEX_MAIN_ISUP = 5;


    //columns for StockrRemoteViewsService
    public static final String[] QUOTE_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.YEARLOW,
            QuoteColumns.YEARHIGH,
            QuoteColumns.NAME
    };

    public static final int INDEX_REMOTE_SYMBOL = 0;
    public static final int INDEX_REMOTE_BIDPRICE = 1;
    public static final int INDEX_REMOTE_PERCENT_CHANGE = 2;
    public static final int INDEX_REMOTE_YEARLOW = 3;
    public static final int INDEX_REMOTE_YEARHIGH = 4;
    public static final int INDEX_REMOTE_NAME = 5;
}
