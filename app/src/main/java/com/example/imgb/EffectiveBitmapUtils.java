package com.example.imgb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Author: 柏洲
 * Email:  baizhoussr@gmail.com
 * Date:   2020/7/20 15:24
 * Desc:
 */
public class EffectiveBitmapUtils {

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("effective-bitmap");
    }

    /**
     * @param optimize 参数为TRUE时，图片压缩算法使用最优的哈夫曼编码表，它需要额外传递数据，因此会耗费CPU运算时间，以及开辟很多临时内存空间。
     *                 参数为FALSE时，使用默认的哈夫曼编码表。在大多数情况，使用最优哈夫曼编码表相比默认哈夫曼编码表，能节省图像文件很大比例的大小。
     */
    public static native String compressBitmap(Bitmap bitmap, int w, int h, int quality,
                                               byte[] fileNameBytes, boolean optimize);

    private static int DEFAULT_QUALITY = 99;

    /**
     * JNI压缩
     */
    public static void compressByJNI(Bitmap bitmap, String fileName, boolean optimize) {
        saveBitmap(bitmap, DEFAULT_QUALITY, fileName, optimize);
    }

    private static void saveBitmap(Bitmap bitmap, int quality, String fileName, boolean optimize) {
        compressBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), quality, fileName.getBytes(), optimize);
    }

    /**
     * 质量压缩
     */
    public static void compressByQuality(Bitmap bitmap, File file) {
        int options = DEFAULT_QUALITY;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 尺寸压缩
     */
    public static void compressBySize(Bitmap bitmap, File file) {
        int ratio = 8;
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / ratio, bitmap.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bitmap.getWidth() / ratio, bitmap.getHeight() / ratio);
        canvas.drawBitmap(bitmap, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 采样压缩
     */
    public static void compressBySample(String filePath, File file) {
        int inSampleSize = 4;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
