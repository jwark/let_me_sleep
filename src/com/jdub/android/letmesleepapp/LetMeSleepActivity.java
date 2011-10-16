package com.jdub.android.letmesleepapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LetMeSleepActivity extends Activity {

    public static final String SLEEPING_STATE = "Sleeping";
    public boolean sleeping = false;

    public LetMeSleepActivity() {
        super();
    }

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
        if (sleeping) {
            ((Button) findViewById(R.id.sleep)).setText(R.string.wake_up);
        } else {
            ((Button) findViewById(R.id.sleep)).setText(R.string.sleep);
        }
    }

    public boolean isSleeping() {
        return this.sleeping;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(this.getClass().getName(), "onCreate");

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.letmesleep_activity);

        // Hook up button presses to the appropriate event handler.
        ((Button) findViewById(R.id.back)).setOnClickListener(mBackListener);
        ((Button) findViewById(R.id.sleep)).setOnClickListener(mSleepListener);
    }

    @Override
    public void onDestroy() {
        Log.i(this.getClass().getName(), "onDestroy: isSleeping=" + isSleeping());
        SharedPreferences settings = getSharedPreferences("LetMeSleep", MODE_PRIVATE);
        Editor edit = settings.edit();
        edit.putBoolean(SLEEPING_STATE, isSleeping());
        edit.commit();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.i(this.getClass().getName(), "onResume");
        super.onResume();

        SharedPreferences settings = getSharedPreferences("LetMeSleep", MODE_PRIVATE);
        if (settings.contains(SLEEPING_STATE)) {
            setSleeping(settings.getBoolean(SLEEPING_STATE, false));
            Log.i(this.getClass().getName(), "onResume: loading sharedPrefs sleeping=" + isSleeping());
        }
    }

    OnClickListener mBackListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(this.getClass().getName(), "onClick: back");
            finish();
        }
    };

    Activity activity = this;
    OnClickListener mSleepListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleSleeping();
        }
    };

    private void toggleSleeping() {
        setSleeping(!isSleeping());
        int text;
        if (isSleeping()) {
            text = R.string.going_to_sleep;
            setSleepingStatusBarNotification(true);
            setFlightMode(true);
            setSilentMode(true);
        } else {
            setSleepingStatusBarNotification(false);
            text = R.string.waking_up;
            setFlightMode(false);
            setSilentMode(false);
        }
        Toast toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private String onOrOffText(boolean on) {
        return on ? "on" : "off";
    }

    private void setSilentMode(boolean on) {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (on) {
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
        Toast.makeText(activity, "Turning silent mode: " + onOrOffText(on), Toast.LENGTH_SHORT).show();
    }

    private void setFlightMode(boolean on) {
        Settings.System.putInt(this.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, on ? 1 : 0);

        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", !on);
        sendBroadcast(intent);

        Toast.makeText(activity, "Turning silent mode: " + onOrOffText(on), Toast.LENGTH_SHORT).show();
    }

    private void setSleepingStatusBarNotification(boolean on) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        final int SLEEPING_NOTIFICATION = 100;
        if (on) {
            CharSequence notificationTitle = "Now sleeping";
            CharSequence notificationText = "...zzzzzzz";

            Intent notificationIntent = new Intent(this, LetMeSleepActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new Notification();
            notification.setLatestEventInfo(this, notificationTitle, notificationText, contentIntent);
            notification.when = System.currentTimeMillis();
            notification.icon = android.R.drawable.ic_notification_clear_all;
            notificationManager.notify(SLEEPING_NOTIFICATION, notification);
        } else {
            notificationManager.cancel(SLEEPING_NOTIFICATION);
        }
    }

}