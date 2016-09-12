/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.dmplayer.R;
import com.dmplayer.manager.MediaController;
import com.dmplayer.streamaudio.ClientUDPThread;
import com.dmplayer.streamaudio.ServerUDPThread;

public class FragmentStream extends Fragment {

	//public static TextView ipTextView;
	Button stopStreamButton;
	//EditText editTextIpAdress,editTextPortAdress;
	//SongDetail audioInfo = MediaController.getInstance().getPlayingSongDetail();
	//String ipAdress,path1;

	Switch switcher;
	//String port="5005";
	//TextView ipTextView,portTextView;
	//LinearLayout clientLayout,serverLayout;
	Button pauseButton,stopButton;
	Button connectToServerButton;
	ServerUDPThread serverUDPThread;
	ClientUDPThread clientUDPThread;

	//Handler handler = new Handler();

	ProgressBar pb,pbClient;
	Handler handler;

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

		//editTextIpAdress = (EditText)view.findViewById(R.id.ip_stream_server);
		//editTextPortAdress=(EditText)view.findViewById(R.id.port_stream_server);
		stopStreamButton=(Button)view.findViewById(R.id.stop_stream_music);
		stopStreamButton.setOnClickListener (startListener);
		pbClient=(ProgressBar)view.findViewById(R.id.progressBarClient);

		pb=(ProgressBar)view.findViewById(R.id.progressBar);
		pauseButton=(Button)view.findViewById(R.id.pause_button);
		stopButton=(Button)view.findViewById(R.id.stop_button);
		connectToServerButton=(Button)view.findViewById(R.id.connect_to_server_button);
		connectToServerButton.setOnClickListener(connectToServerListener);
		switcher=(Switch)view.findViewById(R.id.switchServer);
		switcher.setOnCheckedChangeListener(serverListener);
		//clientLayout=(LinearLayout)view.findViewById(R.id.clientLayout);
		//serverLayout=(LinearLayout)view.findViewById(R.id.serverLayout);
		//ipTextView=(TextView)view.findViewById(R.id.serverIp);
		//portTextView=(TextView)view.findViewById(R.id.serverPort);
	}
	private CompoundButton.OnCheckedChangeListener serverListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			if(switcher.isChecked()){

				//sendButton.setEnabled(false);
				switcher.setText("Server ON");
				stopButton.setEnabled(true);
				pauseButton.setEnabled(true);
				startReciveAudioSocket(pb,getContext());



			}
			else{
				stopButton.setEnabled(false);
				pauseButton.setEnabled(false);
				//sendButton.setEnabled(true);
				switcher.setText("Server OFF");
				if (serverUDPThread != null) {
					if(serverUDPThread.sockett!=null){serverUDPThread.sockett.close();}
					Thread dummy = serverUDPThread;
					serverUDPThread = null;
					dummy.interrupt();
				}
			}
		}
	};
	private  View.OnClickListener connectToServerListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startSendAudioSocket(pbClient);
		}
	};
	private View.OnClickListener startListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {

			if(clientUDPThread.reciveAudioSocket.mediaPlayer!=null) {
				if (clientUDPThread.reciveAudioSocket.mediaPlayer.isPlaying()) {
					clientUDPThread.reciveAudioSocket.mediaPlayer.stop();
				}
			}
			//status = true;
			//startStreaming();
			//ipAdress=editTextIpAdress.getText().toString();
			//if(audioInfo!=null){
				//path1=audioInfo.path;
				//port=editTextPortAdress.getText().toString();
			//	new SendAudioSocket(editTextIpAdress.getText().toString(),audioInfo);
			//}else{
				//song not playing
			//}
		}
	};

	void startReciveAudioSocket(ProgressBar progerssBar,Context context){
		if (serverUDPThread != null) {
			serverUDPThread.sockett.close();
			Thread dummy = serverUDPThread;
			serverUDPThread = null;
			dummy.interrupt();
		}
		serverUDPThread = new ServerUDPThread(MediaController.getInstance().getPlayingSongDetail(),progerssBar,context);
		serverUDPThread.start();
	}
	void startSendAudioSocket(ProgressBar progressBarClient){
		if (clientUDPThread != null) {
			Thread dummy = clientUDPThread;
			clientUDPThread = null;
			dummy.interrupt();
		}
		clientUDPThread = new ClientUDPThread(progressBarClient);
		clientUDPThread.start();
		//ipTextView.setText("Ip: "+ Utils.getIPAddress(true));
		//portTextView.setText("Port: "+clientUDPThread.reciveAudioSocket.serverSocket.getLocalPort());
		//portTextView.setText("Port: 5005");
	}

}
