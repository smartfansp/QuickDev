package com.fansp.quickdev.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kang on 2017/6/20.
 */

public class OkHttpUtils {
    private static OkHttpUtils okHttpUtils = new OkHttpUtils();
    public final static int CONNECT_TIMEOUT =60;
    public final static int READ_TIMEOUT=100;
    public final static int WRITE_TIMEOUT=60;
    private static OkHttpClient okHttpClient;
    public final static String TAG="OkHttpUtils";
    private Handler mHandler;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static OkHttpUtils getInstance() {
        if (okHttpUtils==null){
            okHttpUtils=new OkHttpUtils();
        }
        return okHttpUtils;
    }


    private OkHttpUtils() {
        okHttpClient=new OkHttpClient();
        okHttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

        mHandler=new Handler(Looper.getMainLooper());
    }

    public interface CallBack{
        void requestSuccess(String result) throws Exception;
        void requestFailure(Request request, IOException e);
    }

    /**
     * 异步get请求方法
     * @param url
     * @param params
     * @param callBack
     */
    public static void get(String url, Map<String,String> params, CallBack callBack){
        getInstance().doGetFromAsync(url,params,callBack);
    }

    /**
     * 异步post请求方法
     * @param url
     * @param params
     * @param callBack
     */
    public static void post(String url, Map<String,String> params, CallBack callBack){
        getInstance().doPostFromAsync(url,params,callBack);
    }

    /**
     * 异步postjson请求方法
     * @param url
     * @param json
     * @param callBack
     */
    public static void postJson(String url, String json, CallBack callBack){
        getInstance().doPostJsonAsync(url,json,callBack);
    }

    /**
     * 上传文件方法
     * @param url
     * @param params
     * @param callBack
     */
    public static void upLoadFile(String url, Map<String,Object> params, CallBack callBack){
        getInstance().doUpLoadFile(url,params,callBack);
    }


    /**
     * 下载文件方法
     * @param url
     * @param destFileDir
     * @param callBack
     */
    public static void downLoadFile(String url, String fileName, String destFileDir, CallBack callBack){
        getInstance().doDownLoadFile(url,fileName,destFileDir,callBack);
    }




    /**
     * 下载文件的实现
     * @param url
     * @param destFileDir
     * @param callBack
     */
    private void doDownLoadFile(String url, String fileName, String destFileDir, final CallBack callBack) {
        final File file = new File(destFileDir, fileName);
        if (file.exists()) {
            deliverDataSuccess("文件已存在",callBack);
            return;
        }
        final Request request = new Request.Builder().url(url).build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
                deliverDataFailure(request,e,callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];

                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current);
                    }
                    fos.flush();
                    deliverDataSuccess("下载成功",callBack);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    deliverDataFailure(request,e, callBack);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }


    /**
     * 上传文件的实现
     * @param requestUrl
     * @param params
     * @param callBack
     */
    private void doUpLoadFile(String requestUrl, Map<String, Object> params, final CallBack callBack) {
        try {
            //补全请求地址
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : params.keySet()) {
                Object object = params.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                }
            }
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder()
                    .tag("对比图片")
                    .url(requestUrl)
                    .post(body)
                    .addHeader("Connection","keep-alive")
                    .addHeader("Content-Length", "41268")
                    .addHeader("Content-Type", "multipart/form-data; boundary=xyVhy6eP-3zgp56pgqgMBRVTW_asiL0yqpi1")
                    .addHeader("Host","172.17.202.109:9189")
                    .addHeader("User-Agent", "Apache-HttpClient/4.5.5 (Java/1.8.0_201)")
                    .build();
            //单独设置参数 比如读取超时时间
            final Call call = okHttpClient.newBuilder().writeTimeout(500, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
                    deliverDataFailure(request,e,callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.e(TAG, "response ----->" + string);
                        deliverDataSuccess(string, callBack);
                    } else {
                        throw new IOException(response+"");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 异步get请求实现
     * @param url
     * @param params
     * @param callBack
     */
    private void doGetFromAsync(String url, Map<String, String> params, final CallBack callBack) {
        if (params==null){
            params=new HashMap<>();
        }
        //请求url（baseurl+参数）
        final String doUrl=urlJoint(url,params);
        //新建一个请求
        final Request request= new Request.Builder().url(doUrl).build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request,e,callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    //请求成功的操作
                    String result=response.body().string();
                    deliverDataSuccess(result,callBack);
                }
            }
        });
    }


    /**
     * 异步post请求实现
     * @param url
     * @param params
     * @param callBack
     */
    private void doPostFromAsync(String url, Map<String, String> params, final CallBack callBack) {
        RequestBody requestBody;
        if (params==null){
            params=new HashMap<>();
        }
        FormBody.Builder builder=new FormBody.Builder();
        //对添加的参数进行遍历
        for (Map.Entry<String,String> entry :params.entrySet()){
            String key=entry.getKey();
            String value;
            if (entry.getValue()==null){
                value="";
            }else {
                value=entry.getValue();
            }
            builder.add(key,value);
        }
        requestBody=builder.build();
        //结果返回
        final Request request=new Request.Builder().url(url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request,e,callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String result=response.body().string();
                    deliverDataSuccess(result,callBack);
                }else {
                    throw new IOException(response+"");
                }
            }
        });
    }

    /**
     * 异步postJson请求实现
     * @param url
     * @param json
     * @param callBack
     */
    private void doPostJsonAsync(String url, String json, final CallBack callBack) {
        RequestBody body = RequestBody.create(JSON, json);
        //结果返回
        final Request request=new Request.Builder().url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request,e,callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String result=response.body().string();
                    deliverDataSuccess(result,callBack);
                }else {
                    throw new IOException(response+"");
                }
            }
        });
    }

    /**
     * 拼接url和params
     * @param url
     * @param params
     * @return
     */
    private String urlJoint(String url, Map<String, String> params) {
        StringBuilder endUrl=new StringBuilder(url);
        boolean isFirst=true;
        Set<Map.Entry<String,String>> entrySet=params.entrySet();
        for (Map.Entry<String,String> entry: entrySet){
            if (isFirst&&!url.contains("?")){
                isFirst=false;
                endUrl.append("?");
            }else {
                endUrl.append("&");
            }
            endUrl.append(entry.getKey());
            endUrl.append("=");
            endUrl.append(entry.getValue());
        }
        return endUrl.toString();
    }

    /**
     * 分发成功的操作
     * @param result
     * @param callBack
     */
    private void deliverDataSuccess(final String result, final CallBack callBack) {
        //异步操作
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack!=null){
                    try {
                        callBack.requestSuccess(result);
//                        Gson gson=new Gson();
//                        ResultPublic resultPublic=gson.fromJson(result, ResultPublic.class);
//                        if (resultPublic!=null&&resultPublic.isIsError()&&resultPublic.getMessage().equals("验证码验证失败")){
//                            YZApplication.GetServerDate();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 分发失败的操作
     * @param request
     * @param e
     * @param callBack
     */
    private void deliverDataFailure(final Request request, final IOException e, final CallBack callBack) {
        //异步操作
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack!=null){
                    callBack.requestFailure(request,e);
                }
            }
        });
    }

}
