package com.tapc.update.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2017/6/28.
 */
public class FileCoperUtils {
    private static final String ORIGIN_FILE_MODE = "r";
    private static final String TARGET_FILE_MODE = "rw";

    private String originFileName;
    private String targetFileName;

    private RandomAccessFile originFile;
    private RandomAccessFile targetFile;

    private static long startTime = 0;

    private int threadCount;
    private static int totalThreadCount = 0;
    private static int executedCount = 0;

    public FileCoperUtils() {
        this.threadCount = 1;
        totalThreadCount = this.threadCount;
    }

    public FileCoperUtils(String originFile, String targetFile) {
        try {
            this.originFileName = originFile;
            this.targetFileName = targetFile;
            this.originFile = new RandomAccessFile((originFile), ORIGIN_FILE_MODE);
            this.targetFile = new RandomAccessFile((targetFile), TARGET_FILE_MODE);
            this.threadCount = 1;
            totalThreadCount = this.threadCount;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public FileCoperUtils(String originFile, String targetFile, int threadCount) {
        try {
            this.originFileName = originFile;
            this.targetFileName = targetFile;
            this.originFile = new RandomAccessFile((originFile), ORIGIN_FILE_MODE);
            this.targetFile = new RandomAccessFile((targetFile), TARGET_FILE_MODE);
            this.threadCount = 1;
            totalThreadCount = this.threadCount;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void init(String originFile, String targetFile) throws Exception {
        this.originFileName = originFile;
        this.targetFileName = targetFile;
        this.originFile = new RandomAccessFile((originFile), ORIGIN_FILE_MODE);
        this.targetFile = new RandomAccessFile((targetFile), TARGET_FILE_MODE);
        this.threadCount = 1;
        totalThreadCount = this.threadCount;
    }


    public void init(String originFile, String targetFile, int threadCount) throws Exception {
        this.originFileName = originFile;
        this.targetFileName = targetFile;
        this.originFile = new RandomAccessFile((originFile), ORIGIN_FILE_MODE);
        this.targetFile = new RandomAccessFile((targetFile), TARGET_FILE_MODE);
        this.threadCount = threadCount;
        totalThreadCount = this.threadCount;
    }


    public void init(RandomAccessFile originFile, RandomAccessFile targetFile) throws Exception {
        this.originFile = originFile;
        this.targetFile = targetFile;
        this.threadCount = 1;
        totalThreadCount = this.threadCount;
    }


    public void init(RandomAccessFile originFile, RandomAccessFile targetFile, int threadCount) throws Exception {
        this.originFile = originFile;
        this.targetFile = targetFile;
        this.threadCount = threadCount;
        totalThreadCount = this.threadCount;
    }


    public static synchronized void finish() {
        executedCount++;
        System.out.println("总线程【" + totalThreadCount + "】,已经完成【" + executedCount + "】个线程的复制！！！");
        if (totalThreadCount == executedCount) {
            executedCount = 0;
            long endTime = System.currentTimeMillis();
            System.out.println("花费时长：" + (endTime - startTime));
            System.out.println("所有【" + totalThreadCount + "】线程复制完成！！！");
        }
    }


    public void start() throws Exception {
        if (this.originFile.length() == 0)
            return;
        if (this.threadCount == 0)
            this.threadCount = 1;
        // 设置目标文件大小
        this.targetFile.setLength(this.originFile.length());
        this.targetFile.seek(0);
        this.originFile.seek(0);
        startTime = System.currentTimeMillis();
        System.out.println(this.originFile.length());
        // 把文件分块，每一块有一对值：当前块在文件中的起始位置和结束位置
        long[][] splits = new long[this.threadCount][2];
        long originFileLength = this.originFile.length();
        int startPos = 0;
        for (int i = 0; i < this.threadCount; i++) {
            splits[i][0] = 0;
            splits[i][1] = 0;
            if (i == 0) {
                splits[i][0] = 0;
                splits[i][1] = originFileLength / this.threadCount;

            } else if (i == this.threadCount - 1) {
                // 注意：此处不能加1，如果加1，线程多文件就会出现乱码
                // splits[i][0] = startPos + 1;
                splits[i][0] = startPos;
                splits[i][1] = originFileLength;
            } else {
                // 注意：此处不能加1，如果加1，线程多文件就会出现乱码
                // splits[i][0] = startPos + 1;
                splits[i][0] = startPos;
                splits[i][1] = startPos + originFileLength / this.threadCount;
            }
            startPos += originFileLength / this.threadCount;
            // System.out.println(splits[i][0] + " " + splits[i][1]);

            Coper fc = new Coper("thread-" + i);
            fc.init(this.originFile, this.targetFile, splits[i][0], splits[i][1]);
            fc.setOriginFileName(this.originFileName);
            fc.setTargetFileName(this.targetFileName);
            fc.start();
        }
    }


    public void startNew() throws Exception {
        if (this.originFile.length() == 0)
            return;
        // 设置目标文件大小
        this.targetFile.setLength(this.originFile.length());
        this.targetFile.seek(0);
        this.originFile.seek(0);

        long startPosition;
        long endPosition;
        long block = this.originFile.length() / 1029;

        if (block <= 1)
            this.threadCount = 1;

        for (int i = 0; i < this.threadCount; i++) {
            // 定义每次转移的长度
            startPosition = i * 1029 * (block / this.threadCount);
            endPosition = (i + 1) * 1029 * (block / this.threadCount);
            if (i == (this.threadCount - 1))
                endPosition = this.originFile.length();
            Coper fc = new Coper("thread-" + i);
            fc.init(this.originFile, this.targetFile, startPosition, endPosition);
            fc.setOriginFileName(this.originFileName);
            fc.setTargetFileName(this.targetFileName);
            fc.start();
        }
    }

    private class Coper extends Thread {


        private String originFileName;


        private String targetFileName;

        private RandomAccessFile originFile;


        private RandomAccessFile targetFile;


        private String threadId;


        private long startPosition;


        private long endPosition;


        private long blockCapacity;


        public void setOriginFileName(String originFileName) {
            this.originFileName = originFileName;
        }


        public void setTargetFileName(String targetFileName) {
            this.targetFileName = targetFileName;
        }

        public Coper(String threadId) {
            this.threadId = threadId;
        }


        public void init(RandomAccessFile originFile, RandomAccessFile targetFile, long startPosition, long
                endPosition) throws Exception {
            this.originFile = originFile;
            this.targetFile = targetFile;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.blockCapacity = this.endPosition - this.startPosition;
        }

        public void run() {
            // System.out.println(this.threadId + " 启动，开始复制文件【" +
            // this.originFileName + "】中的文件块【" + this.startPosition + "," +
            // this.endPosition + "】到目标文件【" + this.targetFileName + "】中...");
            synchronized (this.originFile) {
                try {
                    // 记录当前拷贝的字节数
                    int copyCount = 0;
                    // 数据拷贝的启示偏移量
                    long offSet = this.startPosition;
                    byte[] b = new byte[16 * 1024 * 1024];
                    // 动态设置一次读取的字节数缓冲
                    long blockSize = 0;
                    while (copyCount < this.blockCapacity) {
                        this.originFile.seek(offSet);
                        if (this.blockCapacity - copyCount > 16 * 1024 * 1024)
                            blockSize = 16 * 1024 * 1024;
                        else
                            blockSize = this.blockCapacity - copyCount;
                        if (blockSize > this.blockCapacity - copyCount)
                            blockSize = this.blockCapacity - copyCount;
                        int count = this.originFile.read(b, 0, (int) blockSize);
                        synchronized (this.targetFile) {
                            try {
                                if (copyCount == 0)
                                    this.targetFile.seek(offSet);
                                else
                                    this.targetFile.seek(offSet + 1);

                                this.targetFile.write(b, 0, count);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // 增加拷贝的字节数
                        copyCount += count;
                        // 拷贝其实【偏移量下移
                        offSet += count;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // System.out.println(this.threadId + " 复制文件【" + this.originFileName
            // + "】中的文件块【" + this.startPosition + "," + this.endPosition +
            // "】到目标文件【" + this.targetFileName + "】完成!");

            // 通知主线程，当前线程完成复制工作
            finish();
        }
    }
}