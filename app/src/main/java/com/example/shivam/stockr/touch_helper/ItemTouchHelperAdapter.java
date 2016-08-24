package com.example.shivam.stockr.touch_helper;

/**
 * Created by sam_chordas on 10/6/15.
 * credit to Paul Burke (ipaulpro)
 * Interface to enable swipe to delete
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
