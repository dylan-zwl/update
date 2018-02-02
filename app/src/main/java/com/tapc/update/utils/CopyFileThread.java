package com.tapc.update.utils;

/**
 * Created by Administrator on 2017/7/19.
 */


import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CopyFileThread {
    private long mStartTime = 0;
    private boolean isCopyFailed = false;
    private List<CopyThread> mCopyThreads;
    private CountDownLatch mCountDownLatch;
    private long mLength;

    /**
     * 功能描述 : 复制线程
     *
     * @param : mSrcPath 原始路径
     * @param : mDestPath 目标路径
     * @param : mCopyListener 复制监听器
     */
    public boolean startCopy(String srcPath, String destPath, int threadCount) {
        if (TextUtils.isEmpty(srcPath)) {
            return false;
        }
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            return false;
        }
        long len = srcFile.length();
        mLength = len;
        int oneNum = (int) (len / threadCount);

        if (oneNum < 1024 * 1024) {
            threadCount = 1;
        }

        mStartTime = System.currentTimeMillis();

        mCountDownLatch = new CountDownLatch(threadCount);

        mCopyThreads = new ArrayList<>();
        for (int i = 0; i < threadCount - 1; i++) {
            CopyThread ct = new CopyThread(srcPath, destPath, oneNum * i, oneNum * (i + 1));
            mCopyThreads.add(ct);
            ct.start();
        }
        CopyThread ct = new CopyThread(srcPath, destPath, oneNum * (threadCount - 1), (int) len);
        mCopyThreads.add(ct);
        ct.start();

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - mStartTime;
        Log.d(destPath + " 复制完成", "花费时长：" + time);
        return !isCopyFailed;
    }

    private void stopCopyThread() {
        for (CopyThread copyThread : mCopyThreads) {
            if (copyThread != null) {
                copyThread.interrupt();
            }
            mCopyThreads = null;
        }
    }

    private class CopyThread extends Thread {
        private String mSrcPath;
        private String mDestPath;
        private int mStart, mEnd;
        private FileChannel mOutChannel;

        public CopyThread(String srcPath, String destPath, int start, int end) {
            //要复制的源文件路径
            this.mSrcPath = srcPath;
            //复制到的文件路径
            this.mDestPath = destPath;
            //复制起始位置
            this.mStart = start;
            //复制结束位置
            this.mEnd = end;
        }

        public void run() {
            String message = "";
            RandomAccessFile in = null;
            RandomAccessFile out = null;
            FileLock lock = null;
            try {
//                in = new RandomAccessFile(mSrcPath, "r");
//                out = new RandomAccessFile(mDestPath, "rw");
//                int count = 0;
//                int len = 0;
//                byte[] b = new byte[1024 * 64];
//                in.seek(mStart);    //设置读文件偏移位置
//                out.seek(mStart); //设置写文件偏移位置
//                int size = (mEnd - mStart);
//                while (((len = in.read(b)) != -1) && (count <= size)) {    //读取文件内容设置写文件停止条件
//                    out.write(b, 0, len);
//                    count = count + len;
//                }
//                in.close();
//                out.close();

                in = new RandomAccessFile(mSrcPath, "r");
                out = new RandomAccessFile(mDestPath, "rw");

                in.seek(mStart);
                out.seek(mStart);

                FileChannel inChannel = in.getChannel();
                mOutChannel = out.getChannel();

                //锁住需要操作的区域
                int size = (mEnd - mStart);
                lock = mOutChannel.lock(mStart, size, false);
//                inChannel.transferTo(mStart, size, mOutChannel);
                mOutChannel.transferFrom(inChannel, mStart, size);
            } catch (Exception e) {
                e.printStackTrace();
                isCopyFailed = true;
                stopCopyThread();
            }
            try {
                if (lock != null) {
                    lock.release();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        public long getCopySize() {
            if (mOutChannel != null) {
                try {
                    return mOutChannel.size();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
    }

    public long getLength() {
        return mLength;
    }

    public long getCopySize() {
        long size = 0;
        if (mCopyThreads != null) {
            for (CopyThread copyThread : mCopyThreads) {
                size = size + copyThread.getCopySize();
            }
        }
        return size;
    }
}
