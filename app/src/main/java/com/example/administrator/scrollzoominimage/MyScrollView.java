package com.example.administrator.scrollzoominimage;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/9/2 0002.
 */
public class MyScrollView extends ScrollView {
    private ViewGroup innerLayout;//ScrololView里的布局
    private View headerView;// 头布局 必须在ScrollView里面
    private int originalHeight;//头布局原始高度
    private float downY;//手指按下的Y坐标
    private View emputyView;//空的布局  用于占位符
    private View footerView;//底部布局


    private boolean isOpen;
    private boolean isOpening;

    protected final static float OFFSET_RADIO = 1.8f; // 偏移量
    protected final static float OPEN_RADIO = 1.8f; // 打开比例

    public MyScrollView(Context context) {
        this(context, null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //布局已经加载完成后调用  一些params参数在这里都能取到值了
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (childCount == 1) {
            innerLayout = (ViewGroup) getChildAt(0);

            emputyView = new LinearLayout(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            emputyView.setLayoutParams(lp);

            footerView = new LinearLayout(getContext());
            footerView.setLayoutParams(lp);

            innerLayout.addView(emputyView, 0);
            innerLayout.addView(footerView, innerLayout.getChildCount());
        } else {
            throw new RuntimeException("ScrollView 只能有一个子布局");
        }
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setOpening(boolean opening) {
        isOpening = opening;
    }

    public void setHeaderView(View headerView) {
        if (headerView != null) {
            this.headerView = headerView;
            originalHeight = headerView.getLayoutParams().height;
        }
    }

    public void setOpenViewListener(OpenViewListener openViewListener) {
        this.openViewListener = openViewListener;
    }

    OpenViewListener openViewListener;

    public interface OpenViewListener {
        public void openVeiw(View headerVeiw);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getRawY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float tempY = ev.getRawY();
                float delatY = tempY - downY;//手指竖直方向滑动的距离
                downY = tempY;
                float scrollY = getScrollY();//竖直方向 滚动的值
                float offset = innerLayout.getMeasuredHeight() - getHeight();//偏移量
                if (scrollY == 0 && delatY > 0) {//表示滑动到顶部了
                    int openOffset = (int) (delatY / OFFSET_RADIO);
                    if (headerView != null) {
                        int afterHeight = upDateViewHeight(headerView, openOffset);
                        if (afterHeight > originalHeight * OPEN_RADIO && openViewListener != null && !isOpen) {
                            //TODO  需要打开
                            isOpening = true;
                            openViewListener.openVeiw(headerView);
                        } else {
                            setViewHeight(headerView, afterHeight);
                        }
                    } else {
                        int afterHeight = upDateViewHeight(emputyView, openOffset);
                        setViewHeight(emputyView, afterHeight);
                    }
                }
                if (scrollY == offset && delatY < 0) {//滑动到底部了
                    int afterHeight = upDateViewHeight(footerView, (int) -delatY);
                    setViewHeight(footerView, afterHeight);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP://手指弹开
                //手指弹开 让布局的高度 从现在的高度变成0 使用动画 也可以使用Scroller  使用动画简单
                if (headerView != null && headerView.getHeight() > originalHeight && !isOpening) {
                    closeView(headerView, headerView.getHeight(), originalHeight);
                } else if (emputyView.getHeight() > 0) {
                    closeView(emputyView, emputyView.getHeight(), 0);
                }

                if (footerView.getHeight() > 0) {
                    closeView(footerView, footerView.getHeight(), 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }


    public void closeView(final View view, int fromHeight, final int toHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (int) valueAnimator.getAnimatedValue();
                view.getLayoutParams().height = height;
                view.setLayoutParams(view.getLayoutParams());
                if (view == headerView && height == toHeight) {
                    isOpen = false;
                    isOpening = false;
                }
            }
        });
        animator.start();
        animator.setDuration(300);
    }

    public void openView(final View view, int fromHeight, final int toHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (int) valueAnimator.getAnimatedValue();
                view.getLayoutParams().height = height;
                view.setLayoutParams(view.getLayoutParams());
                if (height == toHeight && view == headerView) {
                    isOpen = true;
                    isOpening = false;
                }
            }
        });
        animator.start();
        animator.setDuration(300);
    }


    /**
     * 改变 布局的高度
     *
     * @param view
     * @param upDateHeight 更新的高度
     * @return 改变后的高度
     */
    public int upDateViewHeight(View view, int upDateHeight) {
        int nowHeight = view.getLayoutParams().height;
        int afterHeight = nowHeight + upDateHeight;
        return afterHeight;
    }

    /**
     * 设置高度
     *
     * @param view
     * @param afterHeight
     */
    public void setViewHeight(View view, int afterHeight) {
        view.getLayoutParams().height = afterHeight;
        view.setLayoutParams(view.getLayoutParams());
    }
}
