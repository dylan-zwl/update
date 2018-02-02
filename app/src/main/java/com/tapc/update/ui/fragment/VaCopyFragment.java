package com.tapc.update.ui.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.StatFs;
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
import com.tapc.update.ui.adpater.VaAdapter;
import com.tapc.update.ui.base.BaseRecyclerViewAdapter;
import com.tapc.update.ui.presenter.VaPresenter;
import com.tapc.update.ui.view.UpdateItem;
import com.tapc.update.utils.CopyFileUtils;
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

    @BindView(R.id.va_origin_path)
    UpdateItem mVaOriginPath;
    @BindView(R.id.va_target_path)
    UpdateItem mVaTargetPath;

    @BindView(R.id.va_file_manager)
    UpdateItem mVaFileManager;
    @BindView(R.id.va_check)
    UpdateItem mVaCheck;

    private Handler mHandler;
    private String mOriginPath;
    private String mTargetPath;
    private VaPresenter mVaPresenter;

    @Override
    public int getContentView() {
        return R.layout.fragment_vacopy;
    }

    @Override
    public void initView() {
        mHandler = new Handler();
        mTitle.setText(getString(R.string.func_vacopy));
        mStartUpdate.setText(getString(R.string.va_copy));
        mVaPresenter = new VaPresenter();

        mVaFileManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileManager();
            }
        });
        mVaCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVa();
            }
        });

        initVaPath();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        mVaOriginPath.setRightTx(mOriginPath);
        mVaTargetPath.setRightTx(mTargetPath);
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
                long originFileSize = 0;
                try {
                    originFileSize = FileUtil.getFileSize(new File(mOriginPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    long size = getFreeSizes(Config.IN_SD_FILE_PATH);
                    if (size < (originFileSize + 100 * 1024)) {
                        ShowInforUtil.send(mContext, "VA", getString(R.string.copy), false, mContext.getString(R.string
                                .no_free_size));
                        stopUpdate();
                        return;
                    }
                }

                long startTime = System.currentTimeMillis();
                boolean result = new CopyFileUtils().copyFolder(mOriginPath, mTargetPath, new CopyFileUtils
                        .ProgressCallback() {
                    @Override
                    public void onProgress(int progress) {
                        updateProgressUi(progress);
                    }

                    @Override
                    public void onCompeleted(boolean isSuccessed, String msg) {

                    }
                });
                if (result) {
                    checkVa();
                }
//                new CopyFileUtil().copyFolder(mOriginPath, mTargetPath, new CopyFileUtil.ProgressCallback() {
//                            @Override
//                            public void onProgress(int progress) {
//                                long usetime = (System.currentTimeMillis() - mCopyTime) / 1000;
//                                String time = String.format("%02d:%02d:%02d", usetime / 3600, usetime % 3600 / 60,
//                                        usetime % 60);
//                                updateProgressUi(progress);
//                                Log.d("copy progress", "" + progress + "  use time: " + time);
//                            }
//
//                            @Override
//                            public void onCompeleted(boolean isSuccessd, String msg) {
//                                ShowInforUtil.send(mContext, "VA", getString(R.string.copy), isSuccessd, "");
//                                //检测文件
//                                if (isSuccessd) {
//                                    checkVa();
//                                }
//                                stopUpdate();
//                            }
//                        }
//                );
                long usetime = (System.currentTimeMillis() - startTime) / 1000;
                Log.d("copy progress", "  use time: " + usetime);
                stopUpdate();
            }
        }).start();
    }

    @OnClick(R.id.va_file_manager)
    void openFileManager() {
        IntentUtil.startApp(mContext, "com.estrongs.android.pop");
    }

    @OnClick(R.id.va_check)
    void checkVa() {
        boolean result = mVaPresenter.check(mOriginPath, mTargetPath);
        initPlayView(mContext);
        ShowInforUtil.send(mContext, "VA", getString(R.string.check_file), result, "");
    }

//
//    private boolean copyFolder(String originFile, String targetFile) {
//        try {
//            (new File(targetFile)).mkdirs();
//            File listFile = new File(originFile);
//            final String[] file = listFile.list();
//            File temp = null;
//
//            for (int i = 0; i < file.length; i++) {
//                if (originFile.endsWith(File.separator)) {
//                    temp = new File(originFile + file[i]);
//                } else {
//                    temp = new File(originFile + File.separator + file[i]);
//                }
//                if (temp.isFile()) {
//                    mCopyFileResult = false;
//                    CopyFileUtil copyFileUtil = new CopyFileUtil();
//                    String srcPath = temp.getAbsolutePath();
//                    String destPath = targetFile + "/" + (temp.getName()).toString();
//                    copyFileUtil.start(srcPath, destPath, 5, new CopyFileUtil.CopyListener() {
//                        @Override
//                        public void copyResult(boolean isCopySuccess) {
//                            mCopyFileResult = isCopySuccess;
//                            synchronized (this) {
//
//                            }
//                        }
//
//                        @Override
//                        public void copyCount(int size) {
//                            mCopySize = mCopySize + size;
//                        }
//                    });
//                    wait();
//                    while (copyFileUtil.isFinish() == false) {
//                        SystemClock.sleep(1);
//                    }
//                    if (mCopyFileResult == false) {
//                        return false;
//                    }
//                } else if (temp.isDirectory()) {
//                    return copyFolder(originFile + "/" + file[i], targetFile + "/" + file[i]);
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            System.out.println("复制整个文件夹内容操作出错");
//            e.printStackTrace();
//            return false;
//        }
//    }

    /**
     * 功能描述 : va 播放
     *
     * @param :
     */
    @BindView(R.id.va_play_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.va_play_progress)
    SeekBar mPlayProgress;

    private ArrayList<PlayEntity> mPlayList;
    private VaAdapter mVaAdapter;
    private Disposable mDisposable;

    private void initPlayView(Context context) {
        if (mVaAdapter == null) {
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager);
            mVaAdapter = new VaAdapter(mPlayList);
            mVaAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<PlayEntity>() {
                @Override
                public void onItemClick(View view, PlayEntity playEntity) {
                    startPlay(playEntity);
                }
            });
            mRecyclerView.setAdapter(mVaAdapter);
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
        initPlayList();
    }

    private void initPlayList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPlayList = new ArrayList<PlayEntity>();
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
                mVaAdapter.notifyDataSetChanged(mPlayList);
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
