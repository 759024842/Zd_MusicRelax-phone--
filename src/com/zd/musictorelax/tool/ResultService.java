package com.zd.musictorelax.tool;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ResultService {
	public static int RLID = -1;
	private SQLiteDatabase db;
	private Dao dao;
	private String tablename = "M_xueyang";
	private Cursor curExamSub;

	public ResultService(Context context) {
		dao = new Dao(context);
		db = dao.getDb();
	}
    
	public List<ExamSub> selectSubData(int offer,int max){
		  List<ExamSub> list=new ArrayList<ExamSub>();
		  if(dao.tableExit(tablename)==false){
				return null;
			}else{
				db.beginTransaction();
				try{
				curExamSub=db.rawQuery("select * from "+tablename+" limit ?,?", new String[]{Integer.toString(offer),Integer.toString(max)});
				while(curExamSub.moveToNext()){
					int _ID=curExamSub.getInt(curExamSub.getColumnIndex("_ID"));
					
					String strXUE=curExamSub.getString(curExamSub.getColumnIndex("strXUE"));
			
					ExamSub examSub=new ExamSub(_ID,strXUE);
					examSub.set_ID(_ID);
					examSub.setStrXUE(strXUE);
					list.add(new ExamSub(_ID,strXUE));
					
					}
				curExamSub.close();
				db.setTransactionSuccessful();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.endTransaction();
				db.close();
			}
				dao.close();
				
				}	  
		return list;  
	  }
	public long getCount(){
		Cursor cursor=db.rawQuery("select count(*)  from "+tablename+"",null);
		cursor.moveToFirst();
		return cursor.getLong(0);
		
	}
}
