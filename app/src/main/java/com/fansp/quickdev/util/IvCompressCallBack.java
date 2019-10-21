package com.fansp.quickdev.util;

import java.io.File;

public interface IvCompressCallBack {
    void onSucceed(File file);
    void onFailure(String msg);
}
