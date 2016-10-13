package cn.tedu.mediaplayer.activity;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.tedu.mediaplayer.R;
import cn.tedu.mediaplayer.app.Musicapplication;
import cn.tedu.mediaplayer.entity.Music;
import cn.tedu.mediaplayer.fragment.HotMusicListFragment;
import cn.tedu.mediaplayer.fragment.NewMusicListFragment;
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
	private cn.tedu.mediaplayer.ui.CircleImageView circle;

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
		this.registerReceiver(receiver, filter);
	}

	/*** 广播接收器 接受音乐状态相关的广播 */
	class MusicStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("ACTION_MUSIC_STARTED")) {
				// 音乐正在播放，收到广播
				// 获得当前正在播放的音乐对象，去app中拿
				Musicapplication app = Musicapplication.getapp();
				// 获得当前播放的音乐
				Music music = app.getCurrentMusic();
				// 设置歌名
				String title = music.getTitle();
				tvmusicname.setText(title);
				// 设置艺术家的名字
				String singer = music.getAuthor();
				tvmusicautor.setText(singer);
				// 设置圆形图片
				String path = music.getPic_small();
				bitmaputil.loadBitmap(path, new BitmapCallback() {

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
				MucsicBinder binder = (MucsicBinder) service;
				// NewMusicListFragment 设置bind
				NewMusicListFragment f1 = (NewMusicListFragment) fragments.get(0);
				f1.setbinder(binder);
			}
		};
		this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
	}

	/**
	 * ���ü�����
	 */
	private void setListeners() {
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
