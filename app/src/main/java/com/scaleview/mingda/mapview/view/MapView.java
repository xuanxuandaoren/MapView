package com.scaleview.mingda.mapview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.scaleview.mingda.mapview.R;

/**
 * 可缩放的地图控件
 * Created by 玉光 on 2017-9-25.
 */

public final class MapView extends FrameLayout {
    /**
     * 左上角的坐标
     */
    private PointF leftPoint = new PointF();
    /**
     * 右上角的坐标
     */
    private PointF topPoint = new PointF();
    /**
     * 右下角的坐标
     */
    private PointF rightPoint = new PointF();
    /**
     * 左下角的坐标
     */
    private PointF bottomPoint = new PointF();
    /**
     * 触碰中心的点
     */
    private PointF midPoint = new PointF();
    /**
     * 缩放的等级
     */
    private float scaleLevel = 1.0f;
    /**
     * 背景的ID
     */
    private int backgroundResId;
    /**
     * 背景的图片
     */
    private Bitmap bitmapSrc;
    /**
     * 是否在运动中
     */
    private boolean isAnmiting;
    /**
     * 上一个点的X坐标
     */
    private float lastX;
    /**
     * 上一个点的Y坐标
     */
    private float lastY;
    /**
     * 是否是多点触控
     */
    private boolean isMultiPoint = false;
    /**
     * 上一个两点距离的位置
     */
    private float lastDistance;
    /**
     * 控件的宽
     */
    private int width;
    /**
     * 控件的高
     */
    private int height;
    /**
     * 动画结束的Y坐标
     */
    private float endY;
    /**
     * 动画结束的x坐标
     */
    private float endX;
    /**
     * 动画开始的x坐标
     */
    private float startX;
    /**
     * 动画结束的x坐标
     */
    private float startY;
    /**
     * 动画的时间
     */
    private long animatingDuring = 400;
    /**
     * 垂直位置最大偏移量
     */
    private int maxHeightOffset = 225;
    /**
     * 水平位置最大偏移量
     */
    private int maWidthOffset = 150;

    public MapView(@NonNull Context context) {
        super(context);
    }

