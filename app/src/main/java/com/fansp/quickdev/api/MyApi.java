package com.fansp.quickdev.api;


import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import rx.Observable;

public interface MyApi {
    //登录
    @POST("api/UserInfo/Login")
    Observable<String> Login(@Body RequestBody body);

    //注册
    @POST("api/UserInfo/UserRegistration")
    Observable<String> Regist(@Body RequestBody body);

    //忘记密码
    @POST("api/UserInfo/ForgetPassword")
    Observable<String> ForgetPassword(@Body RequestBody body);

    //获取验证码
    @POST("api/UserInfo/SendCode2Phone")
    Observable<String> SendVerCode(@Body RequestBody body);

    @Multipart
    @POST("api/UserInfo/UpdateFaceImg")
    Observable<String> postFaceImg(@PartMap HashMap<String, RequestBody> maps, @Part List<MultipartBody.Part> parts);

}
