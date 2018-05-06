package com.gx.mydairy.dairy.adapter;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.Dairy_Context;
import com.gx.mydairy.dairy.Util.DBHelper;
import com.gx.mydairy.dairy.Util.SwipeListView;

import java.util.List;

/**
 * Created by GX on 2016/11/16.
 */

public class SwipeAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private List<Dairy_Context> objects;
    private SwipeListView mSwipeListView;
    private Context context;
    private final String Action_Name_del = "delete_diary";
    private final String Action_Name_up = "update_diary";
    private Handler mHandler;

    public SwipeAdapter(Context context, int textViewResourceId, List<Dairy_Context> objects, SwipeListView mSwipeListView,Handler mHandler) {
        this.objects = objects;
        this.context = context;
        this.mSwipeListView = mSwipeListView;
        this.mHandler = mHandler;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dairy_item, parent, false);
            holder = new ViewHolder();
            holder.mTiTle = (TextView) convertView.findViewById(R.id.row_tv_title);
            holder.mContext = (TextView) convertView.findViewById(R.id.row_tv_context);
            holder.mBackEdit = (Button) convertView.findViewById(R.id.row_b_action_bianji);
            holder.mBackDelete = (Button) convertView.findViewById(R.id.row_b_action_del);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTiTle.setText(objects.get(position).getTitle());
        holder.mContext.setText(objects.get(position).getContext());

        holder.mBackDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sendBroadcasts(position,Action_Name_del);
                Message msg = mHandler.obtainMessage();
                msg.arg1 = position;
                msg.what = 3;
                mHandler.sendMessage(msg);

                mSwipeListView.closeAnimate(position);//关闭动画
                mSwipeListView.dismiss(position);//调用dismiss方法删除该项（这个方法在MainActivity中）
            }


        });
        holder.mBackEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sendBroadcasts(position,Action_Name_up);
                Message msg = mHandler.obtainMessage();
                msg.arg1 = position;
                msg.what = 2;
                mHandler.sendMessage(msg);

                mSwipeListView.closeAnimate(position);//关闭动画
            }
        });

        return convertView;
    }

    /**
     * 发送广播
     * @param position
     * @param action
     */
    private void sendBroadcasts(int position,String action) {
        Intent mIntent = new Intent(action);
        mIntent.putExtra("MSG", position);
        context.sendBroadcast(mIntent);
    }



    class ViewHolder {
        TextView mTiTle, mContext;
        Button mBackEdit, mBackDelete;
    }

}
