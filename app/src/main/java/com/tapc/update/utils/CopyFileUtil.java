package com.tapc.update.utils;

/**
 * Created by Administrator on 2017/7/19.
 */


import java.io.File;

public class CopyFileUtil {
    private long mCopySize;
    private int mOldProgress;
    private boolean isCopySuccess;
    private String mErrorMessage = "";

    /**
     * 功能描述 : 复制文件监听器
     */
    public interface ProgressCallback {
        void onProgress(int progress);

        void onCompeleted(boolean isSuccessd, String msg);
    }

    private synchronized boolean copyFolder(String originFile, String targetFile, long originFileSize, final
    ProgressCallback callback) throws Exception {
        new File(targetFile).mkdirs();
        File listFile = new File(originFile);
        final String[] file = listFile.list();
        if (file == null && file.length <= 0) {
            return true;
        }

        File tempFile = null;
        for (int i = 0; i < file.length; i++) {
            if (originFile.endsWith(File.separator)) {
                tempFile = new File(originFile + file[i]);
            } else {
                tempFile = new File(originFile + File.separator + file[i]);
            }
            if (tempFile.isFile()) {
                boolean result = copyFile(tempFile, targetFile, originFileSize, callback);
                if (!result) {
                    return false;
                }
            } else if (tempFile.isDirectory()) {
                copyFolder(originFile + "/" + file[i], targetFile + "/" + file[i], originFileSize, callback);
            }
        }
        return true;
    }

    private synchronized boolean copyFile(File originFile, final String targetPath, final long originFileSize, final
    ProgressCallback callback) {
        isCopySuccess = false;
        String srcPath = originFile.getAbsolutePath();
        String destPath = targetPath + "/" + (originFile.getName()).toString();
        isCopySuccess = new CopyFileThread().startCopy(srcPath, destPath, 5);
        final CopyFileThread copyFileThread = new CopyFileThread();
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                long size = mCopySize + copyFileThread.getCopySize();
//                int progress = (int) (size * 100 / originFileSize);
//                if (mOldProgress != progress) {
//                    mOldProgress = progress;
//                    callback.onProgress(progress);
//                }
//            }
//        }, 50, 500);
//        timer.cancel();
//        isCopySuccess = copyFileThread.startCopy(srcPath, destPath, 5);

        mCopySize = mCopySize + copyFileThread.getLength();
        return isCopySuccess;
    }

    /**
     * 功能描述 :  复制文件
     *
     * @param :
     */
    public synchronized void copyFile(final String originFile, final String targetFile, final ProgressCallback
            callback) {
        try {
            File file = new File(originFile);
            if (file == null || !file.exists()) {
                callback.onCompeleted(false, "no file");
                return;
            }
            if (file.isFile()) {
                callback.onCompeleted(false, "not a file");
                return;
            }
            mCopySize = 0;
            long fileSize = FileUtil.getFileSize(file);
            mOldProgress = -1;
            mErrorMessage = "";

            boolean result = copyFile(file, targetFile, fileSize, callback);
            if (result) {
                callback.onCompeleted(true, "");
            } else {
                callback.onCompeleted(true, mErrorMessage);
            }
        } catch (Exception e) {
            callback.onCompeleted(false, e.getMessage());
        }
    }

    /**
     * 功能描述 :  复制文件夹
     *
     * @param :
     */
    public synchronized void copyFolder(final String originFile, final String targetFile, final ProgressCallback
            callback) {
        try {
            mCopySize = 0;
            long fileSize = FileUtil.getFileSize(new File(originFile));
            mOldProgress = -1;
            mErrorMessage = "";

            boolean result = copyFolder(originFile, targetFile, fileSize, callback);
            if (result) {
                callback.onCompeleted(true, "");
            } else {
                callback.onCompeleted(true, mErrorMessage);
            }
        } catch (Exception e) {
            callback.onCompeleted(false, e.getMessage());
        }
    }
}