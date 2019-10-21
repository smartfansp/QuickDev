package com.fansp.quickdev.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.collection.LruCache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @创建者 CSDN_LQR
 * @描述 视频缩略图加载工具
 */
public class VideoThumbLoader {
    private ImageView imgView;
    private String path;

    static VideoThumbLoader instance;

    public static VideoThumbLoader getInstance() {
        if (instance == null) {
            synchronized (VideoThumbLoader.class) {
                if (instance == null) {
                    instance = new VideoThumbLoader();
                }
            }
        }
        return instance;
    }

    // 创建cache
    private LruCache<String, Bitmap> lruCache;

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            if (imgView.getTag().equals(path)) {
                Bitmap btp = (Bitmap) msg.obj;
                imgView.setImageBitmap(btp);
            }
        }
    };

    // @SuppressLint("NewApi")
    private VideoThumbLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();// 获取最大的运行内存
        int maxSize = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 这个方法会在每次存入缓存的时候调用
                // return value.getByteCount();
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    private void addVideoThumbToCache(String path, Bitmap bitmap) {
        if (getVideoThumbToCache(path) == null && bitmap != null) {
            // 当前地址没有缓存时，就添加
            lruCache.put(path, bitmap);
        }
    }

    private Bitmap getVideoThumbToCache(String path) {

        return lruCache.get(path);

    }

    public void showThumb(String path, ImageView imgview, int width, int height) {

        if (getVideoThumbToCache(path) == null) {
            // 异步加载
            imgview.setTag(path);
            new MyBobAsynctack(imgview, path, width, height).execute(path);
        } else {
            imgview.setImageBitmap(getVideoThumbToCache(path));
        }

    }
    public String showThumb(String path, int width, int height) {
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();// 获取sdcard的根路径
        String filePath = sdpath + File.separator + "genetic" + File.separator;
        File localFile = new File(filePath);
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        String name=path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
        Log.i("kang",name);
        File file = new File(localFile, name + ".jpg");//将要保存图片的路径
        if (file.exists()){
            return file.getAbsolutePath();
        }else {
            File files=saveBitmapFile(name,
                    createVideoThumbnail(path,width,height,
                            MediaStore.Video.Thumbnails.MICRO_KIND));
            return files.getAbsolutePath();
        }

    }
    public File saveBitmapFile(String name, Bitmap bitmap) {
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();// 获取sdcard的根路径
        String filePath = sdpath + File.separator + "genetic" + File.separator;
        File localFile = new File(filePath);
        if (!localFile.exists()) {
            localFile.mkdir();
        }
        File file = new File(localFile, name + ".jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
    class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;
        private int width;
        private int height;

        public MyBobAsynctack(ImageView imageView, String path, int width,
                              int height) {
            this.imgView = imageView;
            this.path = path;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = createVideoThumbnail(params[0], width, height,
                    MediaStore.Video.Thumbnails.MICRO_KIND);
            // 加入缓存中
            if (getVideoThumbToCache(params[0]) == null && bitmap != null) {
                addVideoThumbToCache(path, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imgView.getTag() != null && imgView.getTag().equals(path)) {
                imgView.setImageBitmap(bitmap);
            }
        }
    }

    private void showDateByThread(ImageView imageview, final String path,
                                  final int width, final int height) {
        imgView = imageview;
        this.path = path;
        new Thread(new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = createVideoThumbnail(path, width, height,
                        MediaStore.Video.Thumbnails.MICRO_KIND);
                Message msg = new Message();
                msg.obj = bitmap;
                msg.what = 1001;
                mHandler.sendMessage(msg);
            }
        }).start();

    }

    private static Bitmap createVideoThumbnail(String vidioPath, int width,
                                               int height, int kind) {
        if (vidioPath.startsWith("http")){
            Bitmap bitmap=createVideoThumbnail(vidioPath,width,height);
            return bitmap;
        }else {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            return bitmap;
        }

    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }
}
