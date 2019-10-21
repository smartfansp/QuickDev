package com.fansp.quickdev.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class IvCompressUtil {
    private static String rootPath = "movePhoto";
    private final static String PHOTO_JPG_BASEPATH = "/" + rootPath + "/surveyImgs/";
    private final static String PHOTO_COMPRESS_JPG_BASEPATH = "/" + rootPath + "/CompressImgs/";

    public static void setRootPath(String rootPath) {
        IvCompressUtil.rootPath = rootPath;
    }

    /**
     * @param fileName :System.currentTimeMillis() + ".jpg"//用时间戳
     * @return 获取保存原始文件的位置
     */

    public static String getJpgFileAbsolutePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("fileName isEmpty");
        }
        String photoPath = "";
        if (!fileName.endsWith(".jpg")) {
            fileName = fileName + ".jpg";
        }
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + PHOTO_JPG_BASEPATH;
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        photoPath = fileBasePath + fileName;
        return photoPath;
    }

    /**
     * 获取保存压缩图片文件的位置
     *
     * @return
     */
    public static String getCompressJpgFileAbsolutePath() {
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + PHOTO_COMPRESS_JPG_BASEPATH;
        File file = new File(fileBasePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return fileBasePath;
    }


    /**
     * 使用LuBan 压缩单张图片
     *
     * @param context
     * @param imageUrl
     * @param compressCallBack 结果回调
     */
    public static void luBanCompress(Context context, String imageUrl, IvCompressCallBack compressCallBack) {
        Luban.with(context)
                .load(new File(imageUrl))
                .ignoreBy(200)//低于100的图片不压缩
                .putGear(3)//设置压缩级别 默认是3
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setTargetDir(IvCompressUtil.getCompressJpgFileAbsolutePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        compressCallBack.onSucceed(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        compressCallBack.onFailure(e.getMessage());
                    }
                }).launch();
    }

    /**
     * 使用LuBan 压缩单张图片，指定路径
     *
     * @param context
     * @param imageUrl
     * @param compressCallBack 结果回调
     */
    public static void luBanZDPathCompress(Context context, String imageUrl, IvCompressCallBack compressCallBack) {
        Luban.with(context)
                .load(new File(imageUrl))
                .ignoreBy(100)//低于100的图片不压缩
                .putGear(3)//设置压缩级别 默认是3
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setTargetDir(IvCompressUtil.getCompressJpgFileAbsolutePath())
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        String[] split = filePath.split("/");
                        return split[split.length - 1];
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        compressCallBack.onSucceed(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        compressCallBack.onFailure(e.getMessage());
                    }
                }).launch();
    }
}
