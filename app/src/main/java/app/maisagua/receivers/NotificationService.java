package app.maisagua.receivers;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import app.maisagua.R;
import app.maisagua.activities.NoteActivity;
import app.maisagua.activities.SettingsActivity;
import app.maisagua.dataSource.DataBaseContract;
import app.maisagua.helpers.DataSourceHelper;

/**
 * Created by romario on 03/06/17.
 */

public class NotificationService extends IntentService {

    private static final int ONE_HOUR_IN_MILLISECONDS = 3600000;

    public NotificationService() {
        super("app.maisagua.receivers.service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        DataSourceHelper mDataSourceHelper = new DataSourceHelper(this);
        String[] projection = new String[]{
                DataBaseContract.SettingsEntry.COLUMN_NAME_NOTIFICATION_INTERVAL
        };

        Cursor c = mDataSourceHelper.query(DataBaseContract.SettingsEntry.TABLE_NAME, projection, null, null, null);

        if(c.getCount() > 0) {
            c.moveToFirst();
            int interval = c.getInt(c.getColumnIndex(DataBaseContract.SettingsEntry.COLUMN_NAME_NOTIFICATION_INTERVAL));
            scheduleNotification(getNotification(), interval);
        }else {
            scheduleNotification(getNotification(), 1);
        }
    }

    public void scheduleNotification(Notification notification, int interval){
        AlarmManager mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        intent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);

        interval *= ONE_HOUR_IN_MILLISECONDS;

        long triggerAtMillis = SystemClock.elapsedRealtime();

        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, interval, pendingIntent);
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.lembrete));
        builder.setContentText(getString(R.string.lembrete_message));
        builder.setSmallIcon(R.drawable.ic_cup);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        Intent resultIntent = new Intent(this, NoteActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        Notification notification = builder.build();
        return notification;
    }

}
