package cn.tedu.mediaplayer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class bitmaputil {
	/***
	 * 
	 * @param
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
