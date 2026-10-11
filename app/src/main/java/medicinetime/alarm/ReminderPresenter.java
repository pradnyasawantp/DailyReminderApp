package medicinetime.alarm;

import androidx.annotation.NonNull;

import medicinetime.data.source.MedicineAlarm;
import medicinetime.data.source.MedicineDataSource;
import medicinetime.data.source.MedicineRepository;

public class ReminderPresenter implements ReminderContract.Presenter {

    private final MedicineRepository medicineRepository;

    private final ReminderContract.View reminderView;

    public ReminderPresenter(
            @NonNull MedicineRepository medicineRepository,
            @NonNull ReminderContract.View reminderView) {

        this.medicineRepository = medicineRepository;
        this.reminderView = reminderView;

        this.reminderView.setPresenter(this);
    }

    @Override
    public void start() {
        // Not used
    }

    @Override
    public void finishActivity() {

        if (reminderView.isActive()) {
            reminderView.onFinish();
        }
    }

    @Override
    public void onStart(long id) {
        loadMedicineById(id);
    }

    @Override
    public void loadMedicineById(long id) {
        loadMedicine(id);
    }

    private void loadMedicine(long id) {

        medicineRepository.getMedicineAlarmById(
                id,
                new MedicineDataSource.GetTaskCallback() {

                    @Override
                    public void onTaskLoaded(
                            MedicineAlarm medicineAlarm) {

                        if (!reminderView.isActive()) {
                            return;
                        }

                        if (medicineAlarm == null) {
                            reminderView.showNoData();
                            return;
                        }

                        reminderView.showMedicine(medicineAlarm);
                    }

                    @Override
                    public void onDataNotAvailable() {

                        if (reminderView.isActive()) {
                            reminderView.showNoData();
                        }
                    }
                });
    }

}