package com.tapc.platform.model.device.controller.helper;

import com.tapc.platform.model.device.controller.uart.Utility;

public class SpeedRatioParam {
	public static final int RECV_DATA_SUCCESS = 0;
	private int mMotorBeltDiameter;
	private int mRollerDiameter;
	private int mDrumPulleyDiameter;

	public SpeedRatioParam() {
	}
	
	public SpeedRatioParam(byte[] datas) {
		mMotorBeltDiameter = getDataInt(datas, 0, 2);
		mRollerDiameter = getDataInt(datas, 2, 2);
		mDrumPulleyDiameter = getDataInt(datas, 4, 2);
	}

	public void setAllParam(byte[] datas) {
		mMotorBeltDiameter = getDataInt(datas, 0, 2);
		mRollerDiameter = getDataInt(datas, 2, 2);
		mDrumPulleyDiameter = getDataInt(datas, 4, 2);
	}

	public byte[] getAllParam() {
		byte[] byte1 = Utility.getByteArrayFromInteger(mMotorBeltDiameter, 2);
		byte[] byte2 = Utility.getByteArrayFromInteger(mRollerDiameter, 2);
		byte[] byte3 = Utility.getByteArrayFromInteger(mDrumPulleyDiameter, 2);
		byte[] datas = new byte[6];
		System.arraycopy(byte1, 0, datas, 0, 2);
		System.arraycopy(byte2, 0, datas, 2, 2);
		System.arraycopy(byte3, 0, datas, 4, 2);
		return datas;
	}

	private int getDataInt(byte[] datas, int start, int bits) {
		byte[] dataCache = new byte[2];
		System.arraycopy(datas, start, dataCache, 0, bits);
		return Utility.getIntegerFromByteArray(dataCache);
	}

	public void setMotorBeltDiameter(int value) {
		mMotorBeltDiameter = value;
	}

	public void setRollerDiameter(int value) {
		mRollerDiameter = value;
	}

	public void setDrumPulleyDiameter(int value) {
		mDrumPulleyDiameter = value;
	}

	public int getMotorBeltDiameter() {
		return mMotorBeltDiameter;
	}

	public int getRollerDiameter() {
		return mRollerDiameter;
	}

	public int getDrumPulleyDiameter() {
		return mDrumPulleyDiameter;
	}
}
