package com.daviddetena.tapeando.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.fragment.MenuFragment;
import com.daviddetena.tapeando.fragment.TableDetailFragment;
import com.daviddetena.tapeando.fragment.TableListFragment;
import com.daviddetena.tapeando.fragment.TablePagerFragment;
import com.daviddetena.tapeando.model.Course;
import com.daviddetena.tapeando.model.Courses;
import com.daviddetena.tapeando.model.Table;
import com.daviddetena.tapeando.model.Tables;
import com.daviddetena.tapeando.network.MenuDownloader;

public class MainActivity extends AppCompatActivity
        implements  MenuDownloader.OnCoursesReceivedListener,
                    TableListFragment.OnTableSelectedListener,
                    TablePagerFragment.OnTablePageChangedListener,
                    MenuFragment.OnCourseSelectedListener {

    static final int RESULT_UPDATED_TABLE = 1;
    static final int RESULT_COURSE_UPDATED = 3;

    private FloatingActionButton mAddCourseButton;
    private Table mSelectedTable;

    private boolean shouldShowFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asignamos la Toolbar como "ActionBar"
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Inicializamos la mesa seleccionada con la primera mesa disponible
        mSelectedTable = Tables.getInstance(this).getTables().get(0);

        // Obtenemos la referencia al FAB para decirle qué pasa si lo pulsan
        mAddCourseButton = (FloatingActionButton) findViewById(R.id.add_course_button);
        if (mAddCourseButton != null) {
            mAddCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent menuIntent = new Intent(MainActivity.this, MenuActivity.class);
                    menuIntent.putExtra(MenuActivity.EXTRA_TABLE_NUMBER, mSelectedTable.getTableNumber());
                    startActivityForResult(menuIntent, RESULT_UPDATED_TABLE);
                }
            });

            mAddCourseButton.setVisibility(View.GONE);
        }

        //Insertamos el fragment con la lista de mesas
        FragmentManager fm = getFragmentManager();
        if (findViewById(R.id.table_list) != null) {
            if (fm.findFragmentById(R.id.table_list) == null) {
                fm.beginTransaction()
                        .add(R.id.table_list, TableListFragment.newInstance())
                        .commit();
            }
        }

        //Insertamos el fragment con el detalle de la mesa seleccionada (si existe el FrameLayout "table_detail" en el xml de layout)
        if (findViewById(R.id.table_detail) != null) {
            if (fm.findFragmentById(R.id.table_detail) == null) {
                fm.beginTransaction()
                        .add(R.id.table_detail, TablePagerFragment.newInstance(mSelectedTable.getTableNumber()))
                        .commit();
            }
            shouldShowFab = true;
        }
        else if (fm.findFragmentById(R.id.table_detail) != null) {
            fm.findFragmentById(R.id.table_detail).setMenuVisibility(false);
            shouldShowFab = false;
        }
        handleFAB();
        // Descargamos el menu (asynctask)
        downloadMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_UPDATED_TABLE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK){
                mSelectedTable = Tables.getInstance(this).getTable(data.getIntExtra(MenuActivity.RESULT_TABLE_NUMBER, 0));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleFAB();
    }

    @Override
    public void onCoursesReceivedListener() {
        // Vuelta del AsyncTask al terminar de descargar los platos
        Snackbar.make(
                findViewById(R.id.coordinator),
                R.string.courses_downloaded,
                Snackbar.LENGTH_LONG).show();

        handleFAB();
    }

    @Override
    public void onTableSelected(Table table, int index) {
        mSelectedTable = table;
        FragmentManager fm = getFragmentManager();
        if (findViewById(R.id.table_detail) != null) {
            // Se carga el fragment de detalle de mesa con los datos de la mesa pulsada
            if (fm.findFragmentById(R.id.table_detail) != null){
                ((TablePagerFragment)fm.findFragmentById(R.id.table_detail)).goToTable(mSelectedTable.getTableNumber());
            }
        } else {
            // No existe un hueco "table_detail", por lo que lanzamos la activity
            Intent tableDetailIntent = new Intent(MainActivity.this, TableDetailActivity.class);
            tableDetailIntent.putExtra(TableDetailFragment.ARG_TABLE_NUMBER, mSelectedTable.getTableNumber());
            startActivity(tableDetailIntent);

        }
        handleFAB();
    }

    @Override
    public void onTablePageChanged(Table table, int index) {
        mSelectedTable = table;
    }

    private void downloadMenu() {
        // Le estamos pasando al AsyncTask el listener en el constructor (this)
        if (Courses.getInstance().getCourses().size() == 0){
            AsyncTask<String, Integer, Courses> coursesTask = new MenuDownloader(this);
            coursesTask.execute();
        }
    }

    private void handleFAB() {
        if (shouldShowFab && Courses.getInstance().getCourses().size() > 0) {
            showFAB();
        } else {
            hideFAB();
        }
    }

    private void showFAB() {
        Handler showAddButton = new Handler();
        showAddButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAddCourseButton != null) {
                    mAddCourseButton.show();
                }
            }
        }, 1000);
    }

    private void hideFAB() {
        if (mAddCourseButton != null) {
            mAddCourseButton.hide();
        }
    }

    @Override
    public void onCourseSelectedListener(Course course) {
        Intent courseDetailIntent = new Intent(this, CourseDetailActivity.class);
        courseDetailIntent.putExtra(CourseDetailActivity.EXTRA_COURSE, course);
        courseDetailIntent.putExtra(MenuActivity.EXTRA_TABLE_NUMBER, mSelectedTable);
        courseDetailIntent.putExtra(CourseDetailActivity.EXTRA_IS_UPDATING_COURSE, true);
//        startActivityForResult(menuIntent, RESULT_COURSE_UPDATED);
        startActivity(courseDetailIntent);
    }
}
