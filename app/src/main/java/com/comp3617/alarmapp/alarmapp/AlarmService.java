package com.comp3617.alarmapp.alarmapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.VolumeProviderCompat;

public class AlarmService extends Service {
    public static final String TAG = "MyServiceTag";

    private MediaPlayer player;
    private NotificationManager nManager;
    private NotificationCompat.Builder nBuilder;
    private SharedPreferences pref;
    private String rUri;
    private VolumeProviderCompat myVolumeProvider = null;
    private Vibrator vib;


    public AlarmService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        if (pref.contains(MainActivity.PREF)) {
            rUri = pref.getString(MainActivity.SAVED_RVAL, "");
        }

        nBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_whatshot_black_24dp).setContentTitle("Alarm started. Please click to disable the alarm.");
        Intent aaIntent = new Intent(this, AlarmActivity.class);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent apIntent = PendingIntent.getActivity(this, 0, aaIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(apIntent);

        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Uri uri = Uri.parse(rUri);
        player = MediaPlayer.create(this, uri);
        player.setLooping(true);

        vib =  (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //nManager.notify(2, nBuilder.build());
        startForeground(2, nBuilder.build());
        player.start();

        vib.vibrate(3600000);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        vib.cancel();
        player.stop();
    }

    /*
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    */

}
