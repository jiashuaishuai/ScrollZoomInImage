package com.example.administrator.scrollzoominimage;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/9/1 0001.
 */
public class MyView extends LinearLayout {
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private String TAG = "MyView";

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int llHeight;
    private LinearLayout.LayoutParams sc;
    private LinearLayout.LayoutParams ll;
    private int mDown;
    private int mUp;
    private int mMove;
    private int moveHeight;

    public void setView(ScrollView scrollView, LinearLayout linearLayout) {
        this.scrollView = scrollView;
        this.linearLayout = linearLayout;

        sc = (LayoutParams) scrollView.getLayoutParams();
        ll = (LayoutParams) linearLayout.getLayoutParams();
        llHeight = ll.height;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDown = (int) ev.getRawY();
                moveHeight = ll.height;
                break;
            case MotionEvent.ACTION_MOVE:

                if (scrollView.getScrollY() == 0) {
                    mMove = (int) (ev.getRawY() - mDown);

                    ll.height = moveHeight + (int) (mMove * 0.3);
                    if (ll.height >= llHeight) {
                        Log.e(TAG, "move--------");
                        ll.height = llHeight;
                    }
                    linearLayout.setLayoutParams(ll);
                    if (ll.height < llHeight / 3) {
                        ll.height=llHeight / 3;
                        return super.dispatchTouchEvent(ev);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                moveHeight = ll.height;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
