package cn.tedu.mediaplayer.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.tedu.mediaplayer.R;
import cn.tedu.mediaplayer.fragment.HotMusicListFragment;
import cn.tedu.mediaplayer.fragment.NewMusicListFragment;

public class MainActivity extends FragmentActivity {
	private RadioGroup radioGroup;
	private RadioButton radioNew;
	private RadioButton radioHot;
	private ViewPager viewPager;
	private ArrayList<Fragment> fragments;
	private MainPagerAdapter pagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//�ؼ���ʼ��
		setViews();
		//ΪviewPager����������
		setPagerAdapter();
		//���ü�����
		setListeners();
	}

	/**
	 * ���ü�����
	 */
	private void setListeners() {
		//RadioGroup����ViewPager
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radioNew: //ѡ�����¸��
					viewPager.setCurrentItem(0);
					break;
				case R.id.radioHot: //ѡ�����ȸ��
					viewPager.setCurrentItem(1);
					break;
				}
			}
		});
		
		//ViewPager����RadioGroup
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				switch (position) {
				case 0: //�������¸��
					radioNew.setChecked(true);
					break;
				case 1: //�������ȸ��
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
		//����Fragment���� ��Ϊviewpager������Դ
		fragments = new ArrayList<Fragment>();
		//��ӵ�һҳ
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
	}
	
	/**
	 * viewpager��������
	 */
	class MainPagerAdapter extends FragmentPagerAdapter{

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




