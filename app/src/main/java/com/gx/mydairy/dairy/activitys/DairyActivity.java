package com.gx.mydairy.dairy.activitys;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.Dairy_Context;
import com.gx.mydairy.dairy.Entry.Uesr;
import com.gx.mydairy.dairy.Util.BaseSwipeListViewListener;
import com.gx.mydairy.dairy.Util.DBHelper;
import com.gx.mydairy.dairy.Util.HttpRequestUtil;
import com.gx.mydairy.dairy.Util.MySqlUtil;
import com.gx.mydairy.dairy.Util.SwipeListView;
import com.gx.mydairy.dairy.Util.UtilToos;
import com.gx.mydairy.dairy.adapter.SwipeAdapter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DairyActivity extends BaseActivity {

    private final String TAG = "DairyActivity";
    public static final int REFRESH_DELAY = 4000;

    private SwipeListView mSwipeListView;
    private ImageView Iv_dairy_add;
    private SwipeAdapter mAdapter;
    public static int deviceWidth;
    private List<Dairy_Context> testData;

    private boolean isMore = false;// menu菜单翻页控制
    private AlertDialog menuDialog;// menu菜单Dialog
    private GridView menuGrid;
    private View menuView;
    private final String Action_Name = "update_List";
    private final String Action_Name_del = "delete_diary";
    private final String Action_Name_up = "update_diary";
    /**
     * 菜单图片
     **/
    private final int[] menu_image_array = {R.mipmap.dairy_kapian,
            R.mipmap.dairy_reflash, R.mipmap.dairy_tongbu};
    //菜单文字
    private final String[] menu_name_array = {"卡片模式", "刷新列表", "数据同步"};

    //广播
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int position = intent.getIntExtra("MSG", 0);
            getDairy_List();
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 3:
                    del_Diary(msg.arg1);
                    break;
                case 2:
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), DairyWriteActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("dairy_id", testData.get(msg.arg1).get_id());
                    bundle.putString("writedate", testData.get(msg.arg1).getDate());
                    bundle.putString("title", testData.get(msg.arg1).getTitle());
                    bundle.putString("context", testData.get(msg.arg1).getContext());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy);

        findViews();
        init();
        setListeners();
        //上传由于网络问题失败的日记
        uploadDairy_c uploadDairy_c = new uploadDairy_c();
        uploadDairy_c.execute();

        Boradcast();
    }

    /**
     * 广播
     */
    private void Boradcast() {

        IntentFilter miIntentFilter = new IntentFilter();
        miIntentFilter.addAction(Action_Name);
        registerReceiver(mBroadcastReceiver, miIntentFilter);


    }

    @Override
    public void init() {

        setBackVisibity(true);
        setTitleText("我的日记");
        setOkVisibity(true);
        setOKImg(R.mipmap.ic_menu_moreoverflow_norma);
        //更新list数据
        getDairy_List();
        // 创建AlertDialog
        menuDialog = new AlertDialog.Builder(this).create();
        menuDialog.setView(menuView);
        menuDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
                    dialog.dismiss();
                return false;
            }
        });
        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
        reload();

    }

    /**
     * 更新list数据
     */
    private void getDairy_List() {
        testData = getTestData();
        //数据适配
        mAdapter = new SwipeAdapter(getApplicationContext(), R.layout.dairy_item, testData, mSwipeListView, mHandler);

        mSwipeListView.setAdapter(mAdapter);
    }

    private List<Dairy_Context> getTestData() {

        DBHelper dbHelper = new DBHelper(getApplicationContext(), "dairy", null, 1);
        //得到一个可写的数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //参数1：表名 参数2：要想显示的列 参数3：where子句 参数4：where子句对应的条件值 参数5：分组方式
        // 参数6：having条件   参数7：排序方式
        Cursor cursor = db.query("tb_dairy", new String[]{"dairy_id", "writedate", "title", "context"}, null, null, null, null, null);

        List<Dairy_Context> dairys = new ArrayList<>();
        Dairy_Context dairy;
        while (cursor.moveToNext()) {

            String _id = cursor.getString(cursor.getColumnIndex("dairy_id"));
            String writedate = cursor.getString(cursor.getColumnIndex("writedate"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String context = cursor.getString(cursor.getColumnIndex("context"));
            dairy = new Dairy_Context(_id, "", writedate, title, context);
            dairys.add(dairy);
            //Log.v(TAG, dairy.toString());
        }

        //关闭数据库
        db.close();
        return dairys;
    }

    private void reload() {

        mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        //滑动时向左偏移量，根据设备的大小来决定偏移量的大小
        mSwipeListView.setOffsetLeft(deviceWidth - 1);
        //设置动画时间
        mSwipeListView.setAnimationTime(0);
        mSwipeListView.setSwipeOpenOnLongPress(false);
    }

    class MyBaseSwipeListViewListener extends BaseSwipeListViewListener {

        //点击每一项的响应事件
        @Override
        public void onClickFrontView(int position) {
            super.onClickFrontView(position);

            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), DairyWriteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("dairy_id", testData.get(position).get_id());
            bundle.putString("writedate", testData.get(position).getDate());
            bundle.putString("title", testData.get(position).getTitle());
            bundle.putString("context", testData.get(position).getContext());
            intent.putExtras(bundle);
            startActivity(intent);
        }

        //关闭事件
        @Override
        public void onDismiss(int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                Log.i("lenve", "position--:" + position);
                testData.remove(position);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void findViews() {

        menuView = View.inflate(this, R.layout.dairy_gridview_menu, null);
        menuGrid = (GridView) menuView.findViewById(R.id.gridview);
        mSwipeListView = (SwipeListView) findViewById(R.id.lv_dairy_list);
        Iv_dairy_add = (ImageView) findViewById(R.id.Iv_dairy_add);
    }


    @Override
    public void setListeners() {

        //设置事件监听
        mSwipeListView.setSwipeListViewListener(new MyBaseSwipeListViewListener());
        getBasetitle_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        getBasetitle_ok().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });

        //添加日记
        Iv_dairy_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), DairyWriteActivity.class);
                startActivity(intent);
            }
        });

        //菜单栏监听
        menuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:// 卡片
                        menuDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "功能开发中...", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:// 刷新列表
                        getDairy_List();
                        menuDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "刷新成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:// 同步
                        menuDialog.dismiss();
                        //上次数据
                        Toast.makeText(getApplicationContext(), "开始同步请稍后！", Toast.LENGTH_SHORT).show();
                        upload_Download_Data up = new upload_Download_Data();
                        up.execute();
                        break;
                }
            }
        });
    }

    /**
     * 同步数据
     */
    class upload_Download_Data extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {

            //更新三步
            //1 上传数据到数据库
            //2 删除本地数据库数据
            //3 下载数据到本机
            DBHelper dbHelper = new DBHelper(getApplicationContext(), "dairy", null, 1);
            //得到一个可写的数据库
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("tb_dairy", new String[]{"dairy_id", "writedate", "title", "context"}, null, null, null, null, null);

            List<Dairy_Context> dairys = new ArrayList<>();
            Dairy_Context dairy;
            while (cursor.moveToNext()) {

                String _id = cursor.getString(cursor.getColumnIndex("dairy_id"));
                String writedate = cursor.getString(cursor.getColumnIndex("writedate"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String context = cursor.getString(cursor.getColumnIndex("context"));
                dairy = new Dairy_Context(_id, "", writedate, title, context);
                dairys.add(dairy);
                //Log.v(TAG, dairy.toString());
            }

            StringBuilder sb = new StringBuilder();
            ContentValues cv = null;

            if (UtilToos.ping()) {//获取网络状态
                if (dairys.size() != 0) {
                    for (Dairy_Context dairy_context : dairys) {
                        sb.append("insert into tb_diary(dairy_id,user_id,writedate,title,context) values('"
                                + dairy_context.get_id() + "','" + Uesr.getInstance().getUserPhone() + "','" + dairy_context.getDate() + "','"
                                + dairy_context.getTitle() + "','" + dairy_context.getContext() + "');");
                    }
                    //1 上传数据到数据库
                    if (MySqlUtil.execSQLBathh(sb.toString())) {//把数据添加到云数据库
                        Log.v("TAG_同步", "上传数据到数据库成功！");
                    } else {
                        db.close();
                        return "同步失败！";
                    }
                }
                String sqls = "select * from tb_diary where user_id = '"+Uesr.getInstance().getUserPhone()+"'";
                dairys = MySqlUtil.queryAllDownlod(sqls);

                if (dairys != null && dairys.size() != 0) {
                    db.delete("tb_dairy", null, null);//2 删除本地数据
                    Log.v("TAG_同步", "删除本地数据！");

                    //3  下载数据到本机 循环插入数据
                    for (Dairy_Context dairy_context : dairys) {
                        cv = new ContentValues();

                        cv.put("dairy_id", dairy_context.get_id());
                        cv.put("writedate", dairy_context.getDate());
                        cv.put("title", dairy_context.getTitle());
                        cv.put("context", dairy_context.getContext());
                        try {
                            db.insert("tb_dairy", null, cv);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    Log.v("TAG_同步", "下载数据到本机 循环插入数据！");
                }
                db.close();
                return "同步成功！";
            } else {
                db.close();
                return "网络连接，失败请检查网络！";
            }

        }

        @Override
        protected void onPostExecute(Object s) {
            getDairy_List();
            Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 上传由于网络问题失败的日记
     */
    class uploadDairy_c extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {

            if (UtilToos.ping()) {//获取网络状态

                DBHelper dbHelper = new DBHelper(getApplicationContext(), "dairy", null, 1);
                //得到一个可写的数据库
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("tb_dairy_c", new String[]{"mystate", "dairy_id", "writedate", "title", "context"}, null, null, null, null, null);

                List<Dairy_Context> dairys = new ArrayList<Dairy_Context>();
                Dairy_Context dairy;
                while (cursor.moveToNext()) {
                    String mystate = cursor.getString(cursor.getColumnIndex("mystate"));
                    String _id = cursor.getString(cursor.getColumnIndex("dairy_id"));
                    String writedate = cursor.getString(cursor.getColumnIndex("writedate"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String context = cursor.getString(cursor.getColumnIndex("context"));

                    dairy = new Dairy_Context(_id, mystate, writedate, title, context);
                    dairys.add(dairy);
                    //Log.v(TAG, dairy.toString());
                }

                StringBuilder sb = new StringBuilder();

                if (dairys.size() != 0) {
                    for (Dairy_Context dairy_context : dairys) {
                        if (dairy_context.getMystate().equals("a")) {
                            sb.append("insert into tb_diary(dairy_id,user_id,writedate,title,context) values('"
                                    + dairy_context.get_id() + "','" + Uesr.getInstance().getUserPhone() + "','" + dairy_context.getDate() + "','"
                                    + dairy_context.getTitle() + "','" + dairy_context.getContext() + "');");
                        } else if (dairy_context.getMystate().equals("b")) {
                            sb.append("UPDATE tb_diary SET writedate = '"
                                    + dairy_context.getDate() + "',title = '" + dairy_context.getTitle()
                                    + "',context = '" + dairy_context.getContext() + "' where dairy_id = '"
                                    + dairy_context.get_id() + "';");
                        } else if (dairy_context.getMystate().equals("c")) {
                            sb.append("delete from tb_diary where dairy_id = '" + dairy_context.get_id() + "';");
                        }
                    }
                    if (sb.toString().length() > 0) {
                        if (MySqlUtil.execSQLBathh(sb.toString())) {//把数据添加到云数据库
                            db.delete("tb_dairy_c", null, null);//删除本地数据
                            return "上传成功！";
                        } else {
                            return "上传失败！";
                        }
                    } else {
                        return "上传日记state错误";
                    }
                } else {
                    return "没有要上传的数据";
                }
            } else {
                return "网络错误！";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v("上传网络问题TAG", s);
        }
    }

    /**
     * 删除日记
     *
     * @param position
     */
    private void del_Diary(int position) {
        DBHelper dbHelper = new DBHelper(getApplicationContext(), "dairy", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();//得到一个可写的数据库

        db.delete("tb_dairy", "dairy_id=?", new String[]{testData.get(position).get_id()});//删除本地数据

        Delete_Diary mDelete_diary = new Delete_Diary();
        mDelete_diary.execute(testData.get(position));
    }

    /**
     * 删除 日记
     * 异步
     */
    class Delete_Diary extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {
            DBHelper dbHelper = new DBHelper(getApplicationContext(), "dairy", null, 1);
            SQLiteDatabase db = dbHelper.getReadableDatabase();//得到一个可写的数据库
            Dairy_Context dairy_context = (Dairy_Context) params[0];
            if (UtilToos.ping()) {//获取网络状态

                String sql = "delete from tb_diary where dairy_id = '" + dairy_context.get_id() + "'";
                if (MySqlUtil.execSQL(sql)) {//把数据添加到云数据库
                    return "删除成功！";
                } else {
                    return "删除失败！";
                }
            } else {
                ContentValues cv = new ContentValues();
                cv.put("dairy_id", dairy_context.get_id());
                cv.put("mystate", "c");
                cv.put("writedate", dairy_context.getDate());
                cv.put("title", dairy_context.getTitle());
                cv.put("context", dairy_context.getContext());
                db.insert("tb_dairy_c", null, cv);//插入待处理数据库
                return "删除失败,网络错误！";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            Log.v("删除网络数据：", s);
        }
    }

    /**
     * 菜单适配器
     *
     * @param menuNameArray
     * @param imageResourceArray
     * @return
     */
    public SimpleAdapter getMenuAdapter(String[] menuNameArray,
                                        int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.dairy_item_menu, new String[]{"itemImage", "itemText"},
                new int[]{R.id.item_image, R.id.item_text});
        return simperAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");// 必须创建一项
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menuDialog == null) {
            menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
        } else {
            menuDialog.show();
        }
        return false;// 返回为true 则显示系统menu
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
