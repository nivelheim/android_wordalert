package com.comp3617.alarmapp.alarmapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public final static String PREF = "pref";
    public final static String SAVED_HOUR = "hour";
    public final static String SAVED_MIN = "min";
    public final static String SAVED_QNUM = "qnum";
    public final static String SAVED_RVAL = "rval";


    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private TimePicker timePicker;
    private TextView timeView;
    private Button setButton;
    private Button offButton;
    private Calendar alarmTime;
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private int hours;
    private int minutes;
    private int qNum;
    private int aNum;

    private TextView textClock;
    private Spinner qSpinner;
    private Spinner aSpinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qSpinner = (Spinner) findViewById(R.id.qSpinner);
        //ArrayAdapter<Integer> qAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, getIntegers());
        ArrayAdapter<String> qAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getIntegers());
        qSpinner.setAdapter(qAdapter);

        aSpinner = (Spinner) findViewById(R.id.aSpinner);
        List<String> titles = new ArrayList(getRingtones().keySet());
        //ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, titles);
        ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, titles);
        aSpinner.setAdapter(aAdapter);


        pref = getSharedPreferences("pref", MODE_PRIVATE);
        edit = pref.edit();

        timeView = (TextView) findViewById(R.id.timeView);
        timeView.setText(getResources().getString(R.string.nset_message));

        if (pref.contains(PREF)) {
            int temp1 = pref.getInt(SAVED_HOUR, 0);
            int temp2 = pref.getInt(SAVED_MIN, 0);
            timeView.setText(getResources().getString(R.string.set_message) + printTime(temp1, temp2));
        }
        /*
        Toast.makeText(MainActivity.this, temp1, Toast.LENGTH_LONG).show();
        */

        setButton = (Button) findViewById(R.id.setButton);
        offButton = (Button) findViewById(R.id.offButton);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmService.class);
        alarmIntent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
        textClock = (TextView) findViewById(R.id.textClock);

    }


    @TargetApi(19)
    public void onClickSet(View v) {
        builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_whatshot_black_24dp).setContentTitle("Alarm is set");
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        long alarm = 0;
        Calendar now = Calendar.getInstance();
        alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hours);
        alarmTime.set(Calendar.MINUTE, minutes);

        if(alarmTime.getTimeInMillis() <= now.getTimeInMillis())
            alarm = alarmTime.getTimeInMillis() + (alarmManager.INTERVAL_DAY+1);
        else
            alarm = alarmTime.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm, 24 * 60 * 60 * 1000,  alarmIntent);

        timeView.setText(getResources().getString(R.string.set_message) + textClock.getText());

        /*
        Intent notifyIntent = new Intent(this, AlarmService.class);
        PendingIntent notifyPendingIntent = PendingIntent.getService(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(notifyPendingIntent);
        */
        manager.notify(1, builder.build());

        createPreference();
    }

    public void onClickCancel(View v) {
        timeView.setText(getResources().getString(R.string.nset_message));
        edit.clear();
        edit.commit();

        alarmManager.cancel(alarmIntent);
        alarmIntent.cancel();
        edit.clear();
        edit.commit();
    }

    public void onClickTest(View v) {
        Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
        startActivity(intent);
    }

    private void createPreference(){
        edit.putString(PREF, "exists");
        edit.putInt(SAVED_HOUR, hours);
        edit.putInt(SAVED_MIN, minutes);
        edit.putInt(SAVED_QNUM, qSpinner.getSelectedItemPosition()+1);
        edit.putString(SAVED_RVAL, getRingtones().get(aSpinner.getSelectedItem().toString()));
        edit.commit();
    }

    @TargetApi(17)
    public void onClickTextClock(View v) {
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                hours = hour;
                minutes = minute;

                textClock.setText(printTime(hours, minutes));
                textClock.setGravity(Gravity.CENTER);
            }
        }, hours, minutes, false);
        timePicker.show();
    }

    public Map<String, String> getRingtones() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        Map<String, String> list = new HashMap<>();
        while (cursor.moveToNext()) {
            String ringtoneTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String ringtoneUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            list.put(ringtoneTitle, ringtoneUri);
        }

        return list;
    }

    public List<String> getIntegers() {
        List<String> list = new ArrayList<>();
        for (int i=0; i<9; i++) {
            list.add((i+1) + " Questions    ");
        }

        return list;
    }

    public String printTime(int hours, int minutes) {
        String returnString;
        if (hours <= 11) {
            if (minutes <= 9) {
                returnString = hours + " : " + "0" + minutes + " AM";
            }
            else{
                returnString = hours + " : " + minutes + " AM";
            }
        }

        else if (hours == 12) {
            if (minutes <= 9) {
                returnString = hours + " : " + "0" + minutes + " PM";
            }
            else{
                returnString = hours + " : " + minutes + " PM";
            }
        }

        else {
            if (minutes <= 9) {
                returnString = (hours-12) + " : " + "0" + minutes + " PM";
            }
            else {
                returnString = (hours-12)+ " : " + minutes + " PM";
            }
        }
        return returnString;
    }
}
