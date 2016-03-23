package com.zd.musictorelax.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.zd.musictorelax.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class PlayListAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, Object>> list;
	private HashMap<Integer, Boolean> isSelected;
	private HashMap<Integer, Integer> niCbVisible;
	@SuppressWarnings("unused")
	private Context context;
	private LayoutInflater inflater = null;
	public int cur;
	@SuppressLint("UseSparseArrays")
	public PlayListAdapter(ArrayList<HashMap<String,Object>> list,Context context) {
		this.list = list;
		this.context = context;
		//inflater = LayoutInflater.from(context);
		inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		niCbVisible = new HashMap<Integer, Integer>();
		isSelected = new HashMap<Integer, Boolean>();
		cbIsVisible();
		initDate();
	}

	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	private void cbIsVisible() {
		for (int i = 0; i < list.size(); i++) {
			getIsCbVisible().put(i, View.INVISIBLE);
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolderP holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_play_item, null);
			holder = new ViewHolderP();
			holder.cb = (CheckBox) convertView
					.findViewById(R.id.cbx_list_count);
			holder.txtName = (TextView) convertView
					.findViewById(R.id.txt_list_count_name);
			holder.txtTime = (TextView) convertView
					.findViewById(R.id.txt_list_count_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolderP) convertView.getTag();
		}
		holder.cb.setVisibility(getIsCbVisible().get(position));
		holder.cb.setChecked(getIsSelected().get(position));
		holder.txtName.setText(list.get(position).get("sName") + "");
		holder.txtTime.setText(list.get(position).get("splayTime")+"");
		if (cur == position) {
			holder.txtName.setTextColor(Color.BLUE);
			holder.txtTime.setTextColor(Color.BLUE);
		} else {
			holder.txtName.setTextColor(Color.BLACK);
			holder.txtTime.setTextColor(Color.BLACK);
		}
		return convertView;
	}

	public  HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		this.isSelected = isSelected;
	}

	public  HashMap<Integer, Integer> getIsCbVisible() {
		return niCbVisible;
	}

	public  void setIsCbVisible(HashMap<Integer, Integer> niCbVisible) {
		this.niCbVisible = niCbVisible;
	}

	public  class ViewHolderP{
		public CheckBox cb;
		public TextView txtName;
		public TextView txtTime;
	}
}
