package com.lalitp.sampleapp.Activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.lalitp.sampleapp.Adaptor.FragmentPagerAdaptorWithoutName;
import com.lalitp.sampleapp.Fragment.ListFragment;
import com.lalitp.sampleapp.Fragment.MapFragment;
import com.lalitp.sampleapp.R;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private FragmentPagerAdaptorWithoutName adapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        setupViewPager();
    }

    private void setupViewPager() {
        //You could use the normal supportFragmentManger if you like
        adapter = new FragmentPagerAdaptorWithoutName(getSupportFragmentManager());
        adapter.addFragment(MapFragment.newInstance(), "Map");
        adapter.addFragment(ListFragment.newInstance(), "List");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);//this is the new nice thing ;D
    }

    public void refreshList(){
        ListFragment frag = (ListFragment) adapter.getItem(1); //or 1 or 2
        frag.getLocationList();
    }

    public void markerClickListener(){
        viewPager.setCurrentItem(1);
        ListFragment frag = (ListFragment) adapter.getItem(1); //or 1 or 2
        frag.getLocationList();
    }
}
