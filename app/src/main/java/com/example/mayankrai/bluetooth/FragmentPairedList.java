package com.example.mayankrai.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Mayank.Rai on 6/25/2016.
 */
public class FragmentPairedList extends Fragment implements RecyclerViewAdapter.OnItemClickListener,SurfaceHolder.Callback, Camera.PreviewCallback
{
	private RecyclerView mRecyclerView;
	private FloatingActionButton fab ;
	private StaggeredGridLayoutManager mStaggeredLayoutManager;
	private RecyclerViewAdapter adapter ;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private Button start ,cancel;
    private ImageView imageview;
    private RelativeLayout linearLayoutCameraView;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;
    private final String tag = "tagg";

    private static final String TAG = "BluetoothCamera";


    String macAddress;

    private String mConnectedDeviceName = null;


    ArrayList<MobileDataModel> dataModalList;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothManager mCameraService = null;

    private boolean isCameraRunning = false;


    @Override
    public void onStart() {
        super.onStart();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);

        } else if (mCameraService == null) {
            //setup();
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_paired_device, container, false);

        dataModalList = new ArrayList<MobileDataModel>();

        mCameraService = new BluetoothManager(getActivity(), mHandler);

        adapter = new RecyclerViewAdapter(dataModalList,getActivity());
        adapter.setOnItemClickListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        linearLayoutCameraView = (RelativeLayout) view.findViewById(R.id.camera_view);

        linearLayoutCameraView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        imageview = (ImageView) view.findViewById(R.id.previewImage);
        surfaceView = (SurfaceView) view.findViewById(R.id.surfaceview);
        cancel = (Button) view.findViewById(R.id.button_cancel);
        start = (Button) view.findViewById(R.id.button_start);

        surfaceView.setVisibility(View.VISIBLE);
        imageview.setVisibility(View.GONE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.setVisibility(View.VISIBLE);
                start_camera();
            }
        });

        cancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                sendImageData("stop-camera".getBytes());

                linearLayoutCameraView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        mCameraService = new BluetoothManager(getActivity(), mHandler);

        System.out.println("onCreate");

        setUpFab();

        setUpRecyclerView();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        MobileDataModel mobileDataModel;
        if (pairedDevices.size() > 0)
        {
            // Loop through paired devices

            for (BluetoothDevice device : pairedDevices)
            {
                mobileDataModel = new MobileDataModel();
                mobileDataModel.setMobileName(device.getName());
                mobileDataModel.setMobileMac(device.getAddress());
                dataModalList.add(mobileDataModel);
                //adapter.add(dataModalList.size(),mobileDataModel);
            }
            System.out.println("111111   "+dataModalList.size());
        }
        else{
            mobileDataModel = new MobileDataModel();
            mobileDataModel.setMobileName("No device found");
            dataModalList.add(mobileDataModel);
        }




        view.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    if((mRecyclerView.getVisibility()==View.GONE)){
                        linearLayoutCameraView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        return false;
                    }
                }
                return false;
            }
        } );


        return view;
	}


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraService != null) {
            mCameraService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCameraService != null)
        {
            if (mCameraService.getState() == BluetoothManager.STATE_NONE)
            {
                mCameraService.start();
            }
        }
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onItemClick(RecyclerViewAdapter.DataObjectHolder item, int position)
    {
        connectDevice(dataModalList.get(position).getMobileMac(),true);
    }


    private void setStatus(CharSequence subTitle) {
        toolbarTitle.setText(subTitle);
    }

    private void setup() {
        Log.d(TAG, "setup()");

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("Log", "onPictureTaken - raw");
                // camera.setPreviewCallback(Camera.PreviewCallback cb);
            }
        };

        shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() {
                Log.i("Log", "onShutter'd");
            }
        };
        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(String.format(
                            "/sdcard/%d.jpg", System.currentTimeMillis()));
                    outStream.write(data);
                    outStream.close();
                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                Log.d("Log", "onPictureTaken - jpeg");
            }
        };




        linearLayoutCameraView.setVisibility(View.VISIBLE);


        mRecyclerView.setVisibility(View.GONE);
    }


    private void sendImageData(byte[] preview) {

        if (mCameraService.getState() != BluetoothManager.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("sttatt", "sending data");
        mCameraService.write(preview);
    }

    Bitmap previewimage = null;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothManager.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));

                            break;
                        case BluetoothManager.STATE_CONNECTING:
                            setStatus(getString(R.string.title_connecting));
                            break;
                        case BluetoothManager.STATE_LISTEN:
                        case BluetoothManager.STATE_NONE:
                            setStatus("Not Connected");
                            break;
                    }
                    break;
                case Constants.START_CAMERA_SERVICE:
                    start_camera();
                    break;
                case Constants.STOP_CAMERA:

                    break;
                case Constants.TAKE_PICTURE:

                    break;
                case Constants.MESSAGE_DEVICE_NAME:

                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(getActivity(), "From Dialog: Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    setup();
                    break;
                case Constants.MESSAGE_TOAST:
                    linearLayoutCameraView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    Toast.makeText(getActivity(), "From Dialog: "+msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CAMERA_PREVIEW:

                    Toast.makeText(getActivity(), "From Dialog: CAMERA_PREVIEW", Toast.LENGTH_SHORT).show();


                    surfaceView.setVisibility(View.GONE);

                    imageview.setVisibility(View.VISIBLE);

                    byte[] data = (byte[]) msg.obj;
                    previewimage = BitmapFactory.decodeByteArray(data, 0, data.length);
                    System.out.println(data.length);


                    imageview.setImageBitmap(previewimage);

                    Log.d("immgbitt","Preview");

                    break;
            }
        }
    };

    private void connectDevice(String macAddress,boolean secure) {

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        mCameraService.connect(device, secure);
    }



    private void captureImage() {
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    private void start_camera() {
        try {
            camera = Camera.open();
            isCameraRunning = true;
        } catch (RuntimeException e) {
            Log.e(tag, "init_camera: " + e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();
        param.setPreviewFrameRate(10);
        param.setPreviewSize(176, 144);
        camera.setParameters(param);
        //camera.setDisplayOrientation(90);

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(final byte[] data, Camera camera) {

                    surfaceView.setVisibility(View.VISIBLE);
                    imageview.setVisibility(View.GONE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, 176, 144, null);
                            yuvImage.compressToJpeg(new Rect(0, 0, 176, 144), 50, out);
                            byte[] imageBytes = out.toByteArray();
                            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);


                            //imageview.setImageBitmap(image);
                            System.out.println("Writing data");

                            mCameraService.write(imageBytes);
                        }
                    }, 5000);


                }
            });
            camera.startPreview();

        } catch (Exception e) {
            Log.e(tag, "init_camera: " + e);
            return;
        }
    }

    private void stop_camera() {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onPause() {
        mCameraService.stop();
        super.onPause();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}