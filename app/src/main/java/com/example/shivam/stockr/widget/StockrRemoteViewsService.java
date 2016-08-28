package com.example.shivam.stockr.widget;

import android.content.Intent;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.shivam.stockr.R;
import com.example.shivam.stockr.data.QuoteColumns;
import com.example.shivam.stockr.data.QuoteProvider;

/**
 * Created by shivam on 27/08/16.
 */
public class StockrRemoteViewsService extends RemoteViewsService {

    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.YEARLOW,
            QuoteColumns.YEARHIGH,
            QuoteColumns.NAME
    };

    static final int INDEX_SYMBOL = 0;
    static final int INDEX_BIDPRICE = 1;
    static final int INDEX_PERCENT_CHANGE = 2;
    static final int INDEX_YEARLOW = 3;
    static final int INDEX_YEARHIGH = 4;
    static final int INDEX_NAME = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            Cursor cursor = null;

            @Override
            public void onCreate() {

//                cursor = getContentResolver().query(
//                        QuoteProvider.Quotes.CONTENT_URI,
//                        QUOTE_COLUMNS,
//                        null,
//                        null,
//                        QuoteColumns._ID + " desc"
//                );

            }

            @Override
            public void onDataSetChanged() {

                if(cursor != null)
                    cursor.close();

                cursor = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        QUOTE_COLUMNS,
                        null,
                        null,
                        QuoteColumns._ID + " desc"
                );
            }

            @Override
            public void onDestroy() {
                 if(cursor != null) {
                     cursor.close();
                     cursor = null;
                 }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);

                String stockSymbol = cursor.getString(INDEX_SYMBOL);
                String bidPrice = cursor.getString(INDEX_BIDPRICE);
                String percentChange = cursor.getString(INDEX_PERCENT_CHANGE);
                String yearLow = cursor.getString(INDEX_YEARLOW);
                String yearHigh = cursor.getString(INDEX_YEARHIGH);
                String stockName = cursor.getString(INDEX_NAME);

                views.setTextViewText(R.id.stock_symbol, stockSymbol);
                views.setTextViewText(R.id.bid_price, bidPrice);
                views.setTextViewText(R.id.change, percentChange);

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(QuoteColumns.SYMBOL, stockSymbol);
                fillInIntent.putExtra(QuoteColumns.NAME, stockName);
                fillInIntent.putExtra(QuoteColumns.BIDPRICE, bidPrice);
                fillInIntent.putExtra(QuoteColumns.YEARLOW, yearLow);
                fillInIntent.putExtra(QuoteColumns.YEARHIGH, yearHigh);
                views.setOnClickFillInIntent(R.id.list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }

}
