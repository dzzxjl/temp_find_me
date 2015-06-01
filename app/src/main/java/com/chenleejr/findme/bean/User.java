package com.chenleejr.findme.bean;

import com.baidu.mapapi.model.LatLng;

public class User {
	protected String name;
	protected int id;
	
	protected LatLng l;
	public LatLng getL(){
		return l;
	}
	public void setL(LatLng l){
		this.l = l;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPassword() {
		return null;
	}
}
