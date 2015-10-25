package com.daviddetena.tapeando.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.daviddetena.tapeando.R;

import java.lang.ref.WeakReference;

public class CleanAllTablesDialogFragment extends DialogFragment {

    protected WeakReference<OnCleanAllTablesListener> mCleanAllTablesListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle(R.string.clean_all_tables_dialog_title);
        dialog.setMessage(R.string.clean_all_tables_dialog_message);

        dialog.setPositiveButton(android.R.string.ok, null);
        dialog.setNegativeButton(android.R.string.cancel, null);

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
                    mCleanAllTablesListener.get().onCleanAllTablesConfirmListener(CleanAllTablesDialogFragment.this);
                }
            });

            Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCleanAllTablesListener.get().onCleanAllTablesRejectListener(CleanAllTablesDialogFragment.this);
                }
            });
        }
    }

    public void setOnCleanAllTablesListener(OnCleanAllTablesListener onCleanAllTablesListener) {
        mCleanAllTablesListener = new WeakReference<>(onCleanAllTablesListener);
    }

    public interface OnCleanAllTablesListener {
        void onCleanAllTablesConfirmListener(CleanAllTablesDialogFragment dialog);
        void onCleanAllTablesRejectListener(CleanAllTablesDialogFragment dialog);
    }
}
