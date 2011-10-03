package com.jdub.android.letmesleepapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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
            setSleeping(!isSleeping());
            int text;
            if (isSleeping()) {
                text = R.string.going_to_sleep;
            } else {
                text = R.string.waking_up;
            }
            Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
            toast.show();
        }
    };
}
