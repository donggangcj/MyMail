package com.example.administrator.myapplication09;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/16.
 */
public class MyAdapter extends BaseAdapter {
    //声明一个集合
    List<Map<String,Object>> list;

    //声明一个反射器
    //反射器的作用是将布局文件xml反射成一个对象
    LayoutInflater inflater;

    public MyAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    //返回行布局个数的函数
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
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder = null;
        if(convertView==null) {
            convertView= inflater.inflate(R.layout.item_mail, null);   //一级优化convertView实际上是到recycle上找渲染过的item对象,减少反射的次数
            holder=new viewHolder();
            holder.logo = (ImageView) convertView.findViewById(R.id.logo);
            holder.tv_from = (TextView) convertView.findViewById(R.id.tv_from);
            holder.tv_subject = (TextView) convertView.findViewById(R.id.tv_subject);
            holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_data);
            convertView.setTag(holder);
        }else{
            holder = (viewHolder) convertView.getTag();
        }

        Map map = list.get(position);
        holder.logo.setImageResource((Integer) map.get("logo"));
        holder.tv_from.setText((String) map.get("from"));
        holder.tv_subject.setText((String) map.get("subject"));
        holder.tv_text.setText((String) map.get("text"));
        holder.tv_date.setText((String)map.get("date"));


        return convertView;
    }

    public class  viewHolder{
        ImageView logo;
        TextView tv_from;
        TextView tv_subject;
        TextView tv_text ;
        TextView tv_date;

    }


}