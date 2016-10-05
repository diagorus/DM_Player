package com.dmplayer.streamaudio;

import android.widget.ProgressBar;

import com.dmplayer.fragments.FragmentStream;
import com.dmplayer.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Alexvojander on 09.09.2016.
 */
public class ClientUDPThread extends Thread {



    DatagramSocket c;
    public ReciveAudioSocket reciveAudioSocket;

    public ClientUDPThread() {

    }

    public void refresh()
    {
        if(c!=null)
        {
            c.close();
        }
        if (  reciveAudioSocket != null) {
            reciveAudioSocket.refresh();
            Thread dummy =  reciveAudioSocket;
            reciveAudioSocket = null;
            dummy.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            //Open a random port to send the package
            c = new DatagramSocket();
            c.setBroadcast(true);
            String str= Utils.getIPAddress(true);

            byte[] sendData = str.getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
                System.out.println( ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }
                    System.out.println( ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            System.out.println(  ">>> Done looping over all network interfaces. Now waiting for a reply!");
            //Wait for a response
            byte[] recvBuf = new byte[15000];
            final DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

            c.receive(receivePacket);

            //We have a response
            System.out.println( ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            System.out.println(message);

            //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)

            //handler.post(new Runnable(){
//					public void run() {
//
//						text1.setText(receivePacket.getAddress().toString());
//
//					}
//				});

            reciveAudioSocket=new ReciveAudioSocket();
            reciveAudioSocket.start();
            c.close();

        } catch (IOException ex) {
            //Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
}