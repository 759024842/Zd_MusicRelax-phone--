package com.zd.musictorelax.handler;

public class RelaxSSBean {
	public static final String RSID="rsID";
	public static final String SID="sID";
	public static final String RID="rID";
	public static final String ENABLE="Enable";

	private String rsID;
	private String sID;
	private String rID;
	private String Enable;
	
	public RelaxSSBean(){}

	public String getRsID() {
		return rsID;
	}

	public void setRsID(String rsID) {
		this.rsID = rsID;
	}

	public String getsID() {
		return sID;
	}

	public void setsID(String sID) {
		this.sID = sID;
	}

	public String getrID() {
		return rID;
	}

	public void setrID(String rID) {
		this.rID = rID;
	}

	public String getEnable() {
		return Enable;
	}

	public void setEnable(String enable) {
		Enable = enable;
	}
	

}
