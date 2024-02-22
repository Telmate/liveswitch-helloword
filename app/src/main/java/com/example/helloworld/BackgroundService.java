package com.example.helloworld;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

// A Foreground service to run screen sharing, applicable for builds 28 and higher.
// From:
// https://stackoverflow.com/questions/61276730/media-projections-require-a-foreground-service-of-type-serviceinfo-foreground-se#:~:text=You%20have%20to%20start%20a,after%20you%20start%20the%20service.
public class BackgroundService extends Service {

    public BackgroundService() {
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creating a notification channel.
            NotificationChannel channel = new NotificationChannel("ScreenShare", "Foreground Notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        Notification mainActivityNotification = new NotificationCompat.Builder(this, "ScreenShare")
                .setContentTitle("Main Activity notification")
                .setContentText("Sharing")
                .setContentIntent(pendingIntent).build();

        startForeground(1, mainActivityNotification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}
