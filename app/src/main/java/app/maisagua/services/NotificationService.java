package app.maisagua.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Samsung on 05/05/2017.
 */

public class NotificationService extends IntentService {

    public NotificationService() {
        super("app.maisagua.notification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
