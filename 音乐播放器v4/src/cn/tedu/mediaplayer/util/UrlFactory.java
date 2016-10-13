package cn.tedu.mediaplayer.util;

/**
 * url������ ��������url��ַ�ַ���
 */
public class UrlFactory {
	/**
	 * ��ȡ�¸�������ַ
	 * 
	 * @param offset
	 *            ��ʼλ��
	 * @param size
	 *            ���ָ���
	 * @return
	 */
	public static String getNewMusicListUrl(int offset, int size) {
		String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=1&offset="
				+ offset + "&size=" + size;
		return url;
	}

	// 利用songid获取Songinfo地址
	public static String getSongInfoUrl(String songId) {
		// TODO Auto-generated method stub
		String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid="
				+ songId + "&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
		return url;
	}
}
