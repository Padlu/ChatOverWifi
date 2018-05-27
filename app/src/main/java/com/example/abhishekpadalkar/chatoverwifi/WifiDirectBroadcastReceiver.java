package com.example.abhishekpadalkar.chatoverwifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by abhishekpadalkar on 4/14/18.
 */

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mWifiP2pChannel;
    private MainActivity mainActivity;

    public WifiDirectBroadcastReceiver(WifiP2pManager mWifiP2pManager, WifiP2pManager.Channel mWifiP2pChannel, MainActivity mainActivity) {
        this.mWifiP2pManager = mWifiP2pManager;
        this.mWifiP2pChannel = mWifiP2pChannel;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mainActivity.setIsWifiP2pEnabled(true);//Wifi is ON
                mainActivity.setConnectionStatus("Wifi is ON");
                Log.e("WIFI_STATE", "ON");
            } else {
                mainActivity.setIsWifiP2pEnabled(false);//Wifi is OFF
                Log.e("WIFI_STATE", "OFF");
                mainActivity.setConnectionStatus("Wifi is OFF");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            if (mWifiP2pManager != null) {
                mWifiP2pManager.requestPeers(mWifiP2pChannel, mainActivity.peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mWifiP2pManager == null) {
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                mWifiP2pManager.requestConnectionInfo(mWifiP2pChannel, mainActivity.connectionInfoListener);
            }
            else {
                mainActivity.setConnectionStatus("Device Disconnected");
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }

    }
}
