package com.daviddetena.tapeando.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.adapter.CoursesAdapter;
import com.daviddetena.tapeando.model.Course;
import com.daviddetena.tapeando.model.Courses;
import com.daviddetena.tapeando.network.MenuDownloader;

import java.lang.ref.WeakReference;

public class MenuFragment extends Fragment implements MenuDownloader.OnCoursesReceivedListener,
        CoursesAdapter.OnCourseAdapterPressedListener,
        SetCourseNotesDialogFragment.OnCourseNotesSetListener {

    protected WeakReference<OnCourseAddedToTableListener> mOnCourseAddedToTableListener;
    protected WeakReference<OnCourseSelectedListener> mOnCourseSelectedListener;
    private Course mCoursePressed;

    private RecyclerView mCourseList;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        if (root.findViewById(R.id.menu_recycler_list) != null) {
            mCourseList = (RecyclerView) root.findViewById(R.id.menu_recycler_list);
            mCourseList.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCourseList.setItemAnimator(new DefaultItemAnimator());
        }
        else if (root.findViewById(R.id.menu_recycler_grid_2) != null){
            mCourseList = (RecyclerView) root.findViewById(R.id.menu_recycler_grid_2);
            mCourseList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mCourseList.setItemAnimator(new DefaultItemAnimator());
        }
        else if (root.findViewById(R.id.menu_recycler_grid_3) != null){
            mCourseList = (RecyclerView) root.findViewById(R.id.menu_recycler_grid_3);
            mCourseList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            mCourseList.setItemAnimator(new DefaultItemAnimator());
        }

        registerForContextMenu(mCourseList);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Courses.getInstance().getCourses().size() == 0) {
            downloadMenu();
        } else {
            setCourses();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mOnCourseAddedToTableListener = new WeakReference<OnCourseAddedToTableListener>((OnCourseAddedToTableListener) getActivity());
        mOnCourseSelectedListener = new WeakReference<OnCourseSelectedListener>((OnCourseSelectedListener) getActivity());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnCourseAddedToTableListener = new WeakReference<OnCourseAddedToTableListener>((OnCourseAddedToTableListener) getActivity());
        mOnCourseSelectedListener = new WeakReference<OnCourseSelectedListener>((OnCourseSelectedListener) getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnCourseAddedToTableListener = null;
        mOnCourseSelectedListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh_courses) {
            downloadMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        if (item.getItemId() == R.id.action_add) {
            launchCourseNotesDialog();
        } else {
            return false;
        }
        return true;
    }

    private void launchCourseNotesDialog(){
        showSetCourseNotesDialog();
    }

    // Set fellows management
    protected void showSetCourseNotesDialog() {
        SetCourseNotesDialogFragment dialog = new SetCourseNotesDialogFragment();
        dialog.setOnCoursesNotesSetListener(this);
        dialog.show(getFragmentManager(), null);
    }

    private void downloadMenu() {
        showLoading();
        AsyncTask<String, Integer, Courses> coursesTask = new MenuDownloader(this);
        coursesTask.execute();
    }

    private void setCourses() {
        Courses courses = Courses.getInstance();
        if (courses.getCourses().size() > 0) {
            mCourseList.swapAdapter(new CoursesAdapter(courses.getCourses(), getActivity(), R.menu.menu_context_courses, this), false);
        }
    }

    public void showLoading() {
        if (getView() != null) {
            if (getView().findViewById(R.id.menu_recycler_list) != null){
                getView().findViewById(R.id.menu_recycler_list).setVisibility(View.GONE);
            }
            else if (getView().findViewById(R.id.menu_recycler_grid_2) != null){
                getView().findViewById(R.id.menu_recycler_grid_2).setVisibility(View.GONE);
            }
            else if (getView().findViewById(R.id.menu_recycler_grid_3) != null){
                getView().findViewById(R.id.menu_recycler_grid_3).setVisibility(View.GONE);
            }
            getView().findViewById(R.id.loading).setVisibility(View.VISIBLE);
        }
    }

    public void showCourses() {
        if (getView() != null) {
            if (getView().findViewById(R.id.menu_recycler_list) != null){
                getView().findViewById(R.id.menu_recycler_list).setVisibility(View.VISIBLE);
            }
            else if (getView().findViewById(R.id.menu_recycler_grid_2) != null){
                getView().findViewById(R.id.menu_recycler_grid_2).setVisibility(View.VISIBLE);
            }
            else if (getView().findViewById(R.id.menu_recycler_grid_3) != null){
                getView().findViewById(R.id.menu_recycler_grid_3).setVisibility(View.VISIBLE);
            }
            getView().findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }

    @Override
    public void onCoursesReceivedListener() {
        setCourses();
        showCourses();
    }

    @Override
    public void onCourseAdapterLongPressed(Course course) {
        mCoursePressed = course;
    }

    @Override
    public void onCourseAdapterPressed(Course course) {
        // Avisamos a la Activity de que se ha seleccionado un plato, para ver el detalle
        mOnCourseSelectedListener.get().onCourseSelectedListener(course);
    }

    @Override
    public void onCourseNotesSetListener(SetCourseNotesDialogFragment dialog, String courseNotes) {
        // Avisamos a la Activity de que se ha añadido un plato
        if (mOnCourseAddedToTableListener != null && mOnCourseAddedToTableListener.get() != null) {
            mOnCourseAddedToTableListener.get().onCourseAddedToTable(mCoursePressed, courseNotes);
        }
        dialog.dismiss();
    }

    @Override
    public void onCourseNotesCancelListener(SetCourseNotesDialogFragment dialog) {
        dialog.dismiss();
    }

    public interface OnCourseAddedToTableListener {
        void onCourseAddedToTable(Course course, String notes);
    }

    public interface OnCourseSelectedListener {
        void onCourseSelectedListener(Course course);
    }
}
