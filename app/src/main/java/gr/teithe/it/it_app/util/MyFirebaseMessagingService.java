/*
 * Copyright (C) 2018-2020 Raf
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gr.teithe.it.it_app.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.data.local.preference.PreferencesManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    @Override
    public void onNewToken(@NonNull String s)
    {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        PreferencesManager.init(getApplicationContext());

        if(PreferencesManager.isReceivingNotifications())
        {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if(notificationManager != null)
            {
                if(Build.VERSION.SDK_INT >= 26)
                {
                    NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), "Ανακοινώσεις", NotificationManager.IMPORTANCE_HIGH);

                    notificationChannel.setDescription("IEE Apps");
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Notification.DEFAULT_LIGHTS);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 100, 100, 100, 100, 100});
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                String dataId = remoteMessage.getData().get("id");
                String dataTitle = remoteMessage.getData().get("title");
                String dataAbout = remoteMessage.getData().get("category");
                String dataName = remoteMessage.getData().get("name");

                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        .setContentTitle("Νέα Ανακοίνωση στην κατηγορία " + dataAbout)
                        .setContentText(dataName + ": " + dataTitle)
                        .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                int notificationId = new Random().nextInt();

                Bundle args = new Bundle();
                args.putString("id", dataId);
                args.putString("title", dataAbout);

                ThemeHelper.applyTheme(PreferencesManager.getTheme());

                PendingIntent pendingIntent = new NavDeepLinkBuilder(getApplicationContext())
                        .setGraph(R.navigation.navigation_graph)
                        .setDestination(R.id.detailsFragment)
                        .setArguments(args)
                        .createPendingIntent();

                notification.setContentIntent(pendingIntent);

                notificationManager.notify(notificationId, notification.build());
            }
        }
    }
}
