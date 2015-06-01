package com.chenleejr.findme.thread;

import android.os.Handler;
import android.os.Message;

import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.util.Tools;

public class ConfirmMessageThread extends Thread{
	private MyApplication app;
	private Handler handler;
	public ConfirmMessageThread(MyApplication a, Handler h){
		app = a;
		handler = h;
	}
	public void run() {
		String result;
		Message msg = new Message();
		try {
			result = Tools.confirmMessage(app.getSelf().getName(), app.getSelf().getPassword());
			if (result == null || result.equals("")) {
				msg.what = 4;
				handler.sendMessage(msg);
			} else if (result.equals("1")) {
				msg.what = 5;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			msg.what = 3;
			handler.sendMessage(msg);
			e.printStackTrace(System.out);
		}
	}
	
}
