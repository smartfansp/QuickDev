package com.fansp.quickdev.library_qrscan.listener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;

import com.fansp.quickdev.library_qrscan.zxing.camera.CameraManager;
import com.google.zxing.Result;

/**
 * Created by ryan on 18/4/4.
 */

public interface IZXingActivity {
    void handleDecode(Result result, Bitmap bmp);
    CameraManager getCameraManager();
    Rect getCropRect();
    Activity getActivity();
    Handler getHandler();
}
