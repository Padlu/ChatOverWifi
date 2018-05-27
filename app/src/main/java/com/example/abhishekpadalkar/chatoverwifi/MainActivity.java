package com.example.abhishekpadalkar.chatoverwifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage, mConnectionStatus, thisDevice, thisDeviceMessage;
    private EditText mWriteMessage;
    private Switch mWifiSwitch;
    private Button mSendMessage, mCreateGroup;
    private ListView mAvailableDevicesList;
    private WifiManager mWifiManager;
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mWifiP2pChannel;
    private BroadcastReceiver mBroadcastRecevier;
    private IntentFilter mIntentFilter;
    private boolean isWifiP2pEnabled;
    private List<WifiP2pDevice> peers;
    private String [] deviceNameArray;
    private WifiP2pDevice [] p2pDeviceArray;
    private String deviceName;
    private int isOwner; // 1 for Owner :: 0 for Member

    static final int MESSAGE_READ = 1;

//    ServerClass serverClass;
//    ClientClass clientClass;
//    SendReceive sendReceive;
    GroupOwnerClass groupOwnerClass;
    GroupMemberClass groupMemberClass;
    SendReceiveClass sendReceiveClass;
    HandlerClass handlerClass;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        
        toggleListener();

        deviceClickListener();

        sendMessageListener();

//        createGroupListener();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

//    private void createGroupListener() {
//        mCreateGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mWifiP2pManager.createGroup(mWifiP2pChannel, new WifiP2pManager.ActionListener() {
//                    @Override
//                    public void onSuccess() {
//                        mConnectionStatus.setText("group created by this user : GO");
//                        Toast.makeText(getApplicationContext(), "Waiting for connections", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int i) {
//                        Toast.makeText(getApplicationContext(),"Failed to create group", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }

    private void sendMessageListener() {
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String thisMessage = mWriteMessage.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        if (isOwner == 1) {
//                            sendReceiveClass.write(thisMessage.getBytes());
//                        }
//                        else {
                            sendReceiveClass.write(thisMessage.getBytes());
//                        }
                    }
                }).start();
            }
        });
    }

//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//
//            switch (message.what){
//                case MESSAGE_READ :
//                    byte [] readBuff = (byte []) message.obj;
//                    String tempMessage = new String(readBuff,0,message.arg1);
////                    thisDevice.setText(getThisDeviceName());
//                    mConnectionStatus.setText(tempMessage);
//                    break;
//            }
//
//            return true;
//        }
//    });

    private void deviceClickListener() {
        mAvailableDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = p2pDeviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mWifiP2pManager.connect(mWifiP2pChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        deviceName = device.deviceName;
                        Toast.makeText(getApplicationContext(), "Connecting to " + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void deviceDiscoverListener(View view) {

        mWifiP2pManager.discoverPeers(mWifiP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                setConnectionStatus("Discovering...");
            }

            @Override
            public void onFailure(int i) {
                setConnectionStatus("Discovery Failed to Start. Try again.");
            }
        });

    }

    private void toggleListener() {
        mWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (!mWifiManager.isWifiEnabled()) {mWifiManager.setWifiEnabled(true);}

                } else {

                    if (mWifiManager.isWifiEnabled()) {mWifiManager.setWifiEnabled(false);}

                }
            }
        });
    }

    private void initialize() {
        handlerClass = new HandlerClass();

        mTextMessage = (TextView) findViewById(R.id.message1);
        mConnectionStatus = (TextView) findViewById(R.id.tempWifi);
        mWifiSwitch = (Switch) findViewById(R.id.swEnableWifiDirect);
        peers = new ArrayList<WifiP2pDevice>();
        mAvailableDevicesList = (ListView) findViewById(R.id.rvDevicesList);
        thisDevice = (TextView) findViewById(R.id.device_name);
        thisDeviceMessage = (TextView) findViewById(R.id.group_message);
        mWriteMessage = (EditText) findViewById(R.id.edit_input);
        mSendMessage = (Button) findViewById(R.id.btn_send);
        mCreateGroup = (Button) findViewById(R.id.create_group);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mWifiP2pChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);

        mBroadcastRecevier = new WifiDirectBroadcastReceiver(mWifiP2pManager, mWifiP2pChannel, this);
        mIntentFilter = new IntentFilter();
        // Indicates a change in the Wi-Fi P2P status.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            if (!peerList.getDeviceList().equals(peers)) {

                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                p2pDeviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];

                int index = 0;

                for (WifiP2pDevice device : peerList.getDeviceList()) {

                    deviceNameArray[index] = device.deviceName;
                    p2pDeviceArray[index] = device;
                    index++;

                }

                ArrayAdapter<String> deviceNameAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);

                mAvailableDevicesList.setAdapter(deviceNameAdapter);
            }

            if (peers.size() == 0) {
                Log.e("PeersList","No Device Found");
                return;
            }

        }
    };

