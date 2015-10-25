package com.daviddetena.tapeando.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Table implements Serializable, Comparable<Table>{

    private int mTableNumber;
    private int mNumberOfFellows;
    private List<Course> mCourses;
    private String mNotes;

    public Table(int tableNumber, int numberOfFellows, List<Course> courses, String notes) {
        mTableNumber = tableNumber;
        mNumberOfFellows = numberOfFellows;
        mCourses = courses;
        mNotes = notes;
    }

    public Table(int tableNumber) {
        mTableNumber = tableNumber;
        mNumberOfFellows = 0;
        mCourses = new LinkedList<>();
        mNotes = "";
    }

    public int getTableNumber() {
        return mTableNumber;
    }

    public void setTableNumber(int tableNumber) {
        mTableNumber = tableNumber;
    }

    public int getNumberOfFellows() {
        return mNumberOfFellows;
    }

    public void setNumberOfFellows(int numberOfFellows) {
        mNumberOfFellows = numberOfFellows;
    }

    public List<Course> getCourses() {
        Collections.sort(mCourses);
        return mCourses;
    }

    public void setCourses(List<Course> Courses) {
        mCourses = Courses;
    }

    public void cleanTable(){
        mCourses = new LinkedList<>();
        mNumberOfFellows = 0;
    }

    @Override
    public String toString() {
        return String.valueOf(getTableNumber()) + ": " +String.valueOf(getNumberOfFellows());
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public void addCourse (Course Course){
        Course.setId(mCourses.size()+1);
        mCourses.add(Course);
    }

    public void addCourse (Course Course, String notes){
        Course.setId(mCourses.size()+1);
        Course.setNotes(notes);
        mCourses.add(Course);
    }

    public void updateCourse (Course Course){
        for (int i=0; i<mCourses.size(); i++) {
            Course auxCourse = mCourses.get(i);
            if (auxCourse.isTheSame(Course)){
                mCourses.set(i, Course);
                break;
            }
        }
    }

    public void removeCourse (Course Course){
        for (int i=0; i<mCourses.size(); i++) {
            Course auxCourse = mCourses.get(i);
            if (auxCourse.isTheSame(Course)){
                mCourses.remove(i);
                break;
            }
        }
    }

    public float getBill(){
        float total = 0;
        for (Course Course:mCourses) {
            total += Course.getPrice();
        }
        return total;
    }

    public boolean isTheSame (Table table){
        return this.getTableNumber() == table.getTableNumber();
    }

    @Override
    public int compareTo(Table another) {
        return ((Integer)getTableNumber()).compareTo(another.getTableNumber());
    }
}
