package com.gx.mydairy.dairy.activitys;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Util.HttpRequestUtil;
import com.gx.mydairy.dairy.Util.MySqlUtil;
import com.gx.mydairy.dairy.Util.UtilToos;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    private CheckBox checkBox = null;
    private Button btnSignin = null;
    private EditText accountEt = null;
    private EditText pwdEt = null;
    private TextView tv_register = null;
    private ProgressBar progressBar = null;

    private Button subBtn_regidit = null;
    private EditText edit_Phone = null;
    private EditText pwdEt_regedit = null;

    private LinearLayout loginPanel = null;
    private LinearLayout regeditPanel = null;

    private SharedPreferences mSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        setListeners();
        init();
    }

    @Override
    public void findViews() {


        checkBox = (CheckBox) findViewById(R.id.remonberPwd);
        btnSignin = (Button) findViewById(R.id.subBtn);
        accountEt = (EditText) findViewById(R.id.accountEt);
        pwdEt = (EditText) findViewById(R.id.pwdEt);
        progressBar = (ProgressBar) findViewById(R.id.progressBarlogin);
        tv_register = (TextView) findViewById(R.id.tv_register);

        subBtn_regidit = (Button) findViewById(R.id.subBtn_regidit);
        edit_Phone = (EditText) findViewById(R.id.edit_Phone);
        pwdEt_regedit = (EditText) findViewById(R.id.pwdEt_regedit);

        loginPanel = (LinearLayout) findViewById(R.id.loginPanel);
        regeditPanel = (LinearLayout) findViewById(R.id.regeditPanel);
    }

    @Override
    public void init() {
        checkBox.setChecked(true);
        mSp = getSharedPreferences("login", MODE_PRIVATE);
        String id = mSp.getString("id", "");
        String password = mSp.getString("password", "");
        accountEt.setText(id);
        pwdEt.setText(password);
    }

    @Override
    public void setListeners() {
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = accountEt.getText().toString();
                String pwd = pwdEt.getText().toString();
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", id);
                map.put("password", pwd);
                progressBar.setVisibility(View.VISIBLE);
                new Login().execute(map);
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPanel.setVisibility(View.GONE);
                regeditPanel.setVisibility(View.VISIBLE);
            }
        });

        subBtn_regidit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Regeidt mRegeidt = new Regeidt();
                mRegeidt.execute(edit_Phone.getText().toString(), pwdEt_regedit.getText().toString());
            }
        });
    }

    class Regeidt extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... params) {

            String sql = String.format("insert into tb_user(u_name,u_phone,u_pwd,last_login) values('%s','%s','%s','%s')",
                    params[0].toString(), params[0].toString(),
                    params[0].toString(), params[0].toString(), UtilToos.getTimeToString(1));
            if (MySqlUtil.execSQL(sql)) {
                return "0";
            } else {
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            mSp = getSharedPreferences("login", MODE_PRIVATE);
            if (s == "0") {

                SharedPreferences.Editor editor = mSp.edit(); //会生成一个Editor类型的引用变量
                editor.putString("id", edit_Phone.getText().toString());
                editor.putString("password", "");
                editor.commit();

                Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                loginPanel.setVisibility(View.VISIBLE);
                regeditPanel.setVisibility(View.GONE);
            }else {
                Toast.makeText(getApplicationContext(), "注册失败！手机号已被注册！", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(s);
        }
    }

    /**
     * 登入
     */
    class Login extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... params) {
            Map<String, String> map = (Map<String, String>) params[0];
            if (MySqlUtil.login(map.get("username"), map.get("password"))) {
                return "0";
            } else {
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String o) {

            if (o == "0") {
                mSp = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = mSp.edit(); //会生成一个Editor类型的引用变量
                editor.putString("id", accountEt.getText().toString());
                editor.putString("password", pwdEt.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "登入成功！", Toast.LENGTH_LONG).show();
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "信息错误！登入失败！", Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(o);
        }
    }
}

