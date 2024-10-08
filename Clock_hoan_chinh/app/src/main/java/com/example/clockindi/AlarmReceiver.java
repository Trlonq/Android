package com.example.clockindi;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

// AlarmReceiver kế thừa từ BroadcastReceiver, cho phép lớp này nhận các thông điệp hệ thống (broadcast).
public class AlarmReceiver extends BroadcastReceiver {
    // Xác định một ID cho kênh thông báo, sẽ được sử dụng để tạo và hiển thị thông báo.
    private static final String CHANNEL_ID = "AlarmChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Hiển thị Toast(thông báo tạm thời) khi alarm đổ chuông
        Toast.makeText(context, "Alarm is ringing!", Toast.LENGTH_SHORT).show();

        // Tạo thông báo
        createNotificationChannel(context);
        showNotification(context);
    }

    // Phương thức này tạo ra một kênh thông báo, điều này cần thiết cho các phiên bản Android từ Oreo (API 26) trở lên.
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    // Tạo một kênh thông báo với ID, tên và mức độ quan trọng (IMPORTANCE_HIGH) để thông báo sẽ xuất hiện trên màn hình.
                    CHANNEL_ID,
                    "Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Phương thức này chịu trách nhiệm hiển thị thông báo cho người dùng.
    private void showNotification(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class); // Chuyển hướng đến MainActivity khi nhấn vào thông báo
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm) // Hình ảnh nhỏ cho thông báo
                .setContentTitle("Alarm")
                .setContentText("Your alarm is ringing!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Tự động hủy thông báo khi nhấn vào

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build()); // Hiển thị thông báo
        }
    }
}