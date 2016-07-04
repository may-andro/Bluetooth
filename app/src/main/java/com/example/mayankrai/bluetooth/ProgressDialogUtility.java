package com.example.mayankrai.bluetooth;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Mayank.Rai on 6/25/2016.
 */
public class ProgressDialogUtility 
{
	private ProgressDialog pDialog;
	
	public ProgressDialogUtility(Context context)
	{
		// Progress dialog
		pDialog = new ProgressDialog(context);
		pDialog.setCancelable(false);
	}
	
	public void startProgressBar(String message)
	{
		pDialog.setMessage(message);
		showDialog();
	}
	
	public void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	public void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}
