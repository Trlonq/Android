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
        // Tính bán kính đồng hồ bằng cách lấy chiều rộng hoặc chiều cao nhỏ nhất (để đảm bảo đồng hồ hình tròn) chia cho 2 và trừ đi 20 để tạo không gian cho viền của đồng hồ.
        clockRadius = Math.min(w, h) / 2 - 20;  // Bán kính đồng hồ
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Tính tọa độ trung tâm của đồng hồ.
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
        float secondAngle = second * 6;           // Mỗi giây tương ứng với 6 độ (360 độ / 60 giây).
        float minuteAngle = (minute + second / 60) * 6; // Mỗi phút tương ứng với 6 độ, cộng thêm phần giây để tính chính xác hơn.
        float hourAngle = (hour + minute / 60) * 30;  // Mỗi giờ tương ứng với 30 độ (360 độ / 12 giờ), cộng thêm phần phút để tính chính xác hơn.

        // Kích thước chieeuf daif kim
        // Tính chiều dài của các kim đồng hồ dựa trên bán kính của đồng hồ.
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
        /*
        centerX: Tọa độ x của tâm đồng hồ. Đây là điểm mà tất cả các số giờ sẽ được vẽ xung quanh.
        clockRadius - 100: Đưa ra khoảng cách từ tâm đến vị trí của số giờ trên mặt đồng hồ. Việc trừ 100 ở đây có thể là để tạo không gian giữa các số và viền đồng hồ, giúp chúng không dính vào nhau.
        Math.sin(angle): Tính toán phần bù của tọa độ y của số giờ dựa trên góc. Hàm sin trả về giá trị từ -1 đến 1, vì vậy khi nhân với (clockRadius - 100), bạn nhận được khoảng cách từ tâm đến số giờ trên mặt đồng hồ theo phương ngang (trục x).
        Cộng với centerX: Đưa tọa độ x từ điểm tâm của đồng hồ đến vị trí cuối cùng của số giờ.
         */
        for (int i = 1; i <= 12; i++) {
            float angle = (float) (Math.PI / 6 * i); // 30 độ cho mỗi số
            float x = (float) (centerX + (clockRadius - 100) * Math.sin(angle)); //
            float y = (float) (centerY - (clockRadius - 100) * Math.cos(angle) + 20); // đẩy số giờ lên một chút so với vị trí tính toán, tạo thêm khoảng cách giữa số và viền của đồng hồ.
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

