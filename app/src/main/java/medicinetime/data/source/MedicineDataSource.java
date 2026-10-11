package medicinetime.data.source;

import java.util.List;

public interface MedicineDataSource {

    interface LoadMedicineCallbacks {
        void onMedicineLoaded(List<MedicineAlarm> medicineAlarmList);
        void onDataNotAvailable();
    }

    interface GetTaskCallback {
        void onTaskLoaded(MedicineAlarm medicineAlarm);
        void onDataNotAvailable();
    }

    interface LoadHistoryCallbacks {
        void onHistoryLoaded(List<History> historyList);
        void onDataNotAvailable();
    }

    void getMedicineHistory(LoadHistoryCallbacks loadHistoryCallbacks);

    void getMedicineAlarmById(long id, GetTaskCallback callback);

    void saveMedicine(MedicineAlarm medicineAlarm, Pills pills);

    void getMedicineListByDay(int day, LoadMedicineCallbacks callbacks);

    boolean medicineExits(String pillName);

    List<Long> tempIds();

    void deleteAlarm(long alarmId);

    List<MedicineAlarm> getMedicineByPillName(String pillName);

    List<MedicineAlarm> getAllAlarms(String pillName);

    // NEW
    List<MedicineAlarm> getAllAlarms();

    List<MedicineAlarm> getAlarmGroup(String pillName, int alarmId);

    void updateAlarmGroup(String oldPillName, int alarmId, String newPillName,
                          int hour, int minute, String doseQuantity, String doseUnit,
                          boolean[] selectedDays);

    Pills getPillsByName(String pillName);

    long savePills(Pills pills);

    void saveToHistory(History history);
}