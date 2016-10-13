package cn.tedu.mediaplayer.adapter;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import android.app.PendingIntent.OnFinished;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.WorkSource;
import android.provider.UserDictionary.Words;
import android.text.style.SubscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.tedu.mediaplayer.R;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.util.HttpUtils;
import cn.tedu.mediaplayer.util.ImageLoad;
import cn.tedu.mediaplayer.util.bitmaputil;

/**
 * 音乐列表适配器
 */
public class MusicAdapter extends BaseAdapter {

	private Context context;
	private List<Music> musics;
	private ListView listview;
	private LayoutInflater inflater;

	private ImageLoad imageload;

	public MusicAdapter(Context context, List<Music> musics, ListView listview) {
		this.context = context;
		this.musics = musics;
		this.listview = listview;
		this.inflater = LayoutInflater.from(context);
		imageload = new ImageLoad(context, listview);
	}

	@Override
	public int getCount() {
		return musics.size();
	}

	@Override
	public Music getItem(int position) {
		return musics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lv_music, null);
			holder = new ViewHolder();
			holder.ivAlbum = (ImageView) convertView.findViewById(R.id.ivAlbum);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvSinger = (TextView) convertView.findViewById(R.id.tvSinger);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		// 给holde中控件赋值
		Music m = getItem(position);
		holder.tvTitle.setText(m.getTitle());
		holder.tvSinger.setText(m.getAuthor());
		// 获取图片网络路径
		String path = m.getPic_small();

		imageload.display(holder.ivAlbum, path);

		return convertView;
	}

	class ViewHolder {
		ImageView ivAlbum;
		TextView tvTitle;
		TextView tvSinger;
	}

	public void stopthread() {
		imageload.stopthread();
	}
}
