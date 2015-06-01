package com.chenleejr.findme.service;

import android.app.IntentService;
import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.thread.UploadThread;

public class UploadService extends IntentService{
	private LocationClient locationClient = null;
	private BDLocationListener myListener = new SimpleLocationListener();
	private MyApplication app;

	public UploadService() {
		super("UploadService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		app = (MyApplication) this.getApplication();
		locationClient = new LocationClient(this.getApplicationContext());
		locationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000*60*5);//5 min
		locationClient.setLocOption(option);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Service start");
		app.setServiceWanted(false);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		locationClient.start();
		if (locationClient != null && locationClient.isStarted())
			locationClient.requestLocation();
		while(true){
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private class SimpleLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			new UploadThread(location, app, null).start();
		}
	}
	@Override
	public void onDestroy() {
		System.out.println("Service destroy");
		app.setServiceWanted(true);
		super.onDestroy();
	}
	
}
