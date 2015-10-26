package com.daviddetena.tapeando.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.TextView;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.adapter.CoursesAdapter;
import com.daviddetena.tapeando.model.Course;
import com.daviddetena.tapeando.model.Table;
import com.daviddetena.tapeando.model.Tables;

import java.lang.ref.WeakReference;
import java.util.List;

public class TableDetailFragment extends Fragment implements CoursesAdapter.OnCourseAdapterPressedListener{

    protected WeakReference<MenuFragment.OnCourseSelectedListener> mOnCourseSelectedListener;

    private RecyclerView mCourseList;
    private Table mCurrentTable;

    private Course mCoursePressed;

    private TextView mCourseCounterTextView;
    private TextView mTableNameTextView;
    private TextView mTableBillTextView;
    private TextView mNoCoursesTextView;

    public static final String ARG_TABLE_NUMBER = "tableNumber";

    public static TableDetailFragment newInstance(int currentTableNumber) {
        // 1) Nos creamos el fragment
        TableDetailFragment fragment = new TableDetailFragment();

        // 2) Nos creamos los argumentos y los empaquetamos
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_TABLE_NUMBER, currentTableNumber);

        // 3) Asignamos los argumentos al fragment y lo devolvemos ya creado
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Vamos a recoger el argumento que nos pasan al fragment
        if (getArguments() != null) {
            mCurrentTable = Tables.getInstance(getActivity()).getTable(getArguments().getInt(ARG_TABLE_NUMBER));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_table_detail, container, false);

        if (root.findViewById(R.id.table_recycler_list) != null) {
            mCourseList = (RecyclerView) root.findViewById(R.id.table_recycler_list);
            mCourseList.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCourseList.setItemAnimator(new DefaultItemAnimator());
        }

        // Get resources from layout
        mTableNameTextView = (TextView) root.findViewById(R.id.detail_table_name_text_view);
        mCourseCounterTextView = (TextView) root.findViewById(R.id.detail_table_course_number_text_view);
        mTableBillTextView = (TextView) root.findViewById(R.id.detail_table_bill_text_view);
        mNoCoursesTextView = (TextView) root.findViewById(R.id.no_courses);

        setScreenValues();

        return root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mOnCourseSelectedListener = new WeakReference<MenuFragment.OnCourseSelectedListener>((MenuFragment.OnCourseSelectedListener) getActivity());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnCourseSelectedListener = new WeakReference<MenuFragment.OnCourseSelectedListener>((MenuFragment.OnCourseSelectedListener) getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnCourseSelectedListener = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_table_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_clean) {
            Tables.getInstance(getActivity()).cleanTable(mCurrentTable.getTableNumber());
            return true;
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        if (item.getItemId() == R.id.action_delete){
            // Delete course from table
            Tables.getInstance(getActivity()).removeCourse(mCoursePressed, mCurrentTable.getTableNumber());
        }else{
            return false;
        }
        return true;
    }


    private void setScreenValues() {
        mTableNameTextView.setText(String.format("Mesa %d",mCurrentTable.getTableNumber()));
        mCourseCounterTextView.setText(String.format("%d platos",mCurrentTable.getCourses().size()));
        mTableBillTextView.setText(String.format(getActivity().getString(R.string.bill_format), mCurrentTable.getBill()));
        setCourses();
    }

    private void setCourses(){
        List<Course> courses = mCurrentTable.getCourses();
        if (mCurrentTable.getCourses().size() > 0) {
            mCourseList.swapAdapter(new CoursesAdapter(courses, getActivity(), R.menu.menu_context_table, this), false);
            showCourses();
        }
        else{
            hideCourses();
        }
    }

    private void showCourses() {
        mNoCoursesTextView.setVisibility(View.GONE);
        mCourseList.setVisibility(View.VISIBLE);
    }

    private void hideCourses() {
        mNoCoursesTextView.setVisibility(View.VISIBLE);
        mCourseList.setVisibility(View.GONE);
    }

    @Override
    public void onCourseAdapterLongPressed(Course course) {
        mCoursePressed = course;
    }

    @Override
    public void onCourseAdapterPressed(Course course) {
        // Avisar a la Activity para que lance la Activity de detalle de plato
        if (mOnCourseSelectedListener != null && mOnCourseSelectedListener.get() != null) {
            mOnCourseSelectedListener.get().onCourseSelectedListener(course);
        }
    }
}