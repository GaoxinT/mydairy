package com.gx.mydairy.dairy.Util;

import java.util.Timer;
import java.util.TimerTask;
import com.gx.mydairy.dairy.activitys.MusicActivity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

public class MediaService extends Service implements OnCompletionListener ,OnSeekCompleteListener{

	//broadcastReceiver
	public static final String PAUSE = "com.android.ttpod.musicservicecommand.PAUSE";
	public static final String PLAY = "com.android.ttpod.musicservicecommand.PLAY";
	public static final String CONTIUNING = "com.android.ttpod.musicservicecommand.CONTIUNING";
	public static final String SEEK = "com.android.ttpod.musicservicecommand.SEEK";
	public static final String PREV = "com.android.ttpod.musicservicecommand.PREVIOUS";
	public static final String STOP = "com.android.ttpod.musicservicecommand.STOP";
	public static final String ACTION = "com.android.mediaService";
	
	//mediaplayer
	public static MediaPlayer player;
	private Timer timer = new Timer();
	
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String mean = intent.getStringExtra("mean");
			if(mean.equals(PLAY)){
				
				String path = intent.getStringExtra("path");
				if(path!=null && path.trim()!=null){
					
					play(path);
				}
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						
						int pos = player.getCurrentPosition();
						Bundle b = new Bundle();
						b.putInt("posiztion", pos);
						Message msg = new Message();
						msg.what = -1;
						msg.setData(b);
						MusicActivity.seekBarHandle.sendMessage(msg);
					}
				}, 5,500);
			}
			
			if(mean.equals(PAUSE)){
				
				pause();
			}
			
			if(mean.equals(CONTIUNING)){
				
				player.start();
			}
			
			if(mean.equals(SEEK)){
				
				int posiztion = intent.getIntExtra("posiztion", 0);
				playerToPosiztion(posiztion);
			}
			
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
		
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		player = new MediaPlayer();
		player.setOnSeekCompleteListener(this);
		player.setOnCompletionListener(this);
		registerReceiver(receiver, new IntentFilter(ACTION));
	}
	
	

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		timer.cancel();
		stop();
	}

	private void play(String path){
		
		if(player == null){
			player = new MediaPlayer();
		}
		
		try {
			player.reset();
			player.setDataSource(path);
			player.prepare();
			player.start();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private void pause(){
		
		if(player!=null && player.isPlaying()){
			
			player.pause();
		}
	}
	
	private void stop(){
		
		if(player!= null){
			
			player.stop();
			player.release();
		}
	}
	
	private void playerToPosiztion(int posiztion){
		
		if(posiztion>0 && posiztion<player.getDuration()){
			
			player.seekTo(posiztion);
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
//		timer.cancel();
		Intent i = new Intent(MusicActivity.ONPLAYCOMPLETED);
		sendBroadcast(i);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		
		if(player.isPlaying()){
			player.start();
		}
	}

}
