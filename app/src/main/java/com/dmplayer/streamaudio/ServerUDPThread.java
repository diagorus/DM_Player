package com.dmplayer.streamaudio;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.dmplayer.fragments.FragmentSettings;
import com.dmplayer.streamaudio.WifiProfile.WifiProfileObject;
import com.dmplayer.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Alexvojander on 09.09.2016.
 */
public class ServerUDPThread extends Thread {
    SendAudioSocket sendAudioSocket;
    DatagramSocket sockett;

    Context context;

   // SongDetail audioInfo;
    public ServerUDPThread(Context context){
        this.context=context;
       // this.progressBar=progressBar;
    }
    public void refresh()
    {
        //sendAudioSocket.refresh();
        if (  sendAudioSocket != null) {
            Thread dummy =  sendAudioSocket;
            sendAudioSocket = null;
            dummy.interrupt();
        }
        if(sockett!=null){
                sockett.close();
        }
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    private SharedPreferences sharedPreferences;

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
                if (message.equals("GET_SERVER_OBJECT")){
                    sharedPreferences = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
                    //Uri data = result.getData();

                    String avatar = sharedPreferences.getString(FragmentSettings.AVATAR, "");
                    Uri avatarUri = Uri.parse(avatar);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream(new File(avatarUri.getPath()));
                        byte[] buf = new byte[1024];
                        int n;
                        while (-1 != (n = fis.read(buf)))
                            baos.write(buf, 0, n);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    byte[] bbytes = baos.toByteArray();
                    String name = sharedPreferences.getString(FragmentSettings.NAME, "");
                    String ip=Utils.getIPAddress(true).toString();
                    //WifiProfileObject serverObject=new WifiProfileObject(ip,name,bbytes);
                    WifiProfileObject serverObject=new WifiProfileObject(ip,name);


                    //System.out.println(  ">>>BYTEEEEEEEEEEEEEEE: " + bbytes.length);

                    byte[] byteToSendToClient =serverObject.serialize();
                    System.out.println(  ">>>BYTEEEEEEEEEEEEEEE: " + byteToSendToClient.length);
                   // DatagramPacket serverObjectPacket=new DatagramPacket(byteToSendToClient,byteToSendToClient.length,packet.getAddress(), packet.getPort());

                    //int numread;
                    //byte[] buffer = new byte[32384];
                    //InputStream is = new ByteArrayInputStream(byteToSendToClient);
                    //BufferedInputStream bis = new BufferedInputStream(is);

                    Socket socket=new Socket(InetAddress.getByName(ip),5010);


                    OutputStream out = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);

                    InputStream in=socket.getInputStream();
                    DataInputStream dis = new DataInputStream(in);

                    dos.writeInt(byteToSendToClient.length);
                    dis.readBoolean();
                    dos.write(byteToSendToClient,0,byteToSendToClient.length);

                    dos.close();
                    dis.close();
                    in.close();
                    out.close();
                    socket.close();


                   // while ((numread=bis.read(buffer))!=-1){

                   // }
                    //sockett.send(sendPacket);

                    //////////////////////////////////////////////////
                    //sockett.send(serverObjectPacket);

                }
//                else if(message.equals("START_STREAMING_MUSIC")){
//                    byte[] sendData = Utils.getIPAddress(true).getBytes();
//                    //Send a response
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
//
//                    if(MediaController.getInstance().getPlayingSongDetail()!=null){
//
//                        sendAudioSocket= new SendAudioSocket(sendPacket.getAddress().getHostAddress(),MediaController.getInstance().getPlayingSongDetail());
//                        sendAudioSocket.start();
//                    }
//                    System.out.println(  ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
//                }


            }
        } catch (IOException ex) {
           /// Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            Log.e(getClass().getCanonicalName(), Log.getStackTraceString(ex));
        }
    }


}