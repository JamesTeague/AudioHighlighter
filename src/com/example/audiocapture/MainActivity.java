package com.example.audiocapture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	private String listFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + "/files.txt";
	private File orig;
	private File rname;
	private String fileFlags;
	private ImageButton flag;
	private long StartTime = 0;
	private long Threshold = 0;
	private long EndTime = 0;
	private long TotalTime = 0;
	private ArrayList<Long> FlagAbTimes;
	private ArrayList<Integer> FlagRelTimes;
	private ArrayList<String> ListofFileNames;
	private Chronometer myChronometer;
	private TextView lastFlag;
	private TextView lastFlagTime;
	private ImageView record;
	private BufferedWriter bw;
	private BufferedWriter fw;
	private BufferedReader fr;
	private boolean recording;
	private boolean exists = (new File(listFiles)).exists();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("Quote Bite");
		getActionBar().setIcon(R.drawable.quoteformenu);
		FlagAbTimes = new ArrayList<Long>();
		FlagRelTimes = new ArrayList<Integer>();
		ListofFileNames = new ArrayList<String>();
		recording = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		start = (Button)findViewById(R.id.button1);
//		stop = (Button)findViewById(R.id.button2);
		flag = (ImageButton)findViewById(R.id.flagButton);
//		nextBtn = (Button)findViewById(R.id.Next);
		record = (ImageView)findViewById(R.id.imageButton1);
//		stop.setEnabled(false);
		flag.setImageResource(R.drawable.littlerecord07);
		flag.setVisibility(View.INVISIBLE);
		lastFlag = ((TextView)findViewById(R.id.textView2));
		lastFlagTime =((TextView)findViewById(R.id.textView3));
//		nextBtn.setEnabled(false);
		outputFile = Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/myrecording.3gp";
		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		myChronometer = (Chronometer)findViewById(R.id.chronometer);
		myChronometer.setVisibility(View.INVISIBLE);
		if(!exists){
			AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);
			alertDialog.setTitle("What are Bites?!");
			alertDialog.setMessage("Tap the Bite button to capture an important qoute!"
					+ " It will mark 5 seconds of audio before you tap, so you never miss anything good."
					+ " Tap away: You can catch as many QuoteBites in a recording as you'd like.");
			alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				//do nothing
				}
			});
			alertDialog.show();
		}
		try {
			readInFilesforNoob();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		myChronometer.stop();
//		myChronometer.setBase(SystemClock.elapsedRealtime());
//		myChronometer.setText("00:00");
		


	}
	public void goToFiles(View view){
		Intent intent = new Intent(this, Files_list.class);
		intent.putExtra("fileName", Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/"+ fileFlags + ".3gp");

		intent.putExtra("flagFile", Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/"+ fileFlags + ".txt" );
		intent.putExtra("listOfFiles", listFiles);
		myChronometer.stop();
		startActivity(intent);
	}
	public void startOrStop (View view) throws IllegalArgumentException, 
	SecurityException, IllegalStateException, IOException{
		if(recording){
			stop(view);
		}else{
			start(view);
		}	
	}
	/**
	 * Start recording
	 * @param view
	 */
	public void start(View view){
		record.setImageResource(R.drawable.presstostop);
		flag.setVisibility(View.VISIBLE);
		lastFlag.setText("");
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
			myChronometer.setBase(SystemClock.elapsedRealtime());
			myChronometer.setVisibility(View.VISIBLE);
		    myChronometer.start();
			//must prepare before start
			myAudioRecorder.prepare();
			//start recording
			myAudioRecorder.start();
			recording = true;
			//Grab Start Time
			StartTime = System.currentTimeMillis();
			Threshold = StartTime + 5000;
			Log.v("Time Elements", Long.toString(StartTime));
		} catch (IllegalStateException e) {
			// catch error that we called a method on an illegal state
			e.printStackTrace();
		} catch (IOException e) {
			// catch problem with file
			e.printStackTrace();
		}
		//started recording cannot start twice
//		start.setEnabled(false);
		//allow stop and flags
//		stop.setEnabled(true);
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
				myChronometer.stop();
				myChronometer.setVisibility(View.INVISIBLE);
				record.setImageResource(R.drawable.newrecord);
				myAudioRecorder.stop();
				flag.setVisibility(View.INVISIBLE);
				recording = false;
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
//				stop.setEnabled(false);
				flag.setEnabled(false);
				//allow new recording
//				start.setEnabled(true);
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
//		nextBtn.setEnabled(true);
	}

	/**
	 * Grabs system time, and calculates the flag time
	 * relative to the length of the recording
	 * after you have reached flag limit disables button
	 * @param view
	 */
	public void flag(View view){
		if(System.currentTimeMillis() > Threshold){
			int relativeTime;
			//grab system time
			
			FlagAbTimes.add(System.currentTimeMillis()-5000);
			//scale time to be in correct position in recording
			relativeTime = (int)(FlagAbTimes.get(FlagAbTimes.size()-1) - StartTime);	
			lastFlag.setText("LAST BITE AT");
			lastFlagTime.setText(DateUtils.formatElapsedTime(relativeTime/1000));
			//store times away
			
			FlagRelTimes.add(relativeTime);
		}
	}
	/**
	 * renames the audio file and creates corresponding txt file
	 * @param filename - desired filename
	 * @throws IOException
	 */
	public void save(String filename) throws IOException{
		lastFlag.setText(R.string.Recording);
		//initiate writer
		bw = new BufferedWriter (new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ filename +".txt"));
		fw = new BufferedWriter (new FileWriter(listFiles, true));
		fw.append(filename);
		fw.append("\n");
		fw.close();
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
		bw.close();
	}

	private void readInFilesforNoob() throws IOException {
		//create objects required for read
		fr = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(listFiles))));
		String fline = null;
		//read all lines in file
		while((fline = fr.readLine()) != null){
			//add filenames to the ArrayList
			ListofFileNames.add(fline);	
		}
		if(ListofFileNames.size() < 1){
			//TODO: Show pop up
		}
//		Log.v("Time Elements", ListofFileNames.toString());
		//close all streams
		fr.close();
		//release objects
		fr = null;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		 if(id == R.id.folder && !recording){
			goToFiles(item.getActionView());
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {

	}



}