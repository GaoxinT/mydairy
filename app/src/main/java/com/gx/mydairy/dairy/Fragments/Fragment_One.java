package com.gx.mydairy.dairy.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.Uesr;
import com.gx.mydairy.dairy.activitys.DairyActivity;
import com.gx.mydairy.dairy.activitys.DayActivity;
import com.gx.mydairy.dairy.activitys.LoginActivity;
import com.gx.mydairy.dairy.activitys.MusicActivity;
import com.gx.mydairy.dairy.adapter.HomeAdapter;

/**
 * Created by Doraemon on 2016/11/14
 */
public class Fragment_One extends Fragment {

    private GridView Gv_main_menu_ic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_home, null);

        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findView();

        Gv_main_menu_ic.setAdapter(new HomeAdapter(getActivity()));

        setListeners();

    }

    private void setListeners() {

        Gv_main_menu_ic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Uesr.getInstance().setUserId("1");
                //Uesr.getInstance().setUserLastLogin("1");
                //Uesr.getInstance().setUserPhone("1");
                //Uesr.getInstance().setUserPwd("1");
                //Uesr.getInstance().setUserState("1");
                if (position == 0) {
                    Toast.makeText(getActivity(), "功能开发中。。。", Toast.LENGTH_LONG).show();
                }
                if (position == 1) {
                    Intent intent = new Intent();
                    if (Uesr.getInstance().getUserPhone() != null) {

                        intent.setClass(getContext(), DairyActivity.class);
                    } else {
                        intent.setClass(getContext(), LoginActivity.class);
                    }
                    startActivity(intent);
                }
                if (position == 2) {
                    Intent intent = new Intent();
                    if (Uesr.getInstance().getUserId() != null) {
                        intent.setClass(getContext(), DayActivity.class);
                    } else {
                        intent.setClass(getContext(), LoginActivity.class);
                    }
                    startActivity(intent);
                }
                if (position == 3) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), MusicActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void findView() {

        Gv_main_menu_ic = (GridView) getActivity().findViewById(R.id.Gv_main_menu_ic);
    }
}
