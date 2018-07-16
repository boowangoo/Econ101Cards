package io.github.boowangoo.econ101cards.custom_views.pagers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoBackViewPager extends ViewPager {

    private float origXVal;

    public NoBackViewPager(@NonNull Context context) {
        super(context);
    }

    public NoBackViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (this.canSwipe(e)) {
            return super.onTouchEvent(e);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.canSwipe(e)) {
            return super.onInterceptTouchEvent(e);
        }
        return false;
    }

    private boolean canSwipe(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_DOWN) {
            origXVal = e.getX();
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            return e.getX() < origXVal;
        }
        return true;
    }
}
