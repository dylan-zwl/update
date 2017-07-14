package com.tapc.platform.model.device.controller;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.GenericMessageHandler;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.platform.model.device.controller.uart.TransferPacket;
import com.tapc.platform.model.device.controller.uart.Utility;

public class InclineController extends GenericMessageHandler {
    private static final String INCLINECAL_FINISH = "inclinecalfinish";

    private TransferPacket mSetInclinePacket;
    private TransferPacket mSetInclinecalPacket;
    private TransferPacket mGetInclineCalFinish;
    private TransferPacket mGetInclinePacket;

    private int mIncline;
    private int mInclineCalStatus;

    public InclineController(Handler uihandler) {
        super(uihandler);

        mGetInclinePacket = new TransferPacket(Commands.GET_ADC_CURRENT);
        mSetInclinePacket = new TransferPacket(Commands.SET_ADC_TARGET);
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.GET_ADC_CURRENT || cmd == Commands.GET_INCLNE_CAL_FINISH;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        int data = Utility.getIntegerFromByteArray(packet.getData());
        switch (packet.getCommand()) {
            case GET_ADC_CURRENT:
                mIncline = data;
                break;
            case GET_INCLNE_CAL_FINISH:
                mInclineCalStatus = data;
                break;
        }
    }

    public void startInclinecal() {
        mInclineCalStatus = -1;
        if (mSetInclinecalPacket == null) {
            mSetInclinecalPacket = new TransferPacket(Commands.SET_INCLNE_CAL);
            mGetInclineCalFinish = new TransferPacket(Commands.GET_INCLNE_CAL_FINISH);
        }
        getPeriodicCommander().addCommandtoList(INCLINECAL_FINISH, mGetInclineCalFinish);
        send(mSetInclinecalPacket);
    }

    public void stopInclinecal() {
        getPeriodicCommander().removeCommandFromList(INCLINECAL_FINISH);
    }

    public int getIncline() {
        return mIncline;
    }

    public void setIncline(int incline) {
        Log.d("set incline", "" + incline);
        mSetInclinePacket.setData(Utility.getByteArrayFromInteger(incline, Commands.SET_ADC_TARGET
                .getSendPacketDataSize()));
        send(mSetInclinePacket);
    }

    public int getInclinecalStatus() {
        return mInclineCalStatus;
    }
}
