package cn.tedu.mediaplayer.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ���ڷ���http����Ĺ�����
 */
public class HttpUtils {
	/**
	 * ��path��ַ����httpget����
	 * @param path  ������Դ·��
	 * @return
	 * @throws Exception 
	 */
	public static InputStream get(String path) throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		return is;
	}
}





