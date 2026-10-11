package medicinetime.addmedicine;

import medicinetime.BasePresenter;
import medicinetime.BaseView;
import medicinetime.data.source.MedicineAlarm;
import medicinetime.data.source.Pills;

import java.util.List;

public interface AddMedicineContract {

    interface View extends BaseView<Presenter> {

        void showEmptyMedicineError();

        void showMedicineList();

        boolean isActive();

    }

    interface  Presenter extends BasePresenter{


        void saveMedicine(MedicineAlarm alarm, Pills pills);


        boolean isDataMissing();

        boolean isMedicineExits(String pillName);

        long addPills(Pills pills);

        Pills getPillsByName(String pillName);

        List<MedicineAlarm> getMedicineByPillName(String pillName);

        List<Long> tempIds();

        void deleteMedicineAlarm(long alarmId);

        MedicineAlarm getMedicineAlarmById(long alarmId);

        List<MedicineAlarm> getAlarmGroup(String pillName, int alarmId);

        void updateAlarmGroup(String oldPillName, int alarmId, String newPillName,
                              int hour, int minute, String doseQuantity, String doseUnit,
                              boolean[] selectedDays, android.content.Context context);

    }
}
