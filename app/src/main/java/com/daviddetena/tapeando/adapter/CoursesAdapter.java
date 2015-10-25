package com.daviddetena.tapeando.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.model.Course;
import com.daviddetena.tapeando.network.ImageDownloader;
import com.daviddetena.tapeando.view.CourseView;

import java.lang.ref.WeakReference;
import java.util.List;


public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder> {

    protected WeakReference<OnCourseAdapterPressedListener> mOnCourseAdapterPressedListener;

    // Adaptador de RecyclerView que maneja varios ViewHolder eficientemente
    private List<Course> mCourses;
    private Context mContext;
    private int mContextMenuLayoutId;
    private Course mLongPressCourse;

    public CoursesAdapter(List<Course> courses, Context context, int contextMenuLayoutId, OnCourseAdapterPressedListener onCourseAdapterPressedListener) {
        super();
        mCourses = courses;
        mContext = context;
        mContextMenuLayoutId = contextMenuLayoutId;
        mOnCourseAdapterPressedListener = new WeakReference<>(onCourseAdapterPressedListener);
    }

    @Override
    public CoursesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CoursesViewHolder(new CourseView(mContext), mContext);
    }

    @Override
    public void onBindViewHolder(final CoursesViewHolder holder, int position) {
        Course currentCourse = mCourses.get(position);
        holder.bindCourse(currentCourse);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mLongPressCourse = mCourses.get(holder.getAdapterPosition());
                if (mOnCourseAdapterPressedListener != null && mOnCourseAdapterPressedListener.get() != null) {
                    // Le pasamos al fragment el plato que ha lanzado el menu contextual
                    mOnCourseAdapterPressedListener.get().onCourseAdapterLongPressed(mLongPressCourse);
                }
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLongPressCourse = mCourses.get(holder.getAdapterPosition());
                if (mOnCourseAdapterPressedListener != null && mOnCourseAdapterPressedListener.get() != null) {
                    // Le pasamos al fragment el plato que ha lanzado el menu contextual
                    mOnCourseAdapterPressedListener.get().onCourseAdapterPressed(mLongPressCourse);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }


    // ViewHolder que maneja una vista
    public class CoursesViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private CourseView mCourseView;
        private Context mContext;

        public CoursesViewHolder(View itemView, Context context) {
            super(itemView);

            mContext = context;
            mCourseView = (CourseView) itemView;
            itemView.setOnCreateContextMenuListener(this);
        }

        // Nos asocian el modelo con este ViewHolder
        public void bindCourse(final Course course) {

            mCourseView.setCourseName(course.getName());
            mCourseView.setCourseDescription(course.getDescription());
            mCourseView.setCoursePrice(course.getPrice());
            mCourseView.setCourseAllergens(course.getAllergensString());

            ImageDownloader.ImageDownloaderParams params = new ImageDownloader.ImageDownloaderParams(course.getPhotoUrl(), course.getPhoto());
            ImageDownloader imageDownloader = new ImageDownloader(mContext, mCourseView.getCourseImageView(), R.drawable.no_image);
            imageDownloader.execute(params);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(mLongPressCourse.getName());

            if (mContext instanceof Activity) {
                MenuInflater inflater = ((Activity) mContext).getMenuInflater();
                inflater.inflate(mContextMenuLayoutId, menu);
            }
        }
    }

    public interface OnCourseAdapterPressedListener{
        void onCourseAdapterLongPressed(Course course);
        void onCourseAdapterPressed(Course course);
    }
}
