package com.fansp.quickdev.util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.fansp.quickdev.R;
import com.fansp.quickdev.base.MyApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by kang on 2017/11/8.
 */

public class ImageAddFlagUtils {
    private static ImageAddFlagUtils imageAddFlagUtils = new ImageAddFlagUtils();
    @SuppressLint("HandlerLeak")
    private static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    static ImageAddFlagUtils getInstance() {
        if (imageAddFlagUtils == null) {
            imageAddFlagUtils = new ImageAddFlagUtils();
        }
        return imageAddFlagUtils;
    }

    /**
     * 不压缩图片加水印
     *
     * @param path
     * @return
     */
    public static File addFlagNoCompress(String path,String date, String point, String name,String insType) {
        Log.i("qwe", "addFlagNoCompress1: ");
        File file = null;
        try {
            File localfile = new File(getInstance().getimage(path,""));
            file = getInstance().saveBitmapFile(localfile.getAbsolutePath(), addTimeFlag(
                    BitmapFactory.decodeFile(localfile.getAbsolutePath()),date, point, name,"拍照时间",insType));
        } catch (Exception e) {
            Log.i("qwe", "addFlagNoCompress: " + e.toString());
        }
        return file;
    }

    /**
     * 新的 字体加大 只针对查勘
     *
     * @param path
     * @return
     */
    public static File addFlagNoCompress(String billcode,String path,String date, String point,
                                         String name, String address, String insType,
                                         boolean isIns, String earmark, String userName) {
        File file = null;
        try {
            File localfile = new File(getInstance().getimage(path,billcode));
            file = getInstance().saveBitmapFile(localfile.getAbsolutePath(), addTimeFlagBig(
                    BitmapFactory.decodeFile(localfile.getAbsolutePath()),date, point, address, name, insType, isIns, earmark, userName));
        } catch (Exception e) {
            Log.i("qwe", "addFlagNoCompress: " + e.toString());
        }
        return file;
    }


    public static String getimage(String srcPath,String billcode) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(srcPath);
        Bitmap bmp = Bitmap.createBitmap(bitmap);
        if (w > h) {
            bmp = changeBipmapSize(bmp, 1920, 1920 * h / w);
        } else {
            bmp = changeBipmapSize(bmp, 1920 * w / h, 1920);
        }

        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();// 获取sdcard的根路径
        String filePath = sdpath + File.separator + "yz365" + File.separator;

        File localFile = new File(filePath);
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String date = format.format(new Date());
        File file = new File(localFile, (TextUtils.isEmpty(billcode)?UUID.randomUUID():(billcode+"_"+ date))+ ".jpg");//将要保存图片的路径
        Log.i("qwe", "图片1: " + file.getName());
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
    private String getimageNoCompress(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(srcPath);
        Bitmap bmp = Bitmap.createBitmap(bitmap);
//        if (w > h) {
//            bmp = changeBipmapSize(bmp, 1920, 1920 * h / w);
//        } else {
//            bmp = changeBipmapSize(bmp, 1920 * w / h, 1920);
//        }

        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();// 获取sdcard的根路径
        String filePath = sdpath + File.separator + "yz365" + File.separator;

        File localFile = new File(filePath);
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        File file = new File(localFile, UUID.randomUUID() + ".jpg");//将要保存图片的路径
        Log.i("qwe", "图片1: " + file.getName());
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static Bitmap getimage(Bitmap bitmap,String billcode) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(bitmap);
        if (w > h) {
            bmp = changeBipmapSize(bmp, 1920, 1920 * h / w);
        } else {
            bmp = changeBipmapSize(bmp, 1920 * w / h, 1920);
        }
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();// 获取sdcard的根路径
        String filePath = sdpath + File.separator + "yz365" + File.separator;
        File localFile = new File(filePath);
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
        String date = format.format(new Date());
        File file = new File(localFile, (TextUtils.isEmpty(billcode)?UUID.randomUUID():(billcode+"_"+ date))+ ".jpg");//将要保存图片的路径

        Log.i("qwe", "图片2: " + file.getName());
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile
                (file.getAbsolutePath());
    }
    public static Bitmap getimage(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(bitmap);
        if (w > h) {
            bmp = changeBipmapSize(bmp, 1920, 1920 * h / w);
        } else {
            bmp = changeBipmapSize(bmp, 1920 * w / h, 1920);
        }
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();// 获取sdcard的根路径
        String filePath = sdpath + File.separator + "yz365" + File.separator;
        File localFile = new File(filePath);
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
        String date = format.format(new Date());
        File file = new File(localFile, UUID.randomUUID()+".jpg");//将要保存图片的路径

        Log.i("qwe", "图片2: " + file.getName());
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile
                (file.getAbsolutePath());
    }

