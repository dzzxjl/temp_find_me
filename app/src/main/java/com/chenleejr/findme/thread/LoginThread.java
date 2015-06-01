package com.chenleejr.findme.thread;

import android.os.Handler;
import android.os.Message;

import com.chenleejr.findme.application.MyApplication;
import com.chenleejr.findme.bean.SelfUser;
import com.chenleejr.findme.util.Tools;

public class LoginThread extends Thread{
	private MyApplication app;
	private Handler handler;
	private String name;
	private String password;
	public LoginThread(MyApplication a, Handler h, String n, String p){
		this.app = a;
		this.handler = h;
		this.name = n;
		this.password = p;
	}
	public void run() {
		String result;
		Message msg = new Message();
		try {
			result = Tools.login(name, password);
			System.out.println(result);
			if (result == null || result.equals("0")) {
				msg.what = 1;
				handler.sendMessage(msg);
			} else {
				SelfUser s = app.getSelf();
				s.setName(name);
				s.setPassword(password);
				s.setId(Integer.valueOf(result));
				app.setServiceWanted(true);
				msg.what = 2;
				handler.sendMessage(msg);
			}	
		} catch (Exception e) {
			msg.what = 4;
			handler.sendMessage(msg);
			e.printStackTrace(System.out);
		}
	}
}
