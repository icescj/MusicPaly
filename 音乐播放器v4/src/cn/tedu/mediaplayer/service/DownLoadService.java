package cn.tedu.mediaplayer.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import cn.tedu.mediaplayer.R;
import cn.tedu.mediaplayer.util.HttpUtils;

public class DownLoadService extends IntentService {

	private static final int NOTIFICITION_ID = 0;

	public DownLoadService() {
		super("name");
		// TODO Auto-generated constructor stub
	}

	/*** 该方法在工作线程中执行，可用直接编写耗时代码，我们需要在该方法中发送http请求，完成下载业务（边读边解析边保存） */
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String path = intent.getStringExtra("path");
		String title = intent.getStringExtra("title");
		int bitrate = intent.getIntExtra("bitrate", 0);
		int filesize = intent.getIntExtra("filesize", 0);
		try {
			if (path == null || path.equals("")) {
				return;
			}
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
					"_" + bitrate + "/" + title + ".mp3");
			Log.i("tag:file", file.toString());
			if (!file.getParentFile().exists()) {
				// 判断文件是否存在，不存在即创建
				file.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			InputStream is = HttpUtils.get(path);
			byte[] buffer = new byte[1024];
			int length;
			int progress = 0;
			while ((length = is.read()) != -1) {
				fos.write(buffer);
				fos.flush();
				progress += length;
				// 保存过程中，发通知，提示进度
				String jindu = Math.floor(100.0 * progress / filesize) + "%";
				sendNotification("音乐下载", "音乐下载中", "音乐进度： " + jindu);
			}
			fos.close();
			// 下载完毕，再发通知，提示用户
			clearNotification();
			sendNotification("音乐下载", "音乐下载完成", "音乐下载完成");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** 发通知 ***/
	public void sendNotification(String title, String ticker, String text) {
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Builder builder = new Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher).setContentText(text).setContentTitle(title).setTicker(ticker);
		manager.notify(NOTIFICITION_ID, builder.build());
	}

	/** 清除通知 ***/
	public void clearNotification() {
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(NOTIFICITION_ID);
	}

}
