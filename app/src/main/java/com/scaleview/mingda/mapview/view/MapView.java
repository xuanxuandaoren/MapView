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
import android.support.v4.os.TraceCompat;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.scaleview.mingda.mapview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


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
    /**
     * 储存holder的缓存
     */
    private HashMap<Integer, ArrayList<ViewHolder>> mHolderPool = new HashMap<>();
    /**
     * 储存添加在控件里面的控件
     */
    private HashMap<Integer, ViewHolder> addedPools = new HashMap<>();
    /**
     * 适配器
     */
    private Adapter mAdapter;
    /**
     * 背景
     */
    private ImageView backgroundMap;

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
        backgroundMap = new ImageView(getContext());
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

//        for (int i = 1; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            int tag = (int) child.getTag();
//            PointF position = mAdapter.onBindPosition(tag);
//            if (!isInScreen(position)) {
//
//                if (mHolderPool.get(mAdapter.getItemViewType(tag)) == null) {
//                    mHolderPool.put(mAdapter.getItemViewType(tag), new ArrayList<ViewHolder>());
//                }
//                mHolderPool.get(mAdapter.getItemViewType(tag)).add(addedPools.get(tag));
//
//                removeView(child);
//                addedPools.remove(tag);
//            }
//            removeViewAt(i);
//        }

//        if (mAdapter != null) {
//            for (int i = 0; i < mAdapter.getItemCount(); i++) {
//                int itemViewType = mAdapter.getItemViewType(i);
//
//                PointF position = mAdapter.onBindPosition(i);
//
//                if (isInScreen(position)) {
//                    if (addedPools.get(i) != null) {
//                        return;
//                    }
//                    ViewHolder holder = getHolder(itemViewType);
//                    Log.i("xiaozhu", holder + "");
//                    mAdapter.onBindViewHolder(holder, i);
//                    if (holder.itemView.getParent() == null) {
//                        addView(holder.itemView);
//                        holder.itemView.setTag(i);
//                    }
//                }
//            }
        super.onLayout(changed, left, top, right, bottom);
        float bitmapLeft = -leftPoint.x * scaleLevel;
        float bitmapTop = -leftPoint.y * scaleLevel;
        backgroundMap.layout((int) bitmapLeft, (int) bitmapTop, (int) (bitmapLeft + bitmapSrc.getWidth() * scaleLevel), (int) (bitmapTop + bitmapSrc.getHeight() * scaleLevel));
        if (mAdapter != null) {
            for (int i = 1; i < getChildCount(); i++) {
                int tag = (int) getChildAt(i).getTag();
                PointF position = mAdapter.onBindPosition(tag);
                int viewLeft = (int) ((position.x - leftPoint.x) * scaleLevel);
                int viewTop = (int) ((position.y - leftPoint.y) * scaleLevel);
                getChildAt(i).layout(viewLeft, viewTop, viewLeft + getChildAt(i).getMeasuredWidth(), viewTop + getChildAt(i).getMeasuredHeight());

            }

        }

