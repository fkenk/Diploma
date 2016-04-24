package arthurveslo.my.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import arthurveslo.my.myapplication.adapters.AdapterKindsOfSport;
import arthurveslo.my.myapplication.adapters.SpinnerModel;
import arthurveslo.my.myapplication.common.logger.Log;
import arthurveslo.my.myapplication.common.logger.LogView;
import arthurveslo.my.myapplication.common.logger.LogWrapper;
import arthurveslo.my.myapplication.common.logger.MessageOnlyLogFilter;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * This sample demonstrates how to use the Sensors API of the Google Fit platform to find
 * available data sources and to register/unregister listeners to those sources. It also
 * demonstrates how to authenticate a user with Google Play Services.
 */
public class AddActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, android.location.LocationListener {
    public static final String TAG = "BasicSensorsApi";
    private GoogleMap mMap;
    private static Context mContext;
    // [START auth_variable_references]
    private GoogleApiClient mClient = null;
    // [END auth_variable_references]
    private ArrayList<Double> arrayListLatitude = new ArrayList<Double>();
    private ArrayList<Double> arrayListLongitude = new ArrayList<Double>();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // [START mListener_variable_reference]
    // Need to hold a reference to this listener, as it's passed into the "unregister"
    // method in order to stop all sensors from sending data to this listener.
    private ArrayList<OnDataPointListener> arrayListeners = new ArrayList<>();
    // [END mListener_variable_reference]

    //Counters
    int steps = 0;
    double distance = 0.0;
    double speed = 0.0;
    ArrayList<Double> speedList = new ArrayList<>();
    String sport = "Run";

    //MainThread
    Handler mainHandler;

    //Write Flag
    boolean writeFlag = false;

    //Timer
    int mCurrentPeriod = 0;
    private Timer timer;

    //LocationManager
    private LocationManager locationManager;


    // [START auth_oncreate_setup]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Put application specific code here.

        setContentView(R.layout.activity_add);
        AddActivity.setContext(this);

        mainHandler = new Handler(getBaseContext().getMainLooper());
        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        initializeLogging();

