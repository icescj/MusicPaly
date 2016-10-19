package cn.tedu.mediaplayer.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import cn.tedu.mediaplayer.app.Musicapplication;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.entity.SongInfo;
import cn.tedu.mediaplayer.entity.SongUrl;
import cn.tedu.mediaplayer.model.MusicModel.MusicListCallback;
import cn.tedu.mediaplayer.util.HttpUtils;
import cn.tedu.mediaplayer.util.JsonParser;
import cn.tedu.mediaplayer.util.UrlFactory;
import cn.tedu.mediaplayer.util.XmlParser;

/**
 * ��װ�������ҵ��
 */
public class MusicModel {
	/*** 搜索音乐列表 **/
	public void searchMusicLIst(final String keyword, final MusicListCallback callback) {
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>() {
			@Override
			protected List<Music> doInBackground(String... params) {
				// TODO Auto-generated method stub
				String path = UrlFactory.getSearchMusicUrl(keyword);
				InputStream is;
				try {
					is = HttpUtils.get(path);
					String json = HttpUtils.isToString(is);
					// 解析json
					JSONObject obj = new JSONObject(json);
					JSONArray ary = obj.getJSONArray("song_list");
					List<Music> musics = JsonParser.parserSeachResult(ary);
					return musics;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Music> result) {
				// TODO Auto-generated method stub
				callback.onMusicListLoaded(result);
			}
		};
		task.execute();

	}

	/***
	 * 通过歌词路径 加载歌词 并 解析歌词 把一整篇歌词内容都封装到Hashmap
	 **/
	public void loadLrc(final String lrcPath, final LrcCallback callback) {
		AsyncTask<String, String, HashMap<String, String>> task = new AsyncTask<String, String, HashMap<String, String>>() {

			@Override
			protected HashMap<String, String> doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					// 声明缓存文件
					String filename = lrcPath.substring(lrcPath.lastIndexOf("/"));
					// "lrc" 字幕存储文件夹
					File file = new File(Musicapplication.getapp().getCacheDir(), "lrc" + filename);
					InputStream is = null;
					PrintWriter out = null;
					// "isNewFile"是否是新创建的文件
					boolean isNewFile = true;
					// 判读字幕文件夹是否存在
					if (file.exists()) {
						// 文件存在，直接创建输入流
						is = new FileInputStream(file);
						isNewFile = false;
					} else {
						// 判断file父路径是否存在
						if (!file.getParentFile().exists()) {
							// 不存在，即创建
							file.getParentFile().mkdirs();
						}
						out = new PrintWriter(file);
						is = HttpUtils.get(lrcPath);
						isNewFile = true;
						HashMap<String, String> map = new HashMap<String, String>();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						String line;
						while ((line = reader.readLine()) != null) {
							if (isNewFile) {
								// 每读取一行，向文件中写入一行
								out.println(line);
								out.flush();
							}
							// line:[00:00:90] xxxxxx
							if ("".equals(line.trim())) {
								// 空行
								continue;// 直接读下一行
							}
							if (!line.contains(".")) {
								// 格式不正确，不规范的时间表示
								continue;// 直接读下一行
							}
							String time = line.substring(1, 6);
							String cotent = line.substring(10);// 下标位置为10，一直截取到最后
							map.put(time, cotent);
						}
						if (out != null) {
							out.close();
						}
						return map;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(HashMap<String, String> result) {
				// TODO Auto-generated method stub
				callback.onLrcLoaded(result);
			}
		};
		task.execute();

	}

	public interface LrcCallback {
		void onLrcLoaded(HashMap<String, String> lrc);
	}

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
			// 子线程
			public List<Music> doInBackground(String... params) {
				try {
					// ��ȡ��ַ
					String url = UrlFactory.getNewMusicListUrl(offset, size);
					// ����http����
					InputStream is = HttpUtils.get(url);
					// ����is�е�xml���� ��ȡList<Music>
					List<Music> musics = XmlParser.parseMusicList(is);
					return musics;// 子线程把musics返回
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			/**
			 * ��doInBackground����ִ����Ϻ� ���������߳���ִ�и÷��� 主线程
			 */
			public void onPostExecute(List<Music> musics) {
				Log.i("什么线程？", Thread.currentThread().getId() + "");
				// Log.i("info", ""+musics.toString());
				callback.onMusicListLoaded(musics);//
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

	public void getHotMusicList(final int offset, final int size, final MusicListCallback callback) {

		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>() {
			/** ���߳���ִ�� ����ֱ�ӷ���http���� */
			// 子线程
			public List<Music> doInBackground(String... params) {
				try {
					// ��ȡ��ַ
					String url = UrlFactory.getHotMusicListUrl(offset, size);
					// ����http����
					InputStream is = HttpUtils.get(url);
					// ����is�е�xml���� ��ȡList<Music>
					List<Music> musics = XmlParser.parseMusicList(is);
					return musics;// 子线程把musics返回
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			/**
			 * ��doInBackground����ִ����Ϻ� ���������߳���ִ�и÷��� 主线程
			 */
			public void onPostExecute(List<Music> musics) {
				Log.i("什么线程？", Thread.currentThread().getId() + "");
				// Log.i("info", ""+musics.toString());
				callback.onMusicListLoaded(musics);//
			}
		};
		task.execute();
		// TODO Auto-generated method stub
	}
}
