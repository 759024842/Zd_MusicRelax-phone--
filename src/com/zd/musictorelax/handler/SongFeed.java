package com.zd.musictorelax.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import android.content.Context;

public class SongFeed {
	/** xml�ļ����� **/
    private String title;
    /** xml��item������ **/
    private int itemcount;
    /** xml��item�����ü��� **/
    private List<SongBean> itemlist;
    
    public SongFeed(){
        itemlist = new Vector<SongBean>(0);
    }
    
    /**
     * ����һ��Item���뵽Feed����
     * @param item
     * @return
     */
    public int addItem(SongBean item){
        itemlist.add(item);
        itemcount++;
        return itemcount;
    }
    
    public SongBean getItem(int location){
        return itemlist.get(location);
    }
    
    public List<SongBean> getAllItems(){
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
            item.put(SongBean.SID,itemlist.get(i).getsID());
            item.put(SongBean.SNO,itemlist.get(i).getsNo());
            item.put(SongBean.SNAME,itemlist.get(i).getsName());
            item.put(SongBean.KID,itemlist.get(i).getkID());
            item.put(SongBean.SFORMAT,itemlist.get(i).getsFormat());
            item.put(SongBean.SPLAYTIME,itemlist.get(i).getSplayTime());
            item.put(SongBean.SDESCRIBE,itemlist.get(i).getsDescribe());
            item.put(SongBean.SENABLE,itemlist.get(i).getsEnable());
            item.put(SongBean.SGROUP,itemlist.get(i).getsGroup());
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

    public List<SongBean> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<SongBean> itemlist) {
        this.itemlist = itemlist;
    }
}
