package com.gx.mydairy.dairy.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by GX on 2016/11/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";
    public static final int VERSION = 1;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //输出创建数据库的日志信息
        Log.i(TAG, "create Database------------->");
        String sql1 = "create table tb_dairy(dairy_id BIGINT PRIMARY KEY, writedate datetime,title varchar(50), context text)";
        //state 1 插入 2 修改 3 删除
        String sql2 = "create table tb_dairy_c(dairy_id BIGINT PRIMARY KEY,mystate varchar(50), writedate datetime,title varchar(50), context text)";

        String sql3 = "create table tb_user(_id BIGINT PRIMARY KEY,u_name varchar(50), u_phone varchar(50),u_pwd varchar(50), last_login datetime)";

        //execSQL函数用于执行SQL语句
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //输出更新数据库的日志信息
        Log.i(TAG, "update Database------------->");
    }
}
