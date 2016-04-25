package arthurveslo.my.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import arthurveslo.my.myapplication.DB.ActivityDB;
import arthurveslo.my.myapplication.DB.DatabaseHandler;
import arthurveslo.my.myapplication.DB.User;
import arthurveslo.my.myapplication.wrapper.SetImageForSport;

public class SaveActivity extends AppCompatActivity {
    private static final String TAG = "SaveActivity";
    //
    double calories;
    Context ctx;
    String pathMap;
    ArrayList<Double> speedList;
    String address;

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setCtx(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDB();
                Intent intent = new Intent(getCtx(), MainActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int steps = intent.getIntExtra("steps", 0);
        double distance = intent.getDoubleExtra("distance", 0.0);
        speedList = (ArrayList<Double>) getIntent().getSerializableExtra("speedList");
        String sport = intent.getStringExtra("sport");
        String time = intent.getStringExtra("time");
        pathMap = intent.getStringExtra("map");
        address = intent.getStringExtra("address");
        loadImageFromStorage(pathMap);

        ((TextView)findViewById(R.id.textSteps)).setText(steps+"");
        ((TextView)findViewById(R.id.textDistance)).setText(roundResult(distance,2)+"");
        ((TextView)findViewById(R.id.textAvrSpeed)).setText(roundResult(average(speedList),2)+"");
        ((TextView)findViewById(R.id.textLatitude)).setText(sport);
        ///Set image
        SetImageForSport.set_Image(((ImageView)findViewById(R.id.icon_sport)), sport);

        Log.e(TAG + "CALORIES", roundResult(calcCalories(sport, time),2) + "");
        ((TextView)findViewById(R.id.textCalories)).setText(roundResult(calcCalories(sport, time),4)+"");
        ((TextView)findViewById(R.id.textTime)).setText(time);


        Button shareButton = (Button) findViewById(R.id.buttonShare);
        final ImageView imageView = (ImageView) findViewById(R.id.map);
        assert shareButton != null;
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               share_image_text_GPLUS();
            }
        });
    }


    public void share_image_text_GPLUS() {
        File f = new File(pathMap);
        ContentValues values = new ContentValues(2);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg"); //or /jpeg or whatever
        values.put(MediaStore.Images.Media.DATA, f.getAbsolutePath());
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "blahblah");
        intent.setPackage("com.google.android.apps.plus");
        startActivity(intent);
    }
    private double average (ArrayList<Double> list) {

        double sum = 0.0;
        if (list.size() == 0) return 0.0;
        for(int i=0;i<list.size();i++) {
            sum+=list.get(i);
        }

        return sum/list.size();
    }
    private double calcCalories(String sport, String time) {
        String[] timme = time.split ( ":" );
        int minute = Integer.parseInt ( timme[0].trim() );
        int second = Integer.parseInt ( timme[1].trim() );
        DatabaseHandler db = new DatabaseHandler(this);
        User user = db.getUser(User.current_id);
        double weight = user.get_weight();

        if(sport.equals("Bike")) {
            return (weight/0.4536)*2.93*minute;
        }

        double VO2 = (0.2 * average(speedList)) + 3.5;
        return 5.0 * user.get_weight() * VO2 / 1000;
    }
    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.map);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    double roundResult (double d, int precise) {

        precise = 10^precise;
        d = d*precise;
        int i = (int) Math.round(d);
        return (double) i/precise;

    }
    private void saveToDB() {
        DatabaseHandler db = new DatabaseHandler(this);
        ActivityDB activityDB = new ActivityDB();
        activityDB.set_activity( ((TextView)findViewById(R.id.textLatitude)).getText().toString() );
        activityDB.set_user_id(User.current_id);
        activityDB.set_calories( Double.parseDouble(String.valueOf(((TextView)findViewById(R.id.textCalories)).getText())) );
        activityDB.set_steps( Integer.parseInt(((TextView)findViewById(R.id.textSteps)).getText().toString()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdf.format(new Date());
        activityDB.set_date(currentDate);
        activityDB.set_time(((TextView)findViewById(R.id.textTime)).getText().toString());
        activityDB.set_avr_speed(Double.parseDouble(((TextView)findViewById(R.id.textAvrSpeed)).getText().toString()));
        activityDB.set_distance(Double.parseDouble(((TextView)findViewById(R.id.textDistance)).getText().toString()));
        sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());
        activityDB.set_cur_time(currentTime);
        if(address == null) {
            activityDB.set_address("");
        } else {
            activityDB.set_address(address);
        }
        activityDB.set_map(pathMap);
        db.addActivityDB(activityDB);

        List<ActivityDB> users = db.getAllActivityDB();
        for (ActivityDB user : users) {
            String log = "Id: " + user.get_num()
                    + " ,Act: " + user.get_activity()
                    + " ,user id: " + user.get_user_id()
                    + " ,calories : " + user.get_calories()
                    + " ,steps : " + user.get_steps()
                    + " ,date: " + user.get_date()
                    + " ,time: " + user.get_time()
                    + " ,avr speed: " + user.get_avr_speed()
                    + " ,distace: " + user.get_distance();
            Log.d(TAG, log);
        }
    }
}
