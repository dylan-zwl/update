package com.tapc.platform.model.device.controller.uart;

public interface ICommand {
    public int getCommandID();

    public int getSendPacketDataSize();

    public int getReceivePacketDataSize();

    public String getCommandString();
}
