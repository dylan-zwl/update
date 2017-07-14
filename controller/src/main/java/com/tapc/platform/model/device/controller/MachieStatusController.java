package com.tapc.platform.model.device.controller;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.GenericMessageHandler;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.platform.model.device.controller.uart.TransferPacket;
import com.tapc.platform.model.device.controller.uart.Utility;

public class MachieStatusController extends GenericMessageHandler {
    private TransferPacket mStartCommand;
    private TransferPacket mStopCommand;
    private TransferPacket mRegisterPreStart;
    private TransferPacket mPauseCommand;
    private TransferPacket mFancontrol;
    private TransferPacket mVolumeCtl;
    private TransferPacket mEnterERP;
    private TransferPacket mGetMachineVersion;
    private TransferPacket mSetMachineParam;
    private TransferPacket mGetMachineParam;
    private int mFanSpeedLeverl;

    private String mMachineVersion;
    private VersionCallback mVersionCallback;
    private MachinePramCallback mMachinePramCallback;

    public MachieStatusController(Handler uihandler) {
        super(uihandler);

        mStartCommand = new TransferPacket(Commands.SET_MCHINE_START);
        mStopCommand = new TransferPacket(Commands.SET_MACHINE_STOP);
        mRegisterPreStart = new TransferPacket(Commands.REGISTER_PRE_START);
        mPauseCommand = new TransferPacket(Commands.SET_MACHINE_PAUSE);
        mEnterERP = new TransferPacket(Commands.ENTER_ERP);
        mFancontrol = new TransferPacket(Commands.SET_FAN_CNTRL);
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.ENTER_ERP || cmd == Commands.GET_MCB_VERSION || cmd == Commands.GET_MACHINE_PARAM;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        switch (packet.getCommand()) {
            case ENTER_ERP:
                Log.d("ERP", "Enter ERP success");
                break;
            case GET_MCB_VERSION:
                byte[] data = packet.getData();
                if (data != null && data.length > 0) {
                    String version = "";
                    for (int i = 0; i < data.length; i++) {
                        if (i == (data.length - 1)) {
                            version = version + (data[i] & 0xff);
                        } else {
                            version = version + (data[i] & 0xff) + ".";
                        }
                    }
                    mMachineVersion = version;
                    if (mVersionCallback != null) {
                        mVersionCallback.version(version);
                    }
                }
                break;
            case GET_MACHINE_PARAM:
                if (mMachinePramCallback != null) {
                    mMachinePramCallback.recvData(packet.getData());
                }
                break;
        }
    }

    public void startMachine(int speed, int incline) {
        byte[] speedDatabyte = Utility.getByteArrayFromInteger(speed, 2);
        byte[] inclineDatabyte = Utility.getByteArrayFromInteger(incline, 2);
        byte[] databyte = new byte[4];
        System.arraycopy(speedDatabyte, 0, databyte, 0, 2);
        System.arraycopy(inclineDatabyte, 0, databyte, 2, 2);
        mStartCommand.setData(databyte);
        send(mStartCommand);
    }

    public void registerPreStart() {
        send(mRegisterPreStart);
    }

    public void stopMachine(int incline) {
        mStopCommand
                .setData(Utility.getByteArrayFromInteger(incline, Commands.SET_MACHINE_STOP.getSendPacketDataSize()));
        send(mStopCommand);
    }

    public void pauseMachine() {
        send(mPauseCommand);
    }

    public void enterERP(int time) {
        mEnterERP.setData(Utility.getByteArrayFromInteger(time, Commands.ENTER_ERP.getSendPacketDataSize()));
        send(mEnterERP);
    }

    public int getFanSpeedLevel() {
        return mFanSpeedLeverl;
    }

    public void setFanSpeedLevel(int spdlvl) {
        mFanSpeedLeverl = spdlvl;
        mFancontrol.setData(Utility.getByteArrayFromInteger(mFanSpeedLeverl, Commands.SET_FAN_CNTRL
                .getSendPacketDataSize()));
        send(mFancontrol);
    }

    public void sendCtlVersionCmd(VersionCallback callback) {
        mVersionCallback = callback;
        if (mGetMachineVersion == null) {
            mGetMachineVersion = new TransferPacket(Commands.GET_MCB_VERSION);
        }
        mMachineVersion = "";
        send(mGetMachineVersion);
    }

    public String getCtlVersionValue() {
        return mMachineVersion;
    }

    public void setMachinePram(byte[] databyte) {
        if (mSetMachineParam == null) {
            mSetMachineParam = new TransferPacket(Commands.SET_MACHINE_PARAM);
        }
        mSetMachineParam.setData(databyte);
        send(mSetMachineParam);
    }

    public void getMachineParam(MachinePramCallback callback) {
        mMachinePramCallback = callback;
        if (mGetMachineParam == null) {
            mGetMachineParam = new TransferPacket(Commands.GET_MACHINE_PARAM);
        }
        send(mGetMachineParam);
    }

    public void setVolumeSatus(int volume) {
        if (mVolumeCtl == null) {
            mVolumeCtl = new TransferPacket(Commands.SET_VOLUME_CNTRL);
        }
        mVolumeCtl.setData(Utility.getByteArrayFromInteger(volume, Commands.SET_VOLUME_CNTRL.getSendPacketDataSize()));
        send(mVolumeCtl);
    }

    public void sendCommands(Commands commands, byte[] data) {
        TransferPacket command = new TransferPacket(commands);
        if (data != null) {
            command.setData(data);
        }
        send(command);
    }

    public interface VersionCallback {
        void version(String version);
    }

    public interface MachinePramCallback {
        void recvData(byte[] databytes);
    }
}
