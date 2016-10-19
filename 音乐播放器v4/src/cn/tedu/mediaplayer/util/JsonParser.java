package cn.tedu.mediaplayer.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.entity.SongInfo;
import cn.tedu.mediaplayer.entity.SongUrl;

public class JsonParser {
	/***
	 * 解析集合
	 * 
	 * @throws JSONException
	 */
	public static List<SongUrl> parserUrls(JSONArray urlary) throws JSONException {
		// TODO Auto-generated method stub
		List<SongUrl> urls = new ArrayList<SongUrl>();
		for (int i = 0; i < urlary.length(); i++) {
			JSONObject o = urlary.getJSONObject(i);
			SongUrl url = new SongUrl();
			url.setSong_file_id(o.getInt("song_file_id"));
			url.setFile_bitrate(o.getInt("file_bitrate"));
			url.setFile_duration(o.getInt("file_duration"));
			url.setFile_extension(o.getString("file_extension"));
			url.setFile_link(o.getString("file_link"));
			url.setFile_size(o.getInt("file_size"));
			url.setShow_link(o.getString("show_link"));
			urls.add(url);
		}
		Log.i("tag", "urls" + urls.size());
		return urls;
	}

	/***
	 * 解析songinfo对象
	 * 
	 * @throws JSONException
	 */
	public static SongInfo parserSonInfo(JSONObject infoobj) throws JSONException {
		// TODO Auto-generated method stub
		SongInfo info = new SongInfo();
		info.setAlbum_1000_1000(infoobj.getString("album_1000_1000"));
		info.setAlbum_500_500(infoobj.getString("album_500_500"));
		info.setAlbum_id(infoobj.getString("album_id"));
		info.setAlbum_title(infoobj.getString("album_title"));
		info.setAll_artist_id(infoobj.getString("all_artist_id"));
		info.setArtist_1000_1000(infoobj.getString("artist_1000_1000"));
		info.setArtist_480_800(infoobj.getString("artist_480_800"));
		info.setArtist_640_1136(infoobj.getString("artist_640_1136"));
		info.setArtist_id(infoobj.getString("artist_id"));
		info.setAuthor(infoobj.getString("author"));
		info.setCompose(infoobj.getString("compose"));
		info.setFile_duration(infoobj.getString("file_duration"));
		info.setLanguage(infoobj.getString("language"));
		info.setLrclink(infoobj.getString("lrclink"));
		info.setPic_big(infoobj.getString("pic_big"));
		info.setPic_huge(infoobj.getString("pic_huge"));
		info.setPic_premium(infoobj.getString("pic_premium"));
		info.setPic_radio(infoobj.getString("pic_radio"));
		info.setPic_singer(infoobj.getString("pic_singer"));
		info.setPic_small(infoobj.getString("pic_small"));
		info.setPublishtime(infoobj.getString("publishtime"));
		info.setShare_url(infoobj.getString("share_url"));
		info.setSong_id(infoobj.getString("song_id"));
		info.setTitle(infoobj.getString("title"));
		return info;
	}

	public static List<Music> parserSeachResult(JSONArray ary) throws JSONException {
		// TODO Auto-generated method stub
		List<Music> musics = new ArrayList<Music>();
		for (int i = 0; i < ary.length(); i++) {
			JSONObject o = ary.getJSONObject(i);
			Music m = new Music();
			m.setTitle(o.getString("title"));
			m.setSong_id(o.getString("song_id"));
			m.setAuthor(o.getString("author"));
			m.setArtist_id(o.getString("artist_id"));
			m.setAlbum_title(o.getString("album_title"));
			musics.add(m);
		}
		return musics;
	}

}
