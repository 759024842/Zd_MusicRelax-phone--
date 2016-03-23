package com.zd.musictorelax.handler;

public class SongPBean {
	public static final String SNAME="sName";
	public static final String SFORMAT="sFormat";
	public static final String SPLAYTIME="splayTime";
	private String sName;
	private String sFormat;
	private String splayTime;
	public SongPBean(){}
	
	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getsFormat() {
		return sFormat;
	}

	public void setsFormat(String sFormat) {
		this.sFormat = sFormat;
	}

	public String getSplayTime() {
		return splayTime;
	}

	public void setSplayTime(String splayTime) {
		this.splayTime = splayTime;
	}
}