//        Log.i("xiaozhu", getChildCount() + "getChildCount");
    }


    /**
     * 判断点是否在区域里
     *
     * @param position
     * @return
     */

    private boolean isInScreen(PointF position) {
        return position.x > leftPoint.x && position.y > leftPoint.y && position.x < rightPoint.x && position.y < rightPoint.y;
    }

    /**
     * 获取holder
     *
     * @param itemViewType
     * @return
     */
    private ViewHolder getHolder(int itemViewType) {
        ViewHolder holder;
        if (mHolderPool.get(itemViewType) == null || mHolderPool.get(itemViewType).size() == 0) {
            holder = mAdapter.onCreateViewHolder(MapView.this, itemViewType);
        } else {
            holder = mHolderPool.get(itemViewType).get(0);
            mHolderPool.get(itemViewType).remove(0);
        }

        return holder;

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

                break;
            case MotionEvent.ACTION_MOVE:

                if (isMultiPoint) {
                    float distance = calculateDistance(event);
                    float centerX = leftPoint.x + midPoint.x / scaleLevel;
                    float centerY = leftPoint.y + midPoint.y / scaleLevel;

                    scaleLevel = distance * scaleLevel / lastDistance;

                    leftPoint.x = centerX - midPoint.x / scaleLevel;
                    leftPoint.y = centerY - midPoint.y / scaleLevel;


                    updatePoint();
                    lastDistance = distance;
                } else {

                    leftPoint.set(leftPoint.x + (lastX - event.getX()) / scaleLevel, leftPoint.y + (lastY - event.getY()) / scaleLevel);


                    if (leftPoint.x < -maWidthOffset) {
                        leftPoint.x = -maWidthOffset;
                    } else if (leftPoint.x > 0 && bitmapSrc.getWidth() * scaleLevel > width && leftPoint.x > bitmapSrc.getWidth() - width / scaleLevel + maWidthOffset) {
                        leftPoint.x = bitmapSrc.getWidth() - width / scaleLevel + maWidthOffset;
                    }

                    if (leftPoint.y < -maxHeightOffset) {
                        leftPoint.y = -maxHeightOffset;
                    } else if (leftPoint.y > 0 && bitmapSrc.getHeight() * scaleLevel > height && leftPoint.y > bitmapSrc.getHeight() - height / scaleLevel + maxHeightOffset) {
                        leftPoint.y = bitmapSrc.getHeight() - height / scaleLevel + maxHeightOffset;
                    }

                    updatePoint();

                    lastX = event.getX();
                    lastY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:

                if (leftPoint.x < 0 || leftPoint.y < 0 || (leftPoint.x > 0 && bitmapSrc.getWidth() * scaleLevel > width && leftPoint.x > bitmapSrc.getWidth() - width / scaleLevel) || (leftPoint.y > 0 && bitmapSrc.getHeight() * scaleLevel > height && leftPoint.y > bitmapSrc.getHeight() - height / scaleLevel)) {
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
                    if (leftPoint.x > 0 && bitmapSrc.getWidth() * scaleLevel > width && leftPoint.x > bitmapSrc.getWidth() - width / scaleLevel) {
                        endX = bitmapSrc.getWidth() - width / scaleLevel;
                    }
                    if (leftPoint.y > 0 && bitmapSrc.getHeight() * scaleLevel > height && leftPoint.y > bitmapSrc.getHeight() - height / scaleLevel) {
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
                lastDistance = calculateDistance(event);
                midPoint(midPoint, event);
                break;

            case MotionEvent.ACTION_POINTER_UP:

                isMultiPoint = false;
                break;
        }

        requestLayout();
        return true;

    }

    /**
     * 更新数据
     */
    public void notifyDataChanged() {
        removeAllViews();
        addView(backgroundMap, 0);
        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                int itemViewType = mAdapter.getItemViewType(i);

                ViewHolder holder = getHolder(itemViewType);
                Log.i("xiaozhu", holder + "");
                mAdapter.onBindViewHolder(holder, i);
                if (holder.itemView.getParent() == null) {
                    addView(holder.itemView);
                    holder.itemView.setTag(i);
                }
            }
        }
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

    /**
     * 设置 适配器
     *
     * @param mAdapter
     */
    public void setAdapter(Adapter mAdapter) {
        this.mAdapter = mAdapter;
        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                int itemViewType = mAdapter.getItemViewType(i);

                PointF position = mAdapter.onBindPosition(i);


                ViewHolder holder = getHolder(itemViewType);
                Log.i("xiaozhu", holder + "");
                mAdapter.onBindViewHolder(holder, i);
                if (holder.itemView.getParent() == null) {
                    addView(holder.itemView);
                    holder.itemView.setTag(i);
                }
            }
        }
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public abstract static class Adapter<VH extends MapView.ViewHolder> {
        /**
         * an item.
         * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p>
         * The new ViewHolder will be used to display items of the adapter using
         * <p>
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary {@link View#findViewById(int)} calls.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         */
        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * <p>
         * position.
         * <p>
         * Note that unlike {@link android.widget.ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * <p>
         * have the updated adapter position.
         * <p>
         * handle efficient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        public abstract void onBindViewHolder(VH holder, int position);

        /**
         * 返回这个点所在的坐标
         *
         * @param position
         * @return
         */
        public abstract PointF onBindPosition(int position);


        /**
         * @param position position to query
         * @return integer value identifying the type of the view needed to represent the item at
         * <code>position</code>. Type codes need not be contiguous.
         */
        public int getItemViewType(int position) {
            return 0;
        }


        /**
         * @param position Adapter position to query
         * @return the stable ID of the item at position
         */
        public long getItemId(int position) {
            return NO_ID;
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        public abstract int getItemCount();


        /**
         * @see #notifyItemChanged(int)
         * @see #notifyItemRemoved(int)
         */
        public final void notifyDataSetChanged() {
        }

        /**
         * @param position Position of the item that has changed
         */
        public final void notifyItemChanged(int position) {

        }


        /**
         * @param fromPosition Previous position of the item.
         * @param toPosition   New position of the item.
         */
        public final void notifyItemMoved(int fromPosition, int toPosition) {

        }


        /**
         * @param position Position of the item that has now been removed
         */
        public final void notifyItemRemoved(int position) {

        }

    }

    public static abstract class ViewHolder {

        public final View itemView;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }
    }

}