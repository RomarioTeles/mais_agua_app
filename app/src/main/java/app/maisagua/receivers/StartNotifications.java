package app.maisagua.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by romario on 03/06/17.
 */

public class StartNotifications extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, NotificationService.class);
        context.startService(myIntent);
    }
}
