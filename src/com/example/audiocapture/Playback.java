package com.example.audiocapture;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Playback extends Activity {
	String fileName;
	private ArrayList<Integer> FlagRelTimes;
	MediaPlayer m;
	private Button play,flag1,flag2,flag3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m = new MediaPlayer();
		setContentView(R.layout.activity_playback);
		Bundle b = new Bundle();
		b=getIntent().getExtras();
		play = (Button)findViewById(R.id.button3);
		flag1 = (Button)findViewById(R.id.flag1);
		flag2 = (Button)findViewById(R.id.flag2);
		flag3 = (Button)findViewById(R.id.flag3);
		fileName = b.getString("fileName");
		FlagRelTimes = b.getIntegerArrayList("myArray");
		Log.v("Time Elements", fileName);
		//Disable Non-existent flags
		if(FlagRelTimes.size() == 0){
			Log.v("Time Elements", "FlagRelTimes is null");
			flag1.setEnabled(false);
			flag2.setEnabled(false);
			flag3.setEnabled(false);
		}
		else if(FlagRelTimes.size() == 1){
			flag2.setEnabled(false);
			flag3.setEnabled(false);
		}
		else if(FlagRelTimes.size() == 2){
			flag3.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.playback, menu);
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
	public void play(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		m.reset();
		if(m.isPlaying()){
			m.stop();	
		}else{
			m.setDataSource(fileName);
		}
		m.prepare();
		m.start();
		m.seekTo(0);
		Toast.makeText(getApplicationContext(), "Playing audio from beginning", 
				Toast.LENGTH_LONG).show();

	}
	public void flag1(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		m.reset();
		if(m.isPlaying()){
			m.stop();	
		}else{
			m.setDataSource(fileName);
		}
		m.prepare();
		m.start();
		m.seekTo(FlagRelTimes.get(0));
		Toast.makeText(getApplicationContext(), "Playing audio from flag1", 
				Toast.LENGTH_LONG).show();

	}
	public void flag2(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		m.reset();
		if(m.isPlaying()){
			m.stop();	
		}else{
			m.setDataSource(fileName);
		}
		m.prepare();
		m.start();
		m.seekTo(FlagRelTimes.get(1));
		Toast.makeText(getApplicationContext(), "Playing audio from flag2", 
				Toast.LENGTH_LONG).show();

	}
	public void flag3(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		m.reset();
		if(m.isPlaying()){
			m.stop();	
		}else{
			m.setDataSource(fileName);
		}
		m.prepare();
		m.start();
		m.seekTo(FlagRelTimes.get(2));
		Toast.makeText(getApplicationContext(), "Playing audio from flag3", 
				Toast.LENGTH_LONG).show();

	}
	
}
