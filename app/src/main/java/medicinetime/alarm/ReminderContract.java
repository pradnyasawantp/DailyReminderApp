package medicinetime.alarm;

import medicinetime.BasePresenter;
import medicinetime.BaseView;
import medicinetime.data.source.MedicineAlarm;

public interface ReminderContract {

    interface View extends BaseView<Presenter> {

        void showMedicine(MedicineAlarm medicineAlarm);

        void showNoData();

        boolean isActive();

        void onFinish();
    }

    interface Presenter extends BasePresenter {

        void finishActivity();

        void onStart(long id);

        void loadMedicineById(long id);

    }
}