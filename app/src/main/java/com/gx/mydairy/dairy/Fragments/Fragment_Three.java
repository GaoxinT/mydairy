package com.gx.mydairy.dairy.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.Uesr;
import com.gx.mydairy.dairy.activitys.DairyActivity;
import com.gx.mydairy.dairy.activitys.LoginActivity;
import com.mysql.jdbc.Util;

/**
 * Created by Doraemon on 2016/11/14
 */
public class Fragment_Three extends Fragment {

    private TextView Tv_user_id;
    private LinearLayout Layout_user_tcdr;
    private ImageView Iv_user_head_img;
    private SharedPreferences mSp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_user_info, null);

        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findView();
        init();
        setListeners();
    }

    private void init() {
        if (Uesr.getInstance().getUserPhone() != null) {
            Tv_user_id.setText(Uesr.getInstance().getUserPhone());
        }
    }


    private void findView() {

        Iv_user_head_img = (ImageView) getActivity().findViewById(R.id.Iv_user_head_img);
        Tv_user_id = (TextView) getActivity().findViewById(R.id.Tv_user_id);
        Layout_user_tcdr = (LinearLayout) getActivity().findViewById(R.id.Layout_user_tcdr);
    }

    private void setListeners() {

        Layout_user_tcdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Uesr.getInstance().getUserPhone() != null) {
                    mSp = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
                    Uesr.getInstance().setNull();
                    Tv_user_id.setText("请登入");
                    SharedPreferences.Editor editor = mSp.edit(); //会生成一个Editor类型的引用变量
                    editor.putString("id", "");
                    editor.putString("password", "");
                    Toast.makeText(getActivity(), "退出登入成功！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "未登入，无需退出！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Tv_user_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Uesr.getInstance().getUserPhone() == null) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}
