package com.daviddetena.tapeando.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.fragment.CourseDetailFragment;
import com.daviddetena.tapeando.model.Course;
import com.daviddetena.tapeando.model.Table;
import com.daviddetena.tapeando.model.Tables;

public class CourseDetailActivity extends AppCompatActivity implements CourseDetailFragment.OnCourseNotesChangedListener{

    public static final String EXTRA_COURSE = "com.daviddetena.tapeando.activity.CourseDetailActivity.EXTRA_COURSE";
    public static final String EXTRA_IS_UPDATING_COURSE = "com.daviddetena.tapeando.activity.CourseDetailActivity.EXTRA_IS_UPDATING_COURSE";

    private FloatingActionButton mAddCourseButton;
    private Course mCourse;
    private Table mSelectedTable;

    private boolean mIsUpdatingCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Asignamos la Toolbar como "ActionBar"
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ponemos la flecha de volver porque esta actividad siempre
        // viene de otra
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get course/table from bundle arguments
        mCourse = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);
        mSelectedTable = (Table) getIntent().getSerializableExtra(MenuActivity.EXTRA_TABLE_NUMBER);
        mIsUpdatingCourse = getIntent().getBooleanExtra(EXTRA_IS_UPDATING_COURSE, false);

        // Obtenemos la referencia al FAB para decirle qué pasa si lo pulsan
        mAddCourseButton = (FloatingActionButton) findViewById(R.id.add_course_button);
        if (mAddCourseButton != null) {
            mAddCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Añadir plato a la mesa
                    if (mIsUpdatingCourse){
                        Tables.getInstance(CourseDetailActivity.this).updateCourse(mCourse, mSelectedTable.getTableNumber());
                        //getTable(mSelectedTable.getTableNumber()).updateCourse(mCourse);
                    }
                    else{
                        Tables.getInstance(CourseDetailActivity.this).addCourse(mCourse, mSelectedTable.getTableNumber());
                        //.getTable(mSelectedTable.getTableNumber()).addCourse(mCourse);
                    }

                    //Volver a lista de platos del menu: avisando a menuActivity
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);
                    CourseDetailActivity.this.onBackPressed();
                }
            });
        }

        //Insertamos el fragment con el detalle del plato
        FragmentManager fm = getFragmentManager();
        if (findViewById(R.id.course_detail) != null) {
            if (fm.findFragmentById(R.id.course_detail) == null) {
                fm.beginTransaction()
                        .add(R.id.course_detail, CourseDetailFragment.newInstance(mCourse))
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCourseNotesChanged(Course course) {
        mCourse = course;
    }
}
