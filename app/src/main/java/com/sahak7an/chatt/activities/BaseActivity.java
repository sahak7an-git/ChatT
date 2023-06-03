package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_IP_ADDRESS;
import static com.sahak7an.chatt.utilities.Constants.KEY_IS_ONLINE;
import static com.sahak7an.chatt.utilities.Constants.KEY_PORT;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_ID;
import static com.sahak7an.chatt.utilities.Constants.MAX_PORT;
import static com.sahak7an.chatt.utilities.Constants.MIN_PORT;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReferenceStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        documentReferenceStatus = firebaseFirestore.collection(KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(KEY_USER_ID));

        int port = generatePort();

        documentReferenceStatus.update(KEY_PORT, String.valueOf(port));
        documentReferenceStatus.update(KEY_IP_ADDRESS, getDeviceIpAddress());

        preferenceManager.putString(KEY_PORT, String.valueOf(port));
        preferenceManager.putString(KEY_IP_ADDRESS, getDeviceIpAddress());

    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReferenceStatus.update(KEY_IS_ONLINE, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        documentReferenceStatus.update(KEY_IS_ONLINE, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReferenceStatus.update(KEY_IS_ONLINE, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReferenceStatus.update(KEY_IS_ONLINE, true);
    }

    public static String getDeviceIpAddress() {

        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

                NetworkInterface networkInterface = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements();) {

                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {

                        return inetAddress.getHostAddress();

                    }

                }

            }

        } catch (SocketException ex) {

            ex.printStackTrace();

        }

        return null;

    }

    public static int generatePort() {

        return new Random().nextInt(MAX_PORT - MIN_PORT) + MIN_PORT;

    }

}
