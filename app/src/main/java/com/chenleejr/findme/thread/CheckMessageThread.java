package com.chenleejr.findme.thread;

public class CheckMessageThread extends Thread {
//	private MyApplication app;
//	private Handler handler;
//
//	public CheckMessageThread(MyApplication a, Handler h) {
//		this.app = a;
//		this.handler = h;
//	}
//
//	public void run() {
//		String result;
//		String name = app.getSelf().getName();
//		String password = app.getSelf().getPassword();
//		Message msg = new Message();
//		try {
//			result = Tools.checkMessage(name, password);
//			System.out.println(result);
//			if (result == null) {
//				msg.what = 5;
//				handler.sendMessage(msg);
//			} else if (result.equals("")) {
//				return;
//			} else {
//				Tools.startNotificationForMessage(result, app.getApplicationContext(), "You have messages", "Messages");
//			}
//		} catch (Exception e) {
//			msg.what = 4;
//			handler.sendMessage(msg);
//			e.printStackTrace(System.out);
//		}
//	}
}
