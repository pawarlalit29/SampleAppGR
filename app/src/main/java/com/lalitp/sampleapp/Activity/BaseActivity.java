package com.lalitp.sampleapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.lalitp.sampleapp.Database.DatabaseHelper;
import com.lalitp.sampleapp.R;

import butterknife.ButterKnife;

/**
 * Created by lalit on 30/7/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    Toolbar mToolbar;

    // Reference of DatabaseHelper class to access its DAOs and other components
    public static DatabaseHelper databaseHelper = null;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(getLayout());
        ButterKnife.bind(this);
        setToolbar();
    }

    protected void setToolbar() {
        if(!hasCustomToolbar()) return;
        mToolbar = ButterKnife.findById(this,getToolbarId());
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getActivityTitle());
    }

    protected @IdRes
    int getToolbarId(){
        return R.id.toolbar;
    }

    protected @StringRes
    int getActivityTitle(){
        return R.string.app_name;
    }

    protected abstract  @LayoutRes
    int getLayout();

    public Toolbar getToolbar(){
        return mToolbar;
    }

    public boolean hasCustomToolbar(){
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String strName = this.getClass().getSimpleName();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



}
