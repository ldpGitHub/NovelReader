package com.example.ldp.ireader.model.remote;

import android.util.Log;
import android.webkit.WebSettings;

import com.example.ldp.ireader.App;
import com.example.ldp.ireader.utils.Constant;
import com.example.ldp.ireader.utils.LenientGsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ldp on 17-4-20.
 */

public class RemoteHelper {
    private static final String TAG = "RemoteHelper";
    private static RemoteHelper sInstance;
    private Retrofit mRetrofit;
    private Retrofit mRetrofitByBiqugeSearch;
    private Retrofit mRetrofitByBiquge;



    private Retrofit mRetrofitByBiqugeInfo;
    private Retrofit mRetrofitByBiqugeContent;
    private OkHttpClient mOkHttpClient;
    private RemoteHelper(){
        Interceptor logIntercept = new HttpLoggingInterceptor();
        ((HttpLoggingInterceptor) logIntercept).setLevel(HttpLoggingInterceptor.Level.HEADERS);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logIntercept)
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
//                                Request request = chain.request();
                                Request request = chain.request()
                                        .newBuilder()
                                        .removeHeader("User-Agent")//移除旧的
                                        .addHeader("User-Agent", WebSettings.getDefaultUserAgent(App.getContext()))//添加真正的头部
//                                        .addHeader("User-Agent", "okhttp-okgo/jeasonlzy")//添加真正的头部
                                        .build();

                                HttpUrl requestUrl = request.url();
                                String oldUrl = requestUrl.toString();
                                Response response = null;
//                                try {
                                //在这里获取到request后就可以做任何事情了
//                                Log.d(TAG, "+intercept: " + request.url().toString());
                                try {
                                    response = chain.proceed(request);
                                }
                                catch (IOException e){

                                    if(request.url().toString().contains("shuapi.jiaston.com")){
                                        String newUrl = Constant.API_BASE_URL_QUAPP + oldUrl.substring(Constant.API_BASE_URL_BY_BIQUGE.length(), oldUrl.length());

                                        Request newRequest = request
                                                .newBuilder()
                                                .url(newUrl)
                                                .build();
                                        Log.d(TAG, "+IOException" + newRequest.url().toString());

                                        response = chain.proceed(newRequest);


                                    }

                                }
//                                 Log.d(TAG, "+response: " + response.toString());

//                                }catch (SocketTimeoutException e) {
//                                    e.printStackTrace();
//                                }
                                return response;
                            }
                        }
                ).readTimeout(50, TimeUnit.SECONDS).retryOnConnectionFailure(true).build();

        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.API_BASE_URL)
                .build();
        mRetrofitByBiqugeSearch = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.API_BASE_URL_BY_BIQUGE_SEARCH)
                .build();
        mRetrofitByBiquge = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.API_BASE_URL_QUAPP)
                .build();


        mRetrofitByBiqugeInfo = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.API_INFO_BIQUGE)
                .build();
        mRetrofitByBiqugeContent = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.API_CONTEBT_BIQUGE)
                .build();
    }

    public static RemoteHelper getInstance(){
        if (sInstance == null){
            synchronized (RemoteHelper.class){
                if (sInstance == null){
                    sInstance = new RemoteHelper();
                }
            }
        }
        return sInstance;
    }

    public Retrofit getRetrofit(){
        return mRetrofit;
    }

    public Retrofit getRetrofitByBiqugeSearch(){
        return mRetrofitByBiqugeSearch;
    }

    public Retrofit getRetrofitByBiquge(){
        return mRetrofitByBiquge;
    }

    public Retrofit getRetrofitByBiqugeInfo() {
        return mRetrofitByBiqugeInfo;
    }

    public Retrofit getRetrofitByBiqugeContent() {
        return mRetrofitByBiqugeContent;
    }


    public OkHttpClient getOkHttpClient(){
        return mOkHttpClient;
    }
}
