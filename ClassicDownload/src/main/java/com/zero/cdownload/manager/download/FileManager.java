package com.zero.cdownload.manager.download;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zero.cdownload.config.CDownloadConfig;
import com.zero.cdownload.config.ConnectConfig;
import com.zero.cdownload.constants.ConfigConstant;
import com.zero.cdownload.entity.CDownloadTaskEntity;
import com.zero.cdownload.util.DownloadCheckUtil;
import com.zero.cdownload.util.FileUtil;
import com.zero.cdownload.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zero on 2018/4/26.
 *
 * @author zero
 */

public class FileManager {

    private static int connectTimeOut = ConfigConstant.TIME_DEFAULT_CONNECT_OUT;
    private static int readTimeOut = ConfigConstant.TIME_DEFAULT_READ_OUT;

    private static int bufferSize = ConfigConstant.BUFFER_DEFAULT_DOWNLOAD;
    private static String cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com/zero/cdownload/";

    public static void init(CDownloadConfig downloadConfig) {
        if (downloadConfig != null && downloadConfig.getConnectConfig() != null) {
            connectTimeOut = downloadConfig.getConnectConfig().getConnectTimeOut();
            readTimeOut = downloadConfig.getConnectConfig().getReadTimeOut();
            bufferSize = downloadConfig.getConnectConfig().getReadBufferSize();
        }
        if (downloadConfig != null && !TextUtils.isEmpty(downloadConfig.getDiskCachePath())) {
            cachePath = downloadConfig.getDiskCachePath();
        }
    }

    public static void startDownload(CDownloadTaskEntity taskEntity) {
        if (taskEntity == null || taskEntity.getDownloadListener() == null) {
            Log.e("HongLi", "in startDownload task entity is null or download listener is null.");
            return;
        }
        taskEntity.getDownloadListener().onPreStart();
        String localFilePath = PathUtil.getLocalFilePath(taskEntity.getUrl(), cachePath);
        String templocalFilePath = PathUtil.getLocalFilePath(taskEntity.getUrl(), cachePath + ConfigConstant.DEFAULT_TEMP_FOLDER_NAME);
        if (FileUtil.isExist(localFilePath) && DownloadCheckUtil.checkFileDownloadOk(taskEntity.getUrl(), localFilePath)) {
            //文件已经下载成功,不需要执行下载操作
            taskEntity.getDownloadListener().onComplete(localFilePath);
        } else {
            //先删除本地文件，如果有
            FileUtil.deleteFile(localFilePath);
            boolean downloadResult = downloadFile(taskEntity.getUrl(), templocalFilePath, taskEntity);
            if (downloadResult) {
                if (DownloadCheckUtil.checkFileDownloadOk(taskEntity.getUrl(), templocalFilePath)) {
                    //执行文件替换操作
                    synchronized (FileManager.class) {
                        //文件不存在才执行替换操作
                        FileUtil.rename(templocalFilePath, localFilePath);
                        FileUtil.deleteFile(templocalFilePath);
                        taskEntity.getDownloadListener().onComplete(localFilePath);
                    }
                } else {
                    //下载成功，但是校验失败
                    FileUtil.deleteFile(templocalFilePath);
                    taskEntity.getDownloadListener().onError("download success but check error.");
                }
            } else {
                taskEntity.getDownloadListener().onError("download error.");
            }
        }
    }

