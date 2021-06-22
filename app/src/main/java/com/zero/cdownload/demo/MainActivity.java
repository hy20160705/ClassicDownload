package com.zero.cdownload.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zero.cdownload.ApkDownload;
import com.zero.cdownload.listener.CDownloadListener;
import com.zero.cdownload.manager.download.FileManager;
import com.zero.cdownload.util.FileUtil;
import com.zero.cdownload.util.LogTool;
import com.zero.cdownload.util.Utils;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tvProgress;
    private NotificationManager notificationManager;
    private DecimalFormat df = new DecimalFormat("0.00");//格式化小数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.down_loading);
        tvProgress = findViewById(R.id.tv_progress);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel("appDownload", "appDownload", "app下载");
        NotificationUtils.getInstance().init(this);
    }

    private void createNotificationChannel(String channelId, String channelName, String channelDesc) {
        NotificationManager manager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        //适配8.0通知栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDesc);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setVibrationPattern(new long[0]);
            channel.setSound(null, null);
            manager.createNotificationChannel(channel);
        }

    }

    private void showNotification(@FileManager.DownloadSate int sate, int totalSize, int currentSize) {
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, "appDownload");
        } else {
            builder = new Notification.Builder(this);
        }
        switch (sate) {
            case FileManager.DownloadSate.PENDING:
                builder.setContentTitle("准备下载")
                        .setSmallIcon(R.mipmap.ic_samll_download);
                break;
            case FileManager.DownloadSate.DOWNLOADING:
                LogTool.d("下载中");
                builder.setContentTitle("下载中")
                        .setSmallIcon(R.mipmap.ic_samll_download)
                        .setProgress(totalSize, currentSize, false);
                break;
            case FileManager.DownloadSate.CANCEL:
                builder.setContentTitle("已取消")
                        .setSmallIcon(R.mipmap.ic_samll_download)
                        .setSubText("已取消下载");
                break;
            case FileManager.DownloadSate.COMPLETED:
                LogTool.d("下载完成");
                builder.setContentTitle("已完成")
                        .setSmallIcon(R.mipmap.ic_small_complete);
                break;
        }
        builder
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setOngoing(true);
        notificationManager.notify(1, builder.build());
    }

    public void startDownload(View view) {
        ApkDownload.getInstance().create("https://module-bucket.oss-cn-hangzhou.aliyuncs.com/waiterc-module/android/merchant/gray/app_210_1_chs-signed.apk", new CDownloadListener() {
            @Override
            public void onPreStart() {
                LogTool.e("MainActivity", "onPreStart");
            }

            @Override
            public void onProgress(final long maxSize, final long currentSize) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress((int) (((double) currentSize / maxSize) * 100));
                        tvProgress.setText(String.format("%s/%s", Utils.formatSize(currentSize), Utils.formatSize(maxSize)));
                        NotificationUtils.getInstance().showNotification(FileManager.DownloadSate.DOWNLOADING, (int) maxSize, (int) currentSize);
                    }
                });

                LogTool.e("Hey", "in onProgress maxSize:" + Utils.formatSize(maxSize) + ";currentSize:" + Utils.formatSize(currentSize));
            }

            @Override
            public void onComplete(final String localFilePath) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationUtils.getInstance().showNotificationComplete(localFilePath);
                    }
                });

                LogTool.e("MainActivity", "onComplete localFilePath:" + localFilePath);
//                InstallCompat.install(MainActivity.this, new File(localFilePath));
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationUtils.getInstance().cancelNotification();
                        Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
                LogTool.e("MainActivity", "onError errorMessage:" + errorMessage);
            }

            @Override
            public void onCancel() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationUtils.getInstance().showNotification(FileManager.DownloadSate.CANCEL, -1, -1);
                    }
                });

                LogTool.e("MainActivity", "onCancel");
            }
        });
        ApkDownload.getInstance().start("https://module-bucket.oss-cn-hangzhou.aliyuncs.com/waiterc-module/android/merchant/gray/app_210_1_chs-signed.apk");

    }

    public void pause(View view) {
        ApkDownload.getInstance().stop("https://module-bucket.oss-cn-hangzhou.aliyuncs.com/waiterc-module/android/merchant/gray/app_210_1_chs-signed.apk");
    }

    public void clearCache(View view) {
        FileUtil.deleteDirectory(ApkDownload.getInstance().getDownloadConfig().getDiskCachePath());
    }
}
