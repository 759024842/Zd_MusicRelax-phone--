package com.zd.musictorelax.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Context;

public class RelaxSSFeed {

	/** xml�ļ����� **/
    private String title;
    /** xml��item������ **/
    private int itemcount;
    /** xml��item�����ü��� **/
    private List<RelaxSSBean> itemlist;
    
    public RelaxSSFeed(){
        itemlist = new Vector<RelaxSSBean>(0);
    }
    
    /**
     * ����һ��Item���뵽Feed����
     * @param item
     * @return
     */
    public int addItem(RelaxSSBean item){
        itemlist.add(item);
        itemcount++;
        return itemcount;
    }
    
    public RelaxSSBean getItem(int location){
        return itemlist.get(location);
    }
    
    public List<RelaxSSBean> getAllItems(){
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
            item.put(RelaxSSBean.RSID, itemlist.get(i).getRsID());
            item.put(RelaxSSBean.SID, itemlist.get(i).getsID());
            item.put(RelaxSSBean.RID, itemlist.get(i).getrID());
            item.put(RelaxSSBean.ENABLE, itemlist.get(i).getEnable());
          
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

    public List<RelaxSSBean> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<RelaxSSBean> itemlist) {
        this.itemlist = itemlist;
    }
}

