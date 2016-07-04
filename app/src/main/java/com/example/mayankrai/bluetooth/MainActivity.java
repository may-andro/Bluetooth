package com.example.mayankrai.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView textViewMessage;
    private ProgressBar progressBarBluetooth;
    private ViewPager viewPager;

    //Timer for slide show
    private Timer timer;
    //slide show page
    private int page = 0;

    private ArrayList<ModelAdvertisement> advertisementList;

    private BluetoothAdapter bluetoothAdapter;


    public static int REQUEST_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMessage = (TextView) findViewById(R.id.text_message);
        progressBarBluetooth = (ProgressBar) findViewById(R.id.progress_bar);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Phone does not support Bluetooth so let the user know and exit.
        if (bluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }
        else{
            Intent intent = new Intent(MainActivity.this,ListActivity.class);
            startActivity(intent);
            finish();
        }

        setViewPager();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_BLUETOOTH){
            if(bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
                finish();
            } else {
               finish();
            }
        }
    }

    private void setViewPager()
    {
        createAdvertisementData();

        final DynamicViewPagerAdapter adapter = new DynamicViewPagerAdapter();

        viewPager.setAdapter(adapter);

        //create the slide show page dynamically.
        dynamicPageCreation(adapter);

        //set the logic while scrolling the page.
        pagerChangeListener(adapter.getCount());

        //set timer for automatically switching the page.
        timer = new Timer();
        pageSwitcher(4);

        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setPageTransformer(true,new DepthPageTransformer() );
        }


    }

    private void dynamicPageCreation(DynamicViewPagerAdapter a)
    {
        DynamicViewPagerAdapter adapter = a;
        for (int j = 0; j < advertisementList.size(); j++)
        {
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            FrameLayout v0 = (FrameLayout) inflater.inflate (R.layout.fragmentwelcomepage, null);

            ImageView imageViewAdvertisement = (ImageView) v0.findViewById(R.id.imageview_advertisement);
            imageViewAdvertisement.setImageResource(advertisementList.get(j).getAdvertisementImage());

            TextView textviewAdvertisementTitle = (TextView) v0.findViewById(R.id.advertisement_title);
            textviewAdvertisementTitle.setText(advertisementList.get(j).getTitleText());

            TextView textviewAdvertisementDiscription = (TextView) v0.findViewById(R.id.advertisement_discription);
            textviewAdvertisementDiscription.setText(advertisementList.get(j).getDiscriptionText());

            adapter.addView (v0,j);
            adapter.notifyDataSetChanged();
        }
    }

    private void createAdvertisementData()
    {
        ArrayList<Integer> imageAddList=new ArrayList<Integer>();
        imageAddList.add(R.drawable.ic_settings_bluetooth_black_48dp);
        imageAddList.add(R.drawable.ic_settings_bluetooth_black_48dp);
        imageAddList.add(R.drawable.ic_settings_bluetooth_black_48dp);

        ArrayList<String> titleAdvertisementList=new ArrayList<String>();
        titleAdvertisementList.add("Bluetooth Connect");
        titleAdvertisementList.add("Camera Access");
        titleAdvertisementList.add("Remote Access");

        ArrayList<String> discriptionAdvertisementList=new ArrayList<String>();
        discriptionAdvertisementList.add("Select Multilpe Cities and Stay Updated.");
        discriptionAdvertisementList.add("Current City Access.");
        discriptionAdvertisementList.add("Now map are available too.");

        advertisementList = new ArrayList<ModelAdvertisement>();

        for(int i = 0; i<imageAddList.size() ; i++){
            ModelAdvertisement object = new ModelAdvertisement();
            object.setAdvertisementImage(imageAddList.get(i));
            object.setTitleText(titleAdvertisementList.get(i));
            object.setDiscriptionText(discriptionAdvertisementList.get(i));
            advertisementList.add(object);
        }
    }

    private void pagerChangeListener(final int count)
    {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int pos)
            {
                page=pos;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2)
            {

            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {

            }
        });
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer
    {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position)
        {
            ImageView imageViewFirst = (ImageView) view.findViewById(R.id.imageview_advertisement);

            TextView textViewTitleFirst =(TextView) view.findViewById(R.id.advertisement_title);
            TextView textViewDiscriptionFirst =(TextView) view.findViewById(R.id.advertisement_discription);

            if (position < -1)
            {   view.setAlpha(1);

            }
            else if (position <= 0)
            {
                imageViewFirst.setAlpha(position+1);
                imageViewFirst.setScaleX(1+position);
                imageViewFirst.setScaleY(1+position);
                imageViewFirst.setRotation(position * view.getHeight());

            }
            else if (position <= 1)
            {
                imageViewFirst.setAlpha(position+1);
                imageViewFirst.setScaleX(1-position);
                imageViewFirst.setScaleY(1-position);
                imageViewFirst.setRotation(position * view.getHeight());

            }
            else
            { // (1,+Infinity]
                view.setAlpha(1);
            }
        }

    }

    public void pageSwitcher(int seconds) {

        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000);
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {

                    if (page > 2) {
                        timer.cancel();
                    } else {
                        viewPager.setCurrentItem(page++);
                    }
                }
            });
        }
    }

}
