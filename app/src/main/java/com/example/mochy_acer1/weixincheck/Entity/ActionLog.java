package com.example.mochy_acer1.weixincheck.Entity;

import java.util.Date;


public class ActionLog {
	private String url;
	private String ip;
	private String keyword;
	private Date date;
	private long duration;

	public String toLogString() {
		String log="";
		try {
			log = String.format("%s\t%s\t%tT\t%s\t%s",ip, duration, date, keyword, url);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return log;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
