package com.chenleejr.findme.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.thread.UploadThread;

import java.util.concurrent.ExecutorService;

public class UploadService extends Service{
	private LocationClient locationClient = null;
	private BDLocationListener myListener = new SimpleLocationListener();
	private MyApplication app;
    private long nowTime = 0;
    private ExecutorService pool;


	@Override
	public void onCreate() {
        Log.i("FindMe", "service oncreate");
		app = (MyApplication) this.getApplication();
        pool = app.getCachedThreadPool();
		locationClient = new LocationClient(this.getApplicationContext());
		locationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000*60);//5 min
		locationClient.setLocOption(option);
        super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Service start");
		app.setServiceWanted(false);
        locationClient.start();
        if (locationClient != null && locationClient.isStarted())
            locationClient.requestLocation();
        nowTime = System.currentTimeMillis();
        return super.onStartCommand(intent, flags, startId);
	}

	private class SimpleLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
            if (System.currentTimeMillis() - nowTime >= 60 * 1000) {
                //new UploadThread(location, app, null).start();
                pool.execute(new UploadThread(location, app, null));
                Log.i("FindMe", "service upload");
                nowTime = System.currentTimeMillis();
            }
		}
	}
	@Override
	public void onDestroy() {
		Log.i("FindMe", "Service destroy");
		app.setServiceWanted(true);
        locationClient.stop();
		super.onDestroy();
	}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
