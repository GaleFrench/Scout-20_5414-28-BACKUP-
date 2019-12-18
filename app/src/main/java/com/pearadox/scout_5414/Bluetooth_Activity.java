package com.pearadox.scout_5414;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

public class Bluetooth_Activity extends AppCompatActivity {
    String TAG = "Bluetooth_Activity";          // This CLASS name
    String param1 = "";  String param2 = "";
    private String deviceId;                    // Android Device ID
    TextView txt_Device, txt_bt, txt_UUID;
    Switch switch_bt;
    ListView listView_bonded;
    public int devSelected = -1;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVERABLE_BT = 0;
    Boolean turnedON = false;
    ArrayAdapter<String> adapter_dev;
    String MY_UUID = ""; String UUID = "";
    public static String[] btArray = new String[] {" ", " ", " ", " ", " ", " "};
    p_Firebase.devicesObj dev_inst = new p_Firebase.devicesObj();


    // ===========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Log.d(TAG, "@@@@@ Bluetooth_Activity  @@@@@");
        Bundle bundle = this.getIntent().getExtras();
        param1 = bundle.getString("dev");
        param2 = bundle.getString("andid");
        Log.w(TAG, param1 + " " + param2);      // ** DEBUG **
        deviceId = param2;

        txt_Device = (TextView) findViewById(R.id.txt_Device);
        txt_Device.setText(param1);
        txt_bt = (TextView) findViewById(R.id.txt_bt);
        txt_UUID = (TextView) findViewById(R.id.txt_UUID);
        listView_bonded = (ListView) findViewById(R.id.listView_bonded);
        Switch switch_bt = (Switch) findViewById(R.id.switch_bt);
        Pearadox.dev_List.clear();
        txt_UUID.setText("");

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast toast = Toast.makeText(getBaseContext(), "Bluetooth device not supported", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                txt_bt.setText("OFF");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                switch_bt.setChecked(true);
                turnedON = true;
            } else {
                switch_bt.setChecked(true);
                txt_bt.setText("ON");
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                ArrayList list = new ArrayList();
                for (BluetoothDevice device : pairedDevices) {
                    list.add(device.getName());
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.w(TAG, ">>> Device: " + deviceName + "  Addr: " + deviceHardwareAddress);
                    switch (deviceName) {
                        case "Scout Master":
                            dev_inst.setDev_name(deviceName);
                            dev_inst.setDev_id("SM");
                            dev_inst.setBtUUID(deviceHardwareAddress);
                            Pearadox.dev_List.add(dev_inst);
                            MY_UUID = deviceHardwareAddress;
                            break;
                        case "Red-1":
                            dev_inst.setDev_id("R1");
                            dev_inst.setDev_name(deviceName);
                            dev_inst.setBtUUID(deviceHardwareAddress);
                            Pearadox.dev_List.add(dev_inst);
                            btArray[0] = deviceHardwareAddress;
                            break;
                        case "Red-2":
                            dev_inst.setDev_id("R2");
                            dev_inst.setDev_name(deviceName);
                            dev_inst.setBtUUID(deviceHardwareAddress);
                            Pearadox.dev_List.add(dev_inst);
                            btArray[1] = deviceHardwareAddress;
                            break;
                        case "Red-3":
                            dev_inst.setDev_id("R3");
                            dev_inst.setDev_name(deviceName);
                            dev_inst.setBtUUID(deviceHardwareAddress);
                            Pearadox.dev_List.add(dev_inst);
                            btArray[2] = deviceHardwareAddress;
                            break;
                        case "Blue-1":
                            dev_inst.setDev_id("B1");
                            dev_inst.setDev_name(deviceName);
                            dev_inst.setBtUUID(deviceHardwareAddress);
                            Pearadox.dev_List.add(dev_inst);
                            btArray[3] = deviceHardwareAddress;
                            break;
                        case "Blue-2":
                            dev_inst.setDev_id("B2");
                            dev_inst.setDev_name(deviceName);
                            dev_inst.setBtUUID(deviceHardwareAddress);
                            Pearadox.dev_List.add(dev_inst);
                            btArray[4] = deviceHardwareAddress;
                            break;
                        case ("Blue-3"):
                        case ("Gale's Tablet"):         // *** DEBUG!! ***
                            dev_inst.setDev_id("B3");
                            dev_inst.setDev_name(deviceName);
                            dev_inst.setBtUUID(deviceHardwareAddress);
                            Pearadox.dev_List.add(dev_inst);
                            btArray[5] = deviceHardwareAddress;
                            break;
                        default:                //
                            Log.w(TAG, "DEV not a Scout  '" + deviceName + "' ");
                            break;
                    }
                } //end FOR
                Log.w(TAG, "Device list = " + Pearadox.dev_List.size());      // ** DEBUG **
                Log.e(TAG, "@@@ BT = " + btArray[0] + "  " + btArray[1] + "  "  + btArray[2] + "  "  + btArray[3] + "  "  + btArray[4] + "  "  + btArray[5]);

                final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
                listView_bonded.setAdapter(adapter);
            }
        }

        switch_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                Log.w(TAG, "*** setOnCheckedChangeListener *** ");
                if (!turnedON) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                if (bChecked) {
                    txt_bt.setText("ON");
                } else {
                    mBluetoothAdapter.disable();
                    txt_bt.setText("OFF");
                }
            }
        });

        if (switch_bt.isChecked()) {
            txt_bt.setText("ON");
        } else {
            txt_bt.setText("OFF");
        }

        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        listView_bonded.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {
                Log.w(TAG, "*** listView_bonded.setOnItemClickListener ***   Item Selected: " + pos + "  Devs:" + Pearadox.dev_List.size());
                txt_UUID.setText("");
                devSelected = pos;
                String devBT = parent.getItemAtPosition(pos).toString();
                Log.d(TAG, ">>> Sel.Dev: '" + devBT + "'");
                listView_bonded.setSelector(android.R.color.holo_blue_light);
                for(int i=0 ; i < Pearadox.dev_List.size() ; i++) {
                    dev_inst = Pearadox.dev_List.get(i);
                    String dnm = dev_inst.getDev_name();
                    Log.e(TAG, "DEV: '" + dnm + "'   UUID: " + dev_inst.getBtUUID());
                    if (devBT.equals(dnm)) {
                        UUID = dev_inst.getBtUUID();
                        Log.e(TAG, "UUID: " + UUID);
                        txt_UUID.setText(UUID);
                        break;      // exit FOR
                    }
                } //end FOR
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            Log.d(TAG, "*** BroadcastReceiver  ***  " + action);
            txt_bt = (TextView) findViewById(R.id.txt_bt);
            Switch switch_bt = (Switch) findViewById(R.id.switch_bt);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        txt_bt.setText("OFF");
                        switch_bt.setChecked(false);
                        turnedON = false;
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        txt_bt.setText("OFF");
                        switch_bt.setChecked(false);
                        turnedON = false;
                        break;
                    case BluetoothAdapter.STATE_ON:
                        txt_bt.setText("ON");
                        switch_bt.setChecked(true);
                        turnedON = true;
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        txt_bt.setText("ON");
                        switch_bt.setChecked(true);
                        turnedON = true;
                        break;
                }
            }
        }
    };




//###################################################################
//###################################################################
//###################################################################
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "<<<<<  onStart  >>>>>");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "*** onResume ***");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister broadcast listeners
        unregisterReceiver(mReceiver);
    }

}
