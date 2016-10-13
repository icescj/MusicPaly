package cn.tedu.mediaplayer.fragment;

import java.util.List;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.tedu.mediaplayer.R;
import cn.tedu.mediaplayer.adapter.MusicAdapter;
import cn.tedu.mediaplayer.app.Musicapplication;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.entity.SongInfo;
import cn.tedu.mediaplayer.entity.SongUrl;
import cn.tedu.mediaplayer.model.MusicModel;
import cn.tedu.mediaplayer.model.MusicModel.MusicListCallback;
import cn.tedu.mediaplayer.model.MusicModel.SonginfoCallback;
import cn.tedu.mediaplayer.service.PlayMusicservice.MucsicBinder;

/**
 * �����¸���б���� Fragment
 */
public class NewMusicListFragment extends Fragment implements OnItemClickListener {
	private ListView listView;
	private MusicAdapter adapter;
	private List<Music> musics;
	private MusicModel model;
	private MucsicBinder binder;

	/**
	 * ���������ڷ����������Զ����� ��viewpager��Ҫ��ȡFragment��view����ʱ
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music_list, null);
		// 初始化frament中的控件
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		// 调用业务底层代码，访问新歌榜列表
		model = new MusicModel();
		model.getNewMusicList(0, 20, new MusicListCallback() {
			public void onMusicListLoaded(List<Music> musics) {
				NewMusicListFragment.this.musics = musics;
				adapter = new MusicAdapter(getActivity(), musics, listView);
				listView.setAdapter(adapter);
			}
		});
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 销毁adapter中的子线程
		adapter.stopthread();
	}

	/** listview设置监听 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Music m = musics.get(arg2);
		String songId = m.getSong_id();
		Log.i("tag", "songId:" + songId);
		switch (arg0.getId()) {
		case R.id.listView:
			// 把播放列表中position存入application
			Musicapplication app = Musicapplication.getapp();
			app.setMusics(musics);
			app.setPosition(arg2);
			// 调用业务层，获取音乐基本信息
			model.loadSonginfoBysong(songId, new SonginfoCallback() {
				@Override
				public void onSonginfoLoaded(List<SongUrl> urls, SongInfo info) {
					// 回到主线程 更新UI 播放音乐
					String url = urls.get(0).getFile_link();
					binder.playmusic(url);
				}
			});
			break;
		}
	}

	public void setbinder(MucsicBinder binder) {
		// TODO Auto-generated method stub
		this.binder = binder;
	}
}
