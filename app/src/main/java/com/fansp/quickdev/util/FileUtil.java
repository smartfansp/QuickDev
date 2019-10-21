package com.fansp.quickdev.util;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.fansp.quickdev.R;
import com.fansp.quickdev.base.MyApplication;
import com.fansp.quickdev.library_qrscan.utils.ToastUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    private static final String TAG = "TAG";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "BBB";
    public static String jpegName;
    public static boolean flag;

    public static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    public static void openAndroidFile(Context context, String filepath) {
        Intent intent = new Intent();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
            File file = new File(filepath);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);//动作，查看
            intent.setDataAndType(FileProvider.getUriForFile(context, "com.aibaoxian.aInsClaim.fileprovider", file), getMIMEType(file));//设置类型
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showToast(context, "没有找到可打开文档的软件", Toast.LENGTH_LONG, Gravity.CENTER);
            e.printStackTrace();
        }
    }

    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0)
            return type;
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < FileUtil.MIME_MapTable.length; i++) {
            if (fileType.equals(FileUtil.MIME_MapTable[i][0]))
                type = FileUtil.MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * 初始化保存路径
     *
     * @return
     */

    public static String initPath() {
        long sdCardSize = StorageUtil.getAvailableExternalMemorySize();
        if (storagePath.equals("")) {
            if (sdCardSize == 0 || sdCardSize == -1) {
//                storagePath = Environment.getDataDirectory().getAbsolutePath() + "/" + DST_FOLDER_NAME;
                return "-1";
            } else {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
                String date = sDateFormat.format(new Date());
                storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME + "/" + date;
                Log.i(TAG, "initPath: " + storagePath);
            }
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        long dataTake = System.currentTimeMillis();
        jpegName = storagePath + "/" + dataTake + ".jpg";
        return jpegName;
    }

    public static File getSaveFile(Context context, String s) {
        File file = new File(context.getFilesDir(), s + ".jpg");
        return file;
    }

    /**
     * 添加水印
     * @param bitmapFile 原图
     * @param filePath 原图路径
     * @param surveyName 查勘员
     * @param insuranceNameTel 被保险人姓名和电话
     * @param address 地址
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void addWatermark(Bitmap bitmapFile, String filePath, String surveyName, String insuranceNameTel, String address) {
        if(bitmapFile == null || TextUtils.isEmpty(filePath)){
            return;
        }
        Bitmap waterImg = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.sy).copy(Bitmap.Config.ARGB_8888, true);//水印图片
        int width = bitmapFile.getWidth();//原图宽
        int height = bitmapFile.getHeight();//原图高

        int w = waterImg.getWidth();//水印图标宽
        int h = waterImg.getHeight();//水印图标高
        Bitmap newBitmap = copy(bitmapFile, filePath).copy(Bitmap.Config.ARGB_8888, true);
        Canvas mCanvas = new Canvas(newBitmap);

        //添加文字
        String s = getTime() + "\n" + surveyName + "\n" + insuranceNameTel + "\n" + address;//时间、查勘员、被保险人（电话）、地址
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        if (Build.BRAND.equals("Huawei")) {
            textPaint.setTextSize(10.0F);
        } else {
            textPaint.setTextSize(20.0F);
        }
        textPaint.setAntiAlias(true);
        StaticLayout layout = new StaticLayout(s, textPaint, mCanvas.getWidth(), Layout.Alignment.ALIGN_OPPOSITE, 1.0F, 0.0F, true);

        // 往位图中开始画入水印图片
        mCanvas.drawBitmap(waterImg, width - w - 15, height - h - layout.getHeight() - 15, null);
        mCanvas.save();
        mCanvas.translate(-15, height - layout.getHeight() - 15);
        layout.draw(mCanvas);
        mCanvas.restore();//别忘了restore
        //生成新的图片就删除旧的图片
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        saveBitmap(newBitmap, filePath);
    }

    /**
     * 根据原位图生成一个新的位图，并将原位图所占空间释放
     *
     * @param srcBmp 原位图
     * @return 新位图
     */
    public static Bitmap copy(Bitmap srcBmp, String path) {
        Bitmap destBmp = null;
        try {
            // 创建一个临时文件
            File file = new File(path);
            if (file.exists()) {// 临时文件 ， 用一次删一次
                file.delete();
            }
            file.getParentFile().mkdirs();
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            int width = srcBmp.getWidth();
            int height = srcBmp.getHeight();
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, width * height * 4);
            // 将位图信息写进buffer
            srcBmp.copyPixelsToBuffer(map);
            // 释放原位图占用的空间
            srcBmp.recycle();
            // 创建一个新的位图
            destBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
            map.position(0);
            // 从临时缓冲中拷贝位图信息
            destBmp.copyPixelsFromBuffer(map);
            channel.close();
            randomAccessFile.close();
            file.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
            destBmp = null;
            return srcBmp;
        }
        return destBmp;
    }

    /**
     * 保存Bitmap到sdcard
     */

    private static void saveBitmap(Bitmap b, String path) {
        if (TextUtils.isEmpty(path)){
            return;
        }
        try {
            FileOutputStream fout = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            flag = true;
            //  ShareUtils.saveXML("图片",path,YZApplication.getContext());
            Log.i(TAG, "saveBitmap成功" + path);
            Uri uri = Uri.fromFile(new File(path));
            MyApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (IOException e) {
            Log.i(TAG, "saveBitmap:失败");
            e.printStackTrace();
        }
    }

    private static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 添加两个点
     *
     * @param
     */
    public static Bitmap addTimeFlag1(Bitmap src, int x, int y, int x1, int y1) {
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        //添加文字
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(10);
        mCanvas.drawPoint(x, y, textPaint);
        mCanvas.drawPoint(x1, y1, textPaint);
        mCanvas.save();
        mCanvas.restore();
        return newBitmap;
    }

    /**
     * 读取assets下的文件到byte数组
     *
     * @param ctx
     * @param name
     * @return
     * @throws IOException
     */
    public static byte[] resToByteArray(Context ctx, String name) throws IOException {

        AssetManager assets = ctx.getResources().getAssets();
        int available = assets.open(name).available();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(available);
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(assets.open(name));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    /**
     * 初始化.so文件
     *
     * @param cxt
     */
    public static void test3(Context cxt) {
        Log.i("TAG", "CPU_ABI: " + Build.CPU_ABI);
        String cpuAbi = Build.CPU_ABI;
        String resName = "data1.txt";
        if (cpuAbi.equals("armeabi")) {
            resName = "data1.txt";
        } else if (cpuAbi.equals("armeabi-v7a")) {
            resName = "data2.txt";
        }

        int[] s = new int[]{51, 78, 11, 125, 79, 42, 99, 152, 33, 70, 21, 32, 55, 67, 92, 138, 19, 83, 99, 176, 118, 146, 157};
        try {
            byte[] bytes = FileUtil.resToByteArray(cxt, resName);

            byte[] newBytes = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                int j = i % 23;
                newBytes[i] = (byte) (bytes[i] ^ s[j]);
            }
            String absolutePath = cxt.getCacheDir().getAbsolutePath();
            String path1 = absolutePath + "/em.so";
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(path1);
                fileOutputStream.write(newBytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}