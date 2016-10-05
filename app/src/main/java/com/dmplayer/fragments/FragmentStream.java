/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.dmplayer.R;
import com.dmplayer.manager.MediaController;
import com.dmplayer.streamaudio.ClientStreamService;
import com.dmplayer.streamaudio.ClientUDPThread;
import com.dmplayer.streamaudio.Observer.SingleObserverContainer;
import com.dmplayer.streamaudio.ServerStreamService;
import com.dmplayer.streamaudio.ServerUDPThread;

import java.util.Observer;

public class FragmentStream extends Fragment {

	Button stopStreamButton;
	Switch switcher;
	Button connectToServerButton;



	ProgressBar pbServer,pbClient;
	Handler handler;
	BroadcastReceiver brClient;
	BroadcastReceiver brServer;
	public final static String BROADCAST_ACTION_CLIENT = "com.dmplayer.streamaudio.ClientStreamService";
	public final static String BROADCAST_ACTION_SERVER = "com.dmplayer.streamaudio.ServerStreamService";

	final String LOG_TAG = "myLogs";

	public final static String PARAM_TIME = "time";
	public final static String PARAM_TASK = "task";
	public final static String PARAM_RESULT = "result";
	public final static String PARAM_STATUS = "status";


	public final static int STATUS_START = 100;
	public final static int STATUS_FINISH = 200;

	final int TASK1_CODE = 1;
	final int TASK2_CODE = 2;
	final int TASK3_CODE = 3;

	Intent intentServer;
	Intent intentClient;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.fragment_stream, null);
		setupInitialViews(rootview);
		return rootview;
	}
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(brClient);
		getActivity().unregisterReceiver(brServer);
		super.onDestroy();
	}

	private void setupInitialViews(View view) {


		pbClient=(ProgressBar)view.findViewById(R.id.client_progress_bar);
		pbServer=(ProgressBar)view.findViewById(R.id.server_progress_bar);

		connectToServerButton=(Button)view.findViewById(R.id.connect_to_UDPServer);
		connectToServerButton.setOnClickListener(connectToServerListener);
		switcher=(Switch)view.findViewById(R.id.switchServer);
		switcher.setOnCheckedChangeListener(serverListener);

		SingleObserverContainer.getInstance().update(pbClient,pbServer);



		brClient = new BroadcastReceiver() {
			// действия при получении сообщений
			public void onReceive(Context context, Intent intent) {
				//int task = intent.getIntExtra(PARAM_TASK, 0);
				//int status = intent.getIntExtra(PARAM_STATUS, 0);
				//Log.d(LOG_TAG, "onReceive: task = " + task + ", status = " + status);

				// Ловим сообщения о старте задач
				//if (status  == STATUS_START) {
				//	switch (task) {

//						case TASK1_CODE:
//							tvTask1.setText("Task1 start");
//							//break;
//						case TASK2_CODE:
//							tvTask2.setText("Task2 start");
//							//break;
//						case TASK3_CODE:
//							//tvTask3.setText("Task3 start");
//							break;
				//	}
				//}

				// Ловим сообщения об окончании задач
				//if (status == STATUS_FINISH) {
				//	int result = intent.getIntExtra(PARAM_RESULT, 0);
				//	switch (task) {

//						case TASK1_CODE:
//							tvTask1.setText("Task1 finish, result = " + result);
//							break;
//						case TASK2_CODE:
//							tvTask2.setText("Task2 finish, result = " + result);
//							break;
//						case TASK3_CODE:
//							tvTask3.setText("Task3 finish, result = " + result);
//							break;
				//	}
				//}
			}
		};
		brServer = new BroadcastReceiver() {
			// действия при получении сообщений
			public void onReceive(Context context, Intent intent) {

			}
		};
		// создаем фильтр для BroadcastReceiver
		IntentFilter intFiltClient = new IntentFilter(BROADCAST_ACTION_CLIENT);
		IntentFilter intFiltServer = new IntentFilter(BROADCAST_ACTION_SERVER);
		// регистрируем (включаем) BroadcastReceiver
		getActivity().registerReceiver(brClient, intFiltClient);
		getActivity().registerReceiver(brServer, intFiltServer);

	}
	private CompoundButton.OnCheckedChangeListener serverListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			if(switcher.isChecked()){

				//sendButton.setEnabled(false);
				switcher.setText("Server ON");

				// Создаем Intent для вызова сервиса,
				// кладем туда параметр времени и код задачи
				intentServer = new Intent(getActivity(), ServerStreamService.class);
				// стартуем сервис
				getActivity().startService(intentServer);
			}
			else{
				switcher.setText("Server OFF");
				getActivity().stopService(intentServer);
//
			}
		}
	};
	private  View.OnClickListener connectToServerListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {


			// Создаем Intent для вызова сервиса,
			// кладем туда параметр времени и код задачи
			intentClient = new Intent(getActivity(), ClientStreamService.class);
			// стартуем сервис
			getActivity().startService(intentClient);


			//startSendAudioSocket(pbClient);
		}
	};
}
