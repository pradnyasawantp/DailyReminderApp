package medicinetime.addmedicine;

import android.content.Context;

import androidx.annotation.NonNull;

import medicinetime.alarm.AlarmScheduler;

import medicinetime.data.source.MedicineAlarm;
import medicinetime.data.source.MedicineDataSource;
import medicinetime.data.source.Pills;

import java.util.List;

public class AddMedicinePresenter implements AddMedicineContract.Presenter, MedicineDataSource.GetTaskCallback {

    @NonNull
    private final MedicineDataSource mMedicineRepository;

    private final AddMedicineContract.View mAddMedicineView;

    private int mMedicineId;

    private boolean mIsDataMissing;

    public AddMedicinePresenter(int mMedicineId, @NonNull MedicineDataSource mMedicineRepository, AddMedicineContract.View mAddMedicineView, boolean mIsDataMissing) {
        this.mMedicineId = mMedicineId;
        this.mMedicineRepository = mMedicineRepository;
        this.mAddMedicineView = mAddMedicineView;
        this.mIsDataMissing = mIsDataMissing;

        mAddMedicineView.setPresenter(this);
    }

    private boolean isNewTask() {
        return mMedicineId <= 0;
    }

    @Override
    public void start() {

    }

    @Override
    public void saveMedicine(MedicineAlarm alarm, Pills pills) {
        mMedicineRepository.saveMedicine(alarm, pills);
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    @Override
    public boolean isMedicineExits(String pillName) {
        return mMedicineRepository.medicineExits(pillName);
    }

    @Override
    public long addPills(Pills pills) {
        return mMedicineRepository.savePills(pills);
    }

    @Override
    public Pills getPillsByName(String pillName) {
        return mMedicineRepository.getPillsByName(pillName);
    }

    @Override
    public List<MedicineAlarm> getMedicineByPillName(String pillName) {
        return mMedicineRepository.getMedicineByPillName(pillName);
    }

    @Override
    public List<Long> tempIds() {
        return mMedicineRepository.tempIds();
    }

    @Override
    public void deleteMedicineAlarm(long alarmId) {
        mMedicineRepository.deleteAlarm(alarmId);
    }

    @Override
    public MedicineAlarm getMedicineAlarmById(long alarmId) {
        final MedicineAlarm[] result = new MedicineAlarm[1];
        mMedicineRepository.getMedicineAlarmById(alarmId, new MedicineDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(MedicineAlarm medicineAlarm) {
                result[0] = medicineAlarm;
            }

            @Override
            public void onDataNotAvailable() {
            }
        });
        return result[0];
    }

    @Override
    public List<MedicineAlarm> getAlarmGroup(String pillName, int alarmId) {
        return mMedicineRepository.getAlarmGroup(pillName, alarmId);
    }

    @Override
    public void updateAlarmGroup(String oldPillName, int alarmId, String newPillName,
                                 int hour, int minute, String doseQuantity, String doseUnit,
                                 boolean[] selectedDays, Context context) {
        List<MedicineAlarm> oldAlarms = mMedicineRepository.getAlarmGroup(oldPillName, alarmId);

        for (MedicineAlarm oldAlarm : oldAlarms) {
            AlarmScheduler.cancelAlarm(context, alarmId + oldAlarm.getDayOfWeekNumber());
        }

        mMedicineRepository.updateAlarmGroup(oldPillName, alarmId, newPillName, hour, minute,
                doseQuantity, doseUnit, selectedDays);

        List<MedicineAlarm> updated = mMedicineRepository.getAlarmGroup(newPillName, alarmId);
        for (MedicineAlarm alarm : updated) {
            int day = alarm.getDayOfWeekNumber();
            AlarmScheduler.scheduleAlarm(context, alarm.getId(), alarmId + day, hour, minute, day);
        }
    }

    @Override
    public void onTaskLoaded(MedicineAlarm medicineAlarm) {
        // The view may not be able to handle UI updates anymore
        /*if (mAddMedicineView.isActive()){
            mAddMedicineView.setDose(medicineAlarm.getDose());
            mAddMedicineView.setMedName(medicineAlarm.getMedicineName());
            mAddMedicineView.setDays(medicineAlarm.getDays());
            mAddMedicineView.setTime(medicineAlarm.getTime());
        }
        mIsDataMissing = false;*/
    }

    @Override
    public void onDataNotAvailable() {
        if (mAddMedicineView.isActive()) {
            mAddMedicineView.showEmptyMedicineError();
        }
    }
}
