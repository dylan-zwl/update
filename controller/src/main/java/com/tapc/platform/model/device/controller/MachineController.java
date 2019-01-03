package com.tapc.platform.model.device.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.UARTController;

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

    /**
     * 初始化
     */
    public void initController(Context context) {
        if (null != sMachineController) {
            mContext = context;
            mMessageHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                }
            };
            mMachineStatusController = new MachieStatusController(mMessageHandler);
            mHeartController = new HeartController(mMessageHandler);
            mSpeedController = new SpeedController(mMessageHandler);
            mInclineController = new InclineController(mMessageHandler);
            mFootRateController = new FootRateController(mMessageHandler);
            mHardwareStatuscontroller = new HardwareStatusController(mContext, mMessageHandler);
            mKeyboardController = new KeyboardController(mContext, mMessageHandler);
        }
    }

    /**
     * 开始
     */
    public void start() {
        mHeartController.start();
        mSpeedController.start();
        mInclineController.start();
        mFootRateController.start();
        mHardwareStatuscontroller.start();
        mMachineStatusController.start();
        mKeyboardController.start();
    }

    /**
     * 停止
     */
    public void stop() {
        mHeartController.stop();
        mSpeedController.stop();
        mInclineController.stop();
        mFootRateController.stop();
        mHardwareStatuscontroller.stop();
        mMachineStatusController.stop();
        mKeyboardController.stop();
        UARTController.getInstance().stop(true);
    }

    /**
     * ui倒计时通知mcu预开始
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
        if (mIOUpdateController == null) {
            mIOUpdateController = new IOUpdateController(mMessageHandler);
            mIOUpdateController.start();
        }
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
    public void enterErpStatus(int autoWakeupTime) {
        mMachineStatusController.enterERP(autoWakeupTime);
    }

    /**
     * 风扇
     */
    public int getFanLevel() {
        return mMachineStatusController.getFanSpeedLevel();
    }

    public void setFanLevel(int level) {
        mMachineStatusController.setFanSpeedLevel(level);
    }

    /**
     * 设置mcu蜂鸣器音量
     */
    public void setVolumeSatus(int volume) {
        mMachineStatusController.setVolumeSatus(volume);
    }

    /**
     * 设置机器参数值
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
