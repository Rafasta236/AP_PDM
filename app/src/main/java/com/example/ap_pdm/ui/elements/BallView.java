package com.example.ap_pdm.ui.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BallView extends View {

    private float posX = 200f;
    private float posY = 200f;

    private float radius = 20f;

    private float limitX1 = 200f;
    private float limitY1 = 200f;
    private float limitX2 = 600f;
    private float limitY2 = 900f;
    private boolean isLimitSet = false;

    private Paint paint;

    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
    }

    public void setLimit(float limitX1, float limitY1, float limitX2, float limitY2) {
        this.limitX1 = limitX1;
        this.limitY1 = limitY1;
        this.limitX2 = limitX2;
        this.limitY2 = limitY2;

        isLimitSet = true;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    };

    public void updatePosition(float dx, float dy) {
        posX += dx;
        posY += dy;

        if (posX < limitX1 + radius) posX = limitX1 + radius;
        if (posY < limitY1 + radius) posY = limitY1 + radius;
        if (posX > limitX2 - radius) posX = limitX2 - radius;
        if (posY > limitY2 - radius) posY = limitY2 - radius;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!isLimitSet) return;

        Paint border = new Paint();
        border.setColor(Color.BLACK);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(8);
        canvas.drawRect(limitX1, limitY1, limitX2, limitY2, border);

        canvas.drawCircle(posX, posY, radius, paint);
    }
}
