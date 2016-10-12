package cn.tedu.mediaplayer.util;
/**
 * url工厂类
 * 用于生产url地址字符串
 */
public class UrlFactory {
	/**
	 * 获取新歌榜请求地址
	 * @param offset   起始位置
	 * @param size		音乐个数
	 * @return
	 */
	public static String getNewMusicListUrl(int offset, int size){
		String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=1&offset="+offset+"&size="+size;
		return url;
	}
}
