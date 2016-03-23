package com.zd.musictorelax.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Context;

public class RelaxSFeed {
	/** xml�ļ����� **/
    private String title;
    /** xml��item������ **/
    private int itemcount;
    /** xml��item�����ü��� **/
    private List<RelaxSBean> itemlist;
    
    public RelaxSFeed(){
        itemlist = new Vector<RelaxSBean>(0);
    }
    
    /**
     * ����һ��Item���뵽Feed����
     * @param item
     * @return
     */
    public int addItem(RelaxSBean item){
        itemlist.add(item);
        itemcount++;
        return itemcount;
    }
    
    public RelaxSBean getItem(int location){
        return itemlist.get(location);
    }
    
    public List<RelaxSBean> getAllItems(){
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
            item.put(RelaxSBean.RID, itemlist.get(i).getrID());
            item.put(RelaxSBean.RNAME, itemlist.get(i).getrName());
            item.put(RelaxSBean.RCREATORCODE, itemlist.get(i).getrCreatorCode());
            item.put(RelaxSBean.RCREATOR, itemlist.get(i).getrCreator());
            item.put(RelaxSBean.RCREATTIME, itemlist.get(i).getrCreatTime());
            item.put(RelaxSBean.RDESCRIBE, itemlist.get(i).getrDescribe());
            item.put(RelaxSBean.RICO, itemlist.get(i).getrICO());
            item.put(RelaxSBean.RINLAY, itemlist.get(i).getrInlay());
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

    public List<RelaxSBean> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<RelaxSBean> itemlist) {
        this.itemlist = itemlist;
    }
}
