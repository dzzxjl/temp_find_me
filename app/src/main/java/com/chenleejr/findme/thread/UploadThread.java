package com.chenleejr.findme.thread;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.util.Tools;

public class UploadThread extends Thread {
	private MyApplication app;
	private BDLocation location;
	private Handler handler;
	public UploadThread(BDLocation l, MyApplication m, Handler h){
		this.location = l;
		this.app = m;
		this.handler = h;
	}
	public void run() {
		String result;
		Message m = new Message();
		try {
			result = Tools.uploadData(app.getSelf(), location);
			if (result == null || result.equals("0")) {
				if (handler != null){
					m.what = 3;
					handler.sendMessage(m);
				}
			}
		} catch (Exception e) {
			if (handler != null){
				m.what = 4;
				handler.sendMessage(m);
			}
			e.printStackTrace();
		}
	}
}