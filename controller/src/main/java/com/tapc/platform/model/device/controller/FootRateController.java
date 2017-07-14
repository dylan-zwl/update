package com.tapc.platform.model.device.controller;

import android.os.Handler;
import android.os.Message;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.GenericMessageHandler;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.platform.model.device.controller.uart.TransferPacket;
import com.tapc.platform.model.device.controller.uart.Utility;

public class FootRateController extends GenericMessageHandler {
    private int mPaceRate;

    public FootRateController(Handler uihandler) {
        super(uihandler);
        mTransferPacket = new TransferPacket(Commands.GET_FOOT_RATE);
        getPeriodicCommander().addCommandtoList(this.toString(), mTransferPacket);
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.GET_FOOT_RATE;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        mPaceRate = Utility.getIntegerFromByteArray(packet.getData()) & 0x00FF;
    }

    public int getPaceRate() {
        return mPaceRate;
    }
}
