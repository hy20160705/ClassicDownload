package com.zero.cdownload.entity;

import com.zero.cdownload.manager.download.FileManager;

/**
 * @Name: ClassicDownload
 * @Description: 描述信息
 * @Author: Created by heyong on 6/22/21
 */
public class DownloadInfo {
    private long totalSize;
    private long currentSize;
    private String url;
    private String filePath;
    private @FileManager.DownloadSate
    int downloadSate;

    public DownloadInfo(long totalSize, long currentSize, String url, String filePath, @FileManager.DownloadSate int downloadSate) {
        this.totalSize = totalSize;
        this.currentSize = currentSize;
        this.url = url;
        this.filePath = filePath;
        this.downloadSate = downloadSate;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }


    public String getFilePath() {
        return filePath;
    }

    public int getDownloadSate() {
        return downloadSate;
    }

    public String getUrl() {
        return url;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDownloadSate(@FileManager.DownloadSate int downloadSate) {
        this.downloadSate = downloadSate;
    }

    public static class Builder {
        private long totalSize;
        private long currentSize;
        private String url;
        private String filePath;
        private @FileManager.DownloadSate
        int downloadSate;

        public Builder setTotalSize(long totalSize) {
            this.totalSize = totalSize;
            return this;
        }

        public Builder setCurrentSize(long currentSize) {
            this.currentSize = currentSize;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setDownloadSate(@FileManager.DownloadSate int downloadSate) {
            this.downloadSate = downloadSate;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public DownloadInfo build() {
            return new DownloadInfo(totalSize, currentSize, url, filePath, downloadSate);
        }
    }
}
