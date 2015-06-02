package com.chenleejr.findme.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.ViewConfiguration;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.chenleejr.findme.R;
import com.chenleejr.findme.activity.MainActivity;
import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.bean.SelfUser;
import com.chenleejr.findme.bean.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tools {
    public static final Animation animation = new AlphaAnimation(1, 0);
    private static final String server = "http://112.74.92.230/api/";

    static {
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatMode(Animation.REVERSE);
    }

    public static void forceShowOverflowMenu(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setOverflowIconVisiable(Menu menu) {
        try {
            Class clazz = Class
                    .forName("com.android.internal.view.menu.MenuBuilder");
            Field field = clazz.getDeclaredField("mOptionalIconsVisible");
            if (field != null) {
                field.setAccessible(true);
                field.set(menu, true);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * @deprecated
     * 原为好友上线提醒功能，因为好友显示方式改为即时显示，此方法已弃用
     */
//    public static void startNotification(Context context, String content,
//                                         String ticker) {
//        NotificationManager nm = (NotificationManager) context
//                .getSystemService(context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//
//        builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("FindMe")
//                .setOnlyAlertOnce(true).setContentText(content)
//                .setWhen(System.currentTimeMillis())
//                .setPriority(Notification.PRIORITY_MIN).setAutoCancel(true);
//        Notification noti = builder.build();
//        noti.icon = R.drawable.ic_launcher;
//        noti.tickerText = ticker;
//        nm.notify(666, noti);
//    }

//    public static void startNotificationForMessage(String message,
//                                                   Context context, String content, String ticker) {
//        NotificationManager nm = (NotificationManager) context
//                .getSystemService(context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        Intent intent = new Intent(context, MessageActivity.class);
//        intent.putExtra("message", message);
//        builder.setSmallIcon(R.drawable.ic_launcher)
//                .setOnlyAlertOnce(true)
//                .setContentTitle("FindMe")
//                .setContentText(content)
//                .setWhen(System.currentTimeMillis())
//                .setPriority(Notification.PRIORITY_MIN)
//                .setAutoCancel(true)
//                .setContentIntent(
//                        PendingIntent.getActivity(context, 0, intent,
//                                PendingIntent.FLAG_CANCEL_CURRENT));
//        Notification noti = builder.build();
//        noti.icon = R.drawable.ic_launcher;
//        noti.tickerText = ticker;
//        nm.notify(777, noti);
//    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        is.close();
        bos.flush();
        byte[] result = bos.toByteArray();
        return result;
    }

    // every method need name and password!
    public static String uploadData(SelfUser user, BDLocation location)
            throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", server + "upload.php");
        map.put("name", user.getName());
        map.put("password", user.getPassword());
        map.put("latitude", String.valueOf(location.getLatitude()));
        map.put("longtitude", String.valueOf(location.getLongitude()));
        return postData(map);
    }

    public static String login(String name, String password) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", server + "login.php");
        map.put("name", name);
        map.put("password", password);
        return postData(map);
    }

    public static String getData(String name, String password) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", server + "getData.php");
        map.put("name", name);
        map.put("password", password);
        return postData(map);
    }

//    public static String check(String name, String password) throws Exception {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("path", "http://njupt.wang/api/check.php");
//        map.put("name", name);
//        map.put("password", password);
//        return postData(map);
//    }

    public static String checkMessage(String name, String password)
            throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", server + "checkMessage.php");
        map.put("name", name);
        map.put("password", password);
        return postData(map);
    }

    public static String leaveMessage(SelfUser frm, User to, String content)
            throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", server + "leaveMessage.php");
        map.put("name", frm.getName());
        map.put("password", frm.getPassword());
        map.put("frm", String.valueOf(frm.getId()));
        map.put("too", String.valueOf(to.getId()));
        map.put("content", content);
        return postData(map);
    }

    public static String confirmMessage(String name, String password)
            throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", server + "confirmMessage.php");
        map.put("name", name);
        map.put("password", password);
        return postData(map);
    }

    public static String postData(Map<String, String> map) throws Exception {
        HttpURLConnection conn = null;
        String data = "";
        for (String key : map.keySet()) {
            if (key.equals("path")) {
                URL url = new URL(map.get(key));
                conn = (HttpURLConnection) url.openConnection();
            } else {
                data += (key + "=" + URLEncoder.encode(map.get(key)) + "&");
            }
        }
        data = data.substring(0, data.length() - 1);
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", data.length() + "");
        OutputStream os = conn.getOutputStream();
        os.write(data.getBytes());
        int code = conn.getResponseCode();
        Log.i("code", String.valueOf(code));
        if (code == 200) {
            InputStream is = conn.getInputStream();
            byte[] result = getBytes(is);
            return new String(result);
        } else {
            throw new IllegalStateException("bad request");
        }
    }

    public static void setPoint(BaiduMap baidumap, String name,
                                double latitude, double longtitude) {
        LatLng point = new LatLng(latitude, longtitude);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        OverlayOptions option = new MarkerOptions().position(point)
                .icon(bitmap).zIndex(8);
        baidumap.addOverlay(option);

        OverlayOptions textOption = new TextOptions().fontSize(18).text(name)
                .position(point);
        baidumap.addOverlay(textOption);
    }

    public static void RefreshFromOutSide(MyApplication app) {
        ArrayList<Activity> a = app.getList();
        for (Activity aa : a) {
            if (aa instanceof MainActivity) {
                ((MainActivity) aa).refresh();
                break;
            }
        }
    }
}
