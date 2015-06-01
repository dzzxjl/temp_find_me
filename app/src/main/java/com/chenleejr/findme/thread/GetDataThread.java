package com.chenleejr.findme.thread;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.bean.User;
import com.chenleejr.findme.util.Tools;

public class GetDataThread extends Thread{
	private MyApplication app;
	private Handler handler;
	private BaiduMap baidumap;
	public GetDataThread(BaiduMap bd, MyApplication a, Handler h){
		baidumap = bd;
		this.app = a;
		this.handler = h;
	}
	public void run() {
		String result;
		String name = app.getSelf().getName();
		String password = app.getSelf().getPassword();
		Message msg = new Message();
		try {
			result = Tools.getData(name, password);
			System.out.println(result);
			if (result == null) {
				msg.what = 5;
				handler.sendMessage(msg);
			} else if (result.equals("")){
				return;
			} else {
				String[] results = result.split("&");
				baidumap.clear();
				ArrayList<User> friends = new ArrayList<User>();
				for (String r:results){
					System.out.println(r);
					String[] info = r.split(",");
					Tools.setPoint(baidumap, info[0], Double.valueOf(info[1]),  Double.valueOf(info[2]));
					User u = new User();
					u.setId(Integer.valueOf(info[3]));
					u.setName(info[0]);
					u.setL(new LatLng(Double.valueOf(info[1]),  Double.valueOf(info[2])));
					friends.add(u);
				}
				app.setFriends(friends);
			}
		} catch (Exception e) {
			msg.what = 4;
			handler.sendMessage(msg);
			e.printStackTrace(System.out);
		}
	}
}
