package com.daviddetena.tapeando.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.fragment.MenuFragment;
import com.daviddetena.tapeando.model.Course;
import com.daviddetena.tapeando.model.Table;
import com.daviddetena.tapeando.model.Tables;

public class MenuActivity extends AppCompatActivity implements MenuFragment.OnCourseAddedToTableListener, MenuFragment.OnCourseSelectedListener {

    static final int RESULT_COURSE_ADDED = 2;

    public static final String EXTRA_TABLE_NUMBER = "com.daviddetena.tapeando.activity.MenuActivity.EXTRA_TABLE_NUMBER";
    public static final String RESULT_TABLE_NUMBER = "com.daviddetena.tapeando.activity.MenuActivity.RESULT_TABLE_NUMBER";
    private Table mSelectedTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Recuperamos el numero de mesa seleccionada
        mSelectedTable = Tables.getInstance(this).getTable(getIntent().getIntExtra(EXTRA_TABLE_NUMBER, 0));

        // Asignamos la Toolbar como "ActionBar"
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ponemos la flecha de volver porque esta actividad siempre
        // viene de otra
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Insertamos el fragment con la lista de platos
        FragmentManager fm = getFragmentManager();
        if (findViewById(R.id.menu_list) != null) {
            if (fm.findFragmentById(R.id.menu_list) == null) {
                fm.beginTransaction()
                        .add(R.id.menu_list, MenuFragment.newInstance())
                        .commit();
            }
        }
    }

    @Override
    // Cuando se ha añadido un plato desde el detalle del plato
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            showSnackBarCourseAdded();
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
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() == 1 && getSupportActionBar() != null) {
            // Quitamos la flecha de volver
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Comprobamos si hay elementos en la pila pendientes de desapilar
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            Tables.getInstance(this).updateTable(mSelectedTable);
            Intent data = new Intent();
            data.putExtra(RESULT_TABLE_NUMBER, mSelectedTable.getTableNumber());
            setResult(Activity.RESULT_OK, data);
            super.onBackPressed();
        }
    }

    @Override
    public void onCourseAddedToTable(Course course, String notes) {
        Tables.getInstance(this).addCourse(course, notes, mSelectedTable.getTableNumber());
        //mSelectedTable.addCourse(course, notes);
        showSnackBarCourseAdded();
    }

    private void showSnackBarCourseAdded() {
        Snackbar.make(
                findViewById(R.id.menu_list),
                R.string.course_added_snackbar_message,
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCourseSelectedListener(Course course) {
        Intent courseDetailIntent = new Intent(this, CourseDetailActivity.class);
        courseDetailIntent.putExtra(CourseDetailActivity.EXTRA_COURSE, course);
        courseDetailIntent.putExtra(EXTRA_TABLE_NUMBER, mSelectedTable);
        startActivityForResult(courseDetailIntent, RESULT_COURSE_ADDED);
    }
}
