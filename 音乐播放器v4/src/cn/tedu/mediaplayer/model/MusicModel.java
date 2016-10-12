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
 * ��װ�������ҵ��  
 */
public class MusicModel {
	/**
	 * ��ȡ�¸���б�  ��Ҫ����http����
	 * �ò�����Ҫ�ڹ����߳���ִ�� 
	 * @param offset  ��ʼλ��
	 * @param size	   ��ѯ����
	 */
	public void getNewMusicList(final int offset, final int size, final MusicListCallback callback) {
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>(){
			/** ���߳���ִ�� ����ֱ�ӷ���http���� */
			public List<Music> doInBackground(String... params) {
				try {
					//��ȡ��ַ
					String url =UrlFactory.getNewMusicListUrl(offset, size);
					//����http����
					InputStream is=HttpUtils.get(url);
					//����is�е�xml���� ��ȡList<Music>
					List<Music> musics = XmlParser.parseMusicList(is);
					return musics;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			/** ��doInBackground����ִ����Ϻ� 
			 * ���������߳���ִ�и÷��� */
			public void onPostExecute(List<Music> musics) {
				//Log.i("info", ""+musics.toString());
				callback.onMusicListLoaded(musics);
			}
		};
		task.execute();
	}
	
	/**
	 * �ص��ӿ�
	 */
	public interface MusicListCallback{
		/**
		 * �ص�����  �������б������Ϻ�
		 * ������øûص����� 
		 * �ѵõ��������б�������������
		 * ִ�к���ҵ��
		 */
		void onMusicListLoaded(List<Music> musics);
	}
	
}



