package cn.tedu.mediaplayer.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class bitmaputil {

	/** 从文件中读取一个bitmap */
	public static Bitmap loadBitmap(File file) {
		if (!file.exists()) {
			return null;
		}
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}

	/**
	 * 把bitmap压缩成jpg格式保持到file文件中
	 * 
	 * @throws FileNotFoundException
	 */
	public static void save(Bitmap bitmap, File file) throws FileNotFoundException {
		// getParentFile()的作用是获得父目录
		if (!file.getParentFile().exists()) {
			// 假如不存在即创建
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(file);
		// CompressFormat.JPEG 常量
		bitmap.compress(CompressFormat.JPEG, 100, fos);
	}

	/***
	 * 通过输入流 按照用户需要大小进行图片压缩，返回bitmap对象
	 * 
	 * @param is
	 *            数据源
	 * @param with
	 *            宽度
	 * 
	 * @throws IOException
	 */
	public static Bitmap loadbitmap(InputStream is, int with, int heigh) throws IOException {
		// 从输入流中读取byte［］
		// 输入流中的数据写到字节输出流中
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[10 * 1024];
		int length;
		while ((length = is.read(buffer)) != -1) {
			bos.write(buffer, 0, length);
			bos.flush();
		}
		byte[] bytes = bos.toByteArray();
		bos.close();
		// 解析byte 获取图片的原始宽和高
		Options opts = new Options();
		// 设置仅加载位图边界信息（相当于位图的信息，但没有加载位图）
		opts.inJustDecodeBounds = true;
		// Log.i("tag", "opts" + opts.hashCode() + "");
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		int w = opts.outWidth / with;
		int h = opts.outHeight / heigh;
		// 根据用户需求求出压缩比例
		opts.inSampleSize = w > h ? w : h;
		opts.inJustDecodeBounds = false;
		// 再次解析byte［］获取压缩后的图片
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		return bitmap;
	}

}
