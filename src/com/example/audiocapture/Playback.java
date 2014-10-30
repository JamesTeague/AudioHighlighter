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
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class Playback extends Activity {
	String filename; //holds name of 3gp
	String timestampsFile; //holds name of txt (flag file)
	private ArrayList<Integer> FlagRelTimes;
	private String basicFileName;
	private MediaPlayer m;
	//needed to read files
	private BufferedReader br;
	private BufferedWriter bw;
	private ImageButton pause;
	private ShareActionProvider mShareActionProvider;
	private SeekBar seekBar;
	private Handler mHandler;
	private String rfilename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("QuoteBite"); //app name
		getActionBar().setIcon(R.drawable.quoteformenu); //menu icon
		super.onCreate(savedInstanceState);
		Bundle b = new Bundle();
		m = new MediaPlayer();
		FlagRelTimes = new ArrayList<Integer>();
		//setContentView(R.layout.activity_playback);
		b = getIntent().getExtras();
		//path names of files
		basicFileName = b.getString("basicFileName");
		filename = b.getString("fileName");
		rfilename = filename;
		//give proper extensions
		timestampsFile = filename+".txt";
		//reuse of variable to new pathname
		filename = filename+".3gp";
		try {
			readInFlags();
			loadActivity();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//go to recording page
	public void goToRecording(View view){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	//go to files page
	public void goToFiles(View view){
		Intent intent = new Intent(this, Files_list.class);
		startActivity(intent);
	}

	private void loadActivity() throws IOException{
		//new scroll view
		ScrollView sv = new ScrollView(this);
		LinearLayout myBaseLayout = new LinearLayout(this);
		// specifying vertical orientation
		myBaseLayout.setOrientation(LinearLayout.VERTICAL);
		//background image
		myBaseLayout.setBackgroundResource(R.drawable.emptybackground);
		myBaseLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		final LinearLayout linLayout = new LinearLayout(this);
		// specifying vertical orientation
		linLayout.setOrientation(LinearLayout.VERTICAL);
		// creating LayoutParams  
		LayoutParams linLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
		linLayoutParam.setMargins(0, 0, 0, 20);
		// set LinearLayout as a root element of the screen 
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		setContentView(myBaseLayout);
		RelativeLayout.LayoutParams lpText = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpText.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mHandler = new Handler();
		seekBar = new SeekBar(this);
		//seekBar length set to duration of audio
		seekBar.setMax(m.getDuration());
		int x = m.getDuration() - m.getDuration();
		//starts seekBar at the beginning
		seekBar.setProgress(x);
		//runs runnable every second
		mHandler.postDelayed(run, 1000);
		ImageButton play = new ImageButton(this);
		ImageButton flag = new ImageButton(this);
		Button myFileName = new Button(this);
		//displays file name
		myFileName.setText(basicFileName.toUpperCase());
		myFileName.setBackgroundColor(Color.TRANSPARENT);
		myFileName.setTextSize(40);
		myBaseLayout.addView(myFileName);
		pause = new ImageButton(this);
		//pause button
		pause.setImageResource(R.drawable.pause_03);
		pause.setBackgroundColor(Color.TRANSPARENT);
		RelativeLayout horizLayout = new RelativeLayout(this);
		LinearLayout myInnerbuttons = new LinearLayout(this);
		//play button
		play.setImageResource(R.drawable.play_03);
		play.setBackgroundColor(Color.TRANSPARENT);
		//bite button
		flag.setImageResource(R.drawable.quote_03);
		flag.setBackgroundColor(Color.TRANSPARENT);
		//add views
		myBaseLayout.addView(seekBar);
		myInnerbuttons.addView(flag);
		myInnerbuttons.addView(play);
		myInnerbuttons.addView(pause);
		horizLayout.addView(myInnerbuttons, lp);
		myBaseLayout.addView(horizLayout);
		Button mybiteText = new Button(this);
		mybiteText.setBackgroundColor(Color.TRANSPARENT);
		mybiteText.setText("BITES:");
		mybiteText.setTextSize(25);
		myBaseLayout.addView(mybiteText, lpText);;
		myBaseLayout.addView(sv);
		sv.addView(linLayout);
		Collections.sort(FlagRelTimes);
		writeOutFlags(FlagRelTimes);
		
		flag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//does not allow user to flag before 15 seconds
				if(m.getCurrentPosition() > 15000 && m.isPlaying()){
					//subtracts 15 seconds from time user flagged
					FlagRelTimes.add(m.getCurrentPosition()-15000);
					try {
						loadActivity();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					play(v);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					pause(v);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});;
		LayoutParams lpView = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lpView.setMargins(0, 0, 0, 20);
		for( int i=0; i<FlagRelTimes.size(); i++)
		{
			final LinearLayout myFlagButtons = new LinearLayout(this);
			myFlagButtons.setOrientation(LinearLayout.HORIZONTAL);
			final RelativeLayout horLayout = new RelativeLayout(this);
			horLayout.setBackgroundResource(R.drawable.file_button_03);
			RelativeLayout.LayoutParams lpRightRule = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lpRightRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			ImageButton btn = new ImageButton(this);
			Button myText= new Button(this);
			ImageButton delete = new ImageButton(this);
			//deletes a flag
			delete.setId(FlagRelTimes.get(i)+1);
			final int deleteid_ = delete.getId();
			btn.setId(FlagRelTimes.get(i));
			final int id_ = btn.getId();
			myText.setText(DateUtils.formatElapsedTime(id_/1000));
			myText.setBackgroundColor(Color.TRANSPARENT);
			myText.setTextSize(25);
			LinearLayout myInnerLayout = new LinearLayout(this);
			//play button for flags
			btn.setBackgroundResource(R.drawable.play_bite);
			btn.setImageResource(R.drawable.play_bite);
			btn.setBackgroundColor(Color.TRANSPARENT);
			//trash can for flags
			delete.setImageResource(R.drawable.whitetrash);
			delete.setBackgroundColor(Color.TRANSPARENT);
			RelativeLayout.LayoutParams lpLeftRule = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lpLeftRule.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			//add views
			myInnerLayout.addView(myText);
			//myInnerLayout.setGravity(Gravity.CENTER_VERTICAL);
			myFlagButtons.addView(btn, lp);
			myFlagButtons.addView(delete);
			horLayout.addView(myInnerLayout);
			horLayout.addView(myFlagButtons, lpRightRule);
			linLayout.addView(horLayout, lpView);
			delete = ((ImageButton)findViewById(deleteid_));
			delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//deleting flag pop up
					AlertDialog.Builder alertDialog  = new AlertDialog.Builder(Playback.this);
					alertDialog.setTitle("Delete Flag?");
					alertDialog.setMessage("Are you sure you want to delete this flag?");
					alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					});
					//deletes flag
					alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							linLayout.removeView(horLayout);
							FlagRelTimes.remove(Integer.valueOf(deleteid_-1));
							try {
								writeOutFlags(FlagRelTimes);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					alertDialog.show();
				}
			});
			btn = ((ImageButton) findViewById(id_));
			btn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {  
					m.reset();
					if(m.isPlaying()){
						m.stop(); 
					}else{
						try {
							m.setDataSource(filename);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					seekBar.setProgress(id_);
					try {
						m.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					m.start();
					m.seekTo(id_);
				}
			});
		}
	}
	
	/* readInFlags
	 * read in flags from text file
	 * @throws IOException
	 */
	private void readInFlags() throws IOException {
		br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(timestampsFile))));
		String line = null;

		while((line = br.readLine()) != null){
			FlagRelTimes.add(Integer.parseInt(line));
		}

		br.close();
		br = null;
	}
	
	/* writeOutFlags
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
		//close Writer
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

	//menu items
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//sends to recording screen
		if (id == R.id.mic){
			goToRecording(item.getActionView());
		}
		//sends to files page
		if(id == R.id.folderPlayback){
			goToFiles(item.getActionView());
		}
		return super.onOptionsItemSelected(item);
	}
	
	//shares audio (sends to email, dropbox, google play, etc.)
	private Intent getDefaultShareIntent(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("audio/3gpp");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
		//subject line is title of the file
		intent.putExtra(Intent.EXTRA_SUBJECT, basicFileName.toUpperCase());
		//startActivity(Intent.createChooser(intent, "Share sound"));
		return intent;
	}

	//plays audio from beginning or resumes when paused
	public void play(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		int x = m.getDuration() - m.getDuration();
		//plays audio from beginning
		if(seekBar.getProgress() == x) {
			m.setDataSource(filename);
			m.prepare();
			seekBar.setProgress(x);
			m.start();
			m.seekTo(x);
		}
		//plays audio when paused
		if(!m.isPlaying()){
			int mCurrentPosition= m.getCurrentPosition();			
			seekBar.setProgress(mCurrentPosition);
			m.start();
			m.seekTo(mCurrentPosition);			
		}
	}
	
	//pauses audio
	public void pause(View view) throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{
		if(m.isPlaying()){
			m.pause();
		}
	}
	
	Runnable run = new Runnable() {
		@Override public void run() {
			//sets seekBar at position of audio
			if(m.isPlaying()){
				int mCurrentPosition = m.getCurrentPosition();
				seekBar.setProgress(mCurrentPosition);
			}
			//calls ever second
			mHandler.postDelayed(this, 1000);
			int x = m.getDuration() - m.getDuration();
			//sets length of seekBar to duration of audio
			seekBar.setMax(m.getDuration());
			//sets seekBar back to the beginning when it gets to the end
			if (m.getCurrentPosition() == m.getDuration()) {
				seekBar.setProgress(x);
			}
			//handles user action from seekBar 
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				//handles activity for when seekBar is dragged by user
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
					try {
						m.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					seekBar.setProgress(progress);
					m.start();
					m.seekTo(progress);

					int x = m.getDuration() - m.getDuration();
					if (!m.isPlaying()) {
						seekBar.setProgress(x);
						m.stop();
					}
					if (m.getCurrentPosition() == m.getDuration()) {
						m.stop();
						seekBar.setProgress(x);
					}

				}
			});
		}
	};
}
