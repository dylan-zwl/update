package com.tapc.platform.model.device.controller.uart;

public interface ICommunicationPacket {
    public CommandReadWriteMode getReadWriteMode();

    public Commands getCommand();

    public byte[] getData();

    public void setData(byte[] data);

    public byte[] getPacketByteArray();
}