//    WifiP2pManager.GroupInfoListener groupInfoListener = new WifiP2pManager.GroupInfoListener() {
//        @Override
//        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
//            InetAddress groupOwnerAddress = null;
//            try {
//                groupOwnerAddress = InetAddress.getByName(wifiP2pGroup.getOwner().deviceAddress);
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//
//            if (wifiP2pGroup.isGroupOwner() && wifiP2pGroup.getOwner() != null) {
//                mConnectionStatus.setText("Group Owner");
//                serverClass = new ServerClass();
//                serverClass.start();
//            }
//            else {
//                mConnectionStatus.setText("Group Member");
//                clientClass = new ClientClass(groupOwnerAddress);
//                clientClass.start();
//            }
//        }
//    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                mConnectionStatus.setText("Group Owner");
                groupOwnerClass = new GroupOwnerClass(sendReceiveClass, handlerClass);
                groupOwnerClass.start();
                isOwner = 1;
//                serverClass = new ServerClass();
//                serverClass.start();
            }
            else {
                mConnectionStatus.setText("Group Member");
                groupMemberClass = new GroupMemberClass(groupOwnerAddress, sendReceiveClass, handlerClass);
                groupMemberClass.start();
                isOwner = 0;
//                clientClass = new ClientClass(groupOwnerAddress);
//                clientClass.start();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastRecevier,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastRecevier);
    }

//    public class ServerClass extends Thread {
//
//        Socket socket;
//        ServerSocket serverSocket;
//
//        @Override
//        public void run() {
//            try{
//                serverSocket = new ServerSocket(8888);
//                serverSocket.setReuseAddress(true);
//                socket = serverSocket.accept();
////                synchronized (this) {
////                    wait(10000);
////                }
//                sendReceive = new SendReceive(socket);
//                sendReceive.start();
//            } catch(IOException e){
//                e.printStackTrace();
//            }
////            catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//    }
//
//    private class SendReceive extends Thread {
//
//        private Socket socket;
//        private InputStream inputStream;
//        private OutputStream outputStream;
//
//        public SendReceive(Socket socket1) {
//            this.socket = socket1;
//            try {
//                inputStream = socket.getInputStream();
//                outputStream = socket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            byte[] buffer = new byte[1024];
//            int bytes;
//
//            while (socket != null) {
//                try {
//                    bytes = inputStream.read(buffer);
//                    if (bytes > 0) {
//                        handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void write(byte[] bytes) {
//            try {
//                outputStream.write(bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//        }
//    }
//
//    public class ClientClass extends Thread {
//        Socket socket;
//        String hostAdd;
//
//        public ClientClass(InetAddress hostAdd){
//            this.hostAdd = hostAdd.getHostAddress();
//            socket = new Socket();
//        }
//
//        @Override
//        public void run() {
//            try {
//                socket.connect(new InetSocketAddress(hostAdd,8888));
////                synchronized (this) {
////                    wait(5000);
////                }
//                sendReceive = new SendReceive(socket);
//                sendReceive.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//    }

    public String getThisDeviceName() {
        return WifiP2pDevice.class.getName();
    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void setConnectionStatus(String connectionStatus) {
        mConnectionStatus.setText(connectionStatus);
    }
}
