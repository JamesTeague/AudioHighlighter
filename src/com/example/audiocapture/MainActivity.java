package com.example.audiocapture;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	private FileWriter outputDoc = null;
	private File orig;
	private File rname;
	private String fileFlags;
	private Button start,stop,flag,nextBtn;
	private long StartTime = 0;
	private long EndTime = 0;
	private long TotalTime = 0;
	private ArrayList<Long> FlagAbTimes;
	private ArrayList<Integer> FlagRelTimes;
	private int flagCount = 0;
	MediaPlayer m;
	BufferedWriter bw;

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
		nextBtn = (Button)findViewById(R.id.Next);
		stop.setEnabled(false);
		flag.setEnabled(false);
		nextBtn.setEnabled(false);
		outputFile = Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/myrecording.3gp";
		//		bw = new BufferedWriter (outputDoc);
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
				getAbsolutePath() + "/"+ fileFlags + ".3gp");
		intent.putExtra("myArray", FlagRelTimes);
		intent.putExtra("flagFile",Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/"+ fileFlags + ".txt" );
		startActivity(intent);
	}
	/**
	 * Start recording
	 * @param view
	 */
	public void start(View view){
		try {
			if(myAudioRecorder == null){
				//create new recorder object
				myAudioRecorder = new MediaRecorder();
				//set source and format
				myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
				//set place to save recording
				myAudioRecorder.setOutputFile(outputFile);
			}
			//must prepare before start
			myAudioRecorder.prepare();
			//start recording
			myAudioRecorder.start();
			//Grab Start Time
			StartTime = System.currentTimeMillis();
			Log.v("Time Elements", Long.toString(StartTime));
		} catch (IllegalStateException e) {
			// catch error that we called a method on an illegal state
			e.printStackTrace();
		} catch (IOException e) {
			// catch problem with file
			e.printStackTrace();
		}
		//started recording cannot start twice
		start.setEnabled(false);
		//allow stop and flags
		stop.setEnabled(true);
		flag.setEnabled(true);
		Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

	}
	public void stop(View view) throws IllegalArgumentException,
	SecurityException, IllegalStateException, IOException{
		//ask if you really want to stop
		AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);
		alertDialog.setTitle("Save File");
		alertDialog.setMessage("Are you sure you want to save this recording?");
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			//user has selected to save
			public void onClick(DialogInterface dialog, int which) {
				//stop recording
				myAudioRecorder.stop();
				//Grab End Time
				EndTime = System.currentTimeMillis();
				//total length of recording in seconds
				TotalTime = (EndTime - StartTime)/1000;
				Log.v("Time Elements", Long.toString(EndTime));
				Log.v("Time Elements", Long.toString(TotalTime));
				//delete object to record
				myAudioRecorder.release();
				myAudioRecorder  = null;
				//disable stop and flag
				stop.setEnabled(false);
				flag.setEnabled(false);
				//allow new recording
				start.setEnabled(true);
				Toast.makeText(getApplicationContext(), "Audio recorded successfully",
						Toast.LENGTH_LONG).show();
				//m.setDataSource(outputFile);

				AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);
				//ask what user wants to name file
				alertDialog.setTitle("Edit File Name");
				alertDialog.setMessage("What is the name of this recording?");
				final EditText input = new EditText(MainActivity.this);
				alertDialog.setView(input);
				alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Context context = getApplicationContext();
						String fileName = input.getText().toString();
						try {
							//store all the information needed with files
							save(fileName);
						} catch (IOException e) {
							//caught file error
							e.printStackTrace();
						}
						//set up toast
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
		nextBtn.setEnabled(true);
	}

	/**
	 * Grabs system time, and calculates the flag time
	 * relative to the length of the recording
	 * after you have reached flag limit disables button
	 * @param view
	 */
	public void flag(View view){
		flagCount++;
		if(flagCount <= 3){
			int relativeTime;
			//grab system time
			FlagAbTimes.add(System.currentTimeMillis());
			//alert user flag is done
			Toast.makeText(getApplicationContext(), "Flagged! " + flagCount + "/3", 
					Toast.LENGTH_LONG).show();
			//scale time to be in correct position in recording
			relativeTime = (int)(FlagAbTimes.get(FlagAbTimes.size()-1) - StartTime);
			//store times away
			FlagRelTimes.add(relativeTime);
			//check if we have reached our limit
			if(flagCount == 3){
				flag.setEnabled(false);
			}
		}
	}
	/**
	 * renames the audio file and creates corresponding txt file
	 * @param filename - desired filename
	 * @throws IOException
	 */
	public void save(String filename) throws IOException{
		//create file to make txt
		outputDoc = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ filename +".txt");
		//initiate writer
		bw = new BufferedWriter (outputDoc);
		//store back into Global val
		fileFlags = filename;
		//write each element to a line in the text file
		for(int flag : FlagRelTimes){
			Log.v("Time Elements", Integer.toString(flag));
			bw.write(Integer.toString(flag));
			bw.newLine();
			bw.flush();
		}
		//open audio file
		orig = new File(outputFile);
		//determine new file name
		rname = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ filename +".3gp");
		//rename file
		orig.renameTo(rname);
		outputDoc.close();
		bw.close();
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