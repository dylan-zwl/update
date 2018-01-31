package com.tapc.platform.model.device.controller;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.GenericMessageHandler;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.platform.model.device.controller.uart.TransferPacket;
import com.tapc.platform.model.device.controller.uart.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class IOUpdateController extends GenericMessageHandler {
    public static final int MAX_RESEND_ATTEMPT = 3;

    private Thread mUpdateThread = null;
    private IOUpdateHelper mUpdateHelper = new IOUpdateHelper();
    private IOUpdateListener mListener;
    private int mProcess;

    public IOUpdateController(Handler uihandler) {
        super(uihandler);
        mTransferPacket = new TransferPacket(Commands.SET_UPDATE_DATA);
    }

    public int updateIO(String filepath, IOUpdateListener listener) {
        mListener = listener;
//        if (mUpdateHelper.getLastUpdateStatus() == UpdateStatus.IN_PROGRESS) {
//            throw new RuntimeException("Another update is already in progress.");
//        }

        mUpdateThread = new Thread(mUpdateHelper.setBinaryPath(filepath));

        // start update
        mUpdateThread.start();

        return 0;
    }

    public int cancelUpdate() {
        if (mUpdateThread != null && mUpdateThread.isAlive()) {
            mUpdateThread.interrupt();
            try {
                mUpdateThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return 0;
    }

    @Override
    public boolean shouldHandleCommand(Commands cmd) {
        return cmd == Commands.SET_UPDATE_DATA || cmd == Commands.ENTER_UPDATE_MODE;
    }

    @Override
    public void handlePacket(ReceivePacket packet, Message msg) {
        mUpdateHelper.ackReceivedCallBack(packet);
    }

    protected void sendUpdateStatus(UpdateStatus status, String text) {
        if (mListener != null) {
            switch (status) {
                case IN_PROGRESS:
                    mListener.onProgress(mProcess, text);
                    break;
                case SUCCESS:
                    mListener.successful(text);
                    break;
                case CANCELLED:
                case FAILED:
                    mListener.failed(text);
                    break;
            }
        }
    }

    public enum UpdateStatus {
        IN_PROGRESS, SUCCESS, FAILED, CANCELLED
    }

    public interface IOUpdateListener {
        void onProgress(int process, String msg);

        void successful(String msg);

        void failed(String msg);
    }

    // helper class for doing the update
    private class IOUpdateHelper implements Runnable {

        public final int ACK_TIMEOUT = 1 * 1000;
        Utility.ThreadSignaller mAckWait = new Utility.ThreadSignaller();
        int mMax_data_read_buffer_size = 128;
        byte[] data = new byte[mMax_data_read_buffer_size];
        private boolean mAckReceived = false;
        private int mLastSentPacketNumber = 0;
        private int mAckPacketNumber = 0;
        private String mRomBinary;

        private UpdateStatus mLastUpdateStatus;

        private boolean lcbInUpdateMode = false;

        private IOUpdateHelper() {
        }

        public UpdateStatus getLastUpdateStatus() {
            return mLastUpdateStatus;
        }

        public IOUpdateHelper setBinaryPath(String filepath) {
            this.mRomBinary = filepath;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void run() {
            Date start = Calendar.getInstance().getTime();
            mLastUpdateStatus = UpdateStatus.IN_PROGRESS;
            FileInputStream fis = null;
            mLastSentPacketNumber = 0;
            mAckPacketNumber = 0;
            mAckReceived = false;
            TransferPacket enterUpdateModeCommand = new TransferPacket(Commands.ENTER_UPDATE_MODE);
            enterUpdateModeCommand.setData(null);
            lcbInUpdateMode = false;
            mProcess = 0;
            try {
                // get the file inputstream to read the file
                File f = new File(this.mRomBinary);
                fis = new FileInputStream(f);

                long totalFileSize = f.length();

                int attemptTimes = 10;

                while (!lcbInUpdateMode && attemptTimes-- > 0) {
                    // send the command to enter update mode to LCB
                    // wait until get back the ack, once the ack is received
                    // send the data packets
                    send(enterUpdateModeCommand);
                    Thread.sleep(1000);
                }

                if (attemptTimes < 0) {
                    sendUpdateStatus(UpdateStatus.FAILED, "Couldnot enter Update Mode");
                    return;
                }

                int dataReadSize = 0;
                int readProcessed = 0;
                // until reach end of stream while reading the ROM
                // the first 1 byte of the data field is reserved for the
                // packet sequence number so, load the data after the offset 1
                while ((dataReadSize = fis.read(data, 1, mMax_data_read_buffer_size - 1)) != -1) {
                    readProcessed += dataReadSize;
                    mProcess = (int) ((readProcessed * 100) / totalFileSize);
                    sendUpdateStatus(UpdateStatus.IN_PROGRESS, "" + (readProcessed * 100) / totalFileSize);
                    // feed the packet sequence number in the data field
                    mLastSentPacketNumber = (mLastSentPacketNumber + 1) % 256;
                    byte[] seqno = Utility.getByteArrayFromInteger(mLastSentPacketNumber, 1);
                    data[0] = seqno[0];

                    // put the data in the transfer packet
                    if (dataReadSize == mMax_data_read_buffer_size - 1)
                        mTransferPacket.setData(data);
                    else {
                        mTransferPacket.setData(Arrays.copyOf(data, dataReadSize));
                    }

                    // send the packet
                    if (sendPacket() > 0) {
                        // error occurred or failed
                        sendUpdateStatus(UpdateStatus.FAILED, "Update Failed");
                        return;
                    }
                }

                // reached here means, all the data was sent successfully
                // so send the signal that data sent complete
                mTransferPacket.setData(null);
                sendPacket();

                sendUpdateStatus(UpdateStatus.SUCCESS, "Update Completed Successfully, Time taken: " +
                        (Math.abs(Calendar.getInstance().getTimeInMillis() - start.getTime()) / 10000) + 1 + " Second" +
                        "(s).");
            } catch (FileNotFoundException e) {
                sendUpdateStatus(UpdateStatus.FAILED, "File not found: " + e.getMessage());
            } catch (IOException e) {
                sendUpdateStatus(UpdateStatus.FAILED, "IOException occurred: " + e.getMessage());
            } catch (InterruptedException e) {
                sendUpdateStatus(UpdateStatus.CANCELLED, "Update Failed, thread interrupted");
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
        }

        public Integer sendPacket() throws InterruptedException {

            // every time while sending/resending the packet, increment the
            // resend_attempt count.
            // The resend_attempt count gets incremented on every timeout event
            int resend_attempt = -1;

            mAckReceived = false;

            // try sending and resending the packet until an acknowledgement is
            // received
            while (!mAckReceived) {

                if (resend_attempt++ >= MAX_RESEND_ATTEMPT)
                    break;

                // send the prepared packet, In every timeout event, it resends
                // the packet again
                // if the ack from IO is lost and IO receives duplicate packet
                // then it ignores
                // the duplicate packet because while sending back the ack, it
                // sends back
                // the next expected packet sequence number. If that number
                // doesnot match the sequence number in the received packet then
                // the IO drops that packet and
                // sends back an ack with the next expected packet sequence
                // number
                send(mTransferPacket);

//				Log.d("IOUPDATE", "Send pckt number: " + mLastSentPacketNumber);
                if (resend_attempt > 0) {
                    sendUpdateStatus(UpdateStatus.IN_PROGRESS, "ReSent packet no: " + mLastSentPacketNumber + ", " +
                            "attempt: " + resend_attempt);
                }

                // wait for ack to arrive, it blocks over here
                if (!mAckReceived)
                    mAckWait.doWait(ACK_TIMEOUT);
            }

            return mAckReceived ? 0 : 1;
        }

        public void ackReceivedCallBack(ReceivePacket packet) {
            if (packet.getCommand() == Commands.SET_UPDATE_DATA) {

                mAckPacketNumber = Utility.getIntegerFromByteArray(packet
                        .getData());

                // Log.d("IOUPDATE", "Ack pckt number: " + mAckPacketNumber
                // + " Last sent packet: " + mLastSentPacketNumber);

                if (((mLastSentPacketNumber + 1) % 256) == mAckPacketNumber) {
                    mAckReceived = true;
                    mAckWait.doNotify();
                }
            } else if (packet.getCommand() == Commands.ENTER_UPDATE_MODE) {
                lcbInUpdateMode = true;
                sendUpdateStatus(UpdateStatus.IN_PROGRESS, "LCB entered update mode");
            }
        }
    }
}
