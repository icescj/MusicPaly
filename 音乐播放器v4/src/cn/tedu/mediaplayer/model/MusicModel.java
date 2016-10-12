package cn.tedu.mediaplayer.model;

import java.io.InputStream;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.util.HttpUtils;
import cn.tedu.mediaplayer.util.UrlFactory;
import cn.tedu.mediaplayer.util.XmlParser;

/**
 * 封装音乐相关业务  
 */
public class MusicModel {
	/**
	 * 获取新歌榜列表  需要发送http请求
	 * 该操作需要在工作线程中执行 
	 * @param offset  起始位置
	 * @param size	   查询数量
	 */
	public void getNewMusicList(final int offset, final int size, final MusicListCallback callback) {
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>(){
			/** 子线程中执行 可以直接发送http请求 */
			public List<Music> doInBackground(String... params) {
				try {
					//获取地址
					String url =UrlFactory.getNewMusicListUrl(offset, size);
					//发送http请求
					InputStream is=HttpUtils.get(url);
					//解析is中的xml数据 获取List<Music>
					List<Music> musics = XmlParser.parseMusicList(is);
					return musics;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			/** 当doInBackground方法执行完毕后 
			 * 将会在主线程中执行该方法 */
			public void onPostExecute(List<Music> musics) {
				//Log.i("info", ""+musics.toString());
				callback.onMusicListLoaded(musics);
			}
		};
		task.execute();
	}
	
	/**
	 * 回调接口
	 */
	public interface MusicListCallback{
		/**
		 * 回调方法  当音乐列表加载完毕后
		 * 将会调用该回调方法 
		 * 把得到的音乐列表结果交给调用者
		 * 执行后续业务。
		 */
		void onMusicListLoaded(List<Music> musics);
	}
	
}



