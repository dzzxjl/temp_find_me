package com.chenleejr.findme.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.chenleejr.findme.R;
import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.bean.SelfUser;
import com.chenleejr.findme.bean.User;
import com.chenleejr.findme.service.PushService;
import com.chenleejr.findme.service.UploadService;
import com.chenleejr.findme.thread.GetDataThread;
import com.chenleejr.findme.thread.UploadThread;
import com.chenleejr.findme.util.Tools;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnMarkerClickListener {
    private MapView mapView;
    private LocationClient locationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private TextView tv;
    private ProgressBar pb;
    private BaiduMap map;
    private ActionBar bar;
    private BDLocation mLocation;
    private boolean firstRefresh = true;
    private long nowTimeForUpload;
    private long nowTimeForGetData;
    //private long nowTimeForCheckMessage;
    private MyApplication app;
    private Handler myHandler = new Handler() {
        int count = 0;

        public void handleMessage(Message m) {
            String message = "";
            System.out.println(m.what);
            switch (m.what) {
//			case 1:
//				message = "login failed";
//				break;
//			case 2:
//				message = "logout failed";
//				break;
                case 3:
                    message = "upload failed";//upload result is null
                    break;
                case 4:
                    message = "Net status is not stable";//net error
                    break;
                case 5:
                    message = "get data error";//result is null
                    break;
                default:
                    message = "something wrong has happened";
                    break;
            }
            if (!message.equals(""))
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
                        .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("oncreate");
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_frame);
        app = (MyApplication) this.getApplication();
        app.getList().add(this);
        System.out.println("isServiceWanted" + app.isServiceWanted());
        if (!app.isServiceWanted()) {
            this.stopService(new Intent(this, UploadService.class));
            app.setServiceWanted(true);
        }
        nowTimeForUpload = System.currentTimeMillis();
        nowTimeForGetData = System.currentTimeMillis();
        //nowTimeForCheckMessage = System.currentTimeMillis();
        tv = (TextView) this.findViewById(R.id.tv1);
        pb = (ProgressBar) findViewById(R.id.pb_load);
        bar = this.getActionBar();
        bar.setTitle("");
        bar.setDisplayHomeAsUpEnabled(true);
        Tools.forceShowOverflowMenu(this);
        initBD();
        SharedPreferences.Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE).edit();
        editor.putString(PushService.PREF_DEVICE_ID, String.valueOf(app.getSelf().getId()));
        editor.commit();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        PushService.actionStart(getApplicationContext());
    }

    private void initBD() {
        mapView = (MapView) this.findViewById(R.id.bmapView);
        mapView.setVisibility(View.INVISIBLE);
        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        locationClient = new LocationClient(this.getApplicationContext());
        locationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        locationClient.setLocOption(option);
        locationClient.start();
        if (locationClient != null && locationClient.isStarted())
            locationClient.requestLocation();
        map.setOnMarkerClickListener(this);
    }

    /**
     * 功能由刷新改为 将自己位置置中，原本的显示好友功能去除
     */
    public void refresh() {
        // System.out.println(mLocation.getLocType());
        boolean errorNetStatus = (mLocation != null)
                && (mLocation.getLocType() == 61
                || mLocation.getLocType() == 161
                || mLocation.getLocType() == 65
                || mLocation.getLocType() == 66 || mLocation
                .getLocType() == 68);
        if (!errorNetStatus) {
            Toast.makeText(this, "Cannot obtain location information",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (mLocation.getLocType() == 68)
            Toast.makeText(this, "Net status not stable", Toast.LENGTH_SHORT)
                    .show();
        LatLng point = new LatLng(mLocation.getLatitude(),
                mLocation.getLongitude());
//		MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(12)
//				.build();

        //.newMapStatus(mMapStatus);
        if (firstRefresh) {
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point, 15);
            map.setMapStatus(mMapStatusUpdate);
        } else {
            MapStatus mMapStatus = new MapStatus.Builder().target(point).build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            map.animateMapStatus(mMapStatusUpdate);
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(mLocation.getRadius()).direction(0)
                .latitude(mLocation.getLatitude())
                .longitude(mLocation.getLongitude()).build();
        map.setMyLocationData(locData);
//        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_geo);
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true,
                null);
        map.setMyLocationConfigeration(config);
        tv.setText(mLocation.getAddrStr());
        if (firstRefresh) {
            pb.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
            new GetDataThread(map, app, myHandler).start();
        }
        System.out.println("refresh");
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            mLocation = location;
            long time = System.currentTimeMillis();
            if (time - nowTimeForUpload >= 1000 * 10) {
                new UploadThread(location, app, myHandler).start();
                nowTimeForUpload = time;
            }
            if (time - nowTimeForGetData >= 1000 * 10) {
                if (!firstRefresh)
                    new GetDataThread(map, app, myHandler).start();
                nowTimeForGetData = time;
            }

            if (firstRefresh) {
                refresh();
                firstRefresh = false;
            }
        }

    }

    @Override
    public void onBackPressed() {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);

        dialogBuilder
                .withTitle("Are you sure?")
                .withTitleColor("#FFFFFF")
                .withMessage("Do you still want to upload you data after you sign out?")
                .withMessageColor("#FFFFFF")
                .withDialogColor("#00AEFF")
                .withDuration(300)
                .withEffect(Effectstype.RotateBottom)
                .withButton1Text("OK")
                .withButton2Text("NO")
                .isCancelable(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.setServiceWanted(true);
                        finish();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.setServiceWanted(false);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            Tools.setOverflowIconVisiable(menu);
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onDestroy() {
        if (app.isServiceWanted()) {
            Intent intent = new Intent(this, UploadService.class);
            startService(intent);
        }
        locationClient.stop();
        mapView.onDestroy();
        app.getList().remove(this);
        PushService.actionStop(getApplicationContext());
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.flush) {
            if (!firstRefresh)
                refresh();
            else {
                Toast.makeText(this, "Can not refresh now", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        if (id == R.id.close) {
            SelfUser s = app.getSelf();
            s.setName("");
            s.setPassword("");
            app.setServiceWanted(false);
            app.finishAll();
        }
        if (id == R.id.type) {
            if (item.getTitle().equals("Normal")) {
                map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                item.setTitle("Satellite");
            } else {
                map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                item.setTitle("Normal");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        //Log.i("position", String.valueOf(arg0.getPosition().latitude));
        LatLng ll = arg0.getPosition();
        ArrayList<User> friends = app.getFriends();
        //System.out.println(friends.size());
        for (User u : friends) {
            //System.out.println(ll.latitude + " " + ll.longitude);
            //System.out.println(u.getL().latitude + " " + u.getL().longitude);
            if (u.getL().latitude == ll.latitude && u.getL().longitude == ll.longitude) {
                MessageActivity.actionStart(this, "", u.getId());
                break;
            }
        }
        return false;
    }
}
