package medicinetime.notification;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.techmania.medicinereminderapp.R;

import medicinetime.alarm.ReminderActivity;
import medicinetime.alarm.ReminderFragment;

public final class ReminderNotification {

    private static final String CHANNEL_ID = "generic_reminder_channel";
    private static final String CHANNEL_NAME = "Reminders";
    private static final int BASE_NOTIFICATION_ID = 1000;

    private ReminderNotification() { }

    public static void show(Context context, long reminderId, String title, String ignoredDetails) {
        createNotificationChannel(context);

        Intent activityIntent = new Intent(context, ReminderActivity.class);
        activityIntent.putExtra(ReminderFragment.EXTRA_ID, reminderId);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent fullScreenIntent = PendingIntent.getActivity(
                context,
                (int) reminderId,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent dismissIntent = new Intent(context, ReminderActionReceiver.class);
        dismissIntent.setAction(ReminderActionReceiver.ACTION_DISMISS);
        dismissIntent.putExtra(ReminderFragment.EXTRA_ID, reminderId);

        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                (int) reminderId + 10000,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String safeTitle = (title == null || title.trim().isEmpty()) ? "Reminder" : title.trim();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Reminder")
                .setContentText(safeTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(safeTitle))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(fullScreenIntent)
                .setFullScreenIntent(fullScreenIntent, true)
                .addAction(R.drawable.ic_clear, "Dismiss", dismissPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            notificationManager.notify(BASE_NOTIFICATION_ID + (int) reminderId, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager == null) return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

        Uri sound = Uri.parse("android.resource://" + context.getPackageName()
                + "/" + R.raw.alarm_music);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        channel.setSound(sound, audioAttributes);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
    }
}
