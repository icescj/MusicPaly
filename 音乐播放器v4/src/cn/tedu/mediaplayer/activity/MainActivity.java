package cn.tedu.mediaplayer.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.tedu.mediaplayer.R;
import cn.tedu.mediaplayer.adapter.SearchAdapter;
import cn.tedu.mediaplayer.app.Musicapplication;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.entity.SongInfo;
import cn.tedu.mediaplayer.entity.SongUrl;
import cn.tedu.mediaplayer.fragment.HotMusicListFragment;
import cn.tedu.mediaplayer.fragment.NewMusicListFragment;
import cn.tedu.mediaplayer.model.MusicModel;
import cn.tedu.mediaplayer.model.MusicModel.LrcCallback;
import cn.tedu.mediaplayer.model.MusicModel.MusicListCallback;
import cn.tedu.mediaplayer.model.MusicModel.SonginfoCallback;
import cn.tedu.mediaplayer.service.DownLoadService;
import cn.tedu.mediaplayer.service.PlayMusicservice;
import cn.tedu.mediaplayer.service.PlayMusicservice.MucsicBinder;
import cn.tedu.mediaplayer.ui.CircleImageView;
import cn.tedu.mediaplayer.util.GlobalConsts;
import cn.tedu.mediaplayer.util.bitmaputil;
import cn.tedu.mediaplayer.util.bitmaputil.BitmapCallback;

public class MainActivity extends FragmentActivity {
	private RadioGroup radioGroup;
	private RadioButton radioNew;
	private RadioButton radioHot;
	private ViewPager viewPager;
	private ArrayList<Fragment> fragments;
	private MainPagerAdapter pagerAdapter;
	private MusicStateReceiver receiver;
	private TextView tvmusicname;
	private TextView tvmusicautor;
	private cn.tedu.mediaplayer.ui.CircleImageView circle, ivmusic;
	private RelativeLayout rlplaymusic;
	private TextView rltvmusicname, rltvmusicautor, rlmusicmlc, tvmusicstart, tvmusicend;
	private ImageButton ibpress, ibplay, ibpause;
	private ImageView ivPMBackround;
	private SeekBar seekbar;
	private RelativeLayout rlSearchMusic;
	private Button btnSearch, btnToSearch, btnCancel;
	private EditText etKeyWord;
	private ListView lvSearchMusic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// �ؼ���ʼ��
		setViews();
		// ΪviewPager����������
		setPagerAdapter();
		// ���ü�����
		setListeners();
		// 绑定serice
		bindMusicservice();
		// 注册各种组件
		registComponents();

	}

	/** 注册各种组件 */
	private void registComponents() {
		// TODO Auto-generated method stub
		receiver = new MusicStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
		filter.addAction(GlobalConsts.ACTION_UPDATE_MUSIC_PROGRESS);
		this.registerReceiver(receiver, filter);
	}

	/*** 广播接收器 接受音乐状态相关的广播 */
	class MusicStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			// 获得当前正在播放的音乐对象，去app中拿
			Musicapplication app = Musicapplication.getapp();
			// 获得当前播放的音乐
			final Music music = app.getCurrentMusic();
			if (action.equals("ACTION_UPDATE_MUSIC_PROGRESS")) {
				// 更新音乐进度
				int total = intent.getIntExtra("total", 0);
				int current = intent.getIntExtra("current", 0);
				// 更新控件
				seekbar.setMax(total);
				seekbar.setProgress(current);
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
				String ct = sdf.format(new Date(current));
				tvmusicstart.setText(ct);
				tvmusicend.setText(sdf.format(new Date(total)));
				// 更新歌词
				// 取到歌词
				HashMap<String, String> lrc = music.getLrc();
				// 判断是否为空
				if (lrc != null) {
					// 取到对应行的歌词
					String content = lrc.get(ct);
					if (content != null) {
						// 更新到控件上
						rlmusicmlc.setText(content);
					}
				}

			} else if (action.equals("ACTION_MUSIC_STARTED")) {
				// 音乐正在播放，收到广播
				// 设置歌名
				String title = music.getTitle();
				tvmusicname.setText(Html.fromHtml(title));
				rltvmusicname.setText(Html.fromHtml(title));
				// 设置艺术家的名字
				String singer = music.getAuthor();
				tvmusicautor.setText(Html.fromHtml(singer));
				rltvmusicautor.setText(Html.fromHtml(singer));
				// 设置圆形图片
				String path = music.getInfo().getPic_small();
				bitmaputil.loadBitmap(path, 1, new BitmapCallback() {

					@Override
					public void onBitmaploaded(Bitmap bitmap) {
						// TODO Auto-generated method stub
						if (bitmap != null) {
							circle.setImageBitmap(bitmap);
							RotateAnimation anim = new RotateAnimation(0, 360, circle.getWidth() / 2,
									circle.getHeight() / 2);
							anim.setDuration(10000);
							// 匀速运动
							anim.setInterpolator(new LinearInterpolator());
							// 无限旋转
							anim.setRepeatCount(RotateAnimation.INFINITE);
							circle.startAnimation(anim);
						} else {
							circle.setImageResource(R.drawable.ic_launcher);
						}
					}
				});
				// 设置播放界面中的专辑图片
				String albumPath = music.getInfo().getAlbum_500_500();
				bitmaputil.loadBitmap(albumPath, 1, new BitmapCallback() {
					@Override
					public void onBitmaploaded(Bitmap bitmap) {
						// TODO Auto-generated method stub
						if (bitmap != null) {
							ivmusic.setImageBitmap(bitmap);

							RotateAnimation anim = new RotateAnimation(0, 360, ivmusic.getWidth() / 2,
									ivmusic.getHeight() / 2);
							anim.setDuration(10000);
							// 匀速运动
							anim.setInterpolator(new LinearInterpolator());
							// 无限旋转
							anim.setRepeatCount(RotateAnimation.INFINITE);
							ivmusic.startAnimation(anim);

						}
					}
				});
				// 播放界面中背景图片设置
				String bgPath = music.getInfo().getArtist_480_800();
				if (bgPath.equals("")) {
					bgPath = music.getInfo().getArtist_640_1136();
				}
				bitmaputil.loadBitmap(bgPath, 4, new BitmapCallback() {

					@Override
					public void onBitmaploaded(Bitmap bitmap) {
						// TODO Auto-generated method stub
						if (bitmap != null) {
							// 对图片进行模糊处理
							bitmaputil.loadBlurBitmap(bitmap, 10, new BitmapCallback() {

								@Override
								public void onBitmaploaded(Bitmap bitmap) {
									// TODO Auto-generated method stub
									if (bitmap != null) {
										ivPMBackround.setImageBitmap(bitmap);
									}
								}
							});

						}
					}
				});
				// 加载当前音乐的歌词
				String lrcpath = music.getInfo().getLrclink();
				model.loadLrc(lrcpath, new LrcCallback() {

					@Override
					public void onLrcLoaded(HashMap<String, String> lrc) {
						// TODO Auto-generated method stub
						// 将歌词写入对象musci对象中
						music.setLrc(lrc);
					}
				});

			}

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 与service解绑
		this.unbindService(conn);
		// 取消注册广播接收器
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}

	private ServiceConnection conn;

	/** 绑定serice */
	private void bindMusicservice() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, PlayMusicservice.class);
		conn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
			}

			// 当与service的链接时候执行
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				binder = (MucsicBinder) service;
				// NewMusicListFragment 设置bind
				NewMusicListFragment f1 = (NewMusicListFragment) fragments.get(0);
				f1.setbinder(binder);
				HotMusicListFragment f2 = (HotMusicListFragment) fragments.get(1);
				f2.setbinder(binder);

			}
		};
		this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
	}

	/**
	 * ���ü�����
	 */
	Musicapplication app = Musicapplication.getapp();
	private MusicModel model = new MusicModel();
	private MucsicBinder binder;

	private void setListeners() {
		// 图片监听执行下载任务
		ivmusic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 获得当前音乐对象
				Musicapplication app = Musicapplication.getapp();
				final Music m = app.getCurrentMusic();
				final List<SongUrl> urls = m.getUrls();
				// 弹出alertDialog 提供下载版本
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				String[] items = new String[urls.size()];
				for (int i = 0; i < items.length; i++) {
					SongUrl url = urls.get(i);
					// 获取歌曲容量假如数组
					items[i] = Math.floor(100.0 * url.getFile_size() / 1024 / 1024 / 100) + "M";
				}
				builder.setTitle("版本").setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SongUrl url = urls.get(which);
						String title = m.getTitle();
						int bitrate = url.getFile_bitrate();
						String path = url.getFile_link();
						if (path == null || path.equals("")) {
							path = url.getShow_link();
						}
						int filesize = url.getFile_size();
						// 启动Service
						Intent intent = new Intent(MainActivity.this, DownLoadService.class);
						intent.putExtra("path", path);
						intent.putExtra("title", title);
						intent.putExtra("bitrate", bitrate);
						intent.putExtra("filesize", filesize);
						startService(intent);
					}
				}).create().show();

			}
		});
		/*** lvreadchmusic设置监听器 */
		lvSearchMusic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				// 把当前搜索列表list与position存入application
				Musicapplication app = Musicapplication.getapp();
				app.setMusics(searchmusiclist);
				app.setPosition(position);
				// 获取当前需要播发的音乐
				final Music m = searchmusiclist.get(position);
				model.loadSonginfoBysong(m.getSong_id(), new SonginfoCallback() {

					@Override
					public void onSonginfoLoaded(List<SongUrl> urls, SongInfo info) {
						// TODO Auto-generated method stub
						m.setUrls(urls);
						m.setInfo(info);
						String url = urls.get(0).getFile_link();
						if (url.equals("")) {
							url = urls.get(0).getShow_link();
						}
						binder.playmusic(url);
					}
				});
			}
		});
		/** 取消按钮监听 **/
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rlSearchMusic.setVisibility(v.INVISIBLE);
				Animation anim = new TranslateAnimation(0, 0, 0, -rlSearchMusic.getHeight());
				anim.setDuration(400);
				rlSearchMusic.startAnimation(anim);
			}
		});
		/** 搜索按钮监听 **/
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchMusic();
			}
		});
		/** 去搜索界面监听 */
		btnToSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rlSearchMusic.setVisibility(v.VISIBLE);
				Animation anim = new TranslateAnimation(0, 0, -rlSearchMusic.getHeight(), 0);
				anim.setDuration(400);
				rlSearchMusic.startAnimation(anim);
			}
		});

		// rlmusicmlc添加监听事件，消费监听
		rlplaymusic.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		/** 上一首歌曲 */
		ibpress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				app.preMusic();
				final Music m = app.getCurrentMusic();
				model.loadSonginfoBysong(m.getSong_id(), new SonginfoCallback() {

					@Override
					public void onSonginfoLoaded(List<SongUrl> urls, SongInfo info) {
						// TODO Auto-generated method stub
						m.setUrls(urls);
						m.setInfo(info);
						String url = urls.get(0).getFile_link();
						binder.playmusic(url);
						ibplay.setImageResource(R.drawable.selector_item_play);
					}
				});
			}
		});
		/** 播放暂停歌曲 */
		ibplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				binder.playorpause();
				if (binder.isplaying()) {
					ibplay.setImageResource(R.drawable.selector_item_play);
				} else
					ibplay.setImageResource(R.drawable.selector_item_pause);
			}
		});
		/** 下一首歌曲 */
		ibpause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				app.nextMusic();
				final Music m = app.getCurrentMusic();
				model.loadSonginfoBysong(m.getSong_id(), new SonginfoCallback() {
					@Override
					public void onSonginfoLoaded(List<SongUrl> urls, SongInfo info) {
						// TODO Auto-generated method stub
						m.setUrls(urls);
						m.setInfo(info);
						String url = urls.get(0).getFile_link();
						binder.playmusic(url);
						ibplay.setImageResource(R.drawable.selector_item_play);
					}
				});
			}
		});
		// RadioGroup����ViewPager
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radioNew: // ѡ�����¸��
					viewPager.setCurrentItem(0);
					break;
				case R.id.radioHot: // ѡ�����ȸ��
					viewPager.setCurrentItem(1);
					break;
				}
			}
		});

		// ViewPager����RadioGroup
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				switch (position) {
				case 0: // �������¸��
					radioNew.setChecked(true);
					break;
				case 1: // �������ȸ��
					radioHot.setChecked(true);
					break;
				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		});

		circle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.circle:
					rlplaymusic.setVisibility(View.VISIBLE);
					ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1, 0, rlplaymusic.getHeight());
					anim.setDuration(400);
					rlplaymusic.startAnimation(anim);
					break;
				}
			}
		});
	}

	private List<Music> searchmusiclist;

	/** 搜索歌曲 **/
	protected void searchMusic() {
		// TODO Auto-generated method stub
		String keyword = etKeyWord.getText().toString();
		if ("".equals(keyword)) {
			return;
		}
		model.searchMusicLIst(keyword, new MusicListCallback() {

			@Override
			public void onMusicListLoaded(List<Music> musics) {
				// TODO Auto-generated method stub
				// 把musics传到全局变量
				searchmusiclist = musics;
				SearchAdapter searchadapter = new SearchAdapter(MainActivity.this, musics);
				lvSearchMusic.setAdapter(searchadapter);

			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (rlplaymusic.getVisibility() == View.VISIBLE) {
			// 隐藏rlpalymusic
			rlplaymusic.setVisibility(View.INVISIBLE);
			ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0, 0, rlplaymusic.getHeight());
			anim.setDuration(400);
			rlplaymusic.startAnimation(anim);
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * ΪviewPager����������
	 */
	private void setPagerAdapter() {
		// ����Fragment���� ��Ϊviewpager������Դ
		fragments = new ArrayList<Fragment>();
		// ��ӵ�һҳ
		fragments.add(new NewMusicListFragment());
		fragments.add(new HotMusicListFragment());
		pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
	}

	/**
	 * �ؼ���ʼ��
	 */
	private void setViews() {
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioNew = (RadioButton) findViewById(R.id.radioNew);
		radioHot = (RadioButton) findViewById(R.id.radioHot);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		tvmusicname = (TextView) findViewById(R.id.musicname);
		tvmusicautor = (TextView) findViewById(R.id.musicauthor);
		circle = (CircleImageView) findViewById(R.id.circle);

		ivPMBackround = (ImageView) findViewById(R.id.ivPMBackgroud);

		rlplaymusic = (RelativeLayout) findViewById(R.id.rl_playmusic);
		rltvmusicname = (TextView) findViewById(R.id.tv_musicname);
		rltvmusicautor = (TextView) findViewById(R.id.tv_musicauthor);
		rlmusicmlc = (TextView) findViewById(R.id.tv_mlc);
		tvmusicstart = (TextView) findViewById(R.id.tvmusicstart);
		tvmusicend = (TextView) findViewById(R.id.tvmusicend);
		ibpress = (ImageButton) findViewById(R.id.ib_previous);
		ibplay = (ImageButton) findViewById(R.id.ib_play);
		ibpause = (ImageButton) findViewById(R.id.ib_next);

		ivmusic = (CircleImageView) findViewById(R.id.iv_music);

		seekbar = (SeekBar) findViewById(R.id.seekBar);

		rlSearchMusic = (RelativeLayout) findViewById(R.id.rl_SearchMusic);
		btnSearch = (Button) findViewById(R.id.btn_Search);
		btnToSearch = (Button) findViewById(R.id.btn_tosearch);
		btnCancel = (Button) findViewById(R.id.btn_Cancel);
		etKeyWord = (EditText) findViewById(R.id.et_keyword);
		lvSearchMusic = (ListView) findViewById(R.id.lv_searchmusic);
	}

	/**
	 * viewpager��������
	 */
	class MainPagerAdapter extends FragmentPagerAdapter {

		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}

}
