package medicinetime.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import medicinetime.data.source.MedicineAlarm;
import medicinetime.data.source.MedicineDataSource;
import medicinetime.data.source.MedicineRepository;
import medicinetime.data.source.local.MedicinesLocalDataSource;
import medicinetime.notification.ReminderNotification;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    @Override public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) return;
        long reminderId = intent.getLongExtra(ReminderFragment.EXTRA_ID, -1L);
        if (reminderId < 0) { Log.e(TAG, "Invalid reminder id"); return; }
        int requestCode = intent.getIntExtra(AlarmScheduler.EXTRA_REQUEST_CODE, 0);
        int hour = intent.getIntExtra(AlarmScheduler.EXTRA_HOUR, 0);
        int minute = intent.getIntExtra(AlarmScheduler.EXTRA_MINUTE, 0);
        int day = intent.getIntExtra(AlarmScheduler.EXTRA_DAY_OF_WEEK, 1);
        MedicineRepository repository = MedicineRepository.getInstance(MedicinesLocalDataSource.getInstance(context));
        repository.getMedicineAlarmById(reminderId, new MedicineDataSource.GetTaskCallback() {
            @Override public void onTaskLoaded(MedicineAlarm alarm) {
                if (alarm == null) return;
                ReminderNotification.show(context, reminderId, alarm.getPillName(), "");
                AlarmScheduler.rescheduleNextWeek(context, reminderId, requestCode, hour, minute, day);
            }
            @Override public void onDataNotAvailable() { Log.e(TAG, "Reminder no longer exists: " + reminderId); }
        });
    }
}
