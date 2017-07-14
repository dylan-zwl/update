package com.tapc.platform.model.device.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.tapc.platform.model.device.controller.uart.Commands;

@SuppressLint("HandlerLeak")
public final class MachineController {
    private static MachineController sMachineController;
    private Context mContext;
    private Handler mMessageHandler;
    private MachieStatusController mMachineStatusController;
    private KeyboardController mKeyboardController;
    private HeartController mHeartController;
    private SpeedController mSpeedController;
    private InclineController mInclineController;
    private IOUpdateController mIOUpdateController;
    private FootRateController mFootRateController;
    private HardwareStatusController mHardwareStatuscontroller;

    public static MachineController getInstance() {
        if (null == sMachineController) {
            sMachineController = new MachineController();
        }
        return sMachineController;
    }

    public void initController() {
        if (null != sMachineController) {
            mMessageHandler = new Handler();
            mMachineStatusController = new MachieStatusController(mMessageHandler);
            mHeartController = new HeartController(mMessageHandler);
            mSpeedController = new SpeedController(mMessageHandler);
            mInclineController = new InclineController(mMessageHandler);
            mFootRateController = new FootRateController(mMessageHandler);
            mHardwareStatuscontroller = new HardwareStatusController(mContext, mMessageHandler);
            mKeyboardController = new KeyboardController(mContext, mMessageHandler);
        }
    }

    public void setReceiveBroadcast(Context context) {
        mContext = context;
    }

    public void start() {
        mHeartController.start();
        mSpeedController.start();
        mInclineController.start();
        mFootRateController.start();
        mHardwareStatuscontroller.start();
        mMachineStatusController.start();
        mKeyboardController.start();
    }

    public void stop() {
        mHeartController.stop();
        mSpeedController.stop();
        mInclineController.stop();
        mFootRateController.stop();
        mHardwareStatuscontroller.stop();
        mMachineStatusController.stop();
        mKeyboardController.stop();
    }

    /**
     * ui倒计时提醒mcu
     */
    public void registerPreStart() {
        mMachineStatusController.registerPreStart();
    }

    /**
     * 下控状态控制
     */
    public void startMachine(int speed, int incline) {
        mMachineStatusController.startMachine(speed, incline);
    }

    public void stopMachine(int incline) {
        mMachineStatusController.stopMachine(incline);
    }

    public void pauseMachine() {
        mMachineStatusController.pauseMachine();
    }

    /**
     * 坡度校准
     */
    public void startInclinecal() {
        mInclineController.startInclinecal();
    }

    public void stopInclinecal() {
        mInclineController.stopInclinecal();
    }

    public int getInclinecalStatus() {
        return mInclineController.getInclinecalStatus();
    }

    /**
     * 坡度
     */
    public int getIncline() {
        return mInclineController.getIncline();
    }

    public void setIncline(int incline) {
        mInclineController.setIncline(incline);
    }

    /**
     * 速度
     */
    public int getSpeed() {
        return mSpeedController.getSpeed();
    }

    public void setSpeed(int speed) {
        mSpeedController.setSpeed(speed);
    }

    /**
     * 步数检测
     */
    public int getPaceRate() {
        return mFootRateController.getPaceRate();
    }

    /**
     * 心跳检测
     */
    public int getHeartRate() {
        return mHeartController.getHeartRate();
    }

    /**
     * 错误码
     */
    public int getErrorCode() {
        return mHardwareStatuscontroller.getErrorCode();
    }

    /**
     * 安全锁状态
     */
    public int getSafeKeyStatus() {
        return mHardwareStatuscontroller.getSafeKeyStatus();
    }

    /**
     * mcu升级
     */
    public void updateMCU(String filePath, IOUpdateController.IOUpdateListener listener) {
        mIOUpdateController = new IOUpdateController(mMessageHandler);
        mIOUpdateController.start();
        mIOUpdateController.updateIO(filePath, listener);
    }

    public void stopIOUpdateController() {
        if (mIOUpdateController != null) {
            mIOUpdateController.stop();
            mIOUpdateController = null;
        }
    }

    /**
     * 获取mcu版本号
     */
    public void sendCtlVersionCmd(MachieStatusController.VersionCallback callback) {
        mMachineStatusController.sendCtlVersionCmd(callback);
    }

    public String getCtlVersionValue() {
        return mMachineStatusController.getCtlVersionValue();
    }

    /**
     * 进入待机
     */
    public void enterErpStatus(int delayTime) {
        mMachineStatusController.enterERP(delayTime);
    }

    /**
     * 风扇
     */
    public int getFanLevel() {
        return mMachineStatusController.getFanSpeedLevel();
    }

    public void setFanLevel(int spdlvl) {
        mMachineStatusController.setFanSpeedLevel(spdlvl);
    }

    /**
     * 设置mcu蜂鸣器音量
     */
    public void setVolumeSatus(int volume) {
        mMachineStatusController.setVolumeSatus(volume);
    }

    /**
     * 设置机器轮径值
     */
    public void setMachinePram(byte[] databyte) {
        mMachineStatusController.setMachinePram(databyte);
    }

    public void getMachinePram(MachieStatusController.MachinePramCallback callback) {
        mMachineStatusController.getMachineParam(callback);
    }

    /**
     * 发送命令
     */
    public void sendCommands(Commands commands, byte[] data) {
        mMachineStatusController.sendCommands(commands, data);
    }
}
