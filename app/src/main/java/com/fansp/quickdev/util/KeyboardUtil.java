package com.fansp.quickdev.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by 2 on 2017/11/24.
 */

public class KeyboardUtil {
    /**
     * 如果软件键盘显示  就关掉
     * @param context
     */
    public static void closeKeyBoard(Activity context){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(),
                    0);
        }
    }
}
