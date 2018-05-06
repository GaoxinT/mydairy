package com.gx.mydairy.dairy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gx.mydairy.R;

/**
 * Created by GX on 2016/11/14.
 */

public class HomeAdapter extends BaseAdapter {

    private int[] img = {R.mipmap.main_map,R.mipmap.main_dairy,R.mipmap.main_calendar,R.mipmap.play};
    private String[] text ={"找位置","写日记","时间录","音乐"};
    private LayoutInflater mInflater;
    private Context context;
    public HomeAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public Object getItem(int position) {
        return img[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Log.v("BaseAdapterTest", "getView " + position + " " + convertView);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.main_item_menu, null);
            holder = new ViewHolder();
            //得到各个控件的对象
            holder.Img_main_menu_img = (ImageView) convertView.findViewById(R.id.Img_main_menu_img);
            holder.Tv_main_menu_text = (TextView) convertView.findViewById(R.id.Tv_main_menu_text);

            convertView.setTag(holder); //绑定ViewHolder对象
        }
        else {
            holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
        }

        //设置TextView显示的内容，即我们存放在动态数组中的数据
        holder.Img_main_menu_img.setImageResource(img[position]);
        holder.Tv_main_menu_text.setText(text[position]);

        return convertView;
    }

    /*存放控件 的ViewHolder*/
    public final class ViewHolder {
        ImageView Img_main_menu_img;
        TextView Tv_main_menu_text;

    }
}
