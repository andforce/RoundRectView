package com.andforce.roundrect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class RoundRectView extends ImageView {

    private Bitmap mRoundRectBitmap;
    private int mW = 0;
    private int mH = 0;

    private Bitmap mOriginalBitmap;
    private Canvas mOriginalCanvas;

    private Rect mImageRect;

    // 四个角上的椭圆长宽
    // 如果长=宽，就是圆
    // 依次是 x1,y1 x2,y2 x3.y3 x4,y4
    private float mRadii[] = {0, 0, 50, 50, 0, 0, 50, 50};
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Xfermode mXfermode;

    public RoundRectView(Context context) {
        super(context);
    }

    public RoundRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mW = getMeasuredWidth();
        mH = getMeasuredHeight();

        if (mW > 0 && mH > 0 && mOriginalBitmap == null) {

            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

            mImageRect = new Rect(100, 100, 500, 400);

            mOriginalBitmap = Bitmap.createBitmap(mImageRect.width(), mImageRect.height(), Bitmap.Config.ARGB_8888);
            mOriginalCanvas = new Canvas(mOriginalBitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLUE);
            mOriginalCanvas.save();
            mOriginalCanvas.translate(-mImageRect.left, -mImageRect.top);
            mOriginalCanvas.drawRect(mImageRect, paint);
            mOriginalCanvas.restore();

            mRoundRectBitmap = makeRoundRectFrame(mImageRect);
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mW > 0 && mH > 0) {
            int sc = canvas.saveLayer(mImageRect.left, mImageRect.top, mImageRect.right, mImageRect.bottom, null, Canvas.ALL_SAVE_FLAG);

            // 绘制圆角图
            canvas.drawBitmap(mRoundRectBitmap, mImageRect.left, mImageRect.top, mPaint);
            // 取交集
            mPaint.setXfermode(mXfermode);
            // 绘制原图
            canvas.drawBitmap(mOriginalBitmap, mImageRect.left, mImageRect.top, mPaint);

            mPaint.setXfermode(null);

            canvas.restoreToCount(sc);
        }
    }

    private Bitmap makeRoundRectFrame(Rect rect) {
        Bitmap bm = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);

        Path path = new Path();
        RectF sameWH = new RectF(0, 0, rect.width(), rect.height());
        path.addRoundRect(sameWH, mRadii, Path.Direction.CW);
        Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 颜色随便，但不能有透明度
        bitmapPaint.setColor(Color.GREEN);
        c.drawPath(path, bitmapPaint);
        return bm;
    }
}
