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
import android.support.design.widget.Snackbar;
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

import arthurveslo.my.myapplication.DB.ActivityDB;
import arthurveslo.my.myapplication.DB.DatabaseHandler;
import arthurveslo.my.myapplication.wrapper.SetImageForSport;

public class ShowActivity extends AppCompatActivity {

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContext(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final ActivityDB model = (ActivityDB) getIntent().getSerializableExtra("activity");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler db = new DatabaseHandler(getContext());
                db.deleteActivityDB(model);
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((TextView)findViewById(R.id.textSteps)).setText(model.get_steps()+"");
        ((TextView)findViewById(R.id.textDistance)).setText(model.get_distance()+"");
        ((TextView)findViewById(R.id.textAvrSpeed)).setText(model.get_avr_speed()+"");
        ((TextView)findViewById(R.id.textLatitude)).setText(model.get_activity());
        ///Set image
        SetImageForSport.set_Image(((ImageView)findViewById(R.id.icon_sport)), model.get_activity());

        ((TextView)findViewById(R.id.textCalories)).setText(model.get_calories() + "");
        ((TextView)findViewById(R.id.textTime)).setText(model.get_time());
        loadImageFromStorage(model.get_map());

        Button shareButton = (Button) findViewById(R.id.buttonShare);
        final ImageView imageView = (ImageView) findViewById(R.id.map);
        assert shareButton != null;
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_image_text_GPLUS(model.get_map());
            }
        });

    }

    public void share_image_text_GPLUS(String pathMap) {
        File f = new File(pathMap);
        ContentValues values = new ContentValues(2);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg"); //or /jpeg or whatever
        values.put(MediaStore.Images.Media.DATA, f.getAbsolutePath());
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT,
                "Hey this is my activity - "+((TextView)findViewById(R.id.textLatitude)).getText().toString() +"\n"
                        +"steps "+ Integer.parseInt(((TextView)findViewById(R.id.textSteps)).getText().toString()) + "\n"
                        +"burned calories" + Double.parseDouble(String.valueOf(((TextView)findViewById(R.id.textCalories)).getText())) + "\n"
                        +"time " + ((TextView)findViewById(R.id.textTime)).getText().toString() + "\n"
                        +"avr speed "+ Double.parseDouble(((TextView)findViewById(R.id.textAvrSpeed)).getText().toString())
        );
        intent.setPackage("com.google.android.apps.plus");
        startActivity(intent);
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
