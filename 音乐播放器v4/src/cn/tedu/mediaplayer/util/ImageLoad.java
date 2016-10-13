package cn.tedu.mediaplayer.util;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ImageView;
import cn.tedu.mediaplayer.R;

/** 更新holder.ivAlbum */
public class ImageLoad {
	private Map<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
	private List<Imageloadtask> tasks = new ArrayList<Imageloadtask>();
	private Context context;
	private Thread workThread;
	private AbsListView listview;
	private boolean isrunning = true;
	protected static final int HANDLER_IMAGE_LOADED = 0;
	private Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_IMAGE_LOADED:
				// 得到msg数据
				Imageloadtask task = (Imageloadtask) msg.obj;
				// 更新UI
				// 更具path标记取imageView控件
				ImageView ivAlbum = (ImageView) listview.findViewWithTag(task.path);
				if (ivAlbum != null) {// 拿到对应的imageView
					Bitmap b = task.bitmap;
					if (b != null) {// 图片资源也存在（即下载成功）
						ivAlbum.setImageBitmap(b);
					} else {// 无图片资源
						ivAlbum.setImageResource(R.drawable.ic_launcher);
					}
				}
				break;
			}
		}
	};

	public ImageLoad(Context context, AbsListView listview) {
		this.context = context;

		// 启动线程，处理task集合内的图片路径（即获取图片）
		workThread = new Thread() {
			public void run() {
				while (isrunning) {
					if (!tasks.isEmpty()) {// 假如集合tasks不为空
						// 去除集合中第一个任务
						Imageloadtask task = tasks.remove(0);
						String path = task.path;
						// 获取bitmap图片
						Bitmap bitmap = loadbitmap(path);
						Log.i("tag", "图片：" + bitmap + "");
						// 第二步骤 对象task添加bitmap属性
						task.bitmap = bitmap;
						Message msg = Message.obtain();
						msg.what = HANDLER_IMAGE_LOADED;
						msg.obj = task;
						hander.sendMessage(msg);
					} else {
						// 假如集合tasks为空
						try {
							// 锁定线程
							synchronized (workThread) {
								workThread.wait();
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};
		workThread.start();
	}

	public void display(ImageView ivAlbum, String path) {
		// TODO Auto-generated method stub
		// 图片路径打标记
		ivAlbum.setTag(path);
		// 去cache中取bimap
		SoftReference<Bitmap> ref = cache.get(path);
		if (ref != null) {
			Bitmap b = ref.get();
			if (b != null) {
				ivAlbum.setImageBitmap(b);
				return;
			}
		}
		// 文件存储中寻找是否有图片
		String filename = path.substring(path.lastIndexOf("/"));
		File file = new File(context.getCacheDir(), "iamges" + filename);
		Bitmap b = bitmaputil.loadBitmap(file);
		if (b != null) {
			ivAlbum.setImageBitmap(b);
			cache.put(path, new SoftReference<Bitmap>(b));
			return;
		}
		Imageloadtask task = new Imageloadtask();
		// 第一步骤 添加task中的path属性
		task.path = path;
		tasks.add(task);
		Log.i("tag", "tasks集合任务数量：" + tasks.size() + "");
		// 唤醒线程
		synchronized (workThread) {
			workThread.notify();
		}
	}

	// 图片路径及图片类
	class Imageloadtask {
		String path;
		Bitmap bitmap;
	}

	/*** 获取bitmap方法 */
	protected Bitmap loadbitmap(String path) {
		// TODO Auto-generated method stub

		Bitmap bitmap;
		try {
			// HttpUtils.get(path) 获取path的输入流
			InputStream is = HttpUtils.get(path);
			// bitmap = BitmapFactory.decodeStream(is);
			bitmap = bitmaputil.loadbitmap(is, 50, 50);
			// 将bitmap存入hasmap
			SoftReference<Bitmap> value;
			cache.put(path, new SoftReference<Bitmap>(bitmap));
			// 将bitmap存入文件缓存
			String filename = path.substring(path.lastIndexOf("/"));
			File file = new File(context.getCacheDir(), "iamges" + filename);
			Log.i("tag", "图片路径" + file + "");
			bitmaputil.save(bitmap, file);
			return bitmap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 停止线程
	public void stopthread() {
		isrunning = false;
		synchronized (workThread) {
			workThread.notify();
		}
	}
}
