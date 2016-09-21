package com.dmplayer.streamaudio;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

import com.dmplayer.models.SongDetail;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Alexvojander on 09.09.2016.
 */



public class SendAudioSocket extends Thread {
    String LOG_TAG="LOG_TAGsend:";
    final String clientIPAdress;
    final String port="5005";
    final String pathToFileToStream;
    Socket socket;
    int totalRead;
    SongDetail audioInfo;
    ProgressBar progressDialog;
    Context context;
    Handler h;

    public SendAudioSocket(String clientIPAdress, SongDetail audioInfo,ProgressBar progressBar,Context context) {
        this.clientIPAdress = clientIPAdress;
        //this.port = port;
        this.audioInfo=audioInfo;
        this.pathToFileToStream = audioInfo.getPath();
        this.progressDialog=progressBar;
        this.context=context;
    }
    public void refresh()  {
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        progressDialog.setMax(0);
        progressDialog.setProgress(0);
       // this.s

    }

    @Override
    public void run() {
        Log.e(LOG_TAG, "start send thread, thread id: "
                + Thread.currentThread().getId());

        try {

            progressDialog.setMax(0);
            progressDialog.setMax((int)new File(pathToFileToStream).length());
            progressDialog.setProgress(0);
            InputStream is =  new FileInputStream(pathToFileToStream);
            BufferedInputStream bis = new BufferedInputStream(is);

            byte[] buffer = new byte[32384];
            byte[] buffer2 = new byte[32384];
            int numRead;
            boolean started = false;

            InetAddress address = InetAddress.getByName(clientIPAdress);

            totalRead=0;
            socket = new Socket(address,Integer.parseInt(port));
            //socket = new Socket(address,Integer.parseInt(port));
            OutputStream out1 = socket.getOutputStream();
            DataOutputStream dos1 = new DataOutputStream(out1);
            dos1.writeInt((int)new File(pathToFileToStream).length());
            dos1.close();
            out1.close();
            while ((numRead = bis.read(buffer)) != -1) {
                socket = new Socket(address,Integer.parseInt(port));
                //socket = new Socket(address,Integer.parseInt(port));
                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);

                InputStream in=socket.getInputStream();
                DataInputStream dis = new DataInputStream(in);



                label:
                {
                    dos.writeInt(numRead);
                    dos.write(buffer, 0, numRead);

                    int len = dis.readInt();
                    Log.d(LOG_TAG, "len " +len);

                    if (len > 0) {
                        dis.readFully(buffer2,0,len);
                    }

                    if(Arrays.equals(buffer,buffer2)){
                        dos.writeBoolean(true);
                        totalRead += numRead;

                        Log.d(LOG_TAG, "bytes_send(numRead) : " + numRead);
                        Log.d(LOG_TAG, "totalRead : " + totalRead);
                        progressDialog.setProgress(totalRead);
                        Log.d(LOG_TAG, "file size : " + new File(pathToFileToStream).length());

                        if (totalRead >=new File(pathToFileToStream).length() && !started) {
                            Log.e("Player", "BufferHIT:StartPlayServer");
                            totalRead=0;
                        }
                    }
                    else{
                        dos.writeBoolean(false);
                        break label;

                    }
                }
                out.close();
                dos.close();
                in.close();
                dis.close();
                //progressDialog.setProgress(0);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }






}
