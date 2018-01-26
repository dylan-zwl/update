package com.tapc.update.ui.fragment.vacopy;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tapc.platform.model.vaplayer.PlayEntity;
import com.tapc.platform.model.vaplayer.VaPlayer;
import com.tapc.platform.model.vaplayer.ValUtil;
import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.adpater.GalleryAdapter;
import com.tapc.update.ui.fragment.BaseFragment;
import com.tapc.update.ui.view.UpdateItem;
import com.tapc.update.utils.CopyFileUtil;
import com.tapc.update.utils.FileUtil;
import com.tapc.update.utils.IntentUtil;
import com.tapc.update.utils.ShowInforUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/7/10.
 */

public class VaCopyFragment extends BaseFragment {
    @BindView(R.id.func_name_tx)
    TextView mTitle;
    @BindView(R.id.func_start_btn)
    Button mStartUpdate;

    @BindView(R.id.va_from_path)
    UpdateItem mVaFromPath;
    @BindView(R.id.va_target_path)
    UpdateItem mVaTargetPath;

    private Handler mHandler;
    private String mOriginPath;
    private String mTargetPath;
    private long mOriginFileSize;
    private boolean mCopyFileResult;
    private long mCopySize;
    private long mCopyTime;

    @Override
    public int getContentView() {
        return R.layout.fragment_vacopy;
    }

    @Override
    public void initView() {
        mHandler = new Handler();
        mTitle.setText(getString(R.string.func_vacopy));
        mStartUpdate.setText(getString(R.string.va_copy));

        initVaPath();

        mVaFromPath.setRightTx(mOriginPath);
        mVaTargetPath.setRightTx(mTargetPath);

        initPlayView(mContext);
    }

    private void initVaPath() {
        String filePath = Config.MOUNTED_PATH + Config.SAVEFILE_PATH;
        String vaFileName = FileUtil.getFilename(filePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith(".va") || name.endsWith("va")) {
                    return true;
                }
                return false;
            }
        });

        if (TextUtils.isEmpty(vaFileName)) {
            vaFileName = ".va";
        }

        mOriginPath = filePath + "/" + vaFileName;
        mTargetPath = Config.IN_SD_FILE_PATH + Config.SAVEFILE_PATH + "/" + vaFileName;
    }

    @OnClick(R.id.func_start_btn)
    void startCopy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mOriginPath) || !new File(mOriginPath).exists()) {
                    ShowInforUtil.send(mContext, "VA", getString(R.string.copy), false, mContext.getString(R.string
                            .no_file));
                    return;
                }

                startUpdate();

                FileUtil.RecursionDeleteFile(new File(mTargetPath));
                mOriginFileSize = 0;
                try {
                    mOriginFileSize = FileUtil.getFileSize(new File(mOriginPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    long size = getFreeSizes(Config.IN_SD_FILE_PATH);
                    if (size < (mOriginFileSize + 100 * 1024)) {
                        ShowInforUtil.send(mContext, "VA", getString(R.string.copy), false, mContext.getString(R.string
                                .no_free_size));
                        stopUpdate();
                        return;
                    }
                }

                mCopyTime = System.currentTimeMillis();
                new FileUtil().copyFolder(mOriginPath, mTargetPath, new FileUtil.ProgressCallback() {
                            @Override
                            public void onProgress(int progress) {
                                long usetime = (System.currentTimeMillis() - mCopyTime) / 1000;
                                String time = String.format("%02d:%02d:%02d", usetime / 3600, usetime % 3600 / 60,
                                        usetime % 60);
                                updateProgressUi(progress);
                                Log.d("copy progress", "" + progress + "  use time: " + time);
                            }

                            @Override
                            public void onCompeleted(boolean isSuccessd, String msg) {
                                ShowInforUtil.send(mContext, "VA", getString(R.string.copy), isSuccessd, "");
                                //检测文件
                                if (isSuccessd) {
                                    checkVa();
                                }
                                stopUpdate();
                            }
                        }
                );
            }
        }).start();
    }

    @OnClick(R.id.va_file_manager)
    void openFileManager() {
        IntentUtil.startApp(mContext, "com.estrongs.android.pop");
    }

    @OnClick(R.id.va_check)
    void checkVa() {
        boolean result = check(mOriginPath, mTargetPath);
        initPlayView(mContext);
        ShowInforUtil.send(mContext, "VA", getString(R.string.check_file), result, "");
    }

    private boolean check(String originFile, String targetFile) {
        File listFile = new File(originFile);
        final String[] file = listFile.list();
        File temp = null;
        for (int i = 0; i < file.length; i++) {
            if (originFile.endsWith(File.separator)) {
                temp = new File(originFile + file[i]);
            } else {
                temp = new File(originFile + File.separator + file[i]);
            }
            if (temp.isFile()) {
                File checkFile = new File(targetFile + "/" + (temp.getName()).toString());
                if (checkFile.exists()) {
                    if (checkFile.length() == temp.length()) {
                        return true;
                    }
                }
                Log.d("check file fail:", "" + checkFile.getAbsoluteFile());
                return false;
            } else if (temp.isDirectory()) {
                return check(originFile + "/" + file[i], targetFile + "/" + file[i]);
            }
        }
        return true;
    }

    private boolean copyFolder(String originFile, String targetFile) {
        try {
            (new File(targetFile)).mkdirs();
            File listFile = new File(originFile);
            final String[] file = listFile.list();
            File temp = null;

            for (int i = 0; i < file.length; i++) {
                if (originFile.endsWith(File.separator)) {
                    temp = new File(originFile + file[i]);
                } else {
                    temp = new File(originFile + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    mCopyFileResult = false;
                    CopyFileUtil copyFileUtil = new CopyFileUtil();
                    String srcPath = temp.getAbsolutePath();
                    String destPath = targetFile + "/" + (temp.getName()).toString();
                    copyFileUtil.start(srcPath, destPath, 5, new CopyFileUtil.CopyListener() {
                        @Override
                        public void copyResult(boolean isCopySuccess) {
                            mCopyFileResult = isCopySuccess;
                            synchronized (this) {

                            }
                        }

                        @Override
                        public void copyCount(int size) {
                            mCopySize = mCopySize + size;
                        }
                    });
                    wait();
                    while (copyFileUtil.isFinish() == false) {
                        SystemClock.sleep(1);
                    }
                    if (mCopyFileResult == false) {
                        return false;
                    }
                } else if (temp.isDirectory()) {
                    return copyFolder(originFile + "/" + file[i], targetFile + "/" + file[i]);
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
            return false;
        }
    }


    /*
    *
    * va 列表显示
    *
    * */
    @BindView(R.id.va_play_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.va_play_progress)
    SeekBar mPlayProgress;

    private ArrayList<PlayEntity> mPlayList;
    private GalleryAdapter mGalleryAdapter;
    private Disposable mDisposable;

    private void initPlayView(Context context) {
        if (mGalleryAdapter == null) {
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager);
            mGalleryAdapter = new GalleryAdapter(context, mPlayList);
            mGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener<PlayEntity>() {
                @Override
                public void onItemClick(View view, PlayEntity playEntity) {
                    startPlay(playEntity);
                }
            });
            mRecyclerView.setAdapter(mGalleryAdapter);
            mPlayProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mPlayer != null) {
                        mPlayer.setSeekTo(seekBar.getProgress());
                    }
                }
            });
        }

        mPlayList = new ArrayList<PlayEntity>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<PlayEntity> playList1 = ValUtil.getValList(mTargetPath);
                mPlayList.addAll(playList1);
                playListChange();
            }
        }).start();
    }

    private void playListChange() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mGalleryAdapter.notifyDataSetChanged(mPlayList);
            }
        });
    }

    @BindView(R.id.va_play_sv)
    SurfaceView mSurfaceView;
    @BindView(R.id.va_play_status)
    Button mPlayBtn;
    @BindView(R.id.va_play_time)
    TextView mPlayTime;
    private VaPlayer mPlayer;

    private void startPlay(PlayEntity playEntity) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer = null;
        }
        mPlayer = new VaPlayer(mSurfaceView.getHolder());
        mPlayer.init();
        mPlayer.setBackMusicVisibility(false);
        mPlayer.setPlayerListener(new VaPlayer.PlayerListener() {
            @Override
            public void setIncline(int videoIncline) {
            }

            @Override
            public void setPlaySpeed(MediaPlayer player, int videoSpeed) {
            }

            @Override
            public void error(String text) {
            }

            @Override
            public void videoMessage(String text) {
            }

            @Override
            public void startPlayVideo(MediaPlayer mediaPlayer) {
                mPlayProgress.setProgress(0);
                mPlayProgress.setMax(mediaPlayer.getDuration());
                int playTime = mediaPlayer.getDuration() / 1000;
                String time = String.format("%02d:%02d:%02d", playTime / 3600, playTime % 3600 / 60, playTime % 60);
                mPlayTime.setText(time);
            }
        });
        mPlayer.start(playEntity);
        mPlayBtn.setBackgroundResource(R.drawable.btn_va_play_n);
    }

    @OnClick(R.id.va_play_status)
    void playStatus() {
        if (mPlayer == null) {
            return;
        }
        if (mPlayer.isPause()) {
            mPlayer.setPause(false);
            mPlayBtn.setBackgroundResource(R.drawable.btn_va_play_n);
        } else {
            mPlayer.setPause(true);
            mPlayBtn.setBackgroundResource(R.drawable.btn_va_play_y);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getFreeSizes(String path) {
        StatFs fileStats = new StatFs(path);
        fileStats.restat(path);
        return fileStats.getFreeBlocksLong() * fileStats.getBlockSizeLong();
    }
}
