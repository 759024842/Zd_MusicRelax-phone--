package com.zd.musictorelax.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Context;

public class SongKFeed {
	/** xml�ļ����� **/
    private String title;
    /** xml��item������ **/
    private int itemcount;
    /** xml��item�����ü��� **/
    private List<SongKBean> itemlist;
    
    public SongKFeed(){
        itemlist = new Vector<SongKBean>(0);
    }
    
    /**
     * ����һ��Item���뵽Feed����
     * @param item
     * @return
     */
    public int addItem(SongKBean item){
        itemlist.add(item);
        itemcount++;
        return itemcount;
    }
    
    public SongKBean getItem(int location){
        return itemlist.get(location);
    }
    
    public List<SongKBean> getAllItems(){
        return itemlist;
    }
    
    /**
     * �����RSSFeed���������б�����Ҫ������
     * @return
     */
    @SuppressWarnings("rawtypes")
	public List getAllItemForListView(Context context){
        List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
        int size = itemlist.size();
        for(int i=0 ; i<size ; i++){
            HashMap<String , Object> item = new HashMap<String, Object>();
            item.put(SongKBean.KID, itemlist.get(i).getkID());
            item.put(SongKBean.KNAME, itemlist.get(i).getkName());
            item.put(SongKBean.KDESCRIBE, itemlist.get(i).getkDescribe());
            item.put(SongKBean.KICO, itemlist.get(i).getkICO());
            item.put(SongKBean.KGROUP, itemlist.get(i).getkGroup());
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

    public List<SongKBean> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<SongKBean> itemlist) {
        this.itemlist = itemlist;
    }
}


