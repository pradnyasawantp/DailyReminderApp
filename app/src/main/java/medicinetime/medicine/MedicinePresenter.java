package medicinetime.medicine;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import medicinetime.addmedicine.AddMedicineActivity;
import medicinetime.alarm.AlarmScheduler;
import medicinetime.data.source.MedicineAlarm;
import medicinetime.data.source.MedicineDataSource;
import medicinetime.data.source.MedicineRepository;

public class MedicinePresenter implements MedicineContract.Presenter {
    private final MedicineRepository repository;
    private final MedicineContract.View view;
    MedicinePresenter(@NonNull MedicineRepository repository, @NonNull MedicineContract.View view) { this.repository = repository; this.view = view; view.setPresenter(this); }
    @Override public void start() { }
    @Override public void onStart(int day) { loadListByDay(day, true); }
    @Override public void reload(int day) { loadListByDay(day, true); }
    @Override public void result(int requestCode, int resultCode) { if (requestCode == AddMedicineActivity.REQUEST_ADD_TASK && resultCode == Activity.RESULT_OK) view.showSuccessfullySavedMessage(); }
    @Override public void addNewMedicine() { view.showAddMedicine(); }

    @Override public void deleteMedicineAlarm(MedicineAlarm alarm, Context context) {
        if (alarm == null) return;
        List<MedicineAlarm> group = repository.getAlarmGroup(alarm.getPillName(), alarm.getAlarmId());
        if (group == null || group.isEmpty()) group = Collections.singletonList(alarm);
        for (MedicineAlarm item : group) {
            AlarmScheduler.cancelAlarm(context, item.getAlarmId() + item.getDayOfWeekNumber());
            repository.deleteAlarm(item.getId());
        }
        view.showMedicineDeletedSuccessfully();
    }

    @Override public void loadMedicinesByDay(int day, boolean showIndicator) { loadListByDay(day, showIndicator); }
    private void loadListByDay(int day, boolean showLoading) {
        if (showLoading) view.showLoadingIndicator(true);
        repository.getMedicineListByDay(day, new MedicineDataSource.LoadMedicineCallbacks() {
            @Override public void onMedicineLoaded(List<MedicineAlarm> list) {
                if (!view.isActive()) return;
                Collections.sort(list);
                if (showLoading) view.showLoadingIndicator(false);
                if (list == null || list.isEmpty()) view.showNoMedicine(); else view.showMedicineList(list);
            }
            @Override public void onDataNotAvailable() { if (!view.isActive()) return; if (showLoading) view.showLoadingIndicator(false); view.showNoMedicine(); }
        });
    }
}
