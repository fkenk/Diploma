package arthurveslo.my.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arthurveslo.my.myapplication.DB.ActivityDB;
import arthurveslo.my.myapplication.DB.DatabaseHandler;
import arthurveslo.my.myapplication.DB.Foo;
import arthurveslo.my.myapplication.DB.User;
import arthurveslo.my.myapplication.adapters.ExpListAdapter;
import arthurveslo.my.myapplication.async.DownloadImageTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final String MY_PREFS_NAME = "plan";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected ArrayList<String> dDays = new ArrayList<>();
    private final int itemcount = 12;
    boolean flag = false;
    DatabaseHandler db;
    List<Foo> foos;
    Context context;
    CombinedData data; // for chart

    Spinner spinnerDate;
    Spinner spinnerSelector;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContext(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /////Get data from login

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String e_mail = intent.getStringExtra("e_mail");
        String photo_url = intent.getStringExtra("photo_url");


        /////DataBase
        db = new DatabaseHandler(this);
        db.addUser(new User(name, User.current_id)); ///if user exists

        System.out.println("Reading all contacts..");
        List<User> users = db.getAllUsers();
        for (User user : users) {
            String log = "Id: " + user.get_id()
                    + " ,Name: " + user.get_name()
                    + " ,Sex: " + user.get_sex();
            Log.d(TAG, log);
        }
        //////NAV BAR
        View headerView = navigationView.getHeaderView(0); ///???? 0 lol
        /*new DownloadImageTask((ImageView) headerView.findViewById(R.id.imageView))
                .execute(photo_url);*/
//        Log.d(TAG, photo_url);
        TextView textViewName = (TextView) headerView.findViewById(R.id.display_name);
        textViewName.setText(name);
        TextView textViewEMAIL = (TextView) headerView.findViewById(R.id.display_e_mail);
        textViewEMAIL.setText(e_mail);

        ///////////////go to add activity
        FloatingActionButton saveParams = (FloatingActionButton) findViewById(R.id.save_parameters);
        saveParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ParametersActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton startActiv = (FloatingActionButton) findViewById(R.id.start_training);
        startActiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton planActiv = (FloatingActionButton) findViewById(R.id.add_plan);
        planActiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlanActivity.class);
                startActivity(intent);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        ////Graph
        //(new LineCardOne((CardView) findViewById(R.id.card1), this)).init();


// Находим наш list
        /*ArrayList<BarEntry> entriesIncome = new ArrayList<>();
        HashMap<Integer, Float> sumForDaysIncomes = new HashMap();
        java.util.Date currentDate = new java.util.Date();

        SimpleDateFormat mf = new SimpleDateFormat("MM");
        SimpleDateFormat yf = new SimpleDateFormat("yyyy");
        Calendar mycal = new GregorianCalendar(Integer.parseInt(yf.format(currentDate)), Integer.parseInt(mf.format(currentDate)) - 1, 1);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);*/

        fillDays();

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void fillDays() {
        java.util.Date currentDate = new java.util.Date();
        SimpleDateFormat mf = new SimpleDateFormat("MM");
        SimpleDateFormat yf = new SimpleDateFormat("yyyy");
        Calendar mycal = new GregorianCalendar(Integer.parseInt(yf.format(currentDate)), Integer.parseInt(mf.format(currentDate)) - 1, 1);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInMonth; i++) {
            dDays.add("" + i);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void chart() {
        CombinedChart mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);



        /*ПРОСТО МЕГА КОСТИЛЬ*/
        CombinedData data = null;
        if (spinnerDate.getSelectedItem().toString().equals("Days")) {
            if (data != null) {
                data.clearValues();
            }
            data = new CombinedData(dDays);
        }
        if (spinnerDate.getSelectedItem().toString().equals("Month")) {
            if (data != null) {
                data.clearValues();
            }
            data = new CombinedData(mMonths);
        }
        if (data == null) {
            data = new CombinedData(dDays);
        }
        data.setData(generateLineData(
                spinnerDate.getSelectedItem().toString(),
                spinnerSelector.getSelectedItem().toString()));
        data.setData(generateBarData(
                spinnerDate.getSelectedItem().toString(),
                spinnerSelector.getSelectedItem().toString()));
        mChart.setData(data);
        mChart.invalidate();
    }

    private BarData generateBarData(String date, String selector) {

        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<>();
        if (date.equals("Month")) {
            for (int index = 0; index < mMonths.length; index++)
                entries.add(new BarEntry((float) db.getMonthActivityDB(index + 1, selector), index));
        }

        if (date.equals("Day")) {
            for (int index = 0; index < dDays.size(); index++)
                entries.add(new BarEntry((float) db.getDayActivityDB(index + 1, selector), index));
        }
        BarDataSet set = new BarDataSet(entries, selector);
        set.setColor(Color.rgb(0,150,136));
        set.setValueTextColor(Color.rgb(0,150,136));
        set.setValueTextSize(10f);
        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    private LineData generateLineData(String date, String selector) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();
        float data = 0;
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if (selector.equals("Calories")) {
            data = prefs.getFloat("calories", (float) 0.0);
        }
        if (selector.equals("Distance")) {
            data = prefs.getFloat("distance", (float) 0.0);
        }
        if (selector.equals("Steps")) {
            data = prefs.getFloat("steps", (float) 0.0);
        }
        if (selector.equals("Avr.Speed")) {
            data = prefs.getFloat("avr_speed", (float) 0.0);
        }

        if (date.equals("Month")) {
            for (int index = 0; index < mMonths.length; index++)
                entries.add(new Entry(data, index));
        }

        if (date.equals("Day")) {
            for (int index = 0; index < dDays.size(); index++)
                entries.add(new Entry(data, index));
        }

        LineDataSet set = new LineDataSet(entries, "Your Plan");
        set.setColor(Color.rgb(255,87,34));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(255,87,34));
        set.setCircleRadius(3f);
        set.setFillColor(Color.rgb(255,87,34));
        set.setDrawCubic(true);
        set.setDrawValues(false);
        //set.setValueTextSize(10f);
        //set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);
        return d;
    }

    private void addDataToSpinner() {
        ////////////////////1
        spinnerDate = (Spinner) findViewById(R.id.spinnerDate);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.date_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(adapter);
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    foos = db.getUniqueDateActivityDB();
                }
                if (position == 1) {
                    foos = db.getUniqueMonthActivityDB();
                }
                chart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /////////////////2
        spinnerSelector = (Spinner) findViewById(R.id.spinnerSelector);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.date_selector, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelector.setAdapter(adapter2);
        spinnerSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                foos = db.fillFoo(foos);
                /////Expandable L}ist
                ExpandableListView expandableList = (ExpandableListView) findViewById(R.id.expandable_list);
                //Создаем набор данных для адаптера
                //Создаем адаптер и передаем context и список с данными
                final ExpListAdapter adapterExpList = new ExpListAdapter(getContext(), foos,
                        (String) ((TextView) selectedItemView.findViewById(android.R.id.text1)).getText());
                expandableList.setAdapter(adapterExpList);
                expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        ActivityDB activityDB = (ActivityDB) adapterExpList.getChild(groupPosition, childPosition);
                        Intent intent = new Intent(getContext(), ShowActivity.class);
                        intent.putExtra("activity", activityDB);
                        startActivity(intent);
                        return true;
                    }
                });
                chart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://arthurveslo.my.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://arthurveslo.my.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addDataToSpinner();
        //chart();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
