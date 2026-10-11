package medicinetime.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.util.Calendar;

import medicinetime.data.source.MedicineAlarm;

public final class AlarmScheduler {

    public static final String EXTRA_REQUEST_CODE = "requestCode";
    public static final String EXTRA_HOUR = "hour";
    public static final String EXTRA_MINUTE = "minute";
    public static final String EXTRA_DAY_OF_WEEK = "dayOfWeek";

    private AlarmScheduler() {
    }

    /**
     * Schedule a weekly exact alarm.
     */
    public static void scheduleAlarm(
            Context context,
            long medicineId,
            int requestCode,
            int hour,
            int minute,
            int dayOfWeek) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        Calendar now = Calendar.getInstance();
        Calendar calendar = (Calendar) now.clone();

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        if (!calendar.after(now)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        Intent receiverIntent =
                new Intent(context, AlarmReceiver.class);

        receiverIntent.putExtra(
                ReminderFragment.EXTRA_ID,
                medicineId);

        receiverIntent.putExtra(
                EXTRA_REQUEST_CODE,
                requestCode);

        receiverIntent.putExtra(
                EXTRA_HOUR,
                hour);

        receiverIntent.putExtra(
                EXTRA_MINUTE,
                minute);

        receiverIntent.putExtra(
                EXTRA_DAY_OF_WEEK,
                dayOfWeek);

        PendingIntent alarmIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        receiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE);

        // Cancel any existing alarm with the same request code.
        alarmManager.cancel(alarmIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()) {
            return;
        }

        long triggerAtMillis = calendar.getTimeInMillis();

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        alarmIntent);

            } else {

                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        alarmIntent);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restore alarms after reboot.
     */
    public static void scheduleAlarm(
            Context context,
            MedicineAlarm alarm,
            long medicineId) {

        scheduleAlarm(
                context,
                medicineId,
                alarm.getAlarmId(),
                alarm.getHour(),
                alarm.getMinute(),
                alarm.getDayOfWeekNumber());
    }

    /**
     * Cancel an alarm.
     */
    public static void cancelAlarm(
            Context context,
            int requestCode) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        Intent receiverIntent =
                new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        receiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }

    /**
     * Schedule the same alarm again after one week.
     */
    public static void rescheduleNextWeek(
            Context context,
            long medicineId,
            int requestCode,
            int hour,
            int minute,
            int dayOfWeek) {

        scheduleAlarm(
                context,
                medicineId,
                requestCode,
                hour,
                minute,
                dayOfWeek);
    }
}