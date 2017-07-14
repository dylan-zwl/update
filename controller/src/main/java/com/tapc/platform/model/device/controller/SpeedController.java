package com.tapc.platform.model.device.controller;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.GenericMessageHandler;
import com.tapc.platform.model.device.controller.uart.ICommunicationPacket;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.platform.model.device.controller.uart.TransferPacket;
import com.tapc.platform.model.device.controller.uart.Utility;

public class SpeedController extends GenericMessageHandler {
    private ICommunicationPacket mSetSpeedPacket;

    private int mSpeed;

    public SpeedController(Handler uihandler) {
        super(uihandler);

        mTransferPacket = new TransferPacket(Commands.GET_RPM_CURRENT);
        mSetSpeedPacket = new TransferPacket(Commands.SET_RPM_TARGET);
        getPeriodicCommander().addCommandtoList(this.toString(), mTransferPacket);
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.GET_RPM_CURRENT;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        int data = Utility.getIntegerFromByteArray(packet.getData());
        switch (packet.getCommand()) {
            case GET_RPM_CURRENT:
                mSpeed = data;
                break;
        }
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        Log.d("set speed", "" + speed);
        mSetSpeedPacket.setData(Utility.getByteArrayFromInteger(speed, Commands.SET_RPM_TARGET.getSendPacketDataSize
                ()));
        send(mSetSpeedPacket);
    }
}
