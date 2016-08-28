package com.example.shivam.stockr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    @BindView(R.id.loading_spinner)
    ProgressBar loading_spinner;

    @BindView(R.id.graph)
    LineChart graph;

    @BindView(R.id.no_internet_view)
    LinearLayout noInternetView;

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

        Intent intent = getIntent();

        tv_stockName.setText(intent.getStringExtra(QuoteColumns.NAME));
        tv_stockPrice.setText("$" + intent.getStringExtra(QuoteColumns.BIDPRICE));
        tv_stockYearLow.setText("$" + intent.getStringExtra(QuoteColumns.YEARLOW));
        tv_stockYearHigh.setText("$" + intent.getStringExtra(QuoteColumns.YEARHIGH));


        if(Utility.isNetworkAvailable(this)) {
            startLoadingGraph();
        }
        else {
            noInternetView.setVisibility(View.VISIBLE);
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
        dataSet.setColor(R.color.stockr_accent_light_blue_100);

        LineData data = new LineData(dataSet);
        graph.setData(data);
        graph.invalidate();

        loading_spinner.setVisibility(View.GONE);
        graph.setVisibility(View.VISIBLE);
    }

    public void retryLoadingGraph(View view) {
        if(Utility.isNetworkAvailable(this)) {

            noInternetView.setVisibility(View.GONE);
            startLoadingGraph();
        }
    }

    private void startLoadingGraph() {
        loading_spinner.setVisibility(View.VISIBLE);
        historicalDataIntentService = new Intent(this, HistoricalDataIntentService.class);
        historicalDataIntentService.putExtra(QuoteColumns.SYMBOL, getIntent().getStringExtra(QuoteColumns.SYMBOL));
        startService(historicalDataIntentService);
    }


}
