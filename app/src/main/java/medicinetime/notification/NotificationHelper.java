package medicinetime.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class NotificationHelper {

    public static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationManager manager =
                context.getSystemService(NotificationManager.class);

        if (manager == null)
            return;

        NotificationChannel existing =
                manager.getNotificationChannel(
                        NotificationConstants.CHANNEL_ID);

        if (existing != null)
            return;

        Uri sound =
                RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_ALARM);

        AudioAttributes attributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

        NotificationChannel channel =
                new NotificationChannel(
                        NotificationConstants.CHANNEL_ID,
                        NotificationConstants.CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);

        channel.setDescription(
                NotificationConstants.CHANNEL_DESCRIPTION);

        channel.enableLights(true);

        channel.enableVibration(true);

        channel.setLockscreenVisibility(
                android.app.Notification.VISIBILITY_PUBLIC);

        channel.setSound(sound, attributes);

        manager.createNotificationChannel(channel);
    }

}