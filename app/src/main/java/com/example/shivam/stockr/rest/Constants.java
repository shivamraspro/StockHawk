package com.example.shivam.stockr.rest;

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
}
