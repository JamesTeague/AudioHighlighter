package com.example.audiocapture;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	private Button start,stop,flag;
	private long StartTime = 0;
	private long EndTime = 0;
	private long TotalTime = 0;
	private ArrayList<Long> FlagAbTimes;
	private ArrayList<Integer> FlagRelTimes;
	private int flagCount = 0;
	MediaPlayer m;
	boolean isRecording;
	Chronometer myChronometer;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FlagAbTimes = new ArrayList<Long>();
		FlagRelTimes = new ArrayList<Integer>();
		m = new MediaPlayer();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		start = (Button)findViewById(R.id.button1);
		stop = (Button)findViewById(R.id.button2);
		flag = (Button)findViewById(R.id.flagButton);
		stop.setEnabled(false);
		stop.setVisibility(View.INVISIBLE);
		flag.setEnabled(false);
		outputFile = Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/myrecording.3gp";
		isRecording=false;
		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}
	
	public void goToPlayback(View view){
		Intent intent = new Intent(this, Playback.class);
		intent.putExtra("fileName", Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/myrecording.3gp");
		intent.putExtra("myArray", FlagRelTimes);
		startActivity(intent);
	}
	
	
	public void start(View view) throws IllegalArgumentException,
	SecurityException, IllegalStateException, IOException{
		myChronometer = (Chronometer)findViewById(R.id.chronometer);

		try { 
			if(isRecording==false)
			{
				isRecording=true;
			    start.setText("Stop");
			    
			    if(myAudioRecorder == null){
					myAudioRecorder = new MediaRecorder();
					myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
					myAudioRecorder.setOutputFile(outputFile);
				}
	            myChronometer.setBase(SystemClock.elapsedRealtime());
			    myChronometer.start();
				myAudioRecorder.prepare();
				myAudioRecorder.start();
				//Grab Start Time
				StartTime = System.currentTimeMillis();
				Log.v("Time Elements", Long.toString(StartTime));
				
				//start.setEnabled(false);
				//stop.setEnabled(true);
				flag.setEnabled(true);
				Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
			    
			}
			else if(isRecording==true)
			{
				AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);
				alertDialog.setTitle("Save File");
				alertDialog.setMessage("Are you sure you want to save this recording?");
				alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						myChronometer.stop();
						myAudioRecorder.stop();
						//Grab End Time
						EndTime = System.currentTimeMillis();
						//      flagTimes.add(EndTime);
						TotalTime = (EndTime - StartTime)/1000;
						Log.v("Time Elements", Long.toString(EndTime));
						Log.v("Time Elements", Long.toString(TotalTime));
						myAudioRecorder.release();
						myAudioRecorder  = null;
						stop.setEnabled(false);
						flag.setEnabled(false);
						start.setEnabled(true);
						//		play.setEnabled(true);
						Toast.makeText(getApplicationContext(), "Audio recorded successfully",
								Toast.LENGTH_LONG).show();
						//m.setDataSource(outputFile);
						
						AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);

						alertDialog.setTitle("Edit File Name");
						alertDialog.setMessage("What is the name of this recording?");
						final EditText input = new EditText(MainActivity.this);
						alertDialog.setView(input);
						alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								
								Context context = getApplicationContext();
								String fileName = input.getText().toString();
								CharSequence text = "File '"+ fileName +"' has saved!";
								int duration = Toast.LENGTH_SHORT;

								Toast toast = Toast.makeText(context, text, duration);
								toast.show();
							}
						});
						alertDialog.show();
					}
				});
				alertDialog.show();
			}
			
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}
	public void stop(View view) throws IllegalArgumentException,
	SecurityException, IllegalStateException, IOException{

		AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);
		alertDialog.setTitle("Save File");
		alertDialog.setMessage("Are you sure you want to save this recording?");
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//myChronometer.stop();
				myAudioRecorder.stop();
				//Grab End Time
				EndTime = System.currentTimeMillis();
				//      flagTimes.add(EndTime);
				TotalTime = (EndTime - StartTime)/1000;
				Log.v("Time Elements", Long.toString(EndTime));
				Log.v("Time Elements", Long.toString(TotalTime));
				myAudioRecorder.release();
				myAudioRecorder  = null;
				stop.setEnabled(false);
				flag.setEnabled(false);
				start.setEnabled(true);
				//		play.setEnabled(true);
				Toast.makeText(getApplicationContext(), "Audio recorded successfully",
						Toast.LENGTH_LONG).show();
				//m.setDataSource(outputFile);
				
				AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);

				alertDialog.setTitle("Edit File Name");
				alertDialog.setMessage("What is the name of this recording?");
				final EditText input = new EditText(MainActivity.this);
				alertDialog.setView(input);
				alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						Context context = getApplicationContext();
						String fileName = input.getText().toString();
						CharSequence text = "File '"+ fileName +"' has saved!";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
				});
				alertDialog.show();
			}
		});
		alertDialog.show();

	}


	public void flag(View view){
//		flagCount++;
//		if(flagCount <= 3){
			int relativeTime;
			FlagAbTimes.add(System.currentTimeMillis());
			relativeTime = (int)(FlagAbTimes.get(FlagAbTimes.size()-1) - StartTime);
			FlagRelTimes.add(relativeTime);
//			if(flagCount == 3){
//				flag.setEnabled(false);
//			}
//		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}



}