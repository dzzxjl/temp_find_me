package com.chenleejr.findme.application;

import android.app.Activity;
import android.app.Application;

import com.chenleejr.findme.bean.SelfUser;
import com.chenleejr.findme.bean.User;

import java.util.ArrayList;

public class MyApplication extends Application {
    private ArrayList<Activity> list;
    private SelfUser self;
    //friends you can see in the screen
    private ArrayList<User> friends;
    private boolean isServiceWanted;

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public SelfUser getSelf() {
        return self;
    }

    public void setSelf(SelfUser self) {
        this.self = self;
    }

    @Override
    public void onCreate() {
        list = new ArrayList<Activity>();
        self = new SelfUser();
        friends = new ArrayList<User>();
        super.onCreate();
    }

    public boolean isServiceWanted() {
        return isServiceWanted;
    }

    public void setServiceWanted(boolean isServiceWanted) {
        this.isServiceWanted = isServiceWanted;
    }

    public ArrayList<Activity> getList() {
        return list;
    }

    public void finishAll() {
        for (Activity a : list) {
            a.finish();
        }
    }

}