package com.tapc.platform.model.device.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.GenericMessageHandler;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.platform.model.device.controller.uart.Utility;

public class KeyboardController extends GenericMessageHandler {
    public static final String DEVICE_KEY_EVENT = "action.keyboard.keyevent";
    public final static String KEY_CODE = "KEY_CODE";

    private Context mContext;

    public KeyboardController(Context context, Handler uihandler) {
        super(uihandler);
        mContext = context;
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.GET_KEY_CODE;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        int key = Utility.getIntegerFromByteArray(packet.getData()) & 0x00FF;
        Intent intent = new Intent();
        intent.setAction(KeyboardController.DEVICE_KEY_EVENT);
        intent.putExtra(KeyboardController.KEY_CODE, key);
        mContext.sendBroadcast(intent);
    }
}
