package com.example.shivam.stockr.rest;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Created by shivam on 29/08/16.
 */
public class GraphData {
    public ArrayList<Entry> graphDataEntries;
    public ArrayList<String> dates;

    GraphData() {
        graphDataEntries = new ArrayList<>();
        dates = new ArrayList<>();
    }
}
