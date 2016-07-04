package com.example.mayankrai.bluetooth;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * Created by Mayank.Rai on 7/1/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DataObjectHolder>
{
	Context context;
	ArrayList<MobileDataModel> mobileList;
	private OnItemClickListener onItemClickListener;
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	public interface OnItemClickListener
	{
		public void onItemClick(DataObjectHolder item, int position);
	}
	
	public RecyclerViewAdapter(ArrayList<MobileDataModel> mobileList, Context context) {
		this.mobileList =mobileList;
		this.context = context;
	}
	
	public void add(int location, MobileDataModel mobileModal)
	{
		mobileList.add(location, mobileModal);
		notifyItemInserted(location);
	}

    public void delete(int location){
		mobileList.remove(location);
        notifyItemRemoved(location);
        notifyItemRangeChanged(location, mobileList.size());
    }

	public void update(int location, MobileDataModel mobileModal)
	{
		notifyItemChanged(location);
	}

	
	public static class DataObjectHolder extends RecyclerView.ViewHolder implements OnClickListener
   {
		public View view;
		RecyclerViewAdapter recyclerViewAdapter;
        TextView textviewMobileName,getTextviewMobileMac;
        CardView cardItem;

		public DataObjectHolder(View itemView, RecyclerViewAdapter recyclerViewAdapter)
		{
			super(itemView);
			view =itemView;
			this.recyclerViewAdapter = recyclerViewAdapter;
            textviewMobileName = (TextView) itemView.findViewById(R.id.textview_mobile_name);
			getTextviewMobileMac = (TextView) itemView.findViewById(R.id.textview_mobile_mac);
            cardItem = (CardView) itemView.findViewById(R.id.card_item);
			cardItem.setOnClickListener(this);
        }


		@Override
		public void onClick(View v) {
			final OnItemClickListener listener = recyclerViewAdapter.getOnItemClickListener();
            switch (v.getId())
            {
               case R.id.card_item:
                    if(listener != null)
                    {
                        listener.onItemClick(this, getAdapterPosition());
                    }
                    break;
                default:
                    break;
            }
		}
	}

	
	@Override
	public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
		DataObjectHolder dataObjectHolder = new DataObjectHolder(view,this);
		return dataObjectHolder;
	}
	
	@Override
	public void onBindViewHolder(final DataObjectHolder holder, int position)
	{
        holder.textviewMobileName.setText(mobileList.get(position).getMobileName());
		holder.getTextviewMobileMac.setText(mobileList.get(position).getMobileMac());
	}
	
	@Override
	public int getItemCount() 
	{
		return mobileList.size();
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

}

