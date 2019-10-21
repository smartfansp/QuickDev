package com.fansp.quickdev.api;

import com.fansp.quickdev.api.persistentcookiejar.BaseApiRetrofit;
import com.fansp.quickdev.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class ApiRetrofit extends BaseApiRetrofit {
    private MyApi mApi;
    private static ApiRetrofit mInstance;
    private ApiRetrofit() {
        super();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        //在构造方法中完成对Retrofit接口的初始化
        //测试服务器地址
        String BASE_URL = "http://61.50.105.94:8689/";
        //测试服务器地址
        String YSQ_URL = "http://172.16.6.28/";
        // hsk IP地址
        String HSK_URL = "http://192.168.200.168/";
        //福建最新地址
        String NEW_URL = "http://121.204.110.49:8000";
        // gsc IP地址
        String GSC_URL = "http://192.168.200.20/";
        // picc IP地址 http://121.204.110.49:8000
        String PICC_URL = "http://139.224.120.192:8080/";
        mApi = new Retrofit.Builder()
                .baseUrl(NEW_URL)
//                .baseUrl(HSK_URL)
//                .baseUrl(GSC_URL)
//                .baseUrl(YSQ_URL)
//                .baseUrl(PICC_URL)
//                .baseUrl(BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(MyApi.class);
    }

    public static ApiRetrofit getInstance() {
        if (mInstance == null) {
            synchronized (ApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new ApiRetrofit();
                }
            }
        }
        return mInstance;
    }

    private RequestBody getRequestBody(Object obj) {
//        Log.i(AppConst.TAG, (String) obj);
        LogUtils.i("fansp_request_body",(String) obj);
        String route = new Gson().toJson(obj);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("Content-Type:application/x-www-form-urlencoded; charset=utf-8"), route);
        return body;
    }

    private HashMap<String, RequestBody> getJsonBody(Object obj) {
        HashMap<String, RequestBody> hashMap = new HashMap<>();
        String route = new Gson().toJson(obj);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("Content-Type:multipart/form-data"), route);
        hashMap.put("RequestJson", body);
        return hashMap;
    }

    //登录
    public Observable<String> Login(String s){
        return mApi.Login(getRequestBody(s));
    }

    //注册
    public Observable<String> Regist(String s){
        return mApi.Regist(getRequestBody(s));
    }

    //修改密码
    public Observable<String> ForgetPassword(String s){
        return mApi.ForgetPassword(getRequestBody(s));
    }

    //发送验证码
    public Observable<String> SendVerCode(String s){
        return mApi.SendVerCode(getRequestBody(s));
    }


    /**
     * filepath，文件上传
     * @param path
     * @return
     */
    private List<MultipartBody.Part> getPart(List<String> path) {
        File file = null;
        String name = "";
        RequestBody body = null;
        List<MultipartBody.Part> partList = new ArrayList<>();
        if (path != null && path.size() > 0) {
            for (int i = 0; i < path.size(); i++) {
                file = new File(path.get(i));
                name = file.getName();
                if (name.contains(".")) {
                    name = name.substring(0, name.indexOf("."));
                }
                body = RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(name, file.getName(), body);
                partList.add(part);
            }
            return partList;
        }
        return null;
    }
    /**
     * 参数，文件上传 List<MultipartBody.Part>
     * @param param
     * @return
     */
    private HashMap<String, RequestBody> getPartParam(String param){
        LogUtils.i("fansp_request_body", param);
        HashMap<String, RequestBody> map = new HashMap<>();
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), param);
        map.put("RequestJson", requestBody);
        return map;
    }
}
