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
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		player.release();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MucsicBinder();
	}

	public class MucsicBinder extends Binder {
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
