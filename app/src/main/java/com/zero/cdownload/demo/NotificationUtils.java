package com.zero.cdownload.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zero.cdownload.manager.download.FileManager;
import com.zero.cdownload.util.LogTool;

import java.io.File;

/**
 * @Name: ClassicDownload
 * @Description: 描述信息
 * @Author: Created by heyong on 6/22/21
 */
public class NotificationUtils {
    private static final String channelId = "chsApkDownload";
    private static final String channelName = "chsApkDownload";
    private static final String channelDes = "chsApkDownload";
    private NotificationManager notificationManager;
    private Context mContext;
    private final int notificationId = 100;

    private static volatile NotificationUtils instance = null;

    private NotificationUtils() {
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    public static NotificationUtils getInstance() {
        if (instance == null) {
            synchronized (NotificationUtils.class) {
                if (instance == null) {
                    instance = new NotificationUtils();
                }
            }
        }
        return instance;
    }

    /**
     * Return whether the notifications enabled.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(mContext).areNotificationsEnabled();
    }

    public void showNotification(Notification notification) {
        if (notification != null) {
            notificationManager.notify(notificationId, notification);
        }
    }

    private void createNotificationChannel() {
        //适配8.0通知栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDes);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setVibrationPattern(new long[0]);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public void showNotification(@FileManager.DownloadSate int sate, int totalSize, int currentSize) {
        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(mContext, "chsApkDownload");
        switch (sate) {
            case FileManager.DownloadSate.PENDING:
                builder.setContentTitle("准备下载")
                        .setSmallIcon(R.mipmap.ic_samll_download);
                break;
            case FileManager.DownloadSate.DOWNLOADING:
                LogTool.d("下载中");
                builder.setContentTitle("下载中")
                        .setSmallIcon(R.mipmap.ic_samll_download)
                        .setContentIntent(null)
                        .setProgress(totalSize, currentSize, false);
                break;
            case FileManager.DownloadSate.CANCEL:
                builder.setContentTitle("已取消")
                        .setContentInfo(null)
                        .setSmallIcon(R.mipmap.ic_samll_download)
                        .setSubText("已取消下载");
                break;
        }
        builder
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setOngoing(true);
        notificationManager.notify(1, builder.build());
    }

    /**
     * 显示已完成通知
     *
     * @param filePath
     */
    public void showNotificationComplete(String filePath) {
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(mContext, "chsApkDownload");
        } else {
            builder = new Notification.Builder(mContext);
        }

        LogTool.d("下载完成");
        notificationManager.cancelAll();
        builder.setContentTitle("下载完成,点击安装")
                .setSmallIcon(R.mipmap.ic_small_complete);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, InstallCompat.installIntent(mContext, new File(filePath)), 0);
        builder.setContentIntent(pendingIntent);
        builder
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setOngoing(true);
        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * 取消通知
     */
    public void cancelNotification() {
        notificationManager.cancel(notificationId);
    }
}
