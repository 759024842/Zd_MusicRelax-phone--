package com.zd.musictorelax.tool;

public class ExamSub {
	private int _ID;
	private String strXUE;
	public ExamSub(int _ID,String strXUE){
		this._ID=_ID;
		this.strXUE=strXUE;
	}
	public ExamSub(){
		
	}
	
	public int get_ID() {
		return _ID;
	}
	public void set_ID(int _ID) {
		this._ID = _ID;
	}
	public String getStrXUE() {
		return strXUE;
	}
	public void setStrXUE(String strXUE) {
		this.strXUE = strXUE;
	}

}
