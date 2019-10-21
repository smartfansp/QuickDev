package com.fansp.quickdev.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @创建者 CSDN_LQR
 * @描述 sharedPreferences工具类(单例模式)
 */
public class SPUtils {
    private static final String SP_NAME = "MoveSurvey";
    private static SPUtils mSpUtils;
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @SuppressLint("WrongConstant")
    private SPUtils(Context context) {
        this.context = context;
        sp = this.context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SPUtils getInstance(Context context) {

        if (mSpUtils == null) {
            synchronized (SPUtils.class) {
                if (mSpUtils == null) {
                    mSpUtils = new SPUtils(context);
                    return mSpUtils;
                }
            }
        }

        return mSpUtils;

    }

    public void putBoolean(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, Boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public void putString(String key, String value) {
        if (key == null) {
            return;
        }
        editor.putString(key, value);
        editor.commit();
    }


    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    public void remove(String key) {
        sp.edit().remove(key).commit();
    }

    public void removeAll() {
        sp.edit().clear().commit();
    }

    public <T> void setDataList(String tag, List<T> datalist) {

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);

        editor.putString(tag, strJson);
        editor.commit();
    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(String tag, Class<T> tClass) {
        List<T> datalist = new ArrayList<T>();
        String strJson = sp.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }

//        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
//        }.getType());
        datalist = jsonToBeanList(strJson, tClass);
        return datalist;

    }

    public static <T> List<T> jsonToBeanList(String json, Class<T> tClass) {
        List<T> list = new ArrayList<>();
        if (json != null && json.length() > 0 && !json.equals("null")) {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray jsonarray = parser.parse(json).getAsJsonArray();
            for (JsonElement element : jsonarray) {
                list.add(gson.fromJson(element, tClass));
            }
        }

        return list;
    }


    public void putList(String key, List list) {
        try {
            String liststr = ListToString(list);
            editor.putString(key, liststr);
            editor.commit();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public List getList(String key) {

        String liststr = sp.getString(key, "");
        try {
            List list = StringToList(liststr);
            return list;
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String ListToString(List list) throws IOException {
//创建ByteArrayOutputStream对象，用来存放字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//得到的字符放到到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(list);
//Base64.encode将字节文件转换成Base64编码存在String中
        String string = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
// 关闭Stream
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return string;
    }

    @SuppressWarnings("unchecked")
    public static List StringToList(String string)
            throws StreamCorruptedException, IOException,
            ClassNotFoundException {
        byte[] b = Base64.decode(string.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                b);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        List list = (List) objectInputStream
                .readObject();
        // 关闭Stream
        objectInputStream.close();
        byteArrayInputStream.close();
        return list;
    }

}