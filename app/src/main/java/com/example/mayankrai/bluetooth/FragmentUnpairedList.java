package com.example.mayankrai.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mayank.Rai on 6/25/2016.
 */
public class FragmentUnpairedList extends Fragment
{
	private RecyclerView mRecyclerView;
	private FloatingActionButton fab ;
	private StaggeredGridLayoutManager mStaggeredLayoutManager;
	private RecyclerViewAdapter adapter ;

    ArrayList<MobileDataModel> dataModalList;

    private BluetoothAdapter bluetoothAdapter;

    private ProgressDialogUtility progressDialogUtility;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        dataModalList = new ArrayList<MobileDataModel>();

        adapter = new RecyclerViewAdapter(dataModalList,getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        System.out.println("onCreate");

        progressDialogUtility = new ProgressDialogUtility(getActivity());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        doDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        setUpFab();

        setUpRecyclerView();

		return view;
	}



	public void setUpRecyclerView(){

        int coloumns =1;

		mStaggeredLayoutManager = new StaggeredGridLayoutManager(coloumns, StaggeredGridLayoutManager.VERTICAL);

		mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

		RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
		itemAnimator.setAddDuration(1000);
		itemAnimator.setRemoveDuration(1000);
		mRecyclerView.setItemAnimator(itemAnimator);


		mRecyclerView.setAdapter(adapter);


	}

    public void setUpFab(){
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery()
    {
        // Indicate scanning in the title
        //progressDialogUtility.startProgressBar("Scanning Device");

        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView

                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    MobileDataModel mobileDataModel = new MobileDataModel();
                    mobileDataModel.setMobileName(device.getName());
                    mobileDataModel.setMobileMac(device.getAddress());
                    adapter.add(dataModalList.size(),mobileDataModel);
                    System.out.print("COUNT : "+adapter.getItemCount());
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {
                    progressDialogUtility.hideDialog();
                    if (adapter.getItemCount() == 0) {
                        MobileDataModel mobileDataModel = new MobileDataModel();
                        mobileDataModel.setMobileName("No device found");

                        adapter.add(dataModalList.size(),mobileDataModel);
                        System.out.print(mobileDataModel.getMobileName());
                    }
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(mReceiver);
    }
}