    /**
     * 断点续传下载文件
     *
     * @param fileUrl
     * @param localFilePath
     * @return
     */
    public static boolean downloadFile(String fileUrl, String localFilePath, CDownloadTaskEntity taskEntity) {
        if (TextUtils.isEmpty(fileUrl) || TextUtils.isEmpty(localFilePath) || taskEntity == null || taskEntity.getDownloadListener() == null) {
            return false;
        }
        Log.e("HongLi", "start download file:" + fileUrl);
        File file = new File(localFilePath);
        long size = 0;
        if (file.exists()) {
            size = file.length();
        }
        URL url;
        boolean downloadSuccess = false;
        long maxSize = 0;
        RandomAccessFile out = null;
        HttpURLConnection con = null;
        try {
            url = new URL(fileUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(connectTimeOut);
            con.setReadTimeout(readTimeOut);
            // 设置下载区间
            con.setRequestProperty("range", "bytes=" + size + "-");
            con.connect();
            maxSize = con.getContentLength();
            long currentSize = 0;
            // 只要断点下载，返回的已经不是200，206
            int code = con.getResponseCode();
            if (code == 206) {
                InputStream in = con.getInputStream();
                // int serverSize = con.getContentLength();
                // 必须要使用
                out = new RandomAccessFile(file, "rw");
                out.seek(size);
                currentSize = size;
                byte[] b = new byte[bufferSize];
                int len = -1;
                while ((len = in.read(b)) != -1) {
                    if (taskEntity.isHasCancel()) {
                        Log.e("HongLi", "cancel download:" + fileUrl);
                        taskEntity.getDownloadListener().onCancel();
                        break;
                    }
                    out.write(b, 0, len);
                    currentSize += len;
                    taskEntity.getDownloadListener().onProgress(maxSize, currentSize);
                }
                out.close();
                downloadSuccess = true;
            } else {
                //不支持断点续传
                downloadSuccess = downloadFileNormal(fileUrl, localFilePath, taskEntity);
            }
            con.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            downloadSuccess = downloadFileNormal(fileUrl, localFilePath, taskEntity);
            Log.e("HongLi", "error url:" + fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            downloadSuccess = downloadFileNormal(fileUrl, localFilePath, taskEntity);
        } catch (Exception e) {
            e.printStackTrace();
            downloadSuccess = downloadFileNormal(fileUrl, localFilePath, taskEntity);
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (null != con) {
                con.disconnect();
            }
            //通过文件长度来判断下载是否成功
            if (!downloadSuccess || !file.exists() || maxSize == 0 || file.length() != maxSize) {
                downloadSuccess = false;
            }
        }
        Log.e("HongLi", "end download file:" + fileUrl);
        return downloadSuccess;
    }

    /**
     * 不支持断点续传下载文件：使用服务端缓存，不能使用断点续传功能，除非它支持
     *
     * @param fileUrl
     * @param localFilePath
     * @add by ytxu 2015-10-27
     */
    private static boolean downloadFileNormal(String fileUrl,
                                              String localFilePath, CDownloadTaskEntity taskEntity) {
        if (TextUtils.isEmpty(fileUrl) || TextUtils.isEmpty(localFilePath) || taskEntity == null || taskEntity.getDownloadListener() == null) {
            return false;
        }
        File file = new File(localFilePath);
        long size = 0;
        if (file.exists()) {
            size = file.length();
        }
        URL url;
        boolean downloadSuccess = false;
        RandomAccessFile out = null;
        HttpURLConnection con = null;
        long maxSize = 0;
        try {
            url = new URL(fileUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(connectTimeOut);
            con.setReadTimeout(readTimeOut);
            // //设置下载区间
            // con.setRequestProperty("range","bytes="+size+"-");
//            if(null != fileAttr && 0 != fileAttr.length && null != fileAttr[0]){
//                con.setIfModifiedSince(Long.parseLong(fileAttr[0].getEtag()));
//            }
            con.connect();
            maxSize = con.getContentLength();
            long currentSize = 0;
            int code = con.getResponseCode();// 只要断点下载，返回的已经不是200，206
            // if(code==206){
            if (code == 200) {
                InputStream in = con.getInputStream();
                // int serverSize = con.getContentLength();
                // 必须要使用
                out = new RandomAccessFile(file, "rw");
                out.seek(size);
                currentSize = size;
                byte[] b = new byte[bufferSize];
                int len = -1;
                while ((len = in.read(b)) != -1) {
                    if (taskEntity.isHasCancel()) {
                        Log.e("HongLi", "cancel download:" + fileUrl);
                        taskEntity.getDownloadListener().onCancel();
                        break;
                    }
                    out.write(b, 0, len);
                    currentSize += len;
                    taskEntity.getDownloadListener().onProgress(maxSize, currentSize);
                }
//                if(null != fileAttr && 0 != fileAttr.length && null != fileAttr[0]){
//                    fileAttr[0].setEtag(con.getLastModified() + "");
//                }
                out.close();
                downloadSuccess = true;
            } else {
                downloadSuccess = false;
            }

            con.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (null != con) {
                con.disconnect();
            }
            if (!downloadSuccess || !file.exists() || maxSize == 0 || file.length() != maxSize) {
                downloadSuccess = false;
            }
        }
        return downloadSuccess;
    }
}