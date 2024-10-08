package com.example.clockindi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TimePickerDialog;
import java.util.Calendar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView timerText;
    private CountDownTimer countUpTimer;
    private long elapsedTime = 0; // Thời gian đã trôi qua
    private boolean isTimerRunning = false; // Trạng thái của timer

    private TextView digitalClock;
    private Handler handler = new Handler();
    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateDigitalClock();
            handler.postDelayed(this, 1000); // Cập nhật mỗi giây
        }
    };

    // Cập nhật đồng hồ số
    private void updateDigitalClock() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(calendar.getTime());
        digitalClock.setText(currentTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeTask); // Ngừng cập nhật khi activity bị hủy
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ánh xạ các thành phần giao diện
        timerText = findViewById(R.id.timerText);
        Button startTimerButton = findViewById(R.id.startTimerButton);
        Button resetTimerButton = findViewById(R.id.resetTimerButton);
        Button setAlarmButton = findViewById(R.id.setAlarmButton);

        // Thiết lập sự kiện cho các nút
        startTimerButton.setOnClickListener(view -> toggleTimer());
        resetTimerButton.setOnClickListener(view -> resetTimer());
        setAlarmButton.setOnClickListener(view -> setAlarm());

        // Ánh xạ TextView cho đồng hồ số
        digitalClock = findViewById(R.id.digitalClock);

        // Bắt đầu cập nhật thời gian cho đồng hồ số
        handler.post(updateTimeTask);

        // Thiết lập padding cho giao diện
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void toggleTimer() {
        if (isTimerRunning) {
            // Dừng timer
            countUpTimer.cancel();
            isTimerRunning = false;
            Toast.makeText(this, "Timer stopped", Toast.LENGTH_SHORT).show();
        } else {
            // Bắt đầu timer
            countUpTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    elapsedTime += 1000; // Tăng thời gian đã trôi qua 1 giây
                    updateTimer();
                }

                @Override
                public void onFinish() {
                    // Không sử dụng trong trường hợp này
                }
            }.start();
            isTimerRunning = true;
            Toast.makeText(this, "Timer started", Toast.LENGTH_SHORT).show();
        }
    }


    private void resetTimer() {
        elapsedTime = 0; // Reset về 0
        updateTimer();
        if (isTimerRunning) {
            countUpTimer.cancel(); // Dừng timer nếu đang chạy
            isTimerRunning = false;
        }
    }

    // Tính toán số phút và giây từ thời gian đã trôi qua
    private void updateTimer() {
        int minutes = (int) (elapsedTime / 1000) / 60;
        int seconds = (int) (elapsedTime / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerText.setText(timeLeftFormatted);
    }

    private void setAlarm() {
        // Lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();

        // Tạo TimePickerDialog để chọn giờ và phút
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    // Cài đặt thời gian cho báo thức
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    // Chuyển đổi thời gian đã chọn thành milliseconds
                    long triggerTime = calendar.getTimeInMillis();

                    // Nếu thời gian đã chọn trước thời gian hiện tại, đặt báo thức cho ngày mai
                    if (triggerTime < System.currentTimeMillis()) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        triggerTime = calendar.getTimeInMillis();
                    }

                    // Thiết lập AlarmManager
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Thiết lập báo thức
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

                    // Hiển thị thông báo cho người dùng
                    Toast.makeText(this, "Alarm set for " + hourOfDay + ":" + String.format("%02d", minute), Toast.LENGTH_SHORT).show();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);

        // Hiện thị TimePickerDialog
        timePickerDialog.show();
    }


}
