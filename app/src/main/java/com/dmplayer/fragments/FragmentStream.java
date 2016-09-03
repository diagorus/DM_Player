/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import com.dmplayer.R;
import com.dmplayer.manager.MediaController;
import com.dmplayer.models.SongDetail;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedInputStream;
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
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

public class FragmentStream extends Fragment {

	Button sendButton;
	EditText editTextIpAdress,editTextPortAdress;
	SongDetail audioInfo = MediaController.getInstance().getPlayingSongDetail();
	String ipAdress,path1;
	String LOG_TAG="LOG_TAG:";
	Socket socket;
	Switch switcher;
	String port;
	MediaPlayer mediaPlayer = new MediaPlayer();
	ServerSocket serverSocket;
	static final int SocketServerPORT = 5005;
	TextView ipTextView,portTextView;
	LinearLayout clientLayout,serverLayout;

	int totalRead;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.fragment_stream, null);
		setupInitialViews(rootview);
		return rootview;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setupInitialViews(View view) {

		editTextIpAdress = (EditText)view.findViewById(R.id.ip_stream_server);
		editTextPortAdress=(EditText)view.findViewById(R.id.port_stream_server);

		sendButton=(Button)view.findViewById(R.id.send_stream_server);
		sendButton.setOnClickListener (startListener);
		switcher=(Switch)view.findViewById(R.id.switchServer);
		switcher.setOnCheckedChangeListener(serverListener);

		clientLayout=(LinearLayout)view.findViewById(R.id.clientLayout);
		serverLayout=(LinearLayout)view.findViewById(R.id.serverLayout);


		ipTextView=(TextView)view.findViewById(R.id.serverIp);
		portTextView=(TextView)view.findViewById(R.id.serverPort);
	}
	private final CompoundButton.OnCheckedChangeListener serverListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			if(switcher.isChecked()){
				serverLayout.setEnabled(true);
				clientLayout.setEnabled(false);
				sendButton.setEnabled(false);
				switcher.setText("Server ON");
				RecvAudio();
			}
			else{
				serverLayout.setEnabled(false);
				clientLayout.setEnabled(true);
				sendButton.setEnabled(true);
				switcher.setText("Server OFF");
			}
		}
	};
	private final View.OnClickListener startListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			//status = true;
			//startStreaming();
			ipAdress=editTextIpAdress.getText().toString();
			if(audioInfo!=null){
				path1=audioInfo.path;
				port=editTextPortAdress.getText().toString();
				SendAudio();
			}else{
				//song not playing
			}
		}
	};
	public void SendAudio()
	{
		Thread thrd = new Thread(new Runnable() {
			@Override
			public void run()
			{
				Log.e(LOG_TAG, "start send thread, thread id: "
						+ Thread.currentThread().getId());

				try {
					//File bufferFile = File.createTempFile("test", "mp3");
					//BufferedOutputStream bufferOS = new BufferedOutputStream(new FileOutputStream(bufferFile));

					InputStream is =  new FileInputStream(path1);
					BufferedInputStream bis = new BufferedInputStream(is);

					byte[] buffer = new byte[32384];
					byte[] buffer2 = new byte[32384];
					int numRead;
					boolean started = false;

					InetAddress address = InetAddress.getByName(ipAdress);
					//DatagramSocket datagramSocket = new DatagramSocket();
//100.76.119.103
					totalRead=0;


					while ((numRead = bis.read(buffer)) != -1) {
						socket = new Socket(address,Integer.parseInt(port));
						OutputStream out = socket.getOutputStream();
						DataOutputStream dos = new DataOutputStream(out);

						InputStream in=socket.getInputStream();
						DataInputStream dis = new DataInputStream(in);
//100.86.102.81
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
							Log.d(LOG_TAG, "file size : " + new File(path1).length());

							if (totalRead >=new File(path1).length() && !started) {
								Log.e("Player", "BufferHIT:StartPlay");
								totalRead=0;
							}
						}
						else{
							dos.writeBoolean(false);

						}
						out.close();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} // end run
		});
		thrd.start();
	}

	Handler handler = new Handler();
	public void RecvAudio()
	{

		Thread thrd = new Thread(new Runnable() {
			@TargetApi(Build.VERSION_CODES.M)
			@Override
			public void run()
			{
				Boolean bool=false;
				try {

					serverSocket = new ServerSocket(SocketServerPORT);

					handler.post(new Runnable(){
						public void run() {
							try {
								ipTextView.setText("Ip: "+ip());
							} catch (SocketException e) {
								e.printStackTrace();
							}
							portTextView.setText("Port: "+serverSocket.getLocalPort());

						}
					});


				} catch (IOException e) {
					e.printStackTrace();
				}

				//FileOutputStream audio_stream=null;

				//byte[] totalbyte=null;

				byte[] buffer = new byte[32384];
				try {
					File bufferFile = File.createTempFile("test", "mp3");
					BufferedOutputStream bufferOS = new BufferedOutputStream(new FileOutputStream(bufferFile));

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
						int len = dis.readInt();
						Log.d(LOG_TAG, "len " +len);
						if (len > 0) {
							dis.readFully(buffer,0,len);
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
							//Log.d(LOG_TAG, "file size : " + new File("storage/emulated/0/Download/Mmdance.mp3").length());

							if (len<32384 ) {
								Log.e("Player", "BufferHIT:StartPlay");
								if(mediaPlayer.isPlaying())
								{
									mediaPlayer.stop();
									mediaPlayer=new MediaPlayer();
								}
								setSourceAndStartPlay(bufferFile, totalRead);
								totalRead=0;

								bufferFile = File.createTempFile("test", "mp3");
								bufferOS = new BufferedOutputStream(new FileOutputStream(bufferFile));

							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} // end run
		});
		thrd.start();

	}
	private String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "SiteLocalAddress: "
								+ inetAddress.getHostAddress() + "\n";
					}

				}

			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}

		return ip;
	}
	public InetAddress ip() throws SocketException {
		Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
		NetworkInterface ni;
		while (nis.hasMoreElements()) {
			ni = nis.nextElement();
			if (!ni.isLoopback()/*not loopback*/ && ni.isUp()/*it works now*/) {
				for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
					//filter for ipv4/ipv6
					if (ia.getAddress().getAddress().length == 4) {
						//4 for ipv4, 16 for ipv6
						return ia.getAddress();
					}
				}
			}
		}
		return null;
	}
	public void setSourceAndStartPlay(File bufferFile, long size) {
		try {

//            mPlayerPosition = videoView.getCurrentPosition();
//            videoView.setVideoPath(bufferFile.getAbsolutePath());

			FileInputStream fis = new FileInputStream(bufferFile);
			FileDescriptor fileDescriptor = fis.getFD();
			mediaPlayer.setDataSource(fileDescriptor, 0, size);

			mediaPlayer.prepare();
			mediaPlayer.start();
//            videoView.start();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
