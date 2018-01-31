package com.tapc.update.ui.presenter;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/26.
 */

public class UpdateInfor implements Serializable {
    public enum UpdateType {
        NETWORK,
        LOCAL
    }

    public enum FileType {
        APP,
        MCU,
        OS,
    }

    private UpdateType updateType;
    private FileType fileType;

    private String path;
    private String savePath;
    private String fileUrl;

    private String fileName;
    private String packageName;

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}