        // When permissions are revoked the app is restarted so onCreate is sufficient to check for
        // permissions core to the ActivityDB's functionality.
        if (!checkPermissions()) {
            requestPermissions();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ///spinner

        final Spinner SpinnerExample = (Spinner) findViewById(R.id.spinner);

        // Set data in arraylist

        // Resources passed to adapter to get image
        Resources res = getResources();

        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        spinnerModels.add(new SpinnerModel("Run", "ic_run_black_48dp"));
        spinnerModels.add(new SpinnerModel("Walk", "ic_walk_white_36dp"));
        spinnerModels.add(new SpinnerModel("Bike", "ic_bike_black_24dp"));
        // Create custom adapter object ( see below CustomAdapter.java )
        AdapterKindsOfSport adapter = new AdapterKindsOfSport(this, R.layout.spinner_rows, spinnerModels, res);

        // Set adapter to spinner
        SpinnerExample.setAdapter(adapter);

        // Listener called when spinner item selected
        SpinnerExample.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                sport = (String) ((TextView)v.findViewById(R.id.sport)).getText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        // button go
        final FloatingActionButton playBtn = (FloatingActionButton) findViewById(R.id.play);
        final FloatingActionButton pauseBtn = (FloatingActionButton) findViewById(R.id.pause);
        final FloatingActionButton stopBtn = (FloatingActionButton) findViewById(R.id.stop);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMargins(pauseBtn, 0, 0, 220, 0);
                playBtn.setVisibility(View.GONE);
                pauseBtn.setVisibility(View.VISIBLE);
                writeFlag = true;
                setLayout(spinnerModels.get(SpinnerExample.getSelectedItemPosition()).getActivityName());
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TimerMethod();
                    }
                }, 0, 1000);

            }
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null)
                    timer.cancel();
                writeFlag = false;
                setMargins(playBtn, 0, 0, 220, 0);
                pauseBtn.setVisibility(View.GONE);
                playBtn.setVisibility(View.VISIBLE);
                arrayListLatitude.clear();
                arrayListLongitude.clear();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeFlag = false;
                arrayListLatitude.clear();
                arrayListLongitude.clear();
                Intent intent = new Intent(getBaseContext(), SaveActivity.class);
                intent.putExtra("steps",steps);
                intent.putExtra("distance", distance);
                intent.putExtra("speed", speed);
                intent.putExtra("speedList",speedList);
                intent.putExtra("sport",sport);
                intent.putExtra("time", ((TextView) findViewById(R.id.textTimer)).getText());
               /* ContextWrapper cw = new ContextWrapper(getContext());
                File directory = cw.getDir(getExternalFilesDir(null), Context.MODE_WORLD_READABLE);*/
                // Create imageDir
                final File file = new File(getContext().getExternalFilesDir(null),mMap.hashCode()+".jpg");

                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    Bitmap bitmap;

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        bitmap = snapshot;


                        try {
                            FileOutputStream out = new FileOutputStream(file);

                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                mMap.snapshot(callback);
                intent.putExtra("map",file.getPath());
                startActivity(intent);
            }
        });
    }

    private void TimerMethod() {
// This method is called directly by the timer
// and runs in the same thread as the timer.

// We call the method that will work with the UI
// through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            mCurrentPeriod++;
            String temp = (new SimpleDateFormat("mm:ss")).format(new Date(
                    mCurrentPeriod * 1000));
            ((TextView) findViewById(R.id.textTimer)).setText(temp);
// This method runs in the same thread as the UI.
// Do something to the UI thread here

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();
    }
    // [END auth_oncreate_setup]

    // [START auth_build_googleapiclient_beginning]

    /**
     * Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     * to connect to Fitness APIs. The scopes included should match the scopes your app needs
     * (see documentation for details). Authentication will occasionally fail intentionally,
     * and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     * can address. Examples of this include the user never having signed in before, or having
     * multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        if (mClient == null && checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SENSORS_API)
                    .addScope(Fitness.SCOPE_ACTIVITY_READ_WRITE)
                    .addScope(Fitness.SCOPE_LOCATION_READ_WRITE)
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                    // Now you can make calls to the Fitness APIs.
                                    findFitnessDataSources();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG,
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.i(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
                            Snackbar.make(
                                    AddActivity.this.findViewById(R.id.add_activity),
                                    "Exception while connecting to Google Play services: " +
                                            result.getErrorMessage(),
                                    Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .build();
        }
    }
    // [END auth_build_googleapiclient_beginning]

    /**
     * Find available data sources and attempt to register on a specific {@link DataType}.
     * If the application cares about a data type but doesn't care about the source of the data,
     * this can be skipped entirely, instead calling
     * {@link com.google.android.gms.fitness.SensorsApi
     * #register(GoogleApiClient, SensorRequest, DataSourceListener)},
     * where the {@link SensorRequest} contains the desired data type.
     */
    private void findFitnessDataSources() {
        // [START find_data_sources]
        // Note: Fitness.SensorsApi.findDataSources() requires the ACCESS_FINE_LOCATION permission.
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE,
                        DataType.TYPE_STEP_COUNT_DELTA
                )
                // Can specify whether data type is raw or derived.
                .setDataSourceTypes(DataSource.TYPE_RAW, DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            // Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive ActivityDB data!
                            if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE) ||
                                    dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)
                                    ) {
                                Log.i(TAG, "Registering.");
                                registerFitnessDataListener(dataSource, dataSource.getDataType());
                            }
                        }
                    }
                });
        // [END find_data_sources]
    }

    /**
     * Register a listener with the Sensors API for the provided {@link DataSource} and
     * {@link DataType} combo.
     */
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]
        OnDataPointListener mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);
                    //android.util.Log.e(TAG, "Detected DataPoint value: " + val);
                    setValuesTextView(field.getName(), val);
                }
            }
        };
        if (!arrayListeners.contains(mListener)) {
            arrayListeners.add(mListener);
            Fitness.SensorsApi.add(
                    mClient,
                    new SensorRequest.Builder()
                            .setDataSource(dataSource) // Optional but recommended for custom data sets.
                            .setDataType(dataType) // Can't be omitted.
                            .setSamplingRate(2, TimeUnit.SECONDS)
                            .build(),
                    mListener)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Listener registered!");
                            } else {
                                Log.i(TAG, "Listener not registered.");
                            }
                        }
                    });
        }
        // [END register_data_listener]
    }

    /**
     * Unregister the listener with the Sensors API.
     */
    private void unregisterFitnessDataListener() {
        if (arrayListeners.size() == 0) {
            // This code only activates one listener at a time.  If there's no listener, there's
            // nothing to unregister.
            return;
        }

        // [START unregister_data_listener]
        // Waiting isn't actually necessary as the unregister call will complete regardless,
        // even if called from within onStop, but a callback can still be added in order to
        // inspect the results.
        for (OnDataPointListener listener : arrayListeners) {
            Fitness.SensorsApi.remove(
                    mClient,
                    listener)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Listener was removed!");
                            } else {
                                Log.i(TAG, "Listener was not removed.");
                            }
                        }
                    });
        }
        // [END unregister_data_listener]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_unregister_listener) {
            unregisterFitnessDataListener();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize a custom log class that outputs both to in-app targets and logcat.
     */
    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        LogView logView = (LogView) findViewById(R.id.sample_logview);

        // Fixing this lint errors adds logic without benefit.
        //noinspection AndroidLintDeprecation
        logView.setTextAppearance(this, R.style.Log);

        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i(TAG, "Ready");
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.add_activity),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(AddActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(AddActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }



    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                buildFitnessClient();
            } else {
                // Permission denied.

                // In this ActivityDB we've chosen to notify the user that they
                // have rejected a core permission for the app since it makes the ActivityDB useless.
                // We're communicating this message in a Snackbar since this is a sample app, but
                // core permissions would typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.add_activity),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        //mMap.setOnMapClickListener(this);


        // Enable LocationLayer of Google Map
        mMap.setMyLocationEnabled(true);
        // Getting LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria,true);
        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        ((TextView) findViewById(R.id.textSport)).setText("latitude:" + location.getLatitude());
        ((TextView) findViewById(R.id.textLongitude)).setText("longitude:" + location.getLongitude());
    }


    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
    private void setValuesTextView(final String name, final Value val) {
        if (writeFlag) {
            final Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    if (name.equals("steps")) {
                        steps += val.asInt();
                        ((TextView) findViewById(R.id.textSteps)).setText(steps + "steps");
                    }
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void setLayout(String activityName) {
        if(activityName.equals("run")) {

        }

        if(activityName.equals("walk")) {

        }

        if(activityName.equals("bike")) {

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        arrayListLatitude.add(location.getLatitude()); //50.632755279541016
        ((TextView) findViewById(R.id.textSport)).setText("latitude:" + location.getLatitude());
        arrayListLongitude.add(location.getLongitude()); //26.2580509185791
        ((TextView) findViewById(R.id.textSport)).setText("longitude:" + location.getLongitude());
        if (writeFlag) {
            /// MAP ADD POLYLINE
            if (arrayListLatitude.size() > 1 && arrayListLongitude.size() > 1) {
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(arrayListLatitude.get(arrayListLatitude.size() - 2),
                                                arrayListLongitude.get(arrayListLongitude.size() - 2)),
                                        new LatLng(arrayListLatitude.get(arrayListLatitude.size() - 1),
                                                arrayListLongitude.get(arrayListLongitude.size() - 1)))
                                .width(5)
                                .color(Color.RED));
                    }
                };
                mainHandler.post(myRunnable);
            }
            //Distance and speed
            float[] results = new float[1];
            if ((arrayListLongitude.size() % 2) == 0) {
                Location.distanceBetween(arrayListLatitude.get(arrayListLatitude.size()-2), arrayListLongitude.get(arrayListLongitude.size()-2),
                        arrayListLatitude.get(arrayListLatitude.size()-1), arrayListLongitude.get(arrayListLongitude.size()-1),results);
                distance += results[0];
                ((TextView) findViewById(R.id.textDistance)).setText("distance: " + distance);
            }
            speed = location.getSpeed();
            speedList.add(speed);
            ((TextView) findViewById(R.id.textAvrSpeed)).setText("speed:" + speed);
        }
    }

    public static Context getContext() {
        return mContext;
    }
    public static void setContext(Context context) {
        mContext = context;
    }
}
