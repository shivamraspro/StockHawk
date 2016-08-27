package com.example.shivam.stockr.rest;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar.SnackbarLayout;
import android.util.AttributeSet;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

/**
 * Created by shivam on 27/08/16.
 *
 * This code is taken from https://github.com/ggajews/coordinatorlayoutwithfabdemo
 * so that the FAB moves up when there is a snackbar and comes down when it isn't
 */


public class FloatingActionButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    public FloatingActionButtonBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}
