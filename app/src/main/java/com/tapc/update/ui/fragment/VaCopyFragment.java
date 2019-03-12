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
import com.tapc.update.ui.presenter.CopyFilePresenter;
import com.tapc.update.ui.view.UpdateItem;
import com.tapc.update.utils.CopyFileUtils;
import com.tapc.update.utils.FileUtil;
import com.tapc.update.utils.IntentUtil;
import com.tapc.update.utils.RxjavaUtils;
import com.tapc.update.utils.ShowInforUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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

    private Disposable mDisposable;
    private Handler mHandler;
    private String mOriginPath;
    private String mTargetPath;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlayView(mContext);
    }

    private void initVaPath() {
        mOriginPath = Config.VA_ORIGIN_PATH;
        mTargetPath = Config.VA_TARGET_PATH;
        mVaOriginPath.setRightTx(mOriginPath);
        mVaTargetPath.setRightTx(mTargetPath);
    }

    @OnClick(R.id.func_start_btn)
    void startCopy() {
        mDisposable = RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                if (TextUtils.isEmpty(mOriginPath) || !new File(mOriginPath).exists()) {
                    ShowInforUtil.send(mContext, "VA", getString(R.string.copy), false, mContext.getString(R.string
                            .no_file));
                    return;
                }

                //先检测是否已复制
                if (CopyFilePresenter.check(mOriginPath, mTargetPath)) {
                    ShowInforUtil.send(mContext, "VA", getString(R.string.check_file), true, "");
                    return;
                }

                //开始复制
                startUpdate();
                FileUtil.RecursionDeleteFile(new File(mTargetPath));
                long originFileSize = 0;
                try {
                    originFileSize = FileUtil.getFileSize(new File(mOriginPath));
                } catch (Exception error) {
                    error.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    long size = getFreeSizes(Config.SAVEFILE_TARGET_PATH);
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
                long usetime = (System.currentTimeMillis() - startTime) / 1000;
                Log.d("copy progress", "  use time: " + usetime);
                stopUpdate();
                e.onComplete();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {

            }
        }, null);
    }

    @OnClick(R.id.va_file_manager)
    void openFileManager() {
        try {
            if (Config.DEVICE_TYPE == Config.DeviceType.RK3399) {
                IntentUtil.startApp(mContext, "com.android.rk");
            } else {
                IntentUtil.startApp(mContext, "com.estrongs.android.pop");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.va_check)
    void checkVa() {
        boolean result = CopyFilePresenter.check(mOriginPath, mTargetPath);
        initPlayView(mContext);
        ShowInforUtil.send(mContext, "VA", getString(R.string.check_file), result, "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxjavaUtils.dispose(mDisposable);
        mDisposable = null;
        stopPlay();
    }

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
        stopPlay();
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

    public void stopPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer = null;
        }
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
