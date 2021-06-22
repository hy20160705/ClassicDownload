package com.zero.cdownload.demo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.zero.cdownload.util.LogTool;

import java.io.File;

/**
 * @Name: ClassicDownload
 * @Description: 描述信息
 * @Author: Created by heyong on 6/21/21
 */
public class InstallCompat {
    private static String TAG = InstallCompat.class.getSimpleName();
    /**
     * 请求权限并安装
     *
     * @param context
     * @param apkFile
     */
//    public static void requireInstall(Context context, @NonNull File apkFile) {
//        boolean haveInstallPermission;
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            haveInstallPermission=context.getPackageManager().canRequestPackageInstalls();
//        }
//
//        XXPermissions.with(context)
//                .permission(Permission.REQUEST_INSTALL_PACKAGES)
//                .request(new OnPermission() {
//                    @Override
//                    public void hasPermission(List<String> granted, boolean all) {
//                        if (all) {
//                            install(context, apkFile);
//                        }
//                    }
//
//                    @Override
//                    public void noPermission(List<String> denied, boolean never) {
//
//                    }
//                });
//    }

    /**
     * 安装apk
     *
     * @param context
     * @param apkFile
     */
    public static void install(@NonNull Context context, @NonNull File apkFile) {
        install(context, file2uri(context, apkFile));
    }

    /**
     * 安装apk
     *
     * @param context
     * @param apkUri
     */
    public static void install(@NonNull Context context, @NonNull Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //兼容7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 安装apk
     *
     * @param context
     * @param apkFile
     */
    public static Intent installIntent(@NonNull Context context, @NonNull File apkFile) {
        return installIntent(context, file2uri(context, apkFile));
    }

    public static Intent installIntent(@NonNull Context context, @NonNull Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //兼容7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

//    /**
//     * 执行命令并且输出结果
//     */
//    public boolean apkInstall(String path) {
//        LogTool.d(TAG, "SilentInstallActivity path:" + path);
//        Process p = null;
//        int value = -1;
//        try {
//            String docmd = "pm install -r -i com.amtcloud.amtphoneagent  --user 0 " + path;
//            p = Runtime.getRuntime().exec(docmd);
//            value = p.waitFor();
//        } catch (Exception e) {
//            LogTool.d(TAG, "SilentInstallActivity error: " + e);
//        } finally {
//            if (p != null) {
//                p.destroy();
//            }
//        }
//        return returnResult(value);
//    }

    public static Uri file2uri(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
