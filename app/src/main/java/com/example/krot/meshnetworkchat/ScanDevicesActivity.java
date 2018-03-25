package com.example.krot.meshnetworkchat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krot.meshnetworkchat.adapter.DeviceAdapter;
import com.example.krot.meshnetworkchat.model.NearbyDevice;
import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;
import com.hypelabs.hype.MessageInfo;
import com.hypelabs.hype.MessageObserver;
import com.hypelabs.hype.NetworkObserver;
import com.hypelabs.hype.StateObserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ScanDevicesActivity extends AppCompatActivity implements StateObserver, NetworkObserver, MessageObserver, View.OnClickListener {

    private static final String PREFIX = ScanDevicesActivity.class.getSimpleName();
    private static final int PERMISSION_CODE = 101;
    private static final int REQUEST_PERMISSION_SETTING = 102;

    //Views
    private RecyclerView deviceList;
    private Button btnStartHype;
    private Button btnStopHype;
    private EditText edtMessage;
    private ImageView icSendMessage;
    private TextView tvReceivedMessage;

    @Nullable
    private Set<NearbyDevice> nearbyDeviceSet;
    private List<NearbyDevice> nearbyDeviceList;
    private DeviceAdapter deviceAdapter;

    private boolean isGranted = false;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);

        String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!hasPermission(this, permission)) {
            Log.i("WTF", PREFIX + ": NOT GRANTED");
            ActivityCompat.requestPermissions(this, permission, PERMISSION_CODE);
        } else {
            Log.i("WTF", PREFIX + ": BEFORE GRANTED");
            initializeHypeService();
            //do something else
            isGranted = true;

        }


        //findViewByIds
        deviceList = findViewById(R.id.device_list);
        btnStartHype = findViewById(R.id.btn_start_hype);
        btnStopHype = findViewById(R.id.btn_stop_hype);
        edtMessage = findViewById(R.id.edt_message);
        icSendMessage = findViewById(R.id.icon_send_message);
        tvReceivedMessage = findViewById(R.id.tv_received_message);

        //setOnClickListener
        btnStartHype.setOnClickListener(this);
        btnStopHype.setOnClickListener(this);
        icSendMessage.setOnClickListener(this);

        //setUpRecyclerViewAdapter
        setUpAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        nearbyDeviceSet = new HashSet<>();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean hasPermission(Context context, String ... permissionQueue) {
        if (context != null && permissionQueue != null) {
            for (String permission : permissionQueue) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    private void setUpAdapter() {
        deviceAdapter = new DeviceAdapter();
        deviceList.setAdapter(deviceAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        deviceList.setLayoutManager(manager);
        deviceList.setItemAnimator(new DefaultItemAnimator());
    }

    private void initializeHypeService() {
        Hype.setContext(this);

        // Add this as an Hype observer
        Hype.addStateObserver(this);
        Hype.addNetworkObserver(this);

        // Generate an app identifier in the HypeLabs dashboard (https://hypelabs.io/apps/),
        // by creating a new app. Copy the given identifier here.
        Hype.setAppIdentifier("f0441ff3");
    }

    private void requestHypeToStart() {
        Hype.start();
    }


    private void requestHypeToStop() {
        Hype.stop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.i("WTF", PREFIX + ": AFTER GRANTED");
                        isGranted = true;
                        initializeHypeService();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Log.i("WTF", PREFIX + ": DENIED");
                        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                        if (shouldShowRationale) {
                            //show the reason why user must grant STORAGE permission
                            //show dialog
                            new AlertDialog.Builder(this).setTitle("Permission Denied").setMessage(R.string.permission_rationale).setPositiveButton("RE-TRY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(ScanDevicesActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
                                }
                            }).setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

                        } else {
                            //never ask again
                            //close dialog and do nothing
                            new AlertDialog.Builder(this)
                                    .setTitle("Grant permission")
                                    .setMessage(R.string.app_setting_permission)
                                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent appSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            appSettingIntent.setData(uri);
                                            startActivityForResult(appSettingIntent, REQUEST_PERMISSION_SETTING);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        }
                    }
                }
                break;
        }
    }


    /**NETWORK OBSERVER CALLBACKS**/
    @Override
    public void onHypeInstanceFound(Instance instance) {
        Log.i("WTF", PREFIX + ": ************onHypeInstanceFound - instance = " + instance.getStringIdentifier());
        Hype.resolve(instance);
    }

    @Override
    public void onHypeInstanceLost(Instance instance, Error error) {
        Log.i("WTF", PREFIX + ": onHypeInstanceLost");
    }

    @Override
    public void onHypeInstanceResolved(Instance instance) {
        Log.i("WTF", PREFIX + ": >>>>>>>>>>>>onHypeInstanceResolved - instance = "
                + instance.getStringIdentifier());
        count += 1;
        nearbyDeviceSet.add(new NearbyDevice(Build.MODEL, instance));
        nearbyDeviceList = new ArrayList<>(nearbyDeviceSet);
        deviceAdapter.setInstanceList(nearbyDeviceList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onHypeInstanceFailResolving(Instance instance, Error error) {
        Log.i("WTF", PREFIX + ": onHypeInstanceFailResolving");
    }




    /**STATE OBSERVER CALLBACKS**/
    @Override
    public void onHypeStart() {
        Log.i("WTF", PREFIX + ": onHypeStart");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScanDevicesActivity.this, "Hype Service: enabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onHypeStop(Error error) {
        Log.i("WTF", PREFIX + ": onHypeStop");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScanDevicesActivity.this, "Hype Service: disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onHypeFailedStarting(final Error error) {
        Log.i("WTF", PREFIX + ": onHypeFailedStarting - error = " + error.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScanDevicesActivity.this, "Error code = "
                        + error.getCode() + " - Description = "
                        + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onHypeReady() {
        Log.i("WTF", PREFIX + ": onHypeReady");
    }

    @Override
    public void onHypeStateChange() {
        Log.i("WTF", PREFIX + ": onHypeStateChange");
    }

    @Override
    public String onHypeRequestAccessToken(int i) {
        Log.i("WTF", PREFIX + ": onHypeRequestAccessToken");
        return "064e04e5ab0669db7eaa5561eb8dde";
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_hype:
                Log.i("WTF", "isGranted = " + isGranted);
                if (isGranted) {
                    requestHypeToStart();
                } else {
                    Toast.makeText(this, "You must request permission for the app first", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_stop_hype:
                requestHypeToStop();
                break;
            case R.id.icon_send_message:
                if (count > 0) {
                    //send message here
                    String message = edtMessage.getText().toString().trim();
                    byte[] messageData = message.getBytes();
                    for (Iterator<NearbyDevice> iterator =  nearbyDeviceSet.iterator(); iterator.hasNext();) {
                        NearbyDevice currentNearbyDevice = iterator.next();
                        Hype.send(messageData, currentNearbyDevice.getInstance());
                    }
                } else {
                    Toast.makeText(this, "No instance found", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }


    /**MESSAGE OBSERVER CALLBACKS**/
    @Override
    public void onHypeMessageReceived(Message message, Instance instance) {
        if (message != null) {
            String receivedMessage = new String(message.getData());
            tvReceivedMessage.setText(receivedMessage);
        }

    }

    @Override
    public void onHypeMessageFailedSending(MessageInfo messageInfo, Instance instance, Error error) {

    }

    @Override
    public void onHypeMessageSent(MessageInfo messageInfo, Instance instance, float v, boolean b) {

    }

    @Override
    public void onHypeMessageDelivered(MessageInfo messageInfo, Instance instance, float v, boolean b) {

    }
}
