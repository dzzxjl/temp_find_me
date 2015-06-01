package com.chenleejr.findme.thread;

public class CheckThread extends Thread {
//    private MyApplication app;
//    private Handler handler;
//
//    public CheckThread(MyApplication a, Handler h) {
//        this.app = a;
//        this.handler = h;
//    }
//
//    public void run() {
//        String result;
//        String name = app.getSelf().getName();
//        String password = app.getSelf().getPassword();
//        Message msg = new Message();
//        try {
//            result = Tools.check(name, password);
//            System.out.println(result);
//            if (result == null) {
//                msg.what = 5;
//                handler.sendMessage(msg);
//            } else if (result.equals("")) {
//                return;
//            } else {
//                String[] results = result.split("&&");
//                ArrayList<User> friends = app.getFriends();
//                ArrayList<String> friendsNames = new ArrayList<String>();
//                for (User u : friends) {
//                    friendsNames.add(u.getName());
//                }
//                String friendsName = "";
//                for (String r : results) {
//                    if (!friendsNames.contains(r)) {
//                        friendsName += (r + " ");
//                    }
//                }
//                if (!friendsName.equals("")) {
//                    Tools.startNotification(app.getApplicationContext(),
//                            friendsName + "online, have a Refresh!",
//                            friendsName + "online");
//                    //Tools.startNotification(app, 1);
//                }
//            }
//        } catch (Exception e) {
//            msg.what = 4;
//            handler.sendMessage(msg);
//            e.printStackTrace(System.out);
//        }
//    }
}
