package com.daviddetena.tapeando.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.daviddetena.tapeando.R;
import com.daviddetena.tapeando.model.Table;
import com.daviddetena.tapeando.model.Tables;

public class TablePagerFragment extends Fragment{

    // Esta es la clave del argumento que nos pasan a este Fragment
    // para sacarlo del Bundle arguments
    private static final String ARG_TABLE_NUMBER = "tableNumber";

    private Tables mTables;
    private ViewPager mPager;
    private int mInitialTableNumber;

    private OnTablePageChangedListener mListener;
    private TableDetailBroadcastReceiver mBroadcastReceiver;

    public static TablePagerFragment newInstance(int initialTableNumber) {
        // 1) Nos creamos el fragment
        TablePagerFragment fragment = new TablePagerFragment();

        // 2) Nos creamos los argumentos y los empaquetamos
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_TABLE_NUMBER, initialTableNumber);

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
            mInitialTableNumber = getArguments().getInt(ARG_TABLE_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_table_pager, container, false);

        mTables = Tables.getInstance(getActivity());

        mPager = (ViewPager) root.findViewById(R.id.view_pager);
        mPager.setAdapter(new TablePagerAdapter(getFragmentManager()));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onTablePageChanged(((TablePagerAdapter)mPager.getAdapter()).getTable(position), position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        goToTable(mInitialTableNumber);

        // Creo el broadcastReceiver y me registro para recibir notificaciones
        mBroadcastReceiver = new TableDetailBroadcastReceiver(mPager.getAdapter());
        // Me suscribo a notificaciones broadcast
        getActivity().registerReceiver(
                mBroadcastReceiver,
                new IntentFilter(Tables.TABLE_LIST_CHANGED_ACTION));

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (OnTablePageChangedListener) getActivity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (OnTablePageChangedListener) activity;
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

    public void goToTable(int tableNumber) {
        mPager.setCurrentItem(tableNumber - 1);
    }

    // Métodos del menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_table_pager_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.next) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            return true;
        }
        else if (item.getItemId() == R.id.previous) {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        menu.clear();
        super.onPrepareOptionsMenu(menu);
        if (mPager != null) {
            MenuItem menuNext = menu.findItem(R.id.next);
            MenuItem menuPrev = menu.findItem(R.id.previous);

            menuPrev.setEnabled(mPager.getCurrentItem() > 0);
            menuNext.setEnabled(mPager.getCurrentItem() < mPager.getAdapter().getCount() - 1);
        }
    }

    protected class TablePagerAdapter extends FragmentStatePagerAdapter {
        private Tables mTables;

        public TablePagerAdapter(FragmentManager fm) {
            super(fm);
            mTables = Tables.getInstance(getActivity());
        }

        @Override
        public Fragment getItem(int i) {
            return TableDetailFragment.newInstance(mTables.getTables().get(i).getTableNumber());
        }

        @Override
        public int getCount() {
            return mTables.getTables().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public Table getTable(int position){
            return mTables.getTables().get(position);
        }
    }

    private class TableDetailBroadcastReceiver extends BroadcastReceiver {
        private PagerAdapter mAdapter;

        // Necesito el adapter al que voy a avisar de que hay nuevos datos
        public TableDetailBroadcastReceiver(PagerAdapter adapter) {
            super();
            mAdapter = adapter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Hay nuevos cambios, aviso al adaptador para que vuelva a recargarse
            mAdapter.notifyDataSetChanged();
        }
    }

    public interface OnTablePageChangedListener{
        void onTablePageChanged(Table table, int index);
    }
}
