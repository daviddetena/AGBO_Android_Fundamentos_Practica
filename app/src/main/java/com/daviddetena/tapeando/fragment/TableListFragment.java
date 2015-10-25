package com.daviddetena.tapeando.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.model.Table;
import com.daviddetena.tapeando.model.Tables;
import com.daviddetena.tapeando.util.Constants;

import java.util.List;

public class TableListFragment extends Fragment  implements CleanAllTablesDialogFragment.OnCleanAllTablesListener{

    private static String TAG = Constants.TAG;
    public static String TABLE_NUMBER = "com.daviddetena.tapeando.fragment.TableListFragment.TABLE_NUMBER";

    private OnTableSelectedListener mListener;
    private TableListBroadcastReceiver mBroadcastReceiver;

    private Tables mTables;
    private int mLongPressTableNumber;
    private int mselectedTableNumber;
    private ListView mList;

    public static TableListFragment newInstance() {
        return new TableListFragment();
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
        View root = inflater.inflate(R.layout.fragment_table_list, container, false);

        mTables = Tables.getInstance(getActivity());
        mList = (ListView) root.findViewById(android.R.id.list);
        final TableListAdapter adapter = new TableListAdapter(getActivity(), mTables.getTables());
        mList.setAdapter(adapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    mListener.onTableSelected(adapter.getItem(position), position);
                    mselectedTableNumber = position;
                }
            }
        });

        // Suscripcion a la notificacion de cambio en el Singleton "Tables"
        mBroadcastReceiver = new TableListBroadcastReceiver(adapter);
        // Me suscribo a notificaciones broadcast
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Tables.TABLE_LIST_CHANGED_ACTION));

        // Activo los menus contextuales de las celdas (long press)
        registerForContextMenu(mList);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (OnTableSelectedListener) getActivity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (OnTableSelectedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Dejo de enterarme de cambios en el modelo Cities
        getActivity().unregisterReceiver(mBroadcastReceiver);
        mBroadcastReceiver = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_clean_all) {
            launchCleanAllTablesDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        mLongPressTableNumber = ((Table)mList.getAdapter().getItem(info.position)).getTableNumber();
        menu.setHeaderTitle(String.format(getActivity().getString(R.string.table_number_title), mLongPressTableNumber));
        inflater.inflate(R.menu.menu_context_list_fragment, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        if (item.getItemId() == R.id.action_clean){
            mTables.cleanTable(mLongPressTableNumber);
            return true;
        }
        return false;
    }


    private void launchCleanAllTablesDialog() {
        showCleanAllTablesDialog();
    }


    // Set clean all tables management
    protected void showCleanAllTablesDialog() {
        CleanAllTablesDialogFragment dialog = new CleanAllTablesDialogFragment();
        dialog.setOnCleanAllTablesListener(this);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public void onCleanAllTablesConfirmListener(CleanAllTablesDialogFragment dialog) {
        mTables.cleanAllTables();
        dialog.dismiss();
    }

    @Override
    public void onCleanAllTablesRejectListener(CleanAllTablesDialogFragment dialog) {
        dialog.dismiss();
    }

    private class TableListBroadcastReceiver extends BroadcastReceiver {
        private ArrayAdapter mAdapter;

        // Necesito el adapter al que voy a avisar de que hay nuevos datos
        public TableListBroadcastReceiver(ArrayAdapter adapter) {
            super();
            mAdapter = adapter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Hay nuevos cambios, aviso al adaptador para que vuelva a recargarse
            mAdapter.notifyDataSetChanged();
        }
    }

    class TableListAdapter extends ArrayAdapter<Table> {

        public TableListAdapter(Context context, List<Table> tableList){
            super(context, R.layout.list_item_table, tableList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View tableRow = inflater.inflate(R.layout.list_item_table, parent, false);

            TextView tableName = (TextView) tableRow.findViewById(R.id.list_item_table_name_text_view);
            TextView tableCourseCounter = (TextView) tableRow.findViewById(R.id.list_item_table_course_number_text_view);
            TextView tableBill = (TextView) tableRow.findViewById(R.id.list_item_table_bill_text_view);

            RelativeLayout tableBackgroud = (RelativeLayout) tableRow.findViewById(R.id.table_item_background);

            Table currentTable = getItem(position);
            tableName.setText(String.format("Mesa %d",currentTable.getTableNumber()));
            tableCourseCounter.setText(String.format("%d platos",currentTable.getCourses().size()));
            tableBill.setText(String.format(getActivity().getString(R.string.bill_format), currentTable.getBill()));

            if (position == mselectedTableNumber){
//                tableBackgroud.setBackgroundColor(Color.WHITE);
            }

            return tableRow;
        }
    }

    public interface OnTableSelectedListener {
        void onTableSelected (Table table, int index);
    }
}
