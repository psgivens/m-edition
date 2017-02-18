package com.MEdition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.PhillipScottGivens.Camcorder.CamcorderService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 6/15/12
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySplashActivity extends Activity {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_splash);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        worker.schedule(task, 10, TimeUnit.MILLISECONDS);
    }

    private Runnable task = new Runnable() {
        public void run() {
            Intent intent = new Intent(MySplashActivity.this, MyService.class);
            startService(intent);
            finish();
        }
    };
}