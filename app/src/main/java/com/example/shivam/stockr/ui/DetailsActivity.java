package com.example.shivam.stockr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shivam.stockr.R;
import com.example.shivam.stockr.data.QuoteColumns;
import com.example.shivam.stockr.rest.Utility;
import com.example.shivam.stockr.service.HistoricalDataIntentService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.stock_name)
    TextView tv_stockName;

    @BindView(R.id.stock_price)
    TextView tv_stockPrice;

    @BindView(R.id.stock_year_low)
    TextView tv_stockYearLow;

    @BindView(R.id.stock_year_high)
    TextView tv_stockYearHigh;

    @BindView(R.id.graph_container)
    FrameLayout graphContainer;

    @BindView(R.id.loading_view)
    TextView tv_loading;

    @BindView(R.id.graph)
    LineChart graph;

    private Intent historicalDataIntentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ButterKnife.bind(this);

        graphContainer.removeView(graph);

        Intent intent = getIntent();

        tv_stockName.setText(intent.getStringExtra(QuoteColumns.NAME));
        tv_stockPrice.setText("$" + intent.getStringExtra(QuoteColumns.BIDPRICE));
        tv_stockYearLow.setText("$" + intent.getStringExtra(QuoteColumns.YEARLOW));
        tv_stockYearHigh.setText("$" + intent.getStringExtra(QuoteColumns.YEARHIGH));

        String stockSymbol = intent.getStringExtra(QuoteColumns.SYMBOL);

        if(Utility.isNetworkAvailable(this)) {
            historicalDataIntentService = new Intent(this, HistoricalDataIntentService.class);
            historicalDataIntentService.putExtra(QuoteColumns.SYMBOL, stockSymbol);
            startService(historicalDataIntentService);
        }
        else {
            Toast.makeText(this, getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public static class GraphDataReadyEvent {
        ArrayList<Double> graphData;

        public GraphDataReadyEvent(ArrayList<Double> graphData) {
            this.graphData = graphData;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGraphDataReady(GraphDataReadyEvent gdre) {

        ArrayList<Entry> entries = new ArrayList<>();

        for(int i = 0; i < gdre.graphData.size(); i++) {
            entries.add(new Entry(i+1, gdre.graphData.get(i).floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(R.color.accent_light_blue_100);

        LineData data = new LineData(dataSet);
        graph.setData(data);
        graph.invalidate();

        graphContainer.removeView(tv_loading);
        graphContainer.addView(graph);
    }


}
