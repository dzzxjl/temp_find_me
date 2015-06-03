package com.chenleejr.findme.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.bean.User;
import com.chenleejr.findme.util.Tools;

public class LeaveMessageThread extends Thread {
	private MyApplication app;
	private Handler handler;
	private String content;
	private User to;
	public LeaveMessageThread(MyApplication m, Handler h, User to, String content){
		this.app = m;
		this.handler = h;
		this.content = content;
		this.to = to;
	}
	public void run() {
		String result;
		Message m = new Message();
		try {
			result = Tools.leaveMessage(app.getSelf(), to, content);
            Log.i("FindMe", "result:" + result);
			if (result == null || result.equals("0")) {
				if (handler != null){
					m.what = 2;
					handler.sendMessage(m);
				}
			} else if (result.equals("1")){
				if (handler != null){
					m.what = 1;
					handler.sendMessage(m);
				}
			}
		} catch (Exception e) {
			if (handler != null){
				m.what = 3;
				handler.sendMessage(m);
			}
			e.printStackTrace();
		}
	}
}