    public MapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public MapView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 初始化控件
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
//        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MapView);
//
//        typedArray.getColor(R.styleable.MapView_slide_background,)
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MapView);
        backgroundResId = typedArray.getResourceId(R.styleable.MapView_map_background, R.drawable.bg);
        ImageView backgroundMap = new ImageView(getContext());
        bitmapSrc = BitmapFactory.decodeResource(getResources(), backgroundResId);

        backgroundMap.setBackgroundResource(backgroundResId);
        addView(backgroundMap, 0, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (width == 0 && height == 0) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();

            leftPoint.set(0, 0);
            topPoint.set(width, 0);
            bottomPoint.set(0, height);
            rightPoint.set(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float bitmapLeft = -leftPoint.x * scaleLevel;
        float bitmapTop = -leftPoint.y * scaleLevel;
        getChildAt(0).layout((int) bitmapLeft, (int) bitmapTop, (int) (bitmapLeft + bitmapSrc.getWidth() * scaleLevel), (int) (bitmapTop + bitmapSrc.getHeight() * scaleLevel));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (isAnmiting) {
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.i("xiaozhu", "ACTION_DOWN" + isMultiPoint);
                lastX = event.getX();
                lastY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:

                if (isMultiPoint) {
                    float distance = calculateDistance(event);
                    float centerX = leftPoint.x + midPoint.x / scaleLevel;
                    float centerY = leftPoint.y + midPoint.y / scaleLevel;
                    Log.i("xiaozhu", "before" + distance + "===" + scaleLevel + "===" + lastDistance + "==" + leftPoint.y + "===" + midPoint.y);
                    scaleLevel = distance * scaleLevel / lastDistance;

                    leftPoint.x = centerX - midPoint.x / scaleLevel;
                    leftPoint.y = centerY - midPoint.y / scaleLevel;
                    Log.i("xiaozhu", "before" + distance + "===" + scaleLevel + "===" + lastDistance + "==" + leftPoint.y + "===" + midPoint.y);

                    updatePoint();
                    lastDistance = distance;
                } else {

                    leftPoint.set(leftPoint.x + (lastX - event.getX()) / scaleLevel, leftPoint.y + (lastY - event.getY()) / scaleLevel);
                    updatePoint();

                    if (leftPoint.x < -maWidthOffset) {
                        leftPoint.x = -maWidthOffset;
                    } else if (leftPoint.x > 0 && bitmapSrc.getWidth() > width && leftPoint.x > bitmapSrc.getWidth() - width / scaleLevel + maWidthOffset) {
                        leftPoint.x = bitmapSrc.getWidth() - width / scaleLevel + maWidthOffset;
                    }

                    if (leftPoint.y < -maxHeightOffset) {
                        leftPoint.y = -maxHeightOffset;
                    } else if (leftPoint.y > 0 && bitmapSrc.getHeight() > height && leftPoint.y > bitmapSrc.getHeight() - height / scaleLevel + maxHeightOffset) {
                        leftPoint.y = bitmapSrc.getHeight() - height / scaleLevel + maxHeightOffset;
                    }

                    lastX = event.getX();
                    lastY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:

                if (leftPoint.x < 0 || leftPoint.y < 0 || (leftPoint.x > 0 && bitmapSrc.getWidth() > width && leftPoint.x > bitmapSrc.getWidth() - width / scaleLevel) || (leftPoint.y > 0 && bitmapSrc.getHeight() > height && leftPoint.y > bitmapSrc.getHeight() - height / scaleLevel)) {
                    isAnmiting = true;


                    startX = leftPoint.x;
                    startY = leftPoint.y;
                    endY = leftPoint.y;
                    endX = leftPoint.x;

                    if (leftPoint.x < 0) {
                        endX = 0;


                    }
                    if (leftPoint.y < 0) {
                        endY = 0;

                    }
                    if (leftPoint.x > 0 && bitmapSrc.getWidth() > width && leftPoint.x > bitmapSrc.getWidth() - width / scaleLevel) {
                        endX = bitmapSrc.getWidth() - width / scaleLevel;
                    }
                    if (leftPoint.y > 0 && bitmapSrc.getHeight() > height && leftPoint.y > bitmapSrc.getHeight() - height / scaleLevel) {
                        endY = bitmapSrc.getHeight() - height / scaleLevel;
                    }


                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                    valueAnimator.setDuration(animatingDuring);
                    valueAnimator.start();


                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            leftPoint.x = startX + (endX - startX) * Float.parseFloat(animation.getAnimatedValue().toString()) * 0.01f;
                            leftPoint.y = startY + (endY - startY) * Float.parseFloat(animation.getAnimatedValue().toString()) * 0.01f;
                            updatePoint();
                            requestLayout();
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            Log.i("xiaozhu", " onAnimationStart;");
                            isAnmiting = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Log.i("xiaozhu", " onAnimationEnd;");
                            isAnmiting = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            Log.i("xiaozhu", " onAnimationCancel;");
                            isAnmiting = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            Log.i("xiaozhu", " onAnimationRepeat;");

                        }
                    });
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i("xiaozhu", "ACTION_POINTER_DOWN" + isMultiPoint);
                isMultiPoint = true;
                lastDistance = calculateDistance(event);
                midPoint(midPoint, event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                Log.i("xiaozhu", "ACTION_POINTER_UP" + isMultiPoint);
                isMultiPoint = false;
                break;
        }

        requestLayout();
        return true;

    }

    /**
     * 更新四个顶点
     */
    private void updatePoint() {
        float x = leftPoint.x;
        float y = leftPoint.y;
        topPoint.set(x + width / scaleLevel, y);
        bottomPoint.set(x, y + height / scaleLevel);
        rightPoint.set(x + width / scaleLevel, y + height / scaleLevel);

    }

    // 计算两个触摸点之间的距离
    private float calculateDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算中点
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
