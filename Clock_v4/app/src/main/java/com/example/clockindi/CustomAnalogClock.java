package com.example.clockindi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Calendar;


public class CustomAnalogClock extends View {

    private Paint paintCircle, paintHour, paintMinute, paintSecond, paintText;
    private int clockRadius;


    public CustomAnalogClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    private void init() {
        // Vẽ mặt đồng hồ
        paintCircle = new Paint();
        paintCircle.setColor(Color.parseColor("#f3e5da")); // Thay đổi màu mặt đồng hồ
        paintCircle.setStyle(Paint.Style.FILL); // Tô màu bên trong
        // paintCircle.setStrokeWidth(1);

        // Vẽ kim giờ
        paintHour = new Paint();
        paintHour.setColor(Color.parseColor("#37524b"));
        paintHour.setStrokeWidth(30);

        // Vẽ kim phút
        paintMinute = new Paint();
        paintMinute.setColor(Color.parseColor("#37524b")); // Thay đổi màu thành màu tùy ý (màu cam)
        paintMinute.setStrokeWidth(30);

        // Vẽ kim giây
        paintSecond = new Paint();
        paintSecond.setColor(Color.parseColor("#c08058"));
        paintSecond.setStrokeWidth(20);

        // Vẽ số trên mặt đồng hồ
        paintText = new Paint();
        paintText.setColor(Color.parseColor("#c08058")); // Thay đổi màu số thành mã màu (#FF5733)
        paintText.setTextSize(100); // Kích thước chữ
        paintText.setTextAlign(Paint.Align.CENTER); // Canh giữa
        paintText.setFakeBoldText(true); // In đậm chữ


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        clockRadius = Math.min(w, h) / 2 - 20;  // Bán kính đồng hồ
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Vẽ mặt đồng hồ
        canvas.drawCircle(centerX, centerY, clockRadius, paintCircle);

        // Lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();
        float hour = calendar.get(Calendar.HOUR);
        float minute = calendar.get(Calendar.MINUTE);
        float second = calendar.get(Calendar.SECOND);

        // Tính góc của các kim đồng hồ
        float secondAngle = second * 6;           // 360/60
        float minuteAngle = (minute + second / 60) * 6;
        float hourAngle = (hour + minute / 60) * 30;  // 360/12

        // Kích thước kim
        float hourLength = clockRadius * 0.432f;
        float minuteLength = clockRadius * 0.72f;
        float secondLength = clockRadius * 0.54f;

        // Vẽ kim giờ
        canvas.save();
        canvas.rotate(hourAngle, centerX, centerY);
        drawRoundedLine(canvas, centerX, centerY, hourLength, paintHour); // Gọi hàm vẽ kim giờ
        canvas.restore();

        // Vẽ kim phút
        canvas.save();
        canvas.rotate(minuteAngle, centerX, centerY);
        drawRoundedLine(canvas, centerX, centerY, minuteLength, paintMinute); // Gọi hàm vẽ kim giờ
        canvas.restore();

        // Vẽ kim giây
        canvas.save();
        canvas.rotate(secondAngle, centerX, centerY);
        drawRoundedLine(canvas, centerX, centerY, secondLength, paintSecond); // Gọi hàm vẽ kim giờ
        canvas.restore();

        // Vẽ số giờ trên mặt đồng hồ
        for (int i = 1; i <= 12; i++) {
            float angle = (float) (Math.PI / 6 * i); // 30 độ cho mỗi số
            float x = (float) (centerX + (clockRadius - 100) * Math.sin(angle)); // 50 là khoảng cách từ tâm
            float y = (float) (centerY - (clockRadius - 100) * Math.cos(angle) + 20);
            canvas.drawText(String.valueOf(i), x, y, paintText);
        }




        // Tự động cập nhật lại mỗi giây
        postInvalidateDelayed(1000);
    }

    // Hàm vẽ kim với đầu bo tròn
    private void drawRoundedLine(Canvas canvas, float startX, float startY, float length, Paint paint) {
        float endX = startX;
        float endY = startY - length;

        // Vẽ thân kim
        canvas.drawLine(startX, startY, endX, endY, paint);

        // Vẽ đầu bo tròn
        float radius = 10; // Bán kính bo tròn
        canvas.drawCircle(endX, endY, radius, paint);
    }
}

