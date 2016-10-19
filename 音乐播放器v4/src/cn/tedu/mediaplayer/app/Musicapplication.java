package cn.tedu.mediaplayer.app;

import java.util.List;

import android.app.Application;
import cn.tedu.mediaplayer.entity.Music;

public class Musicapplication extends Application {
	private static Musicapplication app;
	private List<Music> musics;
	private int position;

	public void setMusics(List<Music> musics) {
		this.musics = musics;
	}

	public static Musicapplication getapp() {
		return app;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		app = this;
	}

	/** 获得当前播放的音乐 */
	public Music getCurrentMusic() {
		return this.musics.get(position);
	}

	/** 跳转上一首歌 */
	public void preMusic() {
		position = position == 0 ? 0 : position - 1;
	}

	/** 跳转上一首歌 */
	public void nextMusic() {
		position = position == musics.size() - 1 ? 0 : position + 1;
	}
}
