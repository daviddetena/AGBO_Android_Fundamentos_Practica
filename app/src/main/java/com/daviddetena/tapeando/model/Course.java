package com.daviddetena.tapeando.model;

import java.io.Serializable;
import java.util.List;

public class Course implements Serializable, Comparable<Course>{

    private int mId;
    private int mMenuId;
    private String mName;
    private String mType;
    private String mPhoto;
    private String mPhotoUrl;
    private String mDescription;
    private List<Allergen> mAllergens;
    private float mPrice;
    private String mNotes;

    public Course(int id, int menuId, String name, String type, String photo, String photoUrl,
                  String description, List<Allergen> allergens, float price, String notes) {
        mId = id;
        mMenuId = menuId;
        mName = name;
        mType = type;
        mPhoto = photo;
        mPhotoUrl = photoUrl;
        mDescription = description;
        mAllergens = allergens;
        mPrice = price;
        mNotes = notes;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getMenuId() {
        return mMenuId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public List<Allergen> getAllergens() {
        return mAllergens;
    }

    public void setAllergens(List<Allergen> allergens) {
        mAllergens = allergens;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice = price;
    }

    public String getAllergensString(){
        String result = "";
        if (mAllergens.size() > 0){
            for (Allergen allergen : mAllergens) {
                result = result + allergen.getName() + ", ";
            }
            return result.substring(0, result.length() - 2);
        }
        return result;
    }

    @Override
    public int compareTo(Course another) {
        return ((Integer)getMenuId()).compareTo(another.getMenuId());
    }


    public boolean isTheSame (Course course){
        return (this.getMenuId() == course.getMenuId());
    }
}
