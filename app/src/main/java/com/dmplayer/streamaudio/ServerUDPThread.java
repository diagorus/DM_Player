package com.dmplayer.streamaudio;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

import com.dmplayer.manager.MediaController;
import com.dmplayer.models.SongDetail;
import com.dmplayer.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Alexvojander on 09.09.2016.
 */
public class ServerUDPThread extends Thread {
    SendAudioSocket sendAudioSocket;
    public DatagramSocket sockett;
    ProgressBar progressBar;
    Context context;

    SongDetail audioInfo;
    public ServerUDPThread(SongDetail audioInfo,ProgressBar progressBar,Context context){
        this.audioInfo=audioInfo;
        this.context=context;
        this.progressBar=progressBar;
    }
    public void refresh()
    {
        sendAudioSocket.refresh();
        if (  sendAudioSocket != null) {

            Thread dummy =  sendAudioSocket;
            sendAudioSocket = null;
            dummy.interrupt();

        }
        if(sockett!=null){
                sockett.close();
        }

    }

    @Override
    public void run() {
        try {
            if (  sendAudioSocket != null) {

                Thread dummy =  sendAudioSocket;
                sendAudioSocket = null;
                dummy.interrupt();
            }
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            sockett = new DatagramSocket(8888);
            sockett.setBroadcast(true);

            while (true) {
                System.out.println(  ">>>Ready to receive broadcast packets!");

                //Receive a packet

                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                sockett.receive(packet);
                //Packet received

                System.out.println(  ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(  ">>>Packet received; data: " + new String(packet.getData()));

                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                // if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {

                byte[] sendData = Utils.getIPAddress(true).getBytes();
                //Send a response
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                sockett.send(sendPacket);

               // sockett.close();
                if(audioInfo!=null){

                    sendAudioSocket= new SendAudioSocket(sendPacket.getAddress().getHostAddress(),MediaController.getInstance().getPlayingSongDetail(),progressBar,context);
                    sendAudioSocket.start();
                }
                System.out.println(  ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());

            }
        } catch (IOException ex) {
            //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}