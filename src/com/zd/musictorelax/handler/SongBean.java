package com.zd.musictorelax.handler;

public class SongBean {
	
	public static final String SID="sID";
	public static final String SNO="sNo";
	public static final String SNAME="sName";
	public static final String KID="kID";
	public static final String SFORMAT="sFormat";
	public static final String SPLAYTIME="splayTime";
	public static final String SDESCRIBE="sDescribe";
	public static final String SENABLE="sEnable";
	public static final String SGROUP="sGroup";

	
	private String sID;
	private String sNo;
	private String sName;
	private String kID;
	private String sFormat;
	private String splayTime;
	private String sDescribe;
	private String sEnable;
	private String sGroup;
	
	public SongBean(){}

	public String getsID() {
		return sID;
	}

	public void setsID(String sID) {
		this.sID = sID;
	}

	public String getsNo() {
		return sNo;
	}

	public void setsNo(String sNo) {
		this.sNo = sNo;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getkID() {
		return kID;
	}

	public void setkID(String kID) {
		this.kID = kID;
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

	public String getsDescribe() {
		return sDescribe;
	}

	public void setsDescribe(String sDescribe) {
		this.sDescribe = sDescribe;
	}

	public String getsEnable() {
		return sEnable;
	}

	public void setsEnable(String sEnable) {
		this.sEnable = sEnable;
	}

	public String getsGroup() {
		return sGroup;
	}

	public void setsGroup(String sGroup) {
		this.sGroup = sGroup;
	}
	
	
}
