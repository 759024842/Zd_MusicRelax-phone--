package com.zd.musictorelax.handler;

public class SongKBean {
	public static final String KID="kID";
	public static final String KNAME="kName";
	public static final String KDESCRIBE="kDescribe";
	public static final String KICO="kICO";
	public static final String KGROUP="kGroup";

	private String kID;
	private String kName;
	private String kDescribe;
	private String kICO;
	private String kGroup;
	
	
	public SongKBean(){}


	public String getkID() {
		return kID;
	}


	public void setkID(String kID) {
		this.kID = kID;
	}


	public String getkName() {
		return kName;
	}


	public void setkName(String kName) {
		this.kName = kName;
	}


	public String getkDescribe() {
		return kDescribe;
	}


	public void setkDescribe(String kDescribe) {
		this.kDescribe = kDescribe;
	}


	public String getkICO() {
		return kICO;
	}


	public void setkICO(String kICO) {
		this.kICO = kICO;
	}


	public String getkGroup() {
		return kGroup;
	}


	public void setkGroup(String kGroup) {
		this.kGroup = kGroup;
	}

	
}
