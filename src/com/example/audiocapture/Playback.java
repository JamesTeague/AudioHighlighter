package com.example.audiocapture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Toast;

public class Playback extends Activity {
	String fileName;
	private ArrayList<Integer> FlagRelTimes;
	MediaPlayer m;
	//private Button play;
	int mySize;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m = new MediaPlayer();
		//setContentView(R.layout.activity_playback);
		Bundle b = new Bundle();
		b=getIntent().getExtras();
		//play = (Button)findViewById(R.id.button3);
		fileName = b.getString("fileName");
		FlagRelTimes = b.getIntegerArrayList("myArray");
		loadActivity();
		
	}
		
	void loadActivity()
	{
		mySize = FlagRelTimes.size();
		LinearLayout baseLayout = new LinearLayout(this);
		baseLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		baseLayout.setOrientation(LinearLayout.VERTICAL);
		baseLayout.setBackgroundColor(Color.parseColor("#000000"));
		
		ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		final LinearLayout linLayout = new LinearLayout(this);
        // specifying vertical orientation
        linLayout.setOrientation(LinearLayout.VERTICAL);
        // creating LayoutParams  
        LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
        // set LinearLayout as a root element of the screen 
        //setContentView(linLayout, linLayoutParam);
        setContentView(baseLayout);
        Button play = new Button(this);
        Button flag = new Button(this);
		linLayout.setBackgroundColor(Color.parseColor("#50ADD8E6"));
		//linLayout.setAlpha(new Float(.5));
        LinearLayout horizLayout = new LinearLayout(this);
        horizLayout.setOrientation(LinearLayout.HORIZONTAL);      
        horizLayout.setPadding(5, 5, 5, 5);
        flag.setText("flag");
        play.setText("Play");
        horizLayout.addView(flag);
        horizLayout.addView(play);
        baseLayout.addView(horizLayout);
       
        baseLayout.addView(sv);
        sv.addView(linLayout);
        LayoutParams lpView = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
        
//        linLayout.addView(play, lpView);
//        linLayout.addView(flag, lpView);
        flag.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FlagRelTimes.add(m.getCurrentPosition());
				loadActivity();
			}
		});
        play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m.reset();
				if(m.isPlaying()){
					m.stop();	
				}else{
					try {
						m.setDataSource(fileName);
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
				m.seekTo(0);
				Toast.makeText(getApplicationContext(), "Playing audio from beginning", 
						Toast.LENGTH_LONG).show();

			}
				
			
		});
        Collections.sort(FlagRelTimes);
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
							m.setDataSource(fileName);
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
		        	
		        }
		    });
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
	
	
	
	
	
	
}
