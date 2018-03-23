package com.tapc.update.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2017/7/19.
 */


public class CopyFileUtils {
    /**
     * 功能描述 : 根据文件路径拷贝文件
     *
     * @param originFilePath 源文件路径
     * @param targetFilePath 目标文件路径
     * @return boolean 成功true、失败false
     */
    public static boolean copyFile(String originFilePath, String targetFilePath) {
        boolean result = false;
        File originFile = new File(originFilePath);
        if (originFile == null || !originFile.exists() || TextUtils.isEmpty(targetFilePath)) {
            return result;
        }
        File targetFile = new File(targetFilePath);
        if (targetFile != null && targetFile.exists()) {
            targetFile.delete();
        }
        try {
            targetFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(originFilePath).getChannel();
            outChannel = new FileOutputStream(targetFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
//            outChannel.transferFrom(inChannel, 0, inChannel.size());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            inChannel.close();
            outChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 功能描述 : 复制整个文件夹内容
     *
     * @param originFilePath 源文件路径
     * @param targetFilePath 复制后路径
     * @return boolean
     */
    public static boolean copyFolder(String originFilePath, String targetFilePath) {
        boolean result = false;
        try {
            (new File(targetFilePath)).mkdirs();
            File originFile = new File(originFilePath);
            String[] file = originFile.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (originFilePath.endsWith(File.separator)) {
                    temp = new File(originFilePath + file[i]);
                } else {
                    temp = new File(originFilePath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    result = copyFile(temp.getAbsolutePath(), targetFilePath + "/" + file[i]);
                } else if (temp.isDirectory()) {
                    result = copyFolder(temp.getAbsolutePath(), targetFilePath + "/" + file[i]);
                }
                if (!result) {
                    break;
                }
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public interface ProgressCallback {
        void onProgress(int progress);

        void onCompeleted(boolean isSuccessed, String msg);
    }

    public static boolean copyFile(String originFilePath, String targetFilePath, ProgressCallback callback) {
        boolean result = false;
        try {
            long bytesum = 0;
            int byteread = 0;
            File originFile = new File(originFilePath);
            if (originFile.exists()) {
                File targetFile = new File(targetFilePath);
                if (targetFile != null && targetFile.exists()) {
                    targetFile.delete();
                }
                try {
                    targetFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long fileSize = originFile.length();
                int oldProgress = -1;
                InputStream inStream = new FileInputStream(originFile.getAbsoluteFile());
                FileOutputStream fs = new FileOutputStream(targetFile.getAbsoluteFile());
                byte[] buffer = new byte[1024 * 4];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                    int progress = (int) ((bytesum * 100 / fileSize));
                    if (progress != oldProgress) {
                        oldProgress = progress;
                        callback.onProgress(progress);
                    }
                }
                inStream.close();
                fs.close();
                callback.onCompeleted(true, "");
                result = true;
            } else {
                callback.onCompeleted(false, "no file");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onCompeleted(false, e.getMessage());
        }
        return result;
    }


    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    private long mCopyLength = 0;

    public boolean copyFolder(String originFilePath, String targetFilePath, ProgressCallback callback) {
        if (TextUtils.isEmpty(originFilePath)) {
            return false;
        }
        mCopyLength = 0;
        long length = 0;
        try {
            length = getFileSize(new File(originFilePath));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        boolean result = copyFolder(originFilePath, targetFilePath, length, callback);
        callback.onCompeleted(result, "");
        return result;
    }

    private boolean copyFolder(String originFilePath, String targetFilePath, long length, ProgressCallback callback) {
        boolean result = false;
        try {
            (new File(targetFilePath)).mkdirs();
            File a = new File(originFilePath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (originFilePath.endsWith(File.separator)) {
                    temp = new File(originFilePath + file[i]);
                } else {
                    temp = new File(originFilePath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(targetFilePath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 4];
                    int len;
                    int oldIndex = -1;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                        mCopyLength += len;
                        int progress = (int) ((mCopyLength * 100 / length));
                        if (progress != oldIndex) {
                            oldIndex = progress;
                            callback.onProgress(progress);
                        }
                    }
                    output.flush();
                    output.close();
                    input.close();
                } else if (temp.isDirectory()) {
                    if (!copyFolder(originFilePath + "/" + file[i], targetFilePath + "/" + file[i], length, callback)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        File flist[] = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }
}