    /**
     * 固定尺寸
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap changeBipmapSize(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream isBm = null;
        try {

            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            Log.i("TAG", "compressImage: " + baos.toByteArray().length);
            while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;//每次都减少10
            }
            isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        } catch (Exception e) {

        }

        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public File saveBitmapFile(String path, Bitmap bitmap) {
        File localFile = new File(path);
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(path)));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MyApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + path)));

        return new File(path);
    }

    /**
     * 修改人： 张国强
     * 修改时间： 2018/8/30
     * 修改内容：增加 addTimeFlagBig(Bitmap src, String location, String address)方法重载
     */
    public static Bitmap addTimeFlagBig(Bitmap src,String date, String location, String address) {
        String[] split = location.split(",");
        String lon = split[0]; //经度
        String lat = split[1]; //纬度
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        //添加文字
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        float size;
        if (android.os.Build.BRAND.equals("Huawei")) {
            size = 25.0F;
        } else {
            size = 35.0F;
        }
        textPaint.setTextSize(size);
        textPaint.setAntiAlias(true);
        String s = "";

        if (date.length() == 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            date = format.format(new Date());
        }
        s = date + "\n" + formatAddress(address) + "\n经度：" + lon + "\n纬度：" + lat;
        StaticLayout layout = new StaticLayout(s, textPaint, (int) size * 11, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        // 这里的参数300，表示字符串的长度，当满300时，就会换行，也可以使用“\r\n”来实现换行
        mCanvas.translate(newBitmap.getWidth() - (int) size * 11, newBitmap.getHeight() - layout.getHeight());
        mCanvas.save();
        layout.draw(mCanvas);
        mCanvas.restore();//别忘了restore
        Bitmap waterBitmap;
        waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
        newBitmap = createWaterMaskBitmap(newBitmap, waterBitmap, 0, 0);
        return newBitmap;
    }

    /**
     * 添加水印
     * 图片
     * 经纬度
     * 地址
     * 养殖户名字
     *
     * @param
     */
    public static Bitmap addTimeFlagBig(Bitmap src,String date, String point, String address, String name, String insType, boolean isIns, String earmark, String userName) {
        String[] split = point.split(",");
        String lon = split[0]; //经度
        String lat = split[1]; //纬度
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        //添加文字
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        float size;
        if (android.os.Build.BRAND.equals("Huawei")) {
            size = 25.0F;
        } else {
            size = 35.0F;
        }
        textPaint.setTextSize(size);
        textPaint.setAntiAlias(true);
        String s = "";
        if (earmark == null) {
//            if (date.length() == 0) {
//                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                date = format.format(new Date());
//            }
            s = date + "\n" + "场户:" + name + "\n" + formatAddress(address) + "\n经度：" + lon + "\n纬度：" + lat + "\n查勘员:" + userName;
        } else {
//            if (date.length() == 0) {
//                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                date = format.format(new Date());
//            }
            s = earmark + "\n" + date + "\n" + "场户:" + name + "\n" + formatAddress(address) + "\n经度：" + lon + "\n纬度：" + lat + "\n查勘员:" + userName;
        }
        StaticLayout layout = new StaticLayout(s, textPaint, (int) size * 11, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        // 这里的参数300，表示字符串的长度，当满300时，就会换行，也可以使用“\r\n”来实现换行
        mCanvas.translate(newBitmap.getWidth() - (int) size * 11, newBitmap.getHeight() - layout.getHeight());
        mCanvas.save();
        layout.draw(mCanvas);
        mCanvas.restore();//别忘了restore
        Bitmap waterBitmap;
        if (!isIns) {
            waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
        } else {
            switch (insType) {
                case "12"://中华联
                    waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
                    break;
                default:
                    waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
                    break;
            }
        }
        newBitmap = createWaterMaskBitmap(newBitmap, waterBitmap, 0, 0);
        return newBitmap;
    }

    /**
     * 添加水印
     * 图片
     * 经纬度
     * 地址
     * 养殖户名字
     *
     * @param
     */
    public static Bitmap tempAddTimeFlagBig(Bitmap src,String date, String point, String address, String name, String insType, boolean isIns, String earmark, String userName) {
        String[] split = point.split(",");
        String lon = split[0]; //经度
        String lat = split[1]; //纬度
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        //添加文字
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        float size;
        if (android.os.Build.BRAND.equals("Huawei")) {
            size = 13.0F;
        } else {
            size = 20.0F;
        }
        textPaint.setTextSize(size);
        textPaint.setAntiAlias(true);
        String s = "";
        if (earmark == null) {
//            if (date.length() == 0) {
//                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                date = format.format(new Date());
//            }
            s = date + "\n" + "场户:" + name + "\n" + formatAddress(address) + "\n经度：" + lon + "\n纬度：" + lat + "\n查勘员:" + userName;
        } else {
//            if (date.length() == 0) {
//                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                date = format.format(new Date());
//            }
            s = earmark + "\n" + date + "\n" + "场户:" + name + "\n" + formatAddress(address) + "\n经度：" + lon + "\n纬度：" + lat + "\n查勘员:" + userName;
        }
        StaticLayout layout = new StaticLayout(s, textPaint, (int) size * 11, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        // 这里的参数300，表示字符串的长度，当满300时，就会换行，也可以使用“\r\n”来实现换行
        mCanvas.translate(newBitmap.getWidth() - (int) size * 11, newBitmap.getHeight() - layout.getHeight());
        mCanvas.save();
        layout.draw(mCanvas);
        mCanvas.restore();//别忘了restore
        Bitmap waterBitmap;
        if (!isIns) {
            waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
        } else {
            switch (insType) {
                case "0"://中华联
                    waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
                    break;
                default:
                    waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
                    break;
            }
        }
        newBitmap = createWaterMaskBitmap(newBitmap, waterBitmap, 0, 0);
        return newBitmap;
    }

    /**
     * 添加水印
     *
     * @param
     */
    public static Bitmap addTimeFlag(Bitmap src,String date, String point, String name,String flag,String insType) {
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        //添加文字
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.RED);
        if (android.os.Build.BRAND.equals("Huawei")) {
            textPaint.setTextSize(13.0F);
        } else {
            textPaint.setTextSize(20.0F);
        }
        textPaint.setAntiAlias(true);
        String s = "";
        s = flag+"：" + date + "\n经纬度：" + point;
        StaticLayout layout = new StaticLayout(s, textPaint, 100000, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        // 这里的参数300，表示字符串的长度，当满300时，就会换行，也可以使用“\r\n”来实现换行
        mCanvas.save();
        layout.draw(mCanvas);
        mCanvas.restore();//别忘了restore
        //生成新的图片就删除旧的图片

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        if (android.os.Build.BRAND.equals("Huawei")) {
            paint.setTextSize(13.0F);
        } else {
            paint.setTextSize(20.0F);
        }
        Rect bounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), bounds);
        Bitmap.Config bitmapConfig = newBitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        newBitmap = newBitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(newBitmap);

        canvas.drawText(name, 0, src.getHeight() -
                bounds.height() - 10, paint);
        Bitmap waterBitmap;
        switch (insType) {
            case "0"://
                waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
            default:
                waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
                break;
        }


        newBitmap = createWaterMaskBitmap(newBitmap, waterBitmap, 0, 0);
        return newBitmap;
    }

    /**
     * 添加水印
     * bitmap    不用了
     * point 经纬度
     * earnum 耳标号
     * name 保险公司
     *
     * @param
     */
    public static Bitmap addTimeFlag(Bitmap src, String point, String name,String date) {
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        //添加文字
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.RED);
        if (android.os.Build.BRAND.equals("Huawei")) {
            textPaint.setTextSize(13.0F);
        } else {
            textPaint.setTextSize(20.0F);
        }
        textPaint.setAntiAlias(true);
        String s = "";
        if (date.length()==0){
            SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            date=format.format(new Date());
        }
        s = "拍照时间：" + date + "\n经纬度：" + point;
        StaticLayout layout = new StaticLayout(s, textPaint, 100000, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        // 这里的参数300，表示字符串的长度，当满300时，就会换行，也可以使用“\r\n”来实现换行
        mCanvas.save();
        layout.draw(mCanvas);
        mCanvas.restore();//别忘了restore
        //生成新的图片就删除旧的图片
//        File file = new File(fileName);
//        file.delete();
//        Uri uri = Uri.fromFile(file);
//        MyApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        if (android.os.Build.BRAND.equals("Huawei")) {
            paint.setTextSize(13.0F);
        } else {
            paint.setTextSize(20.0F);
        }
        Rect bounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), bounds);
        Bitmap.Config bitmapConfig = newBitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        newBitmap = newBitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(newBitmap);

        canvas.drawText(name, 0, src.getHeight() -
                bounds.height() - 10, paint);

        Bitmap waterBitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy);
        newBitmap = createWaterMaskBitmap(newBitmap, waterBitmap, 0, 0);
        return newBitmap;
    }



    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,
                                                int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();

//        scaleWithWH(watermark,width,watermark.getHeight());
        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(Bitmap.createScaledBitmap(watermark, width, height, true),
                paddingLeft, paddingTop, null);
        // 保存
        canvas.save();
        // 存储
        canvas.restore();
        return newb;
    }

    public static Bitmap decodeFile(File f) {
        Bitmap b = null;
        int IMAGE_MAX_SIZE = 1280;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }

    /**
     * 格式化地址 是个字符换行
     *
     * @return
     */
    private static String formatAddress(String address) {
        String thisaddress = address;
        if (!TextUtils.isEmpty(address)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < address.length(); i++) {
                if (i % 9 == 0 && i != 0) {
                    stringBuilder.append("\n").append(address.charAt(i));
                } else {
                    stringBuilder.append(address.charAt(i));
                }
            }
            return stringBuilder.toString();
        }
        return "";

    }

}

