package com.example.facerecognition;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

	Context context;
	List<RowItem> rowItems;
	
	CustomAdapter(Context context,List<RowItem> rowItems)
	{
		this.context=context;
		this.rowItems=rowItems;
	}
	private class ViewHolder {
	ImageView icon;
	TextView  title;
		
	}
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rowItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return rowItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return rowItems.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;
		convertview=null;
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertview==null) {
			convertview=inflater.inflate(R.layout.list_items,null);
			viewHolder=new ViewHolder();
			viewHolder.icon=(ImageView) convertview.findViewById(R.id.icon);
			viewHolder.title=(TextView) convertview.findViewById(R.id.title);
			RowItem pos=rowItems.get(position);
			
			viewHolder.icon.setImageResource(pos.getIcon());
			viewHolder.title.setText(pos.getTitle());
			convertview.setTag(viewHolder);
			
		}return convertview;
	}

}
