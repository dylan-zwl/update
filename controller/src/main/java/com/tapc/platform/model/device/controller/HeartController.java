package com.tapc.platform.model.device.controller;


import android.os.Handler;
import android.os.Message;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.GenericMessageHandler;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.platform.model.device.controller.uart.TransferPacket;
import com.tapc.platform.model.device.controller.uart.Utility;

public class HeartController extends GenericMessageHandler {
    private int mHeartRate;

    public HeartController(Handler uihandler) {
        super(uihandler);
        mTransferPacket = new TransferPacket(Commands.GET_HR_HAND);
        getPeriodicCommander().addCommandtoList(this.toString(), mTransferPacket);
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.GET_HR_HAND;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        mHeartRate = Utility.getIntegerFromByteArray(packet.getData()) & 0x00FF;
    }

    public int getHeartRate() {
        return mHeartRate;
    }
}
