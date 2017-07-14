package com.tapc.update.ui.update;

/**
 * Created by Administrator on 2017/3/17.
 */

public class UpdateConttract {
    public interface View {
        void updateProgress(int percent, String msg);

        void updateCompleted(boolean isSuccess, String msg);

        void reboot();
    }

    public interface AppPresenter {
        void update(UpdateInfor updateInfor);
    }


    public interface McuPresenter {
        void update(UpdateInfor updateInfor);
    }

    public interface OsPresenter {
        void update(UpdateInfor updateInfor);
    }
}
