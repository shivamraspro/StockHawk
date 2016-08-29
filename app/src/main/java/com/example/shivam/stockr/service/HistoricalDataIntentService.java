package com.example.shivam.stockr.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.shivam.stockr.data.QuoteColumns;
import com.example.shivam.stockr.rest.Constants;
import com.example.shivam.stockr.rest.GraphData;
import com.example.shivam.stockr.rest.Utility;
import com.example.shivam.stockr.ui.DetailsActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by shivam on 25/08/16.
 */
public class HistoricalDataIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HistoricalDataIntentService(String name) {
        super(name);
    }

    public HistoricalDataIntentService() {
        super(HistoricalDataIntentService.class.getName());
    }

    private OkHttpClient client = new OkHttpClient();
    private String LOG_TAG = HistoricalDataIntentService.class.getSimpleName();


    @Override
    protected void onHandleIntent(Intent intent) {

        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            String endDate = Utility.getTodayDate();
            String startDate = Utility.getLastYearDate();

            urlStringBuilder.append(Constants.BASE_URL);
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = ", Constants.CHAR_SET_NAME));
            urlStringBuilder.append(URLEncoder.encode("\"" + intent.getStringExtra(QuoteColumns.SYMBOL) + "\"", Constants.CHAR_SET_NAME));
            urlStringBuilder.append(URLEncoder.encode(" and startDate = ", Constants.CHAR_SET_NAME));
            urlStringBuilder.append(URLEncoder.encode("\"" + startDate + "\"", Constants.CHAR_SET_NAME));
            urlStringBuilder.append(URLEncoder.encode(" and endDate = ", Constants.CHAR_SET_NAME));
            urlStringBuilder.append(URLEncoder.encode("\"" + endDate + "\"", Constants.CHAR_SET_NAME));
            urlStringBuilder.append(Constants.END_URL);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String urlString;
        String getResponse;

        if (urlStringBuilder != null) {
            urlString = urlStringBuilder.toString();

            try {
                getResponse = fetchData(urlString);

                GraphData graphData = Utility.getHistoricalData(getResponse);

               EventBus.getDefault().post(new DetailsActivity.GraphDataReadyEvent(graphData));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
