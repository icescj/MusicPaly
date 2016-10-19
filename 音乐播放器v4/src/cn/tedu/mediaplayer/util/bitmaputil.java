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
import android.os.AsyncTask;
import cn.tedu.mediaplayer.app.Musicapplication;

public class bitmaputil {
	/**
	 * 异步模糊化处理图片
	 * 
	 * @param bitmap
	 *            原始图片
	 * @param radius
	 *            模糊半径 半径越大 越模糊
	 * @param callback
	 *            当模糊处理完毕后将会返回bitmap
	 */
	public static void loadBlurBitmap(final Bitmap bitmap, final int radius, final BitmapCallback callback) {
		AsyncTask<String, String, Bitmap> task = new AsyncTask<String, String, Bitmap>() {
			// 工作线程中模糊化处理图片 耗时
			protected Bitmap doInBackground(String... params) {
				Bitmap b = createBlurBitmap(bitmap, radius);
				return b;
			}

			// 调用callback的方法 把处理结果返回给调用者
			protected void onPostExecute(Bitmap result) {
				callback.onBitmaploaded(result);
			}
		};
		task.execute();
	}

	/**
	 * 传递bitmap 传递模糊半径 返回一个被模糊的bitmap 比较耗时
	 * 
	 * @param sentBitmap
	 * @param radius
	 * @return
	 */
	private static Bitmap createBlurBitmap(Bitmap sentBitmap, int radius) {
		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		if (radius < 1) {
			return (null);
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);
		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];
		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);

		}
		yw = yi = 0;
		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;
		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

				}

			}
			stackpointer = radius;
			for (x = 0; x < w; x++) {
				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);

				}
				p = pix[yw + vmin[x]];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				yi++;

			}
			yw += w;

		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;
				sir = stack[i + radius];
				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];
				rbs = r1 - Math.abs(i);
				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

				}
				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;

				}
				p = x + vmin[y];
				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				yi += w;
			}
		}
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

	/** 从文件中读取一个bitmap */
	public static Bitmap loadBitmap(File file) {
		if (!file.exists()) {
			return null;
		}
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}

	/**
	 * 把bitmap压缩成jpg格式保持到file文件中（存放入cache文件缓存中）
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

	public static void loadBitmap(final String path, final int scale, final BitmapCallback bitmapCallback) {
		// TODO Auto-generated method stub
		AsyncTask<String, String, Bitmap> task = new AsyncTask<String, String, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					// 获取缓存图片目录
					String filname = path.substring(path.lastIndexOf("/"));
					
					File file = new File(Musicapplication.getapp().getCacheDir(), "iamgess" + filname);
					Options opts = new Options();
					opts.inSampleSize = scale;
					if (file.exists()) {// 文件已经存在
						return BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
					}
					// 图片不存在 网络下载 并存入文件
					InputStream is = HttpUtils.get(path);
					Bitmap b = BitmapFactory.decodeStream(is);
					save(b, file);
					return BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				bitmapCallback.onBitmaploaded(result);
			}
		};
		task.execute();
	}

	public interface BitmapCallback {
		void onBitmaploaded(Bitmap bitmap);
	}
}
