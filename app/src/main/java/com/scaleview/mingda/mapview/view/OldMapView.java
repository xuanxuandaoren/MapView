package com.scaleview.mingda.mapview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
 * Created by 玉光 on 2017-9-21.
 */

public class OldMapView extends FrameLayout {
    /**
     * 背景的图片
     */
    private int backgroundResId;
    /**
     * 坐标中心点的位置
     */
    private PointF centerPointf = new PointF();
    /**
     * 当前的背景图
     */
    private Bitmap bitmapSrc;
    /**
     * 缩放系数
     */
    private float scaleLevel = 2f;
    /**
     * 触摸事件上一个X坐标
     */
    private float lastX;
    /**
     * 触摸时间上一个Y坐标
     */
    private float lastY;
    /**
     * 垂直位置最大偏移量
     */
    private int maxHeightOffset = 225;
    /**
     * 水平位置最大偏移量
     */
    private int maWidthOffset = 150;
    /**
     * 事都在动画中
     */
    private boolean isAnmiting = false;
    /**
     * 动画开始的点X
     */
    private float startX;
    /**
     * 动画开始的点Y
     */
    private float startY;
    /**
     * 动画结束的点X
     */
    private float endX;
    /**
     * 动画结束的点X
     */
    private float endY;
    /**
     * 动画的时间
     */
    private long animatingDuring = 400;
    // 第一个按下的手指的点
    private PointF startPoint = new PointF();
    /**
     * 是否是多点触控
     */
    private boolean isMultiPoint = false;
    /**
     * 上一次双手的距离
     */
    private float lastDistance;

    public OldMapView(@NonNull Context context) {
        super(context);
        init();
    }

    public OldMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MapView);
        backgroundResId = typedArray.getResourceId(R.styleable.MapView_map_background, R.drawable.bg);

        init();
    }

    public OldMapView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ImageView backgroundMap = new ImageView(getContext());
        bitmapSrc = BitmapFactory.decodeResource(getResources(), backgroundResId);

        backgroundMap.setBackgroundResource(backgroundResId);
        addView(backgroundMap, 0, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (centerPointf.x == 0) {
            centerPointf.x = getMeasuredWidth() / 2;
            centerPointf.y = getMeasuredHeight() / 2;
        }

        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(bitmapSrc.getHeight(), MeasureSpec.EXACTLY);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(bitmapSrc.getWidth(), MeasureSpec.EXACTLY);

        getChildAt(0).measure((int) (childWidthMeasureSpec * scaleLevel), (int) (childHeightMeasureSpec * scaleLevel));


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int bitmapLeft = (int) (centerPointf.x - getMeasuredWidth() / 2);
        int bitmapTop = (int) (centerPointf.y - getMeasuredHeight() / 2);
        getChildAt(0).layout(bitmapLeft, bitmapTop, (int) (bitmapLeft + bitmapSrc.getWidth() * scaleLevel), (int) (bitmapTop + bitmapSrc.getHeight() * scaleLevel));

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);


        if (isAnmiting) {
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                startPoint.x = event.getX();
                startPoint.y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMultiPoint) {
                    float distance = distance(event);
                    scaleLevel = distance * scaleLevel / lastDistance;
//                    centerPointf.x = centerPointf.x +(1-scaleLevel)*bitmapSrc.getWidth()/2;
//
//                    centerPointf.y = centerPointf.y +(1-scaleLevel)*bitmapSrc.getHeight()/2;
//                    lastDistance =distance;
                    lastDistance=distance;
                } else {
                    startPoint.x = event.getX();
                    startPoint.y = event.getY();
                    centerPointf.x += event.getX() - lastX;
                    centerPointf.y += event.getY() - lastY;
                    if (centerPointf.x - getMeasuredWidth() / 2 > maWidthOffset) {
                        centerPointf.x = maWidthOffset + getMeasuredWidth() / 2;
                    } else if (centerPointf.x < getMeasuredWidth() / 2 && centerPointf.x < getMeasuredWidth() * 3 / 2 - maWidthOffset - bitmapSrc.getWidth() * scaleLevel) {
                        centerPointf.x = getMeasuredWidth() * 3 / 2 - maWidthOffset - bitmapSrc.getWidth() * scaleLevel;
                    }

                    if (centerPointf.y - getMeasuredHeight() / 2 > maxHeightOffset) {
                        centerPointf.y = maxHeightOffset + getMeasuredHeight() / 2;
                    } else if (centerPointf.y < getMeasuredHeight() / 2 && centerPointf.y < getMeasuredHeight() * 3 / 2 - maxHeightOffset - bitmapSrc.getHeight() * scaleLevel) {
                        centerPointf.y = getMeasuredHeight() * 3 / 2 - maxHeightOffset - bitmapSrc.getHeight() * scaleLevel;
                    }


                    lastX = event.getX();
                    lastY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:

                if (centerPointf.x - getMeasuredWidth() / 2 > 0 || centerPointf.y - getMeasuredHeight() / 2 > 0 || (centerPointf.x < getMeasuredWidth() / 2 && centerPointf.x < getMeasuredWidth() * 3 / 2 - bitmapSrc.getWidth() * scaleLevel) || (centerPointf.y < getMeasuredHeight() / 2 && centerPointf.y < getMeasuredHeight() * 3 / 2 - bitmapSrc.getHeight() * scaleLevel)) {
                    isAnmiting = true;
                    startX = centerPointf.x;
                    startY = centerPointf.y;
                    endY = centerPointf.y;
                    endX = centerPointf.x;

                    if (centerPointf.x - getMeasuredWidth() / 2 > 0) {
                        endX = getMeasuredWidth() / 2;


                    }
                    if (centerPointf.y - getMeasuredHeight() / 2 > 0) {
                        endY = getMeasuredHeight() / 2;

                    }
                    if (centerPointf.x < getMeasuredWidth() / 2 && centerPointf.x < getMeasuredWidth() * 3 / 2 - bitmapSrc.getWidth() * scaleLevel) {
                        endX = getMeasuredWidth() * 3 / 2 - bitmapSrc.getWidth() * scaleLevel;
                    }
                    if (centerPointf.y < getMeasuredHeight() / 2 && centerPointf.y < getMeasuredHeight() * 3 / 2 - bitmapSrc.getHeight() * scaleLevel) {
                        endY = getMeasuredHeight() * 3 / 2 - bitmapSrc.getHeight() * scaleLevel;
                    }


                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                    valueAnimator.setDuration(animatingDuring);
                    valueAnimator.start();


                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            centerPointf.x = startX + (endX - startX) * Float.parseFloat(animation.getAnimatedValue().toString()) * 0.01f;
                            centerPointf.y = startY + (endY - startY) * Float.parseFloat(animation.getAnimatedValue().toString()) * 0.01f;
                            requestLayout();
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                            isAnmiting = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            isAnmiting = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                            isAnmiting = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {


                        }
                    });
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                isMultiPoint = true;
                lastDistance = distance(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                isMultiPoint = false;
                break;
        }

        requestLayout();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
