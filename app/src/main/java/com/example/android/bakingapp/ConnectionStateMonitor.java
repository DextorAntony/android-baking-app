

package com.example.android.bakingapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {

    private NetworkRequest mNetworkRequest;

    public ConnectionStateMonitor() {
        mNetworkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    public void enable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(mNetworkRequest, this);
    }

    public void unregister(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);

        if (ConnectivityReceiver.sReceiverListener != null) {
            ConnectivityReceiver.sReceiverListener.onNetworkConnectionChanged(true);
        }
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);

        if (ConnectivityReceiver.sReceiverListener != null) {
            ConnectivityReceiver.sReceiverListener.onNetworkConnectionChanged(false);
        }
    }
}
