package com.zd.musictorelax.handler;

public class RelaxSBean {
	public static final String RID="rID";
	public static final String RNAME="rName";
	public static final String RCREATORCODE="rCreatorCode";
	public static final String RCREATOR="rCreator";
	public static final String RCREATTIME="rCreatTime";
	public static final String RDESCRIBE="rDescribe";
	public static final String RICO="rICO";
	public static final String RINLAY="rInlay";

	
	private String rID;
	private String rName;
	private String rCreatorCode;
	private String rCreator;
	private String rCreatTime;
	private String rDescribe;
	private String rICO;
	private String rInlay;
	
	public RelaxSBean(){}

	public String getrID() {
		return rID;
	}

	public void setrID(String rID) {
		this.rID = rID;
	}

	public String getrName() {
		return rName;
	}

	public void setrName(String rName) {
		this.rName = rName;
	}

	public String getrCreatorCode() {
		return rCreatorCode;
	}

	public void setrCreatorCode(String rCreatorCode) {
		this.rCreatorCode = rCreatorCode;
	}

	public String getrCreator() {
		return rCreator;
	}

	public void setrCreator(String rCreator) {
		this.rCreator = rCreator;
	}

	public String getrCreatTime() {
		return rCreatTime;
	}

	public void setrCreatTime(String rCreatTime) {
		this.rCreatTime = rCreatTime;
	}

	public String getrDescribe() {
		return rDescribe;
	}

	public void setrDescribe(String rDescribe) {
		this.rDescribe = rDescribe;
	}

	public String getrICO() {
		return rICO;
	}

	public void setrICO(String rICO) {
		this.rICO = rICO;
	}

	public String getrInlay() {
		return rInlay;
	}

	public void setrInlay(String rInlay) {
		this.rInlay = rInlay;
	}
	

}
