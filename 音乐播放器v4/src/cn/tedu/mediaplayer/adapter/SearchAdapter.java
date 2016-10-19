package cn.tedu.mediaplayer.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.tedu.mediaplayer.R;
import cn.tedu.mediaplayer.entity.Music;

public class SearchAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Music> musics;

	public SearchAdapter(Context context, List<Music> musics) {
		super();
		this.context = context;
		this.musics = musics;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return musics.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return musics.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		Music m = musics.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lv_searchmusic, null);
			holder = new ViewHolder();
			holder.tvSinger = (TextView) convertView.findViewById(R.id.item_music_singer);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.item_music_title);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.tvSinger.setText(Html.fromHtml(m.getAuthor()));
		holder.tvTitle.setText(Html.fromHtml(m.getTitle()));
		return convertView;
	}

	class ViewHolder {
		TextView tvSinger;
		TextView tvTitle;
	}
}
