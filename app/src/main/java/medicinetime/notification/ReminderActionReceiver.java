package medicinetime.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class ReminderActionReceiver extends BroadcastReceiver {

    public static final String ACTION_DISMISS = "medicinetime.action.DISMISS";
    public static final String EXTRA_ACTION = "notification_action";
    private static final int BASE_NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null ||
                !ACTION_DISMISS.equals(intent.getAction())) {
            return;
        }

        long reminderId = intent.getLongExtra("extra_id", -1L);
        if (reminderId < 0) return;

        NotificationManagerCompat.from(context)
                .cancel(BASE_NOTIFICATION_ID + (int) reminderId);
    }
}
