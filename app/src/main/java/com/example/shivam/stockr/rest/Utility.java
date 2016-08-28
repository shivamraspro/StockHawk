package com.example.shivam.stockr.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.shivam.stockr.data.QuoteColumns;
import com.example.shivam.stockr.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utility {

    private static String LOG_TAG = Utility.class.getSimpleName();

    public static final String ACTION_DATA_UPDATED =
            "com.example.shivam.stockr.ACTION_DATA_UPDATED";

    public static boolean showPercent = true;

    public static ArrayList quoteJsonToContentVals(String JSON) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject("results")
                            .getJSONObject("quote");
                    batchOperations.add(buildBatchOperation(jsonObject));
                } else {
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            batchOperations.add(buildBatchOperation(jsonObject));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }

        //This will delete all the old quotes data from database
        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(
                QuoteProvider.Quotes.CONTENT_URI_ISNOTCURRENT);
        batchOperations.add(builder.build());

        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuilder changeBuilder = new StringBuilder(change);
        changeBuilder.insert(0, weight);
        changeBuilder.append(ampersand);
        change = changeBuilder.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString(Constants.QUOTE_CHANGE);
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString(Constants.QUOTE_SYMBOL));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString(Constants.QUOTE_BID_PRICE)));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString(Constants.QUOTE_PERCENT_CHANGE), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.NAME, jsonObject.getString(Constants.QUOTE_NAME));
            builder.withValue(QuoteColumns.YEARHIGH, jsonObject.getString(Constants.QUOTE_YEARHIGH));
            builder.withValue(QuoteColumns.YEARLOW, jsonObject.getString(Constants.QUOTE_YEARLOW));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String getTodayDate() {
        Calendar c = Calendar.getInstance();

        StringBuilder todayDate = new StringBuilder();

        int YEAR = c.get(Calendar.YEAR);
        int MONTH = c.get(Calendar.MONTH) + 1;
        int DATE = c.get(Calendar.DATE);

        todayDate.append(YEAR).append("-");

        if (MONTH < 10)
            todayDate.append("0");

        todayDate.append(MONTH).append("-");

        if (DATE < 10)
            todayDate.append("0");

        todayDate.append(DATE);

        return todayDate.toString();
    }

    public static String getLastYearDate() {
        Calendar c = Calendar.getInstance();

        int YEAR = c.get(Calendar.YEAR);
        int MONTH = c.get(Calendar.MONTH) + 1;
        int DATE = c.get(Calendar.DATE);

        StringBuilder todayDate = new StringBuilder();
        if (!(MONTH == 2 && DATE == 29)) {
            todayDate.append(YEAR - 1).append("-");

            if (MONTH < 10)
                todayDate.append("0");

            todayDate.append(MONTH).append("-");

            if (DATE < 10)
                todayDate.append("0");

            todayDate.append(DATE);
        } else {
            todayDate.append(YEAR - 1).append("-");

            if (MONTH < 10)
                todayDate.append("0");

            todayDate.append(MONTH).append("-");

            if (DATE < 10)
                todayDate.append("0");

            todayDate.append(DATE - 1);
        }

        return todayDate.toString();
    }

    public static ArrayList<Double> getHistoricalData(String JSON) {

        ArrayList<Double> historicalData = new ArrayList<>();

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query").getJSONObject("results");
                jsonArray = jsonObject.getJSONArray("quote");

                if (jsonArray != null && jsonArray.length() != 0) {
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        historicalData.add(jsonArray.getJSONObject(i).getDouble(Constants.QUOTE_DAY_CLOSING_PRICE));
                    }
                }

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON to Arraylist failed: " + e);
        }

        return historicalData;
    }

    public static void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}
