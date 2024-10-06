package com.example.clockclock;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Calendar calendar; // Thay đổi ở đây

    Handler handler;
    Runnable r;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calendar = Calendar.getInstance(); // Khởi tạo Calendar

        r = new Runnable()
        {
            @Override
            public void run()
            {
                calendar.setTimeInMillis(System.currentTimeMillis()); // Cập nhật thời gian
                drawingView dv = new drawingView(MainActivity.this,
                        calendar.get(Calendar.HOUR_OF_DAY), // Lấy giờ
                        calendar.get(Calendar.MINUTE), // Lấy phút
                        calendar.get(Calendar.SECOND), // Lấy giây
                        calendar.get(Calendar.DAY_OF_WEEK), // Lấy ngày trong tuần
                        calendar.get(Calendar.DAY_OF_MONTH), // Lấy ngày trong tháng
                        getBatteryLevel());
                setContentView(dv);
                handler.postDelayed(r, 1000);
            }
        };

        handler = new Handler();
        handler.postDelayed(r, 1000);
    }

    public float getBatteryLevel()
    {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level == -1 || scale == -1)
        {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    public class drawingView extends View
    {
        Paint mBackgroundPaint, mTextPaint, mTextPaintBack;
        Typeface tf;

        int hours, minutes, seconds, weekday, date;
        float battery;


        public drawingView(Context context, int hours, int minutes, int seconds, int weekday, int date, float battery)
        {
            super(context);

            tf = Typeface.createFromAsset(getAssets(), "DS-DIGII.TTF");

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.background));

            mTextPaint = new Paint();
            mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text));
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
            mTextPaint.setTypeface(tf);

            mTextPaintBack = new Paint();
            mTextPaintBack.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_back));
            mTextPaintBack.setAntiAlias(true);
            mTextPaintBack.setTextAlign(Paint.Align.CENTER);
            mTextPaintBack.setTextSize(getResources().getDimension(R.dimen.text_size));
            mTextPaintBack.setTypeface(tf);

            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
            this.weekday = weekday;
            this.date = date;
            this.battery = battery;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            float width = canvas.getWidth();
            float height = canvas.getHeight();

            canvas.drawRect(0, 0, width, height, mBackgroundPaint);

            float centerX = width / 2f;
            float centerY = height / 2f;

            // Màu nền với gradient
            LinearGradient gradient = new LinearGradient(0, 0, 0, height,
                    ContextCompat.getColor(getApplicationContext(), R.color.start_color),
                    ContextCompat.getColor(getApplicationContext(), R.color.end_color),
                    Shader.TileMode.CLAMP);
            mBackgroundPaint.setShader(gradient);
            canvas.drawRect(0, 0, width, height, mBackgroundPaint);


            int cur_hour = hours;
            String cur_ampm = "AM";
            if (cur_hour == 0)
            {
                cur_hour = 12;
            }
            if (cur_hour > 12)
            {
                cur_hour = cur_hour - 12;
                cur_ampm = "PM";
            }

            String text = String.format("%02d:%02d:%02d %s", cur_hour, minutes, seconds, cur_ampm); // Hiển thị AM/PM

            String day_of_week = "";
            switch (weekday) {
                case 1: day_of_week = "MON"; break;
                case 2: day_of_week = "TUE"; break;
                case 3: day_of_week = "WED"; break;
                case 4: day_of_week = "THUR"; break;
                case 5: day_of_week = "FRI"; break;
                case 6: day_of_week = "SAR"; break;
                case 7: day_of_week = "SUN"; break;
                }

            String text2 = String.format("DATE: %s %d", day_of_week, date);
            String batteryLevel = "BATTERY: " + (int) battery + "%";

            //mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_back));
            //canvas.drawText("00 00 00", centerX, centerY, mTextPaintBack);

            //mTextPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
            //canvas.drawText(text, centerX, centerY, mTextPaint);

            // Vẽ văn bản nền trước
            // mTextPaintBack.setTextSize(getResources().getDimension(R.dimen.text_size)); // Thiết lập kích thước chữ
            //float textBackY = centerY; // Vị trí Y cho văn bản nền
            //canvas.drawText("00 00 00", centerX, centerY, mTextPaintBack);

            // Đặt lại màu cho văn bản chính
            mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text)); // Màu cho văn bản chính
            mTextPaint.setTextSize(getResources().getDimension(R.dimen.text_size)); // Kích thước chữ cho văn bản chính
            // float textY = textBackY; // Đặt vị trí Y cho văn bản chính
            canvas.drawText(text, centerX, centerY, mTextPaint); // Vẽ văn bản chính


            //ve pin va ngay
            mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_back));
            mTextPaint.setTextSize(getResources().getDimension(R.dimen.text_size_small));
            canvas.drawText(batteryLevel + " " + text2,
                    centerX,
                    centerY + getResources().getDimension(R.dimen.text_size_small),
                    mTextPaint);

        }
    }
}