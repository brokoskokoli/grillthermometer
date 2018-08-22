package com.example.stean1990.grillthermometer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Vector;

public class PullService extends Service {
    private NotificationManager mNM;
    /**
     * A numeric value that identifies the notification that we'll be sending.
     * This value needs to be unique within this app, but it doesn't need to be
     * unique system-wide.
     */
    public static final int NOTIFICATION_ID = 1;

    private volatile Thread m_worker;
    private String m_NotificationText = "";
    private Vector<Double> m_Temps;
    private Long m_LastConnection;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    //private int NOTIFICATION = R.string.local_service_started;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        PullService getService() {
            return PullService.this;
        }
    }

    @Override
    public void onCreate() {
        m_LastConnection = System.currentTimeMillis()/1000;
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    private void DoAlert()
    {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

    public boolean IsAlert( )
    {
        if( m_Temps.size() < 1 )
        {
            Long time = System.currentTimeMillis()/1000;
            if( time - 300 > m_LastConnection )
            {
                DoAlert();
                return true;
            }
            return false;
        }
        m_LastConnection = System.currentTimeMillis()/1000;

        if( m_Temps.get(0) < GrillThermometerSettings.min_temp_0 )
        {
            DoAlert();
            return true;
        }
        if( m_Temps.get(0) > GrillThermometerSettings.max_temp_0 )
        {
            DoAlert();
            return true;
        }
        if( m_Temps.get(1) < GrillThermometerSettings.min_temp_1 )
        {
            DoAlert();
            return true;
        }
        if( m_Temps.get(1) > GrillThermometerSettings.max_temp_1 )
        {
            DoAlert();
            return true;
        }

        return false;

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Toast.makeText(this, "Service running", Toast.LENGTH_SHORT).show();

         final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (BuildConfig.DEBUG) {
                    Toast.makeText( getApplicationContext(), "5 secs has passed", Toast.LENGTH_SHORT).show();
                }
            }

        };


        m_worker = new Thread(new Runnable(){
            public void run() {
                Thread thisThread = Thread.currentThread();
                while ( m_worker == thisThread )
                {
                    try {
                        TempData data = new TempData();
                        boolean result = data.LoadInfo( GrillThermometerSettings.server_host + "/main.php" );
                        if( result == false )
                        {
                            m_NotificationText = data.GetError();
                        }
                        else
                        {
                            m_Temps = data.GetTemps();
                            IsAlert();
                            m_NotificationText = data.GetOutput();
                        }

                        handler.sendEmptyMessage(0);
                        showNotification();
                        Thread.sleep(5000);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        });
        m_worker.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION_ID);

        m_worker = null;

        // Tell the user we stopped.
        //Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        //CharSequence text = getText(R.string.local_service_started);
        CharSequence text = "Testtext";
        CharSequence label = "Testtext";

                // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                //.setSmallIcon(R.drawable.stat_sample)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle( "Grillthermometer" )
                .setContentIntent(contentIntent)
                //.setVisibility(1)
                .setContentText( m_NotificationText ).build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;


        //  // The intent to send when the entry is clicked


        // Send the notification.
        mNM.notify(NOTIFICATION_ID, notification);
    }



}
