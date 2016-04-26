package arthurveslo.my.myapplication.wrapper;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by User on 26.04.2016.
 */
public class AlarmService {
    private static final int NOTIFY_ID = 104;
    private Context context;
    private PendingIntent mAlarmSender;
    public AlarmService(Context context) {
        this.context = context;
        mAlarmSender = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
    }

    public void startAlarm(int hour, int minute){
        //Set the alarm to 10 seconds from now
        /*Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 4);
        long firstTime = c.getTimeInMillis();*/

        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Schedule the alarm!
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mAlarmSender);
    }
    public static void cancelAlarm(Context context)
    {
        try
        {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Create an alarm intent
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);

            // Create the corresponding PendingIntent object
            PendingIntent alarmPI = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            // cancel any alarms previously set
            alarmMgr.cancel(alarmPI);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}