package com.gx.mydairy.dairy.activitys;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gx.mydairy.R;

/**
 * Created by GX on 2016/11/11.
 */

public abstract class BaseActivity extends FragmentActivity {

    private ImageView basetitle_back;
    private Button basetitle_ok;
    private RelativeLayout llRoot;
    private LinearLayout llBasetitleBack;
    private TextView tvBasetitleTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.activity_base);

        findView();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//        }

    }

    private void findView() {
        llRoot = (RelativeLayout) findViewById(R.id.ll_basetitle_root);
        llBasetitleBack = (LinearLayout) findViewById(R.id.ll_basetitle_back);
        tvBasetitleTitle = (TextView) findViewById(R.id.tv_basetitle_title);
        basetitle_back = (ImageView) findViewById(R.id.basetitle_back);
        basetitle_ok = (Button) findViewById(R.id.basetitle_ok);
    }


    /**
     重点是重写setContentView，让继承者可以继续设置setContentView
     * 重写setContentView
     * @param resId
     */
    @Override
    public void setContentView(int resId) {
        View view = getLayoutInflater().inflate(resId, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, R.id.ll_basetitle);
        if (null != llRoot)
            llRoot.addView(view, lp);
    }

    /**
     * 设置左边按钮是否显示
     * @param visible
     */
    public void setBackVisibity(boolean visible) {
        if (basetitle_back != null) {
            if (visible)
                basetitle_back.setVisibility(View.VISIBLE);
            else
                basetitle_back.setVisibility(View.GONE);
        }
    }

    /**
     *
     * 设置中间标题文字
     * @param title
     */
    public void setTitleText(String title) {
        if (tvBasetitleTitle != null)
            tvBasetitleTitle.setText(title);
    }

    /**
     *
     * 设置右边标题文字
     * @param text
     */
    public void setOKText(String text) {
        if (basetitle_ok != null)
            basetitle_ok.setText(text);
    }

    /**
     *
     * 设置右边背景
     * @param resid
     */
    public void setOKImg(int resid) {
        if (basetitle_ok != null)
            basetitle_ok.setBackgroundResource(resid);
    }


    /**
     * 设置右边按钮是否显示
     * @param visible
     */
    public void setOkVisibity(boolean visible) {
        if (basetitle_ok != null) {
            if (visible)
                basetitle_ok.setVisibility(View.VISIBLE);
            else
                basetitle_ok.setVisibility(View.GONE);
        }
    }

    public LinearLayout getLlBasetitleBack() {
        return llBasetitleBack;
    }

    public TextView getTvBasetitleTitle() {
        return tvBasetitleTitle;
    }

    public ImageView getBasetitle_back() {
        return basetitle_back;
    }

    public Button getBasetitle_ok() {
        return basetitle_ok;
    }

    public abstract void findViews();

    public abstract void init();

    public abstract void setListeners();
}
