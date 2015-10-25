package com.daviddetena.tapeando.model;

import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Tables {

    public static final String TABLE_LIST_CHANGED_ACTION = "com.daviddetena.tapeando.model.Tables.TABLE_LIST_CHANGED_ACTION";

    // Variable for Singleton
    private static Tables sInstance;
    private WeakReference<Context> mContext;
    private List<Table> mTables;

    /**
     * Static method to get the singleton of Tables
     * @param context
     * @return
     */
    public static Tables getInstance(Context context) {
        if (sInstance == null || sInstance.mContext.get() == null) {
            if (sInstance == null) {
                sInstance = new Tables(context);
            } else if (sInstance.mContext.get() == null) {
                sInstance.mContext = new WeakReference<>(context);
            }
        }
        return sInstance;
    }

    public Tables(Context context) {
        mContext = new WeakReference<>(context);
        mTables = new ArrayList<>();
        // Dummy data: 12 tables
        for(int i=0; i<12; i++){
            mTables.add(new Table(i));
        }
        Collections.sort(mTables);
    }

    public List<Table> getTables() {
        return mTables;
    }

    public void cleanAllTables() {
        for (int i = 0; i < mTables.size(); i++) {
            Table table = mTables.get(i);
            table.cleanTable();
            mTables.set(i, table);
        }
        sendDataSetChangedIntent();
    }

    public void cleanTable(int tableNumber) {
        for (int i = 0; i < mTables.size(); i++) {
            Table table = mTables.get(i);
            if (table.getTableNumber() == tableNumber) {
                table.cleanTable();
                mTables.set(i, table);
                sendDataSetChangedIntent();
            }
        }
    }

    protected void sendDataSetChangedIntent() {
        if (mContext.get() != null) {
            Intent broadcast = new Intent(TABLE_LIST_CHANGED_ACTION);
            mContext.get().sendBroadcast(broadcast);
        }
    }


    public Table getTable(int tableNumber) {
        Table desiredTable = null;
        for (int i = 0; i < mTables.size(); i++) {
            Table table = mTables.get(i);
            if (table.getTableNumber() == tableNumber) {
                desiredTable = table;
                break;
            }
        }
        return desiredTable;
    }

    public void updateTable(Table table){
        for (int i = 0; i < mTables.size(); i++) {
            if (mTables.get(i).isTheSame(table)) {
                mTables.set(i, table);
                sendDataSetChangedIntent();
                break;
            }
        }
    }

    public void addCourse(Course course, int tableNumber){
        for (int i = 0; i < mTables.size(); i++) {
            Table table = mTables.get(i);
            if (table.getTableNumber() == tableNumber) {
                table.addCourse(course);
                sendDataSetChangedIntent();
                break;
            }
        }
    }

    public void addCourse(Course course, String notes, int tableNumber){
        for (int i = 0; i < mTables.size(); i++) {
            Table table = mTables.get(i);
            if (table.getTableNumber() == tableNumber) {
                table.addCourse(course, notes);
                sendDataSetChangedIntent();
                break;
            }
        }
    }

    public void removeCourse(Course course, int tableNumber){
        for (int i = 0; i < mTables.size(); i++) {
            Table table = mTables.get(i);
            if (table.getTableNumber() == tableNumber) {
                table.removeCourse(course);
                sendDataSetChangedIntent();
                break;
            }
        }
    }

    public void updateCourse(Course course, int tableNumber){
        for (int i = 0; i < mTables.size(); i++) {
            Table table = mTables.get(i);
            if (table.getTableNumber() == tableNumber) {
                table.updateCourse(course);
                sendDataSetChangedIntent();
                break;
            }
        }
    }
}
