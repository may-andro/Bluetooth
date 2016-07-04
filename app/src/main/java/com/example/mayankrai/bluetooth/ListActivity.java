package com.example.mayankrai.bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mayank.Rai on 7/1/2016.
 */
public class ListActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FloatingActionButton fab;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        setUpToolBar();

        setupPager();

        setUpTabLayout();

        setUpFab();

        timer = new Timer();

    }

    public void setUpToolBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView textUserName = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textUserName.setText(getResources().getString(R.string.app_name));
    }

    private void setupPager() {
        viewPager  = (ViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapterWithTitle adapter = new ViewPagerAdapterWithTitle(getSupportFragmentManager());
        adapter.addFrag(new FragmentPairedList(), getResources().getString(R.string.tab_paired));
        adapter.addFrag(new FragmentUnpairedList(), getResources().getString(R.string.tab_unpaired));
        viewPager.setAdapter(adapter);
        viewPager.requestTransparentRegion(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 1:
                        fab.hide();
                        break;
                    default:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setUpTabLayout(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);


    }

    public void setUpFab(){
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.hide();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
