package com.example.mochy_acer1.weixincheck.Util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class StringHelper {
	public static String format(String format, Date dt) {
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(dt);
	}




}
