package com.gx.mydairy.dairy.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.Uesr;
import com.gx.mydairy.dairy.Util.GalleryView;
import com.gx.mydairy.dairy.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Doraemon on 2016/11/14.
 */
public class Fragment_Two extends Fragment {

    private Button button_huan;
    private GalleryView gallery;
    private ImageAdapter adapter;
    private Integer[] imgs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_find, null);


        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();
        initRes();
        setListener();

    }

    private void setListener() {

        button_huan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImg mLoadImg = new loadImg();
                mLoadImg.execute();
            }
        });

        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //tvTitle.setText(adapter.titles[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "img " + (position+1) + " selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findViews() {
        button_huan = (Button) getActivity().findViewById(R.id.button_huan);
        gallery = (GalleryView) getActivity().findViewById(R.id.mygallery);
    }

    private void initRes() {

        if (Uesr.getInstance().getUserPhone() != null) {
            if (Uesr.getInstance().getUserPhone().equals("15211122701")||Uesr.getInstance().getUserPhone().equals("13975551494")) {
                loadImg mLoadImg = new loadImg();
                mLoadImg.execute();
                button_huan.setVisibility(View.VISIBLE);
            }
        } else {
            button_huan.setVisibility(View.GONE);
        }

    }

    class loadImg extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {
            try {
                imgs = getImg();
                adapter = new ImageAdapter(getActivity(), imgs);
                adapter.createReflectedImages();
                return "0";
            } catch (Exception e) {
                e.printStackTrace();
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == "0") {
                gallery.setAdapter(adapter);
            } else {

            }
            super.onPostExecute(s);
        }
    }

    /**
     * 获取图片id
     *
     * @return
     */
    public Integer[] getImg() {

        imgs = new Integer[10];

        String[] im = new String[40];
        List<Integer> count = new ArrayList<Integer>();

        for (int i = 0; i < im.length; i++) {
            im[i] = "image" + (i + 1);
        }

        for (int i = 0; i < imgs.length; i++) {

            Random random = new Random();
            int ran = random.nextInt(40);

            while (count.contains(ran)) {
                ran = random.nextInt(40);
                if (count.size() > 40) {
                    count.clear();
                }
            }
            imgs[i] = getActivity().getResources().getIdentifier(im[ran], "mipmap", getActivity().getPackageName());
            count.add(ran);
        }
        return imgs;
    }
}
