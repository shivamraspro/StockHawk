package com.example.shivam.stockr.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.example.shivam.stockr.rest.Constants;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");

        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();

        //The service was started to add a stock
        if (intent.getStringExtra(Constants.INSTANT_TAG).equals(Constants.TAG_ADD)) {
            args.putString(Constants.SYMBOL, intent.getStringExtra(Constants.SYMBOL));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        // if the service is started to during initiation, the args will be empty
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(Constants.INSTANT_TAG), args));
    }
}
