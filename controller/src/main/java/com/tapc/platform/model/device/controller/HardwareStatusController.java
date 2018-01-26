package com.tapc.platform.model.device.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.GenericMessageHandler;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.platform.model.device.controller.uart.TransferPacket;
import com.tapc.platform.model.device.controller.uart.Utility;

public class HardwareStatusController extends GenericMessageHandler {
    public static final String DEVICE_SAFEKEY_STATUS = "action.safekey.status";
    public static final String DEVICE_ERROR_STATUS = "action.error.status";
    public static final int ERROR_MASK_VALUE = 0x0001;
    public static final int SAFEKEY_MASK_VALUE = 0x0008;
    public static final int KEY_MASK_VALUE = 0x0002;
    public static final int WDT_OVERFLOW_MASK_VALUE = 0x0004;
    public static final int STATUS_BIT_INVERTER_ERR_MASK_VALUE = 0x0010;
    public static final int STATUS_BIT_ERR_MASK_VALUE = 0xff00;

    private Context mContext;
    private int mErrorCode;
    private int mSafeKeyStatus = -1;

    public HardwareStatusController(Context context, Handler uihandler) {
        super(uihandler);
        mContext = context;
        mTransferPacket = new TransferPacket(Commands.GET_STATUS);
        getPeriodicCommander().addCommandtoList(this.toString(), mTransferPacket);
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.GET_STATUS || cmd == Commands.GET_MACHIE_ERROR;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        int data = Utility.getIntegerFromByteArray(packet.getData());
        Intent intent = new Intent();
        switch (packet.getCommand()) {
            case GET_MACHIE_ERROR:
                mErrorCode = data & 0xFFFF;
                intent.setAction(HardwareStatusController.DEVICE_ERROR_STATUS);
                intent.putExtra(HardwareStatusController.DEVICE_ERROR_STATUS, mErrorCode);
                mContext.sendBroadcast(intent);
                break;
            case GET_STATUS:
                int status = data & SAFEKEY_MASK_VALUE;
                if (mSafeKeyStatus != status) {
                    mSafeKeyStatus = status;
                    intent.setAction(HardwareStatusController.DEVICE_SAFEKEY_STATUS);
                    intent.putExtra(HardwareStatusController.DEVICE_SAFEKEY_STATUS, mSafeKeyStatus);
                    mContext.sendBroadcast(intent);
                    break;
                }
        }
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public int getSafeKeyStatus() {
        return mSafeKeyStatus;
    }
}
