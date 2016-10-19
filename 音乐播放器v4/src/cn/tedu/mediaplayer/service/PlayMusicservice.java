package cn.tedu.mediaplayer.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import cn.tedu.mediaplayer.util.GlobalConsts;

public class PlayMusicservice extends Service {
	private MediaPlayer player = new MediaPlayer();
	private boolean isLoop = true;

	// 绑定serice的时候，第一次执行且执行一次
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 为player添加准备监听
		player.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				player.start();
				Intent intent = new Intent(GlobalConsts.ACTION_MUSIC_STARTED);
				sendBroadcast(intent);
			}
		});
		new Thread() {
			public void run() {
				while (isLoop) {
					try {
						sleep(1000);
						if (player.isPlaying()) {
							int total = player.getDuration();
							int current = player.getCurrentPosition();
							Intent intent = new Intent("ACTION_UPDATE_MUSIC_PROGRESS");
							intent.putExtra("total", total);
							intent.putExtra("current", current);
							sendBroadcast(intent);
						}
					} catch (Exception e) {
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// 释放资源
		player.release();
		// 停止线程
		isLoop = true;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MucsicBinder();
	}

	public class MucsicBinder extends Binder {
		/** 播放状态 */
		public Boolean isplaying() {
			return player.isPlaying() ? true : false;
		}

		/** 暂停或者播放 */
		public void playorpause() {
			if (player.isPlaying()) {
				player.pause();

			} else {
				player.start();

			}

		}

		/** 播放音乐 */
		public void playmusic(String url) {
			try {
				player.reset();
				player.setDataSource(url);
				// 异步准备
				player.prepareAsync();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
