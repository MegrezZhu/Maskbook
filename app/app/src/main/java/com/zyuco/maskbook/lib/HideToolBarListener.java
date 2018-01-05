package com.zyuco.maskbook.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;

import com.zyuco.maskbook.R;

/**
 * Created by 82091 on 2018/1/5.
 */

public abstract class HideToolBarListener extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 20;
    private Context context;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    private int toolBarHeight;
    private int toolbarOffset = 0;
    public HideToolBarListener(Context context) {
        this.context = context;
        final TypedArray styledAttributes = this.context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        toolBarHeight = (int)styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (scrolledDistance < toolBarHeight) {
                onShow();
                controlsVisible = true;
            } else {
                if (controlsVisible) {
                    if (toolbarOffset > HIDE_THRESHOLD) {
                        setInvisible();
                    } else {
                        setVisible();
                    }
                } else {
                    if (toolbarOffset < -HIDE_THRESHOLD) {
                        setVisible();
                    } else {
                        setInvisible();
                    }
                }
            }
        }

    }

    private void setVisible() {
        if(toolbarOffset > 0) {
            onShow();
            toolbarOffset = 0;
        }
        controlsVisible = true;
    }

    private void setInvisible() {
        if(toolbarOffset < toolBarHeight) {
            onHide();
            toolbarOffset = toolBarHeight;
        }
        controlsVisible = false;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(toolbarOffset);
        if((toolbarOffset <toolBarHeight && dy > 0) || (toolbarOffset > 0 && dy < 0)) {
            toolbarOffset += dy;
        }
        if (scrolledDistance < 0) {
            scrolledDistance = 0;
        } else {
            scrolledDistance += dy;
        }
    }

    private void clipToolbarOffset() {
        if(toolbarOffset > toolBarHeight) {
            toolbarOffset = toolBarHeight;
        } else if(toolbarOffset < 0) {
            toolbarOffset = 0;
        }
    }

    public abstract void onHide();
    public abstract void onShow();
    public abstract void onMoved(int distance);
}
