package com.daviddetena.tapeando.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daviddetena.tapeando.R;

public class CourseView extends CardView{

    private int mCourseImage;
    private Bitmap mCourseBitmap;
    private String mCourseName;
    private String mCourseDescription;
    private String mCourseAllergens;
    private float mCoursePrice;

    private TextView mNameTextView;
    private TextView mDescriptionTextView;
    private TextView mPriceTextView;
    private TextView mAllergenTextView;
    private ImageView mCourseImageView;



    public CourseView(Context context) {
        this(context, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
    }

    public CourseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_course, this, true);

        mNameTextView = (TextView) findViewById(R.id.card_course_name);
        mDescriptionTextView = (TextView) findViewById(R.id.card_course_description);
        mPriceTextView = (TextView) findViewById(R.id.card_course_price);
        mCourseImageView = (ImageView) findViewById(R.id.card_course_image);
        mAllergenTextView = (TextView) findViewById(R.id.card_course_allergens);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CourseView, 0, 0);
        if (a != null) {
            mCourseImage = a.getInt(R.styleable.CourseView_image, R.drawable.icon_course);
            mCourseDescription = a.getString(R.styleable.CourseView_description);
            mCoursePrice = a.getFloat(R.styleable.CourseView_price, 0);
            mCourseName = a.getString(R.styleable.CourseView_name);
            mCourseAllergens = a.getString(R.styleable.CourseView_allergens);

            a.recycle();

            setCourseImage(mCourseImage);
            setCourseName(mCourseName);
            setCourseDescription(mCourseDescription);
            setCoursePrice(mCoursePrice);
            setCourseAllergens(mCourseAllergens);
        }
    }

    public int getCourseImage() {
        return mCourseImage;
    }

    public void setCourseImage(int courseImage) {
        mCourseImage = courseImage;
        mCourseImageView.setImageResource(mCourseImage);
    }

    public void setCourseBitmap(Bitmap courseBitmap) {
        mCourseBitmap = courseBitmap;
        mCourseImageView.setImageBitmap(mCourseBitmap);
    }

    public String getCourseName() {
        return mCourseName;
    }

    public void setCourseName(String courseName) {
        mCourseName = courseName;
        mNameTextView.setText(mCourseName);
    }

    public String getCourseDescription() {
        return mCourseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        mCourseDescription = courseDescription;
        mDescriptionTextView.setText(mCourseDescription);
    }

    public String getCourseAllergens() {
        return mCourseAllergens;
    }

    public void setCourseAllergens(String courseAllergens) {
        mCourseAllergens = courseAllergens;
        mAllergenTextView.setText(mCourseAllergens);
    }

    public float getCoursePrice() {
        return mCoursePrice;
    }

    public void setCoursePrice(float coursePrice) {
        mCoursePrice = coursePrice;
        mPriceTextView.setText(String.valueOf(String.format("%.2f €", mCoursePrice)));
    }

    public ImageView getCourseImageView() {
        return mCourseImageView;
    }

}
