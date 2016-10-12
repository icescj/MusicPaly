package cn.tedu.mediaplayer.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.tedu.mediaplayer.R;
import cn.tedu.mediaplayer.adapter.MusicAdapter;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.model.MusicModel;
import cn.tedu.mediaplayer.model.MusicModel.MusicListCallback;

/**
 * �����¸���б���� Fragment
 */
public class NewMusicListFragment extends Fragment {
	private ListView listView;
	private MusicAdapter adapter;

	/**
	 * ���������ڷ����������Զ����� ��viewpager��Ҫ��ȡFragment��view����ʱ
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music_list, null);
		// 初始化frament中的控件
		listView = (ListView) view.findViewById(R.id.listView);
		// 调用业务底层代码，访问新歌榜列表
		MusicModel model = new MusicModel();
		model.getNewMusicList(0, 20, new MusicListCallback() {
			public void onMusicListLoaded(List<Music> musics) {
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
}
