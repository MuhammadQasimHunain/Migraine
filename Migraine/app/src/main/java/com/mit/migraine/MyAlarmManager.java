package com.mit.migraine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by cs on 9/22/2017.
 */

public class MyAlarmManager {

    public MyAlarmManager() {

    }

    public void alarmMethod(Context ctx, String time1, String time2){
        Intent intent = new Intent(ctx, Notifi.class);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        alarmManager.cancel(pendingIntent1);
        pendingIntent1.cancel();

        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time1.split(":")[0]));
        cal1.set(Calendar.MINUTE, Integer.parseInt(time1.split(":")[1].split(" ")[0]));
        cal1.set(Calendar.SECOND, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent1);

        if(!time2.equals("")) {
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(ctx, 1, intent, 0);
            alarmManager.cancel(pendingIntent2);
            pendingIntent2.cancel();

            Calendar cal2 = Calendar.getInstance();
            cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time2.split(":")[0]));
            cal2.set(Calendar.MINUTE, Integer.parseInt(time2.split(":")[1].split(" ")[0]));
            cal2.set(Calendar.SECOND, 0);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent2);
        }
        //1000 * 60 * 60 * 24
        //Toast.makeText(MainActivity.this, "Start Alarm", Toast.LENGTH_LONG).show();
    }
}
