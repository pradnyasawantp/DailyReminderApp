package medicinetime.data.source.local;

import android.content.Context;

import java.net.URISyntaxException;
import java.util.List;

import medicinetime.data.source.History;
import medicinetime.data.source.MedicineAlarm;
import medicinetime.data.source.MedicineDataSource;
import medicinetime.data.source.Pills;

public class MedicinesLocalDataSource implements MedicineDataSource {

    private static MedicinesLocalDataSource mInstance;

    private final MedicineDBHelper mDbHelper;

    private MedicinesLocalDataSource(Context context) {
        mDbHelper = new MedicineDBHelper(context.getApplicationContext());
    }

    public static synchronized MedicinesLocalDataSource getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new MedicinesLocalDataSource(context);
        }

        return mInstance;
    }

    @Override
    public void getMedicineHistory(LoadHistoryCallbacks callbacks) {

        List<History> historyList = mDbHelper.getHistory();

        if (historyList == null) {
            historyList = new java.util.ArrayList<>();
        }

        callbacks.onHistoryLoaded(historyList);
    }

    @Override
    public void getMedicineAlarmById(long id, GetTaskCallback callback) {

        try {

            MedicineAlarm medicineAlarm = mDbHelper.getAlarmById(id);

            if (medicineAlarm != null) {
                callback.onTaskLoaded(medicineAlarm);
            } else {
                callback.onDataNotAvailable();
            }

        } catch (Exception e) {

            e.printStackTrace();
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveMedicine(MedicineAlarm medicineAlarm, Pills pill) {
        mDbHelper.createAlarm(medicineAlarm, pill.getPillId());
    }

    @Override
    public void getMedicineListByDay(int day,
                                     LoadMedicineCallbacks callbacks) {

        List<MedicineAlarm> list =
                mDbHelper.getAlarmsByDay(day);

        if (list == null || list.isEmpty()) {
            callbacks.onDataNotAvailable();
        } else {
            callbacks.onMedicineLoaded(list);
        }
    }

    @Override
    public boolean medicineExits(String pillName) {

        List<Pills> pills = mDbHelper.getAllPills();

        if (pills == null) {
            return false;
        }

        for (Pills pill : pills) {

            if (pillName.equalsIgnoreCase(pill.getPillName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Long> tempIds() {
        return null;
    }

    @Override
    public void deleteAlarm(long alarmId) {
        mDbHelper.deleteAlarm(alarmId);
    }

    @Override
    public List<MedicineAlarm> getMedicineByPillName(String pillName) {

        try {
            return mDbHelper.getAllAlarmsByPill(pillName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<MedicineAlarm> getAllAlarms(String pillName) {

        try {
            return mDbHelper.getAllAlarms(pillName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<MedicineAlarm> getAllAlarms() {
        return mDbHelper.getAllAlarms();
    }

    @Override
    public List<MedicineAlarm> getAlarmGroup(String pillName, int alarmId) {
        return mDbHelper.getAlarmGroup(pillName, alarmId);
    }

    @Override
    public void updateAlarmGroup(String oldPillName, int alarmId, String newPillName,
                                 int hour, int minute, String doseQuantity, String doseUnit,
                                 boolean[] selectedDays) {
        mDbHelper.updateAlarmGroup(oldPillName, alarmId, newPillName, hour, minute,
                doseQuantity, doseUnit, selectedDays);
    }

    @Override
    public Pills getPillsByName(String pillName) {
        return mDbHelper.getPillByName(pillName);
    }

    @Override
    public long savePills(Pills pills) {
        return mDbHelper.createPill(pills);
    }

    @Override
    public void saveToHistory(History history) {
        mDbHelper.createHistory(history);
    }

    public void deletePill(String pillName) {

        try {
            mDbHelper.deletePill(pillName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void addToHistory(History history) {
        mDbHelper.createHistory(history);
    }

    public List<History> getHistory() {
        return mDbHelper.getHistory();
    }

    public int getDayOfWeek(long alarmId) {

        try {
            return mDbHelper.getDayOfWeek(alarmId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return -1;
    }
}