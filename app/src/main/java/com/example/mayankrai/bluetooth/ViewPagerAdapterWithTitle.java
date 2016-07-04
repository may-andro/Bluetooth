package com.example.mayankrai.bluetooth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayank.Rai on 6/25/2016.
 */
public class ViewPagerAdapterWithTitle extends FragmentPagerAdapter 
{
	 private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
     private final List<String> mFragmentTitleList = new ArrayList<String>();
     
     public ViewPagerAdapterWithTitle(FragmentManager manager)
     {
         super(manager);
     }

     @Override
     public Fragment getItem(int position)
     {
         return mFragmentList.get(position);
     }

     @Override
     public int getCount() 
     {
         return mFragmentList.size();
     }

     public void addFrag(Fragment fragment, String title)
     {
         mFragmentList.add(fragment);
         mFragmentTitleList.add(title);
     }
     
     @Override
     public CharSequence getPageTitle(int position) {
         return mFragmentTitleList.get(position);
         
      // return null to display only the icon
      // return null;
     }
}