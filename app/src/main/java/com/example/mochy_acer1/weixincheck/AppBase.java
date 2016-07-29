package com.example.mochy_acer1.weixincheck;

import android.app.Application;


public class AppBase extends Application {
	private static AppBase instance;

	public static AppBase getContext() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
	}


}
