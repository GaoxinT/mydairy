package com.gx.mydairy.dairy.activitys;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Util.FormatTime;

public class DayActivity extends BaseActivity {

    int day = 0;
    int jlday = 0;
    public TextView daytv_1;
    public TextView daytv_2;
    public TextView daytv_3;
    public TextView daytv_4;
    public TextView time;

    private String startdata = "20150606";

    private Handler handler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            switch (paramAnonymousMessage.what) {
                case 10001:
                    daytv_1.setVisibility(View.VISIBLE);
                    daytv_1.setText("亲爱的");
                    break;
                case 10002:
                    daytv_2.setVisibility(View.VISIBLE);
                    daytv_2.setText("我们在一起已经有");
                    break;
                case 10003:
                    time.setVisibility(View.VISIBLE);
                    getdata();
                    break;
                case 10004:
                    daytv_3.setVisibility(View.VISIBLE);
                    int day1 = 0;
                    if (day > 100 && day < 999) {
                        day1 = Integer.parseInt(("" + day).substring(0, 1));
                    } else if(day > 1000 && day < 9999) {
                        day1 = Integer.parseInt(("" + day).substring(0, 2));
                    } else {
                        day1 = Integer.parseInt(("" + day).substring(0, 3));
                    }

                    jlday = (day1 + 1) * 100 - day;

                    if (day1 % 3 == 0) {
                        daytv_3.setText("距离" + (day1 / 3) + "年(" + (365 * (day1 / 3)) + "天)还有");
                    } else {
                        daytv_3.setText("距离" + (day1 + 1) * 100 + "天还有");
                    }
                    break;
                case 10005:
                    daytv_4.setVisibility(View.VISIBLE);
                    getinfo(jlday);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        findViews();
        init();
        setTitleText("时间计算");
        setOkVisibity(false);
        setListeners();
    }

    private void getinfo(int days) {

        int[] arrayOfInt = new int[2];
        arrayOfInt[0] = 0;
        arrayOfInt[1] = days;
        ValueAnimator localValueAnimator = ValueAnimator.ofInt(arrayOfInt);
        localValueAnimator.setDuration(1000L);
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                daytv_4.setText(paramAnonymousValueAnimator.getAnimatedValue().toString() + "天");
            }
        });
        localValueAnimator.start();

    }



    @Override
    public void findViews() {
        this.time = ((TextView) findViewById(R.id.time));
        this.daytv_1 = ((TextView) findViewById(R.id.daytv_1));
        this.daytv_2 = ((TextView) findViewById(R.id.daytv_2));
        this.daytv_3 = ((TextView) findViewById(R.id.daytv_3));
        this.daytv_4 = ((TextView) findViewById(R.id.daytv_4));
    }

    @Override
    public void init() {

        new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 1; i < 10; i++) {
                        if (i == 4) {
                            Thread.sleep(2000L);
                        } else {
                            Thread.sleep(1000L);
                        }
                        handler.sendEmptyMessage(i + 10000);
                    }
                } catch (InterruptedException localInterruptedException) {

                }
            }
        }).start();
    }

    @Override
    public void setListeners() {

        getBasetitle_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getdata() {
        try {
            day = new FormatTime(startdata).getDateDays();
            int[] arrayOfInt = new int[2];
            arrayOfInt[0] = 0;
            arrayOfInt[1] = day;
            ValueAnimator localValueAnimator = ValueAnimator.ofInt(arrayOfInt);
            localValueAnimator.setDuration(3000L);
            localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                    time.setText(paramAnonymousValueAnimator.getAnimatedValue().toString() + "天了");
                }
            });
            localValueAnimator.start();
            return;
        } catch (Exception localException) {
        }
    }
}
