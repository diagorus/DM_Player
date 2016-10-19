package com.dmplayer.streamaudio;

import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dmplayer.streamaudio.Observer.SingleObserverContainer;
import com.dmplayer.streamaudio.WifiProfile.WifiProfileObject;
import com.dmplayer.utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Alexvojander on 09.09.2016.
 */
public class ClientUDPThread extends Thread {
    DatagramSocket datagramSocket;
    ServerSocket serverSocket;
    public ReciveAudioSocket reciveAudioSocket;
    ArrayList<WifiProfileObject> serversList=new ArrayList<>();

    public ClientUDPThread() {

    }

    public void refresh()
    {
        if(datagramSocket!=null)
        {
            datagramSocket.close();
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
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            String str= Utils.getIPAddress(true);

            byte[] sendData = str.getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                datagramSocket.send(sendPacket);
                System.out.println( ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {

            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            serverSocket = new ServerSocket(5010);
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
                        WifiProfileObject clientObject;
                        //sendData = "START_STREAMING_MUSIC".getBytes();
                        sendData=null;
                        sendData = "GET_SERVER_OBJECT".getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        //System.out.println(  ">>>CLIENTTTTT111111111111111TTT: " );
                        datagramSocket.send(sendPacket);
                        System.out.println( ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());


                        datagramSocket.setSoTimeout(100);

                        System.out.println(  ">>>CLIENTTTTT222222222222222TTT: " );
                        boolean keepGoing =true;
                        int count=0;
                        int timeouts=0;

                        serverSocket.setSoTimeout(100);
                        while (keepGoing)
                        {
                            try{
                                Socket socket = serverSocket.accept();

                                InputStream in = socket.getInputStream();
                                DataInputStream dis = new DataInputStream(in);

                                OutputStream out = socket.getOutputStream();
                                DataOutputStream dos = new DataOutputStream(out);

                                int len = dis.readInt();
                                dos.writeBoolean(true);
                                byte[] bytes=new byte[len];
                                dis.readFully(bytes,0,len);

                                dos.close();
                                dis.close();
                                in.close();
                                out.close();
                                socket.close();
                                serverSocket.close();

                                clientObject=WifiProfileObject.deserialize(bytes);

                                serversList.add(clientObject);
                                Log.d("ADDED", "obdject added");


                            }
                            catch (SocketTimeoutException ste){
                                Log.d("TIMEOUT", "timeout " + timeouts);
                                timeouts++;
                            }

                            if(timeouts>=10){
                                Log.d("TIMEOUT", "end");
                                keepGoing=false;
                            }
                        }
                        serverSocket.close();

                    } catch (Exception e) {
                        Log.e("LOG_E:",e.toString());
                        System.out.println(e.toString());
                    }
                    System.out.println( ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            System.out.println(  ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //reciveAudioSocket=new ReciveAudioSocket();
            //reciveAudioSocket.start();
            datagramSocket.close();

            SingleObserverContainer.getInstance().setServersList(serversList);




//            for (WifiProfileObject serverinfo:serversList ) {
//                System.out.println(serverinfo.getName());
//                System.out.println(serverinfo.getIp());
//            }
          //  List<WifiProfileObject> serversList=SingleObserverContainer.getInstance().getServersList();



            // Get a handler that can be used to post to the main thread
            if(SingleObserverContainer.getInstance().getContext()!=null && SingleObserverContainer.getInstance().getListView()!=null){

                Handler mainHandler = new Handler(SingleObserverContainer.getInstance().getContext().getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        List<WifiProfileObject> serversList=SingleObserverContainer.getInstance().getServersList();


                        ListView listView=SingleObserverContainer.getInstance().getListView();
                        List<String> countries=new ArrayList<>();
                        for (WifiProfileObject serverinfo: serversList ) {
                            countries.add(serverinfo.getIp());
                            System.out.println(serverinfo.getName());
                            System.out.println(serverinfo.getIp());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(SingleObserverContainer.getInstance().getContext(),android.R.layout.simple_list_item_1, countries);

                        listView.setAdapter(null);
                        // устанавливаем для списка адаптер
                        listView.setAdapter(adapter);
                    } // This is your code
                };
                mainHandler.post(myRunnable);
            }



        } catch (IOException ex) {

        }
    }
}