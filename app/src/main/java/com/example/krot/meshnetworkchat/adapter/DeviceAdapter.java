package com.example.krot.meshnetworkchat.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.krot.meshnetworkchat.R;
import com.example.krot.meshnetworkchat.model.NearbyDevice;
import com.hypelabs.hype.Instance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krot on 3/21/18.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    @Nullable
    private List<NearbyDevice> nearbyDeviceList;

    public DeviceAdapter() {
    }

    public void setInstanceList(@Nullable List<NearbyDevice> instanceList) {
        this.nearbyDeviceList = instanceList;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.bindData(getInstanceAt(position));
    }

    @Override
    public int getItemCount() {
        return (nearbyDeviceList != null ? nearbyDeviceList.size() : 0);
    }

    public NearbyDevice getInstanceAt(int position) {
        return (nearbyDeviceList != null ? nearbyDeviceList.get(position) : null);
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView tvDeviceName;
        TextView tvDeviceId;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.device_name);
            tvDeviceId = itemView.findViewById(R.id.device_id);
        }

        public void bindData(NearbyDevice nearbyDevice) {
            tvDeviceName.setText(nearbyDevice.getDeviceName());
            tvDeviceId.setText("id = " + nearbyDevice.getInstance().getStringIdentifier());
        }
    }
}
