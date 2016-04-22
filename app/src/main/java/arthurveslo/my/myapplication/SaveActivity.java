package arthurveslo.my.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import arthurveslo.my.myapplication.DB.DatabaseHandler;
import arthurveslo.my.myapplication.DB.User;

public class SaveActivity extends AppCompatActivity {
    //
    double calories;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int steps = intent.getIntExtra("steps", 0);
        double distance = intent.getDoubleExtra("distance", 0.0);
        ArrayList<Double> speedList = (ArrayList<Double>) getIntent().getSerializableExtra("speedList");
        String sport = intent.getStringExtra("sport");
        String time = intent.getStringExtra("time");
        String pathMap = intent.getStringExtra("map");
        id = intent.getStringExtra("id");
        loadImageFromStorage(pathMap);

        ((TextView)findViewById(R.id.textSteps)).setText(steps + "steps");
        ((TextView)findViewById(R.id.textDistance)).setText(steps + "distance");
        ((TextView)findViewById(R.id.textAvrSpeed)).setText(average(speedList) + "average speed");
        ((TextView)findViewById(R.id.textSport)).setText(sport + "steps");
        ((TextView)findViewById(R.id.textCalories)).setText(calcCalories(sport, time) + "calories");
        ((TextView)findViewById(R.id.textTime)).setText(time);
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
        User user = db.getUser(id);
        double weight = user.get_weight();
        if(sport.equals("Run")) {
            return (weight/0.4536)*3.5*minute;
        }
        if(sport.equals("Walk")) {
            return (weight/0.4536)*1.65*minute;
        }
        if(sport.equals("Bike")) {
            return (weight/0.4536)*2.93*minute;
        }

        return weight;
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
}
