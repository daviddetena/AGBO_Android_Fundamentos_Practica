package com.daviddetena.tapeando.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.daviddetena.tapeando.R;

import java.lang.ref.WeakReference;

public class SetCourseNotesDialogFragment extends DialogFragment {

    protected WeakReference<OnCourseNotesSetListener> mOnCourseNotesSetListener;

    protected EditText mCourseNotes;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_set_course_notes, null);
        dialog.setView(dialogView);

        dialog.setTitle(R.string.course_notes_dialog_title);

        dialog.setPositiveButton(android.R.string.ok, null);
        dialog.setNegativeButton(android.R.string.cancel, null);

        mCourseNotes = (EditText) dialogView.findViewById(R.id.course_notes);
        return dialog.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCourseNotesSetListener != null && mOnCourseNotesSetListener.get() != null) {
                        mOnCourseNotesSetListener.get().onCourseNotesSetListener(SetCourseNotesDialogFragment.this, mCourseNotes.getText().toString());
                    }
                }
            });

            Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCourseNotesSetListener != null && mOnCourseNotesSetListener.get() != null) {
                        mOnCourseNotesSetListener.get().onCourseNotesCancelListener(SetCourseNotesDialogFragment.this);
                    }
                }
            });
        }
    }

    public void setOnCoursesNotesSetListener(OnCourseNotesSetListener onCourseNotesSetListener) {
        mOnCourseNotesSetListener = new WeakReference<>(onCourseNotesSetListener);
    }

    public interface OnCourseNotesSetListener {
        void onCourseNotesSetListener(SetCourseNotesDialogFragment dialog, String courseNotes);
        void onCourseNotesCancelListener(SetCourseNotesDialogFragment dialog);
    }
}
