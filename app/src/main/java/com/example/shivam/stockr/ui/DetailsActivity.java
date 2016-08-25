package com.example.shivam.stockr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.shivam.stockr.R;
import com.example.shivam.stockr.data.QuoteColumns;

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
    }
}
