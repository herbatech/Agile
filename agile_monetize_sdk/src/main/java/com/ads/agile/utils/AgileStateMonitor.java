package com.ads.agile.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AgileStateMonitor extends ConnectivityManager.NetworkCallback {

    private static final String TAG = AgileStateMonitor.class.getSimpleName();
    private final NetworkRequest networkRequest;
    public NetworkCallBack callBack;


    public AgileStateMonitor(NetworkCallBack callBack) {
        this.callBack = callBack;
        networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
    }


    public void enable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    // Likewise, you can have a disable method that simply calls ConnectivityManager#unregisterCallback(networkRequest) too.

    @Override
    public void onAvailable(Network network) {
        // Do what you need to do here
       // Log.d(TAG, "onAvailable");
        callBack.onConnected();
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
       // Log.d(TAG, "onLosing");
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
       // Log.d(TAG, "onLost");
        callBack.onDisconnected();
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
       // Log.d(TAG, "onUnavailable");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
       /* Log.d(TAG, "onCapabilitiesChanged");
        Log.d(TAG, "Upload   = " + networkCapabilities.getLinkUpstreamBandwidthKbps() + " kbps");
        Log.d(TAG, "Download = " + networkCapabilities.getLinkDownstreamBandwidthKbps() + " kbps");*/
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
       // Log.d(TAG, "onLinkPropertiesChanged");
    }

    public interface NetworkCallBack {
        void onConnected();
        void onDisconnected();
    }
}