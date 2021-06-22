package com.zero.cdownload.demo;

import android.app.Application;

import com.zero.cdownload.ApkDownload;
import com.zero.cdownload.config.CDownloadConfig;
import com.zero.cdownload.config.ConnectConfig;
import com.zero.cdownload.config.ThreadPoolConfig;

/**
 * @author caizhixing
 * @date 3/15/2018.
 */

public class MyApplication extends Application {
    private static volatile MyApplication instance = null;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NotificationUtils.getInstance().init(this);
        CDownloadConfig downloadConfig = CDownloadConfig.build()
                .setEnableLog(true)
                .setDiskCachePath("")
                .setConnectConfig(ConnectConfig.build().setConnectTimeOut(10000).setReadTimeOut(20000))
                .setIoThreadPoolConfig(ThreadPoolConfig.build().setCorePoolSize(4).setMaximumPoolSize(100).setKeepAliveTime(60));

        ApkDownload.getInstance().init(downloadConfig, this);
    }
}

