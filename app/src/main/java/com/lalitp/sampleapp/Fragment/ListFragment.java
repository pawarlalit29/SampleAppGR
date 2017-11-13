package com.lalitp.sampleapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lalitp.sampleapp.Adaptor.LocationListAdapter;
import com.lalitp.sampleapp.Database.LocationDetails;
import com.lalitp.sampleapp.R;
import com.lalitp.sampleapp.SampleApp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int AMOUNT_OF_DATA = 50;
    private static final String TAG = ListFragment.class.getSimpleName();
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SongListFragment.
     */

    static Context context;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    Unbinder unbinder;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Toolbar toolbar;
    private LocationListAdapter locationListAdapter;
    private List<LocationDetails> locationDetailsList;
    private Dao<LocationDetails, Integer> locationDao;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }


    @Override
    public boolean hasCustomToolbar() {
        return false;
    }

    @Override
    protected int getLayout() {
        return R.layout.frag_list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    /*******************************************************************************************/


    private void init() {
        locationDetailsList = new ArrayList<>();

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.white));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.accent);
        swipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycleview.setLayoutManager(layoutManager);
        locationListAdapter = new LocationListAdapter(locationDetailsList);
        recycleview.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recycleview.setItemAnimator(new DefaultItemAnimator());
        recycleview.setAdapter(locationListAdapter);

        getLocationList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getLocationList() {

        try {
            locationDao = SampleApp.getHelper().getLocationDao();
            // build your query
            QueryBuilder<LocationDetails, Integer> queryBuilder = locationDao.queryBuilder();
            // when you are done, prepare your query and build an iterator
            List<LocationDetails> notificationResults = queryBuilder.orderBy(LocationDetails.COL_TIMESTAMP, false).query();

            if (locationDetailsList != null && !locationDetailsList.isEmpty())
                locationDetailsList.clear();


            locationDetailsList.addAll(notificationResults);

            locationListAdapter.notifyDataSetChanged();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        getLocationList();
    }
}
