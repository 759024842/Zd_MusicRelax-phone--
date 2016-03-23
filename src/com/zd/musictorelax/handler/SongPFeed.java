package com.zd.musictorelax.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.Context;

public class SongPFeed {
	/** xml文件标题 **/
    private String title;
    /** xml中item的数量 **/
    private int itemcount;
    /** xml中item项引用集合 **/
    private List<SongPBean> itemlist;
    
    public SongPFeed(){
        itemlist = new Vector<SongPBean>(0);
    }
    
    /**
     * 负责将一个Item加入到Feed类中
     * @param item
     * @return
     */
    public int addItem(SongPBean item){
        itemlist.add(item);
        itemcount++;
        return itemcount;
    }
    
    public SongPBean getItem(int location){
        return itemlist.get(location);
    }
    
    public List<SongPBean> getAllItems(){
        return itemlist;
    }

    /**
     * 
     * @return
     */
	public ArrayList<HashMap<String, Object>> getSaveForListView(Context context){
    	ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        int size = itemlist.size();
        for(int i=0 ; i<size ; i++){
            HashMap<String , Object> item = new HashMap<String, Object>();
            item.put(SongPBean.SNAME, itemlist.get(i).getsName());
            item.put(SongPBean.SFORMAT, itemlist.get(i).getsFormat());
            item.put(SongPBean.SPLAYTIME, itemlist.get(i).getSplayTime());
            data.add(item);
        }
        return data;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemcount() {
        return itemcount;
    }

    public void setItemcount(int itemcount) {
        this.itemcount = itemcount;
    }

    public List<SongPBean> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<SongPBean> itemlist) {
        this.itemlist = itemlist;
    }
}
