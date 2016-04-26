package arthurveslo.my.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import arthurveslo.my.myapplication.wrapper.AlarmReceiver;
import arthurveslo.my.myapplication.wrapper.AlarmService;
import br.com.goncalves.pugnotification.notification.PugNotification;

public class PlanActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 1;
    private static final String MY_PREFS_NAME = "plan";
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    public static Context context;
    EditText editCalories;
    EditText editSteps;
    EditText editDistance ;
    EditText editAvrSpeed;
    SharedPreferences.Editor editor;
    Button timePick;
    Button cancelNot;
    AlarmService alarmService;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editCalories = ((EditText) findViewById(R.id.editCalories));
        editSteps = ((EditText) findViewById(R.id.editSteps));
        editDistance = ((EditText) findViewById(R.id.editDistance));
        editAvrSpeed = ((EditText) findViewById(R.id.editAvrSpeed));
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putFloat("calories", Float.parseFloat(editCalories.getText().toString()));
                editor.putFloat("steps",  Float.parseFloat(editSteps.getText().toString()));
                editor.putFloat("distance",  Float.parseFloat(editDistance.getText().toString()));
                editor.putFloat("avr_speed",  Float.parseFloat(editAvrSpeed.getText().toString()));
                editor.commit();
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timePick= (Button) findViewById(R.id.buttonTimePick);
        timePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(PlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timePick.setText( selectedHour + ":" + selectedMinute);
                        alarmService = new AlarmService(getApplicationContext());
                        alarmService.startAlarm(selectedHour, selectedMinute);
                        editor.putString("notification_time", selectedHour+":"+selectedMinute);
                        editor.commit();
                        timePick.setText("Set Notification (current "+ prefs.getString("notification_time","0")+")");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        cancelNot = (Button) findViewById(R.id.buttonCancelNotification);
        cancelNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmService.cancelAlarm(PlanActivity.this);
                editor.remove("notification_time");
                editor.commit();
                timePick.setText("Set Notification (current "+ prefs.getString("notification_time","not defined")+")");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            float calories = prefs.getFloat("calories", (float) 0.0);//"No name defined" is the default value.
            float steps = prefs.getFloat("steps", (float) 0.0);//"No name defined" is the default value.
            float distance = prefs.getFloat("distance", (float) 0.0);//"No name defined" is the default value.
            float avr_speed = prefs.getFloat("avr_speed", (float) 0.0);//"No name defined" is the default value.
            String not_time = prefs.getString("notification_time","not defined");
            timePick.setText("Set Notification (current "+ not_time+")");
            editCalories.setText(calories+"");
            editSteps.setText(steps+"");
            editDistance.setText(distance+"");
            editAvrSpeed.setText(avr_speed+"");
        }
}
