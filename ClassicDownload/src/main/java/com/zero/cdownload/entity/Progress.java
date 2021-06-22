package com.zero.cdownload.entity;

import com.zero.cdownload.util.Utils;

/**
 * @Name: ClassicDownload
 * @Description: 描述信息
 * @Author: Created by heyong on 6/21/21
 */
public class Progress {
    private long downloadSize;
    private long totalSize;

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getDownloadSizeStr() {
        return Utils.formatSize(downloadSize);
    }

    public String getTotalSizeStr() {
        return Utils.formatSize(totalSize);
    }

}
