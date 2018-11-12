package me.dkzwm.widget.srl.utils;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import me.dkzwm.widget.srl.ILifecycleObserver;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;

/**
 * Created by dkzwm on 2017/12/18.
 *
 * @author dkzwm
 */
public class QuickConfigAppBarUtil implements ILifecycleObserver, AppBarLayout.OnOffsetChangedListener
        , SmoothRefreshLayout.OnHeaderEdgeDetectCallBack
        , SmoothRefreshLayout.OnFooterEdgeDetectCallBack {
    private int mMinOffset;
    private int mOffset = -1;
    private boolean mFullyExpanded;
    private SmoothRefreshLayout mRefreshLayout;

    @Override
    public void onAttached(SmoothRefreshLayout layout) {
        mRefreshLayout = layout;
        CoordinatorLayout coordinatorLayout = findCoordinatorLayout(layout);
        if (coordinatorLayout == null)
            return;
        AppBarLayout appBarLayout = findAppBarLayout(coordinatorLayout);
        if (appBarLayout == null)
            return;
        appBarLayout.addOnOffsetChangedListener(this);
        layout.setOnHeaderEdgeDetectCallBack(this);
        layout.setOnFooterEdgeDetectCallBack(this);
    }

    @Override
    public void onDetached(SmoothRefreshLayout layout) {
        layout.setOnFooterEdgeDetectCallBack(null);
        layout.setOnHeaderEdgeDetectCallBack(null);
        mRefreshLayout = null;
        CoordinatorLayout coordinatorLayout = findCoordinatorLayout(layout);
        if (coordinatorLayout == null)
            return;
        AppBarLayout appBarLayout = findAppBarLayout(coordinatorLayout);
        if (appBarLayout == null)
            return;
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mOffset = verticalOffset;
        mFullyExpanded = (appBarLayout.getHeight() - appBarLayout.getBottom()) == 0;
        mMinOffset = Math.min(mOffset, mMinOffset);
    }

    private CoordinatorLayout findCoordinatorLayout(ViewGroup group) {
        CoordinatorLayout layout = null;
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i) instanceof CoordinatorLayout) {
                layout = (CoordinatorLayout) group.getChildAt(i);
                break;
            }
        }
        return layout;
    }

    private AppBarLayout findAppBarLayout(ViewGroup group) {
        AppBarLayout layout = null;
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i) instanceof AppBarLayout) {
                layout = (AppBarLayout) group.getChildAt(i);
                break;
            }
        }
        return layout;
    }

    @Override
    public boolean isNotYetInEdgeCannotMoveHeader(SmoothRefreshLayout parent,
                                                  @Nullable View child,
                                                  @Nullable IRefreshView header) {
        View targetView = parent.getScrollTargetView();
        if (targetView == null)
            targetView = child;
        return !mFullyExpanded || ScrollCompat.canChildScrollUp(targetView);
    }

    @Override
    public boolean isNotYetInEdgeCannotMoveFooter(SmoothRefreshLayout parent,
                                                  @Nullable View child,
                                                  @Nullable IRefreshView footer) {
        View targetView = parent.getScrollTargetView();
        if (targetView == null)
            targetView = child;
        return mMinOffset != mOffset || ScrollCompat.canChildScrollDown(targetView);
    }
}