package cn.tedu.mediaplayer.model;

import java.io.InputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.entity.SongInfo;
import cn.tedu.mediaplayer.entity.SongUrl;
import cn.tedu.mediaplayer.util.HttpUtils;
import cn.tedu.mediaplayer.util.JsonParser;
import cn.tedu.mediaplayer.util.UrlFactory;
import cn.tedu.mediaplayer.util.XmlParser;

/**
 * ��װ�������ҵ��
 */
public class MusicModel {
	/**
	 * ��ȡ�¸���б� ��Ҫ����http���� �ò�����Ҫ�ڹ����߳���ִ�� 获得新歌榜列表 发送http请求
	 * 
	 * @param offset
	 *            ��ʼλ��
	 * @param size
	 *            ��ѯ����
	 */
	public void getNewMusicList(final int offset, final int size, final MusicListCallback callback) {
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>() {
			/** ���߳���ִ�� ����ֱ�ӷ���http���� */
			public List<Music> doInBackground(String... params) {
				try {
					// ��ȡ��ַ
					String url = UrlFactory.getNewMusicListUrl(offset, size);
					// ����http����
					InputStream is = HttpUtils.get(url);
					// ����is�е�xml���� ��ȡList<Music>
					List<Music> musics = XmlParser.parseMusicList(is);
					return musics;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			/**
			 * ��doInBackground����ִ����Ϻ� ���������߳���ִ�и÷���
			 */
			public void onPostExecute(List<Music> musics) {
				// Log.i("info", ""+musics.toString());
				callback.onMusicListLoaded(musics);
			}
		};
		task.execute();
	}

	/**
	 * �ص��ӿ�
	 */
	public interface MusicListCallback {
		/**
		 * �ص����� �������б������Ϻ� ������øûص����� �ѵõ��������б�������������
		 * ִ�к���ҵ��
		 */
		void onMusicListLoaded(List<Music> musics);
	}

	// 获取音乐songid和songinfo
	public void loadSonginfoBysong(final String songId, final SonginfoCallback callback) {
		AsyncTask<String, String, Music> task = new AsyncTask<String, String, Music>() {
			@Override
			protected Music doInBackground(String... params) {
				// TODO Auto-generated method stub
				Music m = new Music();
				try {
					String path = UrlFactory.getSongInfoUrl(songId);
					InputStream is = HttpUtils.get(path);
					// 把输入流中的数据解析为json字符串
					String json = HttpUtils.isToString(is);
					// 解析json
					JSONObject obj = new JSONObject(json);
					/******* 解析 urls ********/
					// 获得url数组
					JSONArray urlary = obj.getJSONObject("songurl").getJSONArray("url");
					// 解析额urlary放入list集合
					Log.i("tag", "马上到解析urls");
					List<SongUrl> urls = JsonParser.parserUrls(urlary);
					/******* 解析info ********/
					JSONObject infoobj = obj.getJSONObject("songinfo");
					// 把infoobj解析成SongInfo
					SongInfo info = JsonParser.parserSonInfo(infoobj);
					/***************/
					m.setUrls(urls);
					m.setInfo(info);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return m;
			}

			@Override
			protected void onPostExecute(Music result) {
				// TODO Auto-generated method stub
				callback.onSonginfoLoaded(result.getUrls(), result.getInfo());
			}
		};
		task.execute();
	}

	public interface SonginfoCallback {
		void onSonginfoLoaded(List<SongUrl> urls, SongInfo info);
	}
}
