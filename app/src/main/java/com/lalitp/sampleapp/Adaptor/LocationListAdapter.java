package com.lalitp.sampleapp.Adaptor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lalitp.sampleapp.Database.LocationDetails;
import com.lalitp.sampleapp.R;
import com.lalitp.sampleapp.SampleApp;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {



    private List<LocationDetails> mData;


    public LocationListAdapter(List<LocationDetails> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ViewHolder vh;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_location_list, viewGroup, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof RecyclerView.ViewHolder) {
            ViewHolder viewHolder = ((ViewHolder) holder);

            LocationDetails locationDetails = mData.get(position);

            viewHolder.txtName.setText(locationDetails.getLocName());
            viewHolder.txtLocLat.setText("Latitude " + locationDetails.getLocLat());
            viewHolder.txtLocLong.setText("Longitude " + locationDetails.getLocLong());

            Log.d("LocationAdaptor", locationDetails.getLocPic());
            //SampleApp.getImage(viewHolder.imgLocation,locationDetails.getLocPic(),R.drawable.ic_default_user);

            File imgFile = new  File(locationDetails.getLocPic());

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                viewHolder.imgLocation.setImageBitmap(myBitmap);

            }

        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_location)
        ImageView imgLocation;
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_loc_lat)
        TextView txtLocLat;
        @BindView(R.id.txt_loc_long)
        TextView txtLocLong;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }


}
