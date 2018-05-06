package com.gx.mydairy.dairy.activitys;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.Dairy_Context;
import com.gx.mydairy.dairy.Entry.Uesr;
import com.gx.mydairy.dairy.Util.DBHelper;
import com.gx.mydairy.dairy.Util.MySqlUtil;
import com.gx.mydairy.dairy.Util.UtilToos;

import java.util.HashMap;
import java.util.Map;

public class DairyWriteActivity extends BaseActivity {

    private EditText Ed_dairy_write_title;
    private EditText Ed_dairy_write_content;
    private int mode = 0;
    private Dairy_Context dairy_context;
    private final String Action_Name = "update_List";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_write);

        findViews();
        init();
        setListeners();
    }

    @Override
    public void findViews() {
        Ed_dairy_write_title = (EditText) findViewById(R.id.Ed_dairy_write_title);
        Ed_dairy_write_content = (EditText) findViewById(R.id.Ed_dairy_write_content);
    }

    @Override
    public void init() {
        setTitleText("写日记");
        setOkVisibity(true);
        setOKText("保存");
        setOKImg(R.mipmap.ic_menu_moreoverflow_normanull);
        Ed_dairy_write_title.setText(UtilToos.getTimeToString(4));
        try {
            Intent intent = this.getIntent();
            Bundle bundle =  intent.getExtras();
            dairy_context = new Dairy_Context(bundle.getString("dairy_id"),"",bundle.getString("writedate"),
                    bundle.getString("title"),bundle.getString("context"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dairy_context == null) {
            mode = 0;
        } else {
            Ed_dairy_write_title.setText(dairy_context.getTitle());
            Ed_dairy_write_content.setText(dairy_context.getContext());
            Ed_dairy_write_content.setSelection(Ed_dairy_write_content.getText().toString().length());
            mode = 1;
        }

    }

    @Override
    public void setListeners() {

        getBasetitle_ok().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBHelper dbHelper = new DBHelper(getApplicationContext(), "dairy", null, 1);
                //得到一个可写的数据库
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                ContentValues cv;
                String context = Ed_dairy_write_content.getText().toString();
                String title = Ed_dairy_write_title.getText().toString();

                if (mode == 0) {
                    if (context != null && context != "") {
                        cv = new ContentValues();
                        cv.put("dairy_id", UtilToos.getTimeID());
                        cv.put("writedate", UtilToos.getTimeToString(1));
                        cv.put("title", title);
                        cv.put("context", context);
                        db.insert("tb_dairy", null, cv);
                    } else {
                        Toast.makeText(getApplicationContext(), "日记内容不能为空！", Toast.LENGTH_SHORT).show();
                        db.close();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();

                    //测试网络状态，用线程添加至服务器
                    MyHttp myHttp = new MyHttp();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("dairy_id", UtilToos.getTimeID());
                    map.put("writedate", UtilToos.getTimeToString(1));
                    map.put("title", UtilToos.getTimeToString(1));
                    map.put("context", context);
                    myHttp.execute(map, cv);

                } else {
                    if (context != null && context != "") {
                        cv = new ContentValues();
                        cv.put("title", title);
                        cv.put("context", context);
                        db.update("tb_dairy", cv, "dairy_id=?", new String[]{dairy_context.get_id()});
                        Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();

                        //测试网络状态，用线程添加至服务器
                        MyHttp myHttp = new MyHttp();
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("dairy_id", dairy_context.get_id());
                        map.put("writedate", dairy_context.getDate());
                        map.put("title", title);
                        map.put("context", context);
                        myHttp.execute(map);
                    } else {
                        Toast.makeText(getApplicationContext(), "日记内容不能为空！", Toast.LENGTH_SHORT).show();
                        db.close();
                        return;
                    }
                }
                db.close();
                sendBroadcasts();
                finish();
            }
        });

        getBasetitle_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //写新日记模式
                if (mode == 0) {
                    if (Ed_dairy_write_content.getText().toString() != "") {

                        AlertDialog.Builder ab = new AlertDialog.Builder(getApplicationContext());
                        ab.setTitle("未保存日记是否放弃？");
                        ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ab.show();
                    } else {
                        finish();
                    }
                } else {//修改日记模式

                }
            }
        });
    }

    /**
     * 发送广播
     */
    private void sendBroadcasts() {
        Intent mIntent = new Intent(Action_Name);
        mIntent.putExtra("MSG", -1);
        sendBroadcast(mIntent);
    }

    class MyHttp extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {

            if (UtilToos.ping()) {
                if (mode == 0) {
                    Map<String, String> map = (Map<String, String>) params[0];
                    String sql = "insert into tb_diary(dairy_id,user_id,writedate,title,context) values('" + map.get("dairy_id") + "','" + Uesr.getInstance().getUserPhone() + "','" + map.get("writedate") + "','" + map.get("title") + "','" + map.get("context") + "')";

                    if (MySqlUtil.execSQL(sql)) {
                        return "日记上传成功！";
                    } else {
                        return "日记上传失败！";
                    }
                } else {
                    Map<String, String> map = (Map<String, String>) params[0];
                    String sql = "UPDATE tb_diary SET writedate = '"
                            + map.get("writedate") + "',title = '" + map.get("title")
                            + "',context = '" + map.get("context") +
                            "' where dairy_id = '" + map.get("dairy_id") + "'";

                    if (MySqlUtil.execSQL(sql)) {
                        return "日记修改成功！";
                    } else {
                        return "日记修改失败！";
                    }
                }
            } else {
                if (mode == 0) {
                    DBHelper dbHelper = new DBHelper(getApplicationContext(), "dairy", null, 1);
                    //得到一个可写的数据库
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    ContentValues cv = (ContentValues) params[1];
                    cv.put("mystate", "a");
                    cv = new ContentValues(cv);
                    db.insert("tb_dairy_c", null, cv);

                } else {

                    DBHelper dbHelper = new DBHelper(getApplicationContext(), "dairy", null, 1);
                    //得到一个可写的数据库
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Map<String, String> map = (Map<String, String>) params[0];
                    ContentValues cv = new ContentValues();
                    cv.put("dairy_id", map.get("dairy_id"));
                    cv.put("mystate", "b");
                    cv.put("writedate", map.get("writedate"));
                    cv.put("title", map.get("title"));
                    cv.put("context", map.get("context"));

                    db.insert("tb_dairy_c", null, cv);
                }
            }
            return "网络错误，暂存本地数据库，等待同步！";
        }


        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}
