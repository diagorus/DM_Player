package com.dmplayer.streamaudio;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Alexvojander on 09.09.2016.
 */
public class ReciveAudioSocket extends Thread {
    String LOG_TAG="LOG_TAG:";
    public java.net.ServerSocket serverSocket;
    static final int SocketServerPORT = 5005;
    public MediaPlayer mediaPlayer=new MediaPlayer();

    int totalRead;
    ProgressBar progressBarClient;

    public ReciveAudioSocket(ProgressBar progressBarClient){
        this.progressBarClient=progressBarClient;
        //this.mediaPlayer=mediaPlayer;
        //this.ipTextView=ipTextView;
        //this.portTextView=portTextView;
       // this.handler=handler;
    }
    public void refresh()
    {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();}
       // mediaPlayer=null;
        if(serverSocket!=null)
        {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        progressBarClient.setProgress(0);
        progressBarClient.setMax(0);

    }
    @Override
    public void run() {
        Boolean bool=false;
        try {
            serverSocket = new ServerSocket(SocketServerPORT);

//            handler.post(new Runnable(){
//                public void run() {
//                    ipTextView.setText("Ip: "+serverSocket.getInetAddress());
//                    portTextView.setText("Port: "+serverSocket.getLocalPort());
//
//                }
//            });


        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[32384];
        try {
            File bufferFile = File.createTempFile("test", "mp3");
            BufferedOutputStream bufferOS = new BufferedOutputStream(new FileOutputStream(bufferFile));
            Socket socket1 = serverSocket.accept();
            InputStream in1 = socket1.getInputStream();
            DataInputStream dis1 = new DataInputStream(in1);
            progressBarClient.setMax(dis1.readInt());
            dis1.close();
            in1.close();
            boolean started = false;
            while(true)
            {
                Socket socket = serverSocket.accept();
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer=new MediaPlayer();

                }
                InputStream in = socket.getInputStream();
                DataInputStream dis = new DataInputStream(in);

                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);

                label:{
                    int len = dis.readInt();
                    Log.d(LOG_TAG, "len " +len);
                    try {
                        if (len > 0) {
                            dis.readFully(buffer,0,len);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue ;
                    }

                    dos.writeInt(len);
                    dos.write(buffer, 0, len);
                    bool=dis.readBoolean();

                    if(bool){
                        Log.d(LOG_TAG, "recive2 pack: " + buffer.length);
                        bufferOS.write(buffer, 0,len);
                        bufferOS.flush();
                        totalRead+=len;

                        Log.d(LOG_TAG, "recive2 pack: " + buffer.length);
                        Log.d(LOG_TAG, "bytes_count : " + totalRead);
                        progressBarClient.setProgress(totalRead);
                        //Log.d(LOG_TAG, "file size : " + new File("storage/emulated/0/Download/Mmdance.mp3").length());

                        if (len<32384 ) {
                            Log.e("Player", "BufferHIT:StartPlayClient");
                            if(mediaPlayer.isPlaying())
                            {
                                mediaPlayer.stop();
                                mediaPlayer=new MediaPlayer();
                            }
                            setSourceAndStartPlay(mediaPlayer,bufferFile, totalRead);
                            dos.close();
                            dis.close();
                            in.close();
                            out.close();

                            totalRead=0;
                            bufferFile = File.createTempFile("test", "mp3");
                            bufferOS = new BufferedOutputStream(new FileOutputStream(bufferFile));
                        }
                    }
                    else{
                        break label;
                    }
                }
                dos.close();
                dis.close();
                in.close();
                out.close();
                //progressBarClient.setProgress(0);
            }


        } catch (IOException e ) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }
    public void setSourceAndStartPlay(MediaPlayer mediaPlayer,File bufferFile, long size) {
        try {

//            mPlayerPosition = videoView.getCurrentPosition();
//            videoView.setVideoPath(bufferFile.getAbsolutePath());

            FileInputStream fis = new FileInputStream(bufferFile);
            FileDescriptor fileDescriptor = fis.getFD();
            mediaPlayer.setDataSource(fileDescriptor, 0, size);

            mediaPlayer.prepare();
            mediaPlayer.start();
//            videoView.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
