package com.gx.mydairy.dairy.activitys;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.mapcore.util.ex;
import com.gx.mydairy.R;
import com.gx.mydairy.dairy.Entry.Music;
import com.gx.mydairy.dairy.Util.MediaService;

public class MusicActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */

    // string
    private static final String TIANLAIZHIYIN = "tianlaizhiyin";
    private static final String LASTPATH = "lastPath";
    private static final String PLAYMODE = "playMode";
    public static final String ONPLAYCOMPLETED = "onplaycompleted";

    public static final int PLAY_STOP = 0;
    public static final int PLAY_PAUSE = 1;
    public static final int PLAY_PLAY = 2;

    public int currentPlayStatus = PLAY_STOP;
    //data
    public ArrayList<Music> musicList = new ArrayList<Music>();
    public static int currentPosition = 0;
    //adapter
    private MyAdapter listAdapter;

    //notification
    private NotificationManager notifManager;


    //playMode
    public static final int SINGE = 0;
    public static final int SINGE_R = 1;
    public static final int REPEAT = 2;
    public static final int ORDER = 3;
    public int currentPlayMode = 0;

    //views
    private static TextView playTime;
    private TextView durationTime;
    private static SeekBar seekBar;
    private ListView musicListView;

    private ImageView playMode;
    private ImageView playAndPause;
    private ImageView settings;

    private ImageButton playModeButton;
    private ImageButton preMusicButton;
    private ImageButton nextMusicButton;
    private ImageButton playAndPauseButton;
    private ImageButton settingsPauseButton;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int playMode = getPlayMode();
            if (playMode == SINGE) {

                playAndPause.setImageDrawable(getResources().getDrawable(R.drawable.img_playback_bt_play));
            } else if (playMode == SINGE_R) {

                currentPosition--;
                play();
            } else if (playMode == REPEAT) {

                if (currentPosition < musicList.size()) {

                    currentPosition++;
                    play();
                } else {
                    currentPosition = 0;
                    play();
                }
            } else if (playMode == ORDER) {

                if (currentPosition >= musicList.size()) {

                    playAndPause.setImageDrawable(getResources().getDrawable(R.drawable.img_playback_bt_play));
                } else {
                    currentPosition++;
                    play();
                }
            }
        }
    };

    public static Handler seekBarHandle = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case -1:
                    seekBar.setProgress(msg.getData().getInt("posiztion"));
                    break;

            }

            super.handleMessage(msg);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        setTitleText("音乐");
        setOkVisibity(false);

        new InitDataTask().execute((Void[]) null);
    }

    private void initMusics() {

        Cursor cur = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA},
                null, null, null);

        if (cur != null) {

            while (cur.moveToNext()) {
                Music m = new Music();
                m.setTitle(cur.getString(0));
                m.setDuration(cur.getString(1));
                m.setArtist(cur.getString(2));
                m.setId(cur.getString(3));
                m.setPath(cur.getString(4));
                musicList.add(m);
            }
        }
    }

    private void initViews() {
        //TODO

        playTime = (TextView) findViewById(R.id.txtLapse);
        durationTime = (TextView) findViewById(R.id.txtDuration);
        seekBar = (SeekBar) findViewById(R.id.skbGuage);
        seekBar.setClickable(true);
        seekBar.setOnSeekBarChangeListener(seekBarChageListener);

        playMode = (ImageView) findViewById(R.id.imgPlayMode);
        playAndPause = (ImageView) findViewById(R.id.imgPlay);
        settings = (ImageView) findViewById(R.id.ImgMenu);

        musicListView = (ListView) findViewById(R.id.PlayList);
        listAdapter = new MyAdapter();
        musicListView.setAdapter(listAdapter);
        musicListView.setOnItemClickListener(listListener);

        playModeButton = (ImageButton) findViewById(R.id.IndPlayMode);
        preMusicButton = (ImageButton) findViewById(R.id.btnPrev);
        playAndPauseButton = (ImageButton) findViewById(R.id.btnPlay);
        nextMusicButton = (ImageButton) findViewById(R.id.btnNext);
        settingsPauseButton = (ImageButton) findViewById(R.id.IndMenu);

        playModeButton.setOnClickListener(l);
        preMusicButton.setOnClickListener(l);
        playAndPauseButton.setOnClickListener(l);
        nextMusicButton.setOnClickListener(l);
        settingsPauseButton.setOnClickListener(l);


        initCurrentPosition();
        initPlayMode();
    }

    // class
    class InitDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //singer
            initMusics();
            Intent intent = new Intent(MusicActivity.this, MediaService.class);
            if (MediaService.player == null) {
                startService(intent);
                registerReceiver(receiver, new IntentFilter(ONPLAYCOMPLETED));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            initViews();
            super.onPostExecute(result);
        }
    }

    private OnClickListener l = new OnClickListener() {

        @Override
        public void onClick(View v) {
            //TODO
            if (v.getId() == R.id.IndPlayMode) {

                changePlayMode();

            } else if (v.getId() == R.id.btnPrev && musicList.size() > 0) {
                if (currentPosition - 1 >= 0) {

                    currentPosition--;
                } else {

                    currentPosition = musicList.size();
                }
                play();
            } else if (v.getId() == R.id.btnPlay && musicList.size() > 0) {

                if (currentPlayStatus == PLAY_PLAY) {

                    pause();
                } else if (currentPlayStatus == PLAY_PAUSE) {

                    continuing();
                } else if (currentPlayStatus == PLAY_STOP) {

                    play();
                }

            } else if (v.getId() == R.id.btnNext && musicList.size() > 0) {

                if (currentPosition + 1 <= musicList.size()) {
                    currentPosition++;
                    play();
                } else {
                    currentPosition = 0;
                }
                play();
            } else if (v.getId() == R.id.IndMenu) {

            }

        }
    };

    private OnSeekBarChangeListener seekBarChageListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            int posiztion = seekBar.getProgress();
            Intent i = new Intent();
            i.setAction(MediaService.ACTION);
            i.putExtra("mean", MediaService.SEEK);
            i.putExtra("posiztion", posiztion);
            sendBroadcast(i);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };


    private OnItemClickListener listListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {

            currentPosition = position;
            play();
        }
    };


    @Override
    protected void onDestroy() {
        if (musicList.size() > 0) {

            setLastPath(musicList.get(currentPosition).getPath());
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_MENU) {

            showDialog();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void exit() {

        Intent i = new Intent(this, MediaService.class);
        stopService(i);
        super.onDestroy();
    }


    private void showDialog() {

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("提醒");
        dialog.setMessage("确定退出天籁之音吗？");
        dialog.setButton2("取消", (android.content.DialogInterface.OnClickListener) null);
        dialog.setButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                exit();
            }
        });
        dialog.show();
    }

    //method
    public String getLastPath() {

        SharedPreferences preference = getSharedPreferences(TIANLAIZHIYIN, Context.MODE_PRIVATE);
        String path = preference.getString(LASTPATH, "");
        return path;
    }

    private void setLastPath(String path) {
        SharedPreferences preference = getSharedPreferences(TIANLAIZHIYIN, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putString(LASTPATH, path);
        editor.commit();
    }

    public int getPlayMode() {

        SharedPreferences preference = getSharedPreferences(TIANLAIZHIYIN, Context.MODE_PRIVATE);
        int mode = preference.getInt(PLAYMODE, 0);
        return mode;
    }

    protected void changePlayMode() {

        int current = getPlayMode();
        if (current == SINGE) {

            setPlayMode(SINGE_R);
            playMode.setImageDrawable(getResources().getDrawable(R.drawable.icon_playmode_repeat_single));
        } else if (current == SINGE_R) {

            setPlayMode(REPEAT);
            playMode.setImageDrawable(getResources().getDrawable(R.drawable.icon_playmode_repeat));
        } else if (current == REPEAT) {

            setPlayMode(ORDER);
            playMode.setImageDrawable(getResources().getDrawable(R.drawable.icon_playmode_normal));
        } else if (current == ORDER) {

            setPlayMode(SINGE);
            playMode.setImageDrawable(getResources().getDrawable(R.drawable.icon_playmode_shuffle));
        }
    }

    private void setPlayMode(int mode) {
        SharedPreferences preference = getSharedPreferences(TIANLAIZHIYIN, Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putInt(PLAYMODE, mode);
        editor.commit();
    }

    private void play() {

        playAndPause.setImageDrawable(getResources().getDrawable(R.drawable.img_playback_bt_pause));
        String path = musicList.get(currentPosition).getPath();
        String duration = durationToString(musicList.get(currentPosition).getDuration());
        durationTime.setText(duration);
        Intent i = new Intent();
        i.setAction(MediaService.ACTION);
        i.putExtra("mean", MediaService.PLAY);
        i.putExtra("path", path);
        sendBroadcast(i);
        currentPlayStatus = PLAY_PLAY;
        listAdapter.notifyDataSetChanged();
    }

    private void pause() {

        playAndPause.setImageDrawable(getResources().getDrawable(R.drawable.img_playback_bt_play));
        Intent i = new Intent();
        i.setAction(MediaService.ACTION);
        i.putExtra("mean", MediaService.PAUSE);
        sendBroadcast(i);
        currentPlayStatus = PLAY_PAUSE;
    }

    private void continuing() {

        playAndPause.setImageDrawable(getResources().getDrawable(R.drawable.img_playback_bt_pause));
        Intent i = new Intent();
        i.setAction(MediaService.ACTION);
        i.putExtra("mean", MediaService.CONTIUNING);
        sendBroadcast(i);
        currentPlayStatus = PLAY_PLAY;
    }

    public void initCurrentPosition() {

        if (getLastPath().trim() != "") {

            for (int i = 0; i < musicList.size(); i++) {

                if (musicList.get(i).getPath().equals(getLastPath())) {

                    currentPosition = i;
                }
            }
            if (musicList.size() > 0) {

                durationTime.setText(durationToString(musicList.get(currentPosition).getDuration()));
            }
        }
    }

    public void initPlayMode() {

        int current = getPlayMode();
        if (current == SINGE) {

            playMode.setImageDrawable(getResources().getDrawable(R.drawable.icon_playmode_shuffle));

        } else if (current == SINGE_R) {

            playMode.setImageDrawable(getResources().getDrawable(R.drawable.icon_playmode_repeat_single));

        } else if (current == REPEAT) {

            playMode.setImageDrawable(getResources().getDrawable(R.drawable.icon_playmode_repeat));

        } else if (current == ORDER) {

            playMode.setImageDrawable(getResources().getDrawable(R.drawable.icon_playmode_normal));

        }

    }

    public String durationToString(String duration) {

        String reVal = "";
        int j = Integer.valueOf(duration);
        int i = j / 1000;
        int min = (int) i / 60;
        int sec = i % 60;
        if (min > 9) {
            if (sec > 9) {
                reVal = min + ":" + sec;
            }
            if (sec <= 9) {
                reVal = min + ":0" + sec;
            }
        } else {
            if (sec > 9) {
                reVal = "0" + min + ":" + sec;
            }
            if (sec <= 9) {
                reVal = "0" + min + ":0" + sec;
            }
        }
        seekBar.setMax(j);
        return reVal;
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return musicList.size();
        }

        @Override
        public Object getItem(int arg0) {

            return musicList.get(arg0);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.music_listitem, null);
                holder.tx1 = (TextView) convertView.findViewById(R.id.ListItemName);
                holder.tx2 = (TextView) convertView.findViewById(R.id.ListItemContent);

                convertView.setTag(holder);
            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            if (position == currentPosition) {

                holder.tx1.setTextColor(Color.GREEN);
            } else {
                holder.tx1.setTextColor(Color.WHITE);
            }
            holder.tx1.setText((position + 1) + "." + musicList.get(position).getTitle());
            holder.tx2.setText((musicList.get(position)).getArtist());

            return convertView;
        }
    }

    class ViewHolder {

        public TextView tx1;
        public TextView tx2;
    }


    @Override
    public void findViews() {

    }

    @Override
    public void init() {

    }

    @Override
    public void setListeners() {

    }
}
