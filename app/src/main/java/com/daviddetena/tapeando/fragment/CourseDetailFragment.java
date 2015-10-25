package com.daviddetena.tapeando.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.model.Course;
import com.daviddetena.tapeando.network.ImageDownloader;

import java.lang.ref.WeakReference;

public class CourseDetailFragment extends Fragment{

    private static final String ARG_COURSE = "course";

    protected WeakReference<OnCourseNotesChangedListener> mOnCourseNotesChangedListener;

    private Course mCourse;

    private TextView mCourseTitle;
    private ImageView mCourseImage;
    private TextView mCourseAllergens;
    private TextView mCourseDescription;
    private TextView mCourseNotes;

    public static CourseDetailFragment newInstance(Course course) {
        CourseDetailFragment fragment = new CourseDetailFragment();

        // 2) Nos creamos los argumentos y los empaquetamos
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_COURSE, course);

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
            mCourse = (Course) getArguments().getSerializable(ARG_COURSE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_course_detail, container, false);

        mCourseTitle = (TextView) root.findViewById(R.id.course_detail_title);
        mCourseImage = (ImageView) root.findViewById(R.id.course_detail_image);
        mCourseAllergens = (TextView) root.findViewById(R.id.course_detail_allergens);
        mCourseDescription = (TextView) root.findViewById(R.id.course_detail_description);
        mCourseNotes = (TextView) root.findViewById(R.id.course_detail_notes);

        mCourseTitle.setText(mCourse.getName());
        mCourseAllergens.setText(mCourse.getAllergensString());
        mCourseDescription.setText(mCourse.getDescription());
        mCourseNotes.setText(mCourse.getNotes());

        mCourseNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: avisar a la activity para que actualice el mCourse
                if (mOnCourseNotesChangedListener != null && mOnCourseNotesChangedListener.get() != null) {
                    mCourse.setNotes(mCourseNotes.getText().toString());
                    mOnCourseNotesChangedListener.get().onCourseNotesChanged(mCourse);
                }
            }
        });

        ImageDownloader.ImageDownloaderParams params = new ImageDownloader.ImageDownloaderParams(mCourse.getPhotoUrl(), mCourse.getPhoto());
        ImageDownloader imageDownloader = new ImageDownloader(getActivity(), mCourseImage, R.drawable.icon_course);
        imageDownloader.execute(params);

        /*
        final ArrayAdapter<Ingredient> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_ingredient, mCourse.getIngredients());
        mIngredientsList.setAdapter(adapter);
        */

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mOnCourseNotesChangedListener = new WeakReference<OnCourseNotesChangedListener>((OnCourseNotesChangedListener) getActivity());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnCourseNotesChangedListener = new WeakReference<OnCourseNotesChangedListener>((OnCourseNotesChangedListener) getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnCourseNotesChangedListener = null;
    }

    public interface OnCourseNotesChangedListener {
        void onCourseNotesChanged(Course course);
    }

}
