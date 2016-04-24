package arthurveslo.my.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.PlusShare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    Context ctx;
    String pathMap;
    private static final int REQ_SELECT_PHOTO = 1;
    private static final int REQ_START_SHARE = 2;
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
        pathMap = intent.getStringExtra("map");
        loadImageFromStorage(pathMap);

        ((TextView)findViewById(R.id.textSteps)).setText(steps + "steps");
        ((TextView)findViewById(R.id.textDistance)).setText(steps + "distance");
        ((TextView)findViewById(R.id.textAvrSpeed)).setText(average(speedList) + "average speed");
        ((TextView)findViewById(R.id.textSport)).setText(sport + "steps");
        ((TextView)findViewById(R.id.textCalories)).setText(calcCalories(sport, time) + "calories");
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
        Log.e("iDDDDDDDDDDDD",User.current_id);
        User user = db.getUser(User.current_id);
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

        return 0;
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

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
