package medicinetime.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import medicinetime.data.source.MedicineAlarm;
import medicinetime.data.source.MedicineRepository;
import medicinetime.data.source.local.MedicinesLocalDataSource;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (context == null || intent == null) {
            return;
        }

        String action = intent.getAction();

        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {
            return;
        }

        Log.d(TAG, "Restoring alarms after reboot...");

        MedicineRepository repository =
                MedicineRepository.getInstance(
                        MedicinesLocalDataSource.getInstance(context));

        List<MedicineAlarm> alarms = repository.getAllAlarms();

        if (alarms == null || alarms.isEmpty()) {
            Log.d(TAG, "No alarms found.");
            return;
        }

        for (MedicineAlarm alarm : alarms) {

            try {

                int requestCode = alarm.getAlarmId() + alarm.getDayOfWeekNumber();
                AlarmScheduler.scheduleAlarm(
                        context,
                        alarm,
                        requestCode);

                Log.d(TAG,
                        "Restored alarm : "
                                + alarm.getPillName());

            } catch (Exception e) {

                Log.e(TAG,
                        "Unable to restore alarm",
                        e);
            }
        }
    }
}