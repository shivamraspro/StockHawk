package com.example.shivam.stockr.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.shivam.stockr.R;
import com.example.shivam.stockr.data.QuoteColumns;
import com.example.shivam.stockr.data.QuoteProvider;
import com.example.shivam.stockr.rest.Constants;
import com.example.shivam.stockr.rest.QuoteCursorAdapter;
import com.example.shivam.stockr.rest.RecyclerViewItemClickListener;
import com.example.shivam.stockr.rest.RecyclerViewItemDecorator;
import com.example.shivam.stockr.rest.Utility;
import com.example.shivam.stockr.service.StockIntentService;
import com.example.shivam.stockr.service.StockTaskService;
import com.example.shivam.stockr.touch_helper.SimpleItemTouchHelperCallback;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    @BindView(R.id.recycler_view_container)
    CoordinatorLayout recyclerViewContainer;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Intent mServiceIntent;
    private ItemTouchHelper mItemTouchHelper;
    private static final int CURSOR_LOADER_ID = 0;
    private QuoteCursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mCursor;
    boolean isConnected;
    boolean hasPlayServices;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private final static String[] QUOTE_DETAILS_COLUMNS = {
        QuoteColumns.SYMBOL,
        QuoteColumns.NAME,
        QuoteColumns.BIDPRICE,
        QuoteColumns.YEARLOW,
        QuoteColumns.YEARHIGH
    };

    private static final int INDEX_SYMBOL = 0;
    private static final int INDEX_NAME = 1;
    private static final int INDEX_BIDPRICE = 2;
    private static final int INDEX_YEARLOW = 3;
    private static final int INDEX_YEARHIGH = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        hasPlayServices = checkPlayServices();


        isConnected = Utility.isNetworkAvailable(mContext);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        swipeRefreshLayout.setColorSchemeResources(R.color.stockr_blue_800);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStocksList();
            }
        });

        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        mServiceIntent = new Intent(this, StockIntentService.class);

        if (hasPlayServices) {
            if (isConnected) {
                // Run the initialize task service so that some stocks appear upon an empty database
                mServiceIntent.putExtra(Constants.INSTANT_TAG, Constants.TAG_INIT);
                startService(mServiceIntent);
            } else {
                networkSnackBar();
            }
        }

//        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCursorAdapter = new QuoteCursorAdapter(this, null);
        recyclerView.setAdapter(mCursorAdapter);

        final Intent intent = new Intent(this, DetailsActivity.class);

        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        Cursor cursor = getContentResolver().query(
                                QuoteProvider.Quotes.CONTENT_URI,
                                QUOTE_DETAILS_COLUMNS,
                                null,
                                null,
                                QuoteColumns._ID + " desc");

                        if (cursor != null && cursor.moveToPosition(position)) {
                            intent.putExtra(QuoteColumns.SYMBOL, cursor.getString(INDEX_SYMBOL));
                            intent.putExtra(QuoteColumns.NAME, cursor.getString(INDEX_NAME));
                            intent.putExtra(QuoteColumns.BIDPRICE, cursor.getString(INDEX_BIDPRICE));
                            intent.putExtra(QuoteColumns.YEARLOW, cursor.getString(INDEX_YEARLOW));
                            intent.putExtra(QuoteColumns.YEARHIGH, cursor.getString(INDEX_YEARHIGH));
                            startActivity(intent);
                        }
                    }
                }));


        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = Utility.isNetworkAvailable(mContext);
                if (isConnected && hasPlayServices) {
                    new MaterialDialog.Builder(mContext).title(R.string.symbol_search)
                            .content(R.string.content_test)
                            .inputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                            .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // On FAB click, receive user input. Make sure the stock doesn't already exist
                                    // in the DB and proceed accordingly
                                    Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                                            new String[]{QuoteColumns.SYMBOL}, QuoteColumns.SYMBOL + "= ?",
                                            new String[]{input.toString().toUpperCase()}, null);
                                    if (c.getCount() != 0) {
                                        Toast toast =
                                                Toast.makeText(MainActivity.this, "This stock is already saved!",
                                                        Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                                        toast.show();
                                        return;
                                    } else {
                                        // Add the stock to DB
                                        mServiceIntent.putExtra(Constants.INSTANT_TAG, Constants.TAG_ADD);
                                        //stock symbol is always in upper case now
                                        mServiceIntent.putExtra(Constants.QUOTE_SYMBOL, input.toString().toUpperCase());
                                        startService(mServiceIntent);
                                    }
                                }
                            })
                            .show();
                } else {
                    networkSnackBar();
                }

            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(this, R.drawable.divider));

        startPeriodicTask();

        setupStetho();
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        isConnected = Utility.isNetworkAvailable(mContext);
        if (!isConnected)
            networkSnackBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void networkSnackBar() {
        Snackbar snackbar = Snackbar
                .make(recyclerViewContainer, getString(R.string.no_internet_message), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open_settings_label), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                    }
                });
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_change_units) {
            // this is for changing stock changes from percent value to dollar value
            if (Utility.showPercent) {
                item.setIcon(R.drawable.ic_dollar);
            } else {
                item.setIcon(R.drawable.ic_percent);
            }
            Utility.showPercent = !Utility.showPercent;
            this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
        } else if (id == R.id.action_refresh) {
            refreshStocksList();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                QuoteColumns._ID + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        mCursor = data;
        EventBus.getDefault().post(new LoadingEvent(false));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    /**
     * This piece of code is copied from Advanced Android Development Course from Udacity
     * <p/>
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            //happens on main thread?
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void startPeriodicTask() {
        if (isConnected && hasPlayServices) {
            long period = 1800L;
            long flex = 10L;

            // create a periodic task to pull stocks once every 15 minutes after the app has been opened. This
            // is so Widget data stays up to date.
            // The task will persist through system reboots
            PeriodicTask periodicTask = new PeriodicTask.Builder()
                    .setService(StockTaskService.class)
                    .setPeriod(period)
                    .setFlex(flex)
                    .setTag(Constants.PERIODIC_TAG)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setPersisted(true)
                    .setRequiresCharging(false)
                    .build();
            // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
            // are updated.
            GcmNetworkManager.getInstance(this).schedule(periodicTask);
        }
    }

    private void setupStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        );
    }

    public class LoadingEvent {
        public final boolean loading;

        LoadingEvent(boolean loading) {
            this.loading = loading;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopLoading(LoadingEvent loadingEvent) {
        swipeRefreshLayout.setRefreshing(loadingEvent.loading);
    }

    private void refreshStocksList() {
        isConnected = Utility.isNetworkAvailable(mContext);
        if (isConnected) {
            // Run the initialize task service so that some stocks appear upon an empty database
            swipeRefreshLayout.setRefreshing(true);
            mServiceIntent.putExtra(Constants.INSTANT_TAG, Constants.TAG_INIT);
            startService(mServiceIntent);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            networkSnackBar();
        }
    }
}