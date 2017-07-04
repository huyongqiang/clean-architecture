package com.tylersuehr.cleanarchitecture.ui.views;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import com.tylersuehr.cleanarchitecture.R;
/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 4/28/2017.
 *
 * This is an implementation of {@link AppCompatImageView} that will calculate a circle at the
 * center of its measured size to do the following:
 *      1. Draw a border circle with the given width and color.
 *      2. Draw a back circle with the given color.
 *      3. Crop the Drawable into Bitmap with the proper dimensions
 *      4. Draw the Bitmap in the center of the circle (leaving enough space for the border)
 *
 * <b>Immutable Properties</b>
 * {@link #circleRadius} stores the radius based on the needed size.
 * {@link #viewSize} stores the smallest size of the view's dimensions.
 */
public class CircleImageView extends AppCompatImageView {
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect circleRect = new Rect();
    private int borderWidth; // Width of the border around image
    private int borderColor; // Color of the border around image
    private int backColor; // Color of the area that's not the border
    private int circleRadius;
    private int viewSize;


    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
        DisplayMetrics dm = getResources().getDisplayMetrics();

        // Set XML attributes
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        this.borderColor = a.getColor(R.styleable.CircleImageView_borderColor, ContextCompat.getColor(c, R.color.colorPrimary)); // Primary color
        this.backColor = a.getColor(R.styleable.CircleImageView_circleColor, borderColor); // borderColor
        this.borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_borderWidth, (int)(1f * dm.density)); // 1dp
        a.recycle();

        // Setup border paint
        this.borderPaint.setStyle(Paint.Style.FILL);
        this.borderPaint.setColor(borderColor);

        // Setup main paint
        this.mainPaint.setColor(ContextCompat.getColor(c, R.color.grey_50)); // Grey 50
        this.mainPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }

        // Get the smallest size from the canvas (length or width)
//        int viewWidth = getMeasuredWidth();
//        int viewHeight = getMeasuredHeight();
        int viewWidth = canvas.getWidth();
        int viewHeight = canvas.getHeight();
        this.viewSize = Math.min(viewWidth, viewHeight);

        // Using that small size, calculate the exact center of the circle
        int circleCenterX = (viewWidth - viewSize) / 2;
        int circleCenterY = (viewHeight - viewSize) / 2;
        this.circleRadius = (viewSize - (borderWidth * 2)) / 2;
        this.circleRect.set(0, 0, viewSize, viewSize);

        // Maximize available border size
        if (viewSize == 0) { return; }
        if (viewSize / 3 < borderWidth) {
            this.borderWidth = viewSize / 3;
        }

        // Take the drawable from the ImageView and crop it into a circle
        Drawable drawable = getDrawable();
        Bitmap bitmap = cutIntoCircle(drawableToBitmap(drawable));
        if (bitmap == null) {
            return;
        }

        // Calculate the radius (including its border)
        int radius = circleRadius + borderWidth;
        canvas.translate(circleCenterX, circleCenterY);

        // Draw the back circle
        this.borderPaint.setColor(backColor);
        canvas.drawCircle(radius, radius, circleRadius, borderPaint); // Back circle

        // Draw the border circle
        this.borderPaint.setColor(borderColor);
        canvas.drawCircle(radius, radius, radius, borderPaint); // Border circle

        // Draw the actual image
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    /**
     * Sets both the {@link #backColor} and {@link #borderColor} of the circle
     * the same given color.
     * @param color {@link ColorInt}
     */
    public void setCircleColor(@ColorInt int color) {
        this.backColor = color;
        this.borderColor = color;
        this.borderPaint.setColor(color);
        invalidate();
    }

    public void setBorderColor(@ColorInt int color) {
        this.borderColor = color;
        this.borderPaint.setColor(color);
        invalidate();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderBackColor(@ColorInt int color) {
        this.backColor = color;
        this.borderPaint.setColor(color);
        invalidate();
    }

    public int getBackColor() {
        return backColor;
    }

    public void setBorderWidth(int width) {
        this.borderWidth = width;
        invalidate();
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    private Bitmap cutIntoCircle(Bitmap bitmap) { // OutOfMemory Exception
        if (bitmap == null) { return null; }

        Bitmap output = Bitmap.createBitmap(viewSize, viewSize, Bitmap.Config.ARGB_8888);
        int radius = circleRadius + borderWidth;

        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(radius, radius, circleRadius, borderPaint);
        canvas.drawBitmap(bitmap, circleRect, circleRect, mainPaint);

        return output;
    }

    private Bitmap drawableToBitmap(Drawable drawable) { // OutOfMemory Exception
        if (drawable == null) { return null; }

        Bitmap bitmap = Bitmap.createBitmap(viewSize, viewSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, viewSize, viewSize);
        drawable.draw(canvas);

        return bitmap;
    }
}