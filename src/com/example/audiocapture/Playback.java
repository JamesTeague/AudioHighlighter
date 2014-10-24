package com.example.audiocapture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class Playback extends Activity {
	String filename; //holds name of 3gp
	String timestampsFile; //holds name of txt (flag file)
	private ArrayList<Integer> FlagRelTimes;
	MediaPlayer m;
	//needed to read files
	BufferedReader br;
	BufferedWriter bw;
	private Button pause_resume;
	private ShareActionProvider mShareActionProvider;
	private SeekBar seekBar;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m = new MediaPlayer();
		FlagRelTimes = new ArrayList<Integer>();

		//		setContentView(R.layout.activity_playback);
		Bundle b = new Bundle();
		b=getIntent().getExtras();
		//path names of files
		filename = b.getString("fileName");
		//give proper extensions
		timestampsFile = filename+".txt";
		filename = filename+".3gp";
		//		Log.v("Time Elements", timestampsFile);

		try {
			readInFlags();
			loadActivity();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadActivity() throws IOException{
		ScrollView sv = new ScrollView(this);
		sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		final LinearLayout linLayout = new LinearLayout(this);
		// specifying vertical orientation
		linLayout.setOrientation(LinearLayout.VERTICAL);
		// creating LayoutParams  
		//		LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
		// set LinearLayout as a root element of the screen 
		//setContentView(linLayout, linLayoutParam);
		setContentView(sv);
		sv.addView(linLayout);
		mHandler = new Handler();
		seekBar = new SeekBar(this);
		seekBar.setMax(m.getDuration());
		int x = m.getDuration() - m.getDuration();
		seekBar.setProgress(x);
		mHandler.postDelayed(run, 1000);
		Button play = new Button(this);
		Button flag = new Button(this);
		pause_resume = new Button(this);
		play.setText("Play");
		flag.setText("Flag");
		pause_resume.setText("Pause");
		linLayout.addView(seekBar);
		linLayout.addView(flag);
		linLayout.addView(play);
		linLayout.addView(pause_resume);
		pause_resume.setEnabled(false);
		Collections.sort(FlagRelTimes);
		writeOutFlags(FlagRelTimes);
		flag.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlagRelTimes.add(m.getCurrentPosition());
				try {
					loadActivity();
					pause_resume.setEnabled(true);
					pause_resume.setText("Pause");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		play.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					play(v);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		pause_resume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					pause(v);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});;
		LayoutParams lpView = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		for( int i=0; i<FlagRelTimes.size(); i++)
		{
			final LinearLayout horLayout = new LinearLayout(this);
			horLayout.setOrientation(LinearLayout.HORIZONTAL);
			Button btn = new Button(this);
			Button delete = new Button(this);
			delete.setId(FlagRelTimes.get(i)+1);
			final int deleteid_ = delete.getId();
			delete.setText("Delete");
			btn.setId(FlagRelTimes.get(i));
			final int id_ = btn.getId();
			btn.setText("Flag " + DateUtils.formatElapsedTime(id_/1000));
			horLayout.addView(btn);
			horLayout.addView(delete);
			linLayout.addView(horLayout, lpView);
			delete = ((Button)findViewById(deleteid_));

			delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialog  = new AlertDialog.Builder(Playback.this);
					alertDialog.setTitle("Delete Flag?");
					alertDialog.setMessage("Are you sure you want to delete this flag?");
					alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}

					});

					alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							linLayout.removeView(horLayout);
							FlagRelTimes.remove(Integer.valueOf(deleteid_-1));
							try {
								writeOutFlags(FlagRelTimes);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					});
					alertDialog.show();
				}
			});
			btn = ((Button) findViewById(id_));
			btn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {  

					m.reset();

					if(m.isPlaying()){

						m.stop(); 

					}else{

						try {
							m.setDataSource(filename);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					seekBar.setProgress(id_);
					try {
						m.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					m.start();
					m.seekTo(id_);
					Toast.makeText(getApplicationContext(), "Playing audio from flag ", 
							Toast.LENGTH_LONG).show();
					pause_resume.setEnabled(true);
					pause_resume.setText("Pause");
				}
			});
		}
	}
	/**
	 * readInFlags
	 * read in flags from text file
	 * @throws IOException
	 */
	private void readInFlags() throws IOException {
		br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(timestampsFile))));
		String line = null;

		while((line = br.readLine()) != null){
			FlagRelTimes.add(Integer.parseInt(line));
			Log.v("Time Elements", "IN");
		}

		br.close();
		br = null;
	}
	/**
	 * writeOutFlags
	 * Write out all flags to txt
	 * @param flags - ArrayList that holds timestamps (flags)
	 * 				  to be written
	 * @throws IOException
	 */
	private void writeOutFlags(ArrayList<Integer> flags) throws IOException{
		bw = new BufferedWriter(new FileWriter(timestampsFile));
		//write every flag to the file
		for(int flag : flags){
			bw.write(Integer.toString(flag));
			bw.newLine();
			bw.flush();
		}
		//close Write
		bw.close();
		//release object
		bw = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.playback, menu);
		//Getting the actionprovider associated with the menu item whose id is share
		mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();

		//Setting a share intent
		mShareActionProvider.setShareIntent(getDefaultShareIntent());

		return super.onCreateOptionsMenu(menu);
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
	private Intent getDefaultShareIntent(){

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("audio/3gpp");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
		intent.putExtra(Intent.EXTRA_SUBJECT, filename);
		//startActivity(Intent.createChooser(intent, "Share sound"));
		return intent;
	}

	public void play(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		m.reset();
		if(m.isPlaying()){
			m.stop();	
		}else{
			m.setDataSource(filename);
		}
		seekBar.setProgress(0);
		m.prepare();
		m.start();
		m.seekTo(0);
		Toast.makeText(getApplicationContext(), "Playing audio from beginning", 
				Toast.LENGTH_LONG).show();
		pause_resume.setEnabled(true);
		pause_resume.setText("Pause");
	}
	public void pause(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		pause_resume = (Button)view;
		String buttonText = pause_resume.getText().toString();
		if (buttonText == "Pause") {
			pause_resume.setText("Resume");
			if(m.isPlaying()){
				m.pause();
				Toast.makeText(getApplicationContext(), "Paused audio", 
						Toast.LENGTH_LONG).show();
			}
		}
		else if (buttonText == "Resume"){
			int mCurrentPosition= m.getCurrentPosition();

			seekBar.setProgress(mCurrentPosition);
			m.start();
			m.seekTo(mCurrentPosition);
			pause_resume.setText("Pause");
			Toast.makeText(getApplicationContext(), "Playing audio from paused spot", 
					Toast.LENGTH_LONG).show();
		}
	}
	Runnable run = new Runnable() {
		@Override public void run() {
			if(m.isPlaying()){
				int mCurrentPosition = m.getCurrentPosition();
				seekBar.setProgress(mCurrentPosition);
			}
			mHandler.postDelayed(this, 1000);

			int x = m.getDuration() - m.getDuration();
			seekBar.setMax(m.getDuration());
			if (m.getCurrentPosition() == m.getDuration()) {
				pause_resume.setEnabled(false);
				seekBar.setProgress(x);
			}
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				}
			});
		}
	};
}
