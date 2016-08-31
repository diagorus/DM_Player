/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import com.dmplayer.R;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class FragmentEqualizer extends Fragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener,View.OnClickListener{

	TextView bass_boost_label = null;
	SeekBar bass_boost = null;
	CheckBox enabled = null;
	Button flat = null;

	Equalizer equalizer = null;
	BassBoost bb = null;

	int min_level = 0;
	int max_level = 100;

	static final int MAX_SLIDERS = 8; // Must match the XML layout
	SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
	TextView slider_labels[] = new TextView[MAX_SLIDERS];
	int num_sliders = 0;
	public FragmentEqualizer() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		View rootview = inflater.inflate(R.layout.fragment_equalizer, null);
		setupInitialViews(rootview);

		return rootview;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setupInitialViews(View view) {

		enabled = (CheckBox)view.findViewById(R.id.enabled);
		enabled.setOnCheckedChangeListener (this);

		flat = (Button)view.findViewById(R.id.flat);
		flat.setOnClickListener(this);

		bass_boost = (SeekBar)view.findViewById(R.id.bass_boost);
		bass_boost.setOnSeekBarChangeListener(this);
		bass_boost_label = (TextView)view.findViewById (R.id.bass_boost_label);

		sliders[0] = (SeekBar)view.findViewById(R.id.slider_1);
		slider_labels[0] = (TextView)view.findViewById(R.id.slider_label_1);
		sliders[1] = (SeekBar)view.findViewById(R.id.slider_2);
		slider_labels[1] = (TextView)view.findViewById(R.id.slider_label_2);
		sliders[2] = (SeekBar)view.findViewById(R.id.slider_3);
		slider_labels[2] = (TextView)view.findViewById(R.id.slider_label_3);
		sliders[3] = (SeekBar)view.findViewById(R.id.slider_4);
		slider_labels[3] = (TextView)view.findViewById(R.id.slider_label_4);
		sliders[4] = (SeekBar)view.findViewById(R.id.slider_5);
		slider_labels[4] = (TextView)view.findViewById(R.id.slider_label_5);
		sliders[5] = (SeekBar)view.findViewById(R.id.slider_6);
		slider_labels[5] = (TextView)view.findViewById(R.id.slider_label_6);
		sliders[6] = (SeekBar)view.findViewById(R.id.slider_7);
		slider_labels[6] = (TextView)view.findViewById(R.id.slider_label_7);
		sliders[7] = (SeekBar)view.findViewById(R.id.slider_8);
		slider_labels[7] = (TextView)view.findViewById(R.id.slider_label_8);

		equalizer = new Equalizer(0, 0);

		if (equalizer != null)
		{
			equalizer.setEnabled(true);
			int num_bands = equalizer.getNumberOfBands();
			num_sliders = num_bands;
			short r[] = equalizer.getBandLevelRange();
			min_level = r[0];
			max_level = r[1];
			for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
			{
				int[] freq_range = equalizer.getBandFreqRange((short)i);
				sliders[i].setOnSeekBarChangeListener(this);
				slider_labels[i].setText (formatBandLabel (freq_range));
				slider_labels[i].setTextSize(10);

			}
		}
		for (int i = num_sliders ; i < MAX_SLIDERS; i++)
		{
			sliders[i].setVisibility(View.GONE);
			slider_labels[i].setVisibility(View.GONE);
		}

		bb = new BassBoost (0, 0);
		if (bb != null)
		{
		}
		else
		{
			bass_boost.setVisibility(View.GONE);
			bass_boost_label.setVisibility(View.GONE);
		}

		updateUI();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onProgressChanged (SeekBar seekBar, int level,
								   boolean fromTouch)
	{
		if (seekBar == bass_boost)
		{
			bb.setEnabled (level > 0 ? true : false);
			bb.setStrength ((short)level); // Already in the right range 0-1000
		}
		else if (equalizer != null)
		{
			int new_level = min_level + (max_level - min_level) * level / 100;

			for (int i = 0; i < num_sliders; i++)
			{
				if (sliders[i] == seekBar)
				{
					equalizer.setBandLevel ((short)i, (short)new_level);
					break;
				}
			}
		}
	}

	public String formatBandLabel (int[] band)
	{
		return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
	}
	public String milliHzToString (int milliHz)
	{
		if (milliHz < 1000) return "";
		if (milliHz < 1000000)
			return "" + (milliHz / 1000) + "Hz";
		else
			return "" + (milliHz / 1000000) + "kHz";
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void updateSliders ()
	{
		for (int i = 0; i < num_sliders; i++)
		{
			int level;
			if (equalizer != null)
				level = equalizer.getBandLevel ((short)i);
			else
				level = 0;
			int pos = 100 * level / (max_level - min_level) + 50;
			sliders[i].setProgress (pos);
		}
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void updateBassBoost ()
	{
		if (bb != null)
			bass_boost.setProgress (bb.getRoundedStrength());
		else
			bass_boost.setProgress (0);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCheckedChanged (CompoundButton view, boolean isChecked)
	{
		if (view == (View) enabled)
		{
			equalizer.setEnabled (isChecked);
		}
	}

	@Override
	public void onClick (View view)
	{
		if (view == (View) flat)
		{
			setFlat();
		}
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void updateUI ()
	{
		updateSliders();
		updateBassBoost();
		enabled.setChecked (equalizer.getEnabled());
	}


	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void setFlat ()
	{
		if (equalizer != null)
		{
			for (int i = 0; i < num_sliders; i++)
			{
				equalizer.setBandLevel ((short)i, (short)0);
			}
		}

		if (bb != null)
		{
			bb.setEnabled (false);
			bb.setStrength ((short)0);
		}

		updateUI();
	}

	public void showAbout ()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity().getApplicationContext());

		alertDialogBuilder.setTitle("About Simple EQ");
		alertDialogBuilder.setMessage(R.string.copyright_message);
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setPositiveButton (R.string.ok,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
					}
				});
		AlertDialog ad = alertDialogBuilder.create();
		ad.show();

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
//		switch (item.getItemId())
//		{
//			case R.id.about:
//				showAbout();
//				return true;
//		}
		return super.onOptionsItemSelected(item);

	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}
}