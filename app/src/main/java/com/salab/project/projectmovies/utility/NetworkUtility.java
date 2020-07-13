package com.salab.project.projectmovies.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtility {

    public static final String pingIPAddress = "8.8.8.8";
    public static final int pingIPAddressPort = 53;


    /**
     * Check network connectivity
     * https://developer.android.com/training/basics/network-ops/managing
     */
    public static boolean isOnline(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = manager.getActiveNetwork();
        return activeNetwork == null ? false : true;
    }

}
