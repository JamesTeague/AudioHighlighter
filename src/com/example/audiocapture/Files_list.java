package com.example.audiocapture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
public class Files_list extends Activity {
	String listFiles;
	private ArrayList<String> ListofFileNames;
	BufferedReader fr;
	BufferedWriter fw;
	final private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/files.txt";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_files_list);
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		ListofFileNames = new ArrayList<String>();
		listFiles = b.getString("listOfFiles");
		try {
			readInFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadActivity();
	}
	
	/**
	 * loadActivity
	 * Dynamically create the layout of page 
	 * after finding file names
	 */
	private void loadActivity(){
		//make new scrollView
		ScrollView sv = new ScrollView(this);
		sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		LinearLayout linLayout = new LinearLayout(this);
		// specifying vertical orientation
		linLayout.setOrientation(LinearLayout.VERTICAL);
		// creating LayoutParams  
//		LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
		// set LinearLayout as a root element of the screen 
		//setContentView(linLayout, linLayoutParam);
		setContentView(sv);
		sv.addView(linLayout);
		LayoutParams lpView = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//loop through list of file names
		for( int i=0; i<ListofFileNames.size(); i++)
		{
			//create button, set id, and set the text
			Button btn = new Button(this);
			btn.setId(i);
			btn.setText(ListofFileNames.get(i));
			final String fileName = ListofFileNames.get(i);
			linLayout.addView(btn, lpView);
			btn = ((Button) findViewById(i));
			btn.setOnClickListener(new View.OnClickListener() {
				//call Playback page
				public void onClick(View view) {  
					Intent intent = new Intent(view.getContext(), Playback.class);
					//pass file name to activity
					intent.putExtra("fileName", Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/"+fileName);
					startActivity(intent);
				}
			});
		}
	}
	
	/**
	 * readInFiles
	 * Will read in file names from the specified .txt file
	 * and insert those files into a global ArrayList
	 * @throws IOException
	 */
	private void readInFiles() throws IOException {
		//create objects required for read
		fr = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(listFiles))));
		String fline = null;
		//read all lines in file
		while((fline = fr.readLine()) != null){
			//add filenames to the ArrayList
			ListofFileNames.add(fline);	
		}
//		Log.v("Time Elements", ListofFileNames.toString());
		//close all streams
		fr.close();
		//release objects
		fr = null;
	}
	
	/**
	 * writeOutFiles
	 * specified to overwrite the files list in the case of deletion
	 * @param lof - ArrayList that will be made to Rewrite collection of Files
	 * @throws IOException
	 */
	private void writeOutFiles(ArrayList<String> lof) throws IOException{
		//create objects needed for writing files
		fw = new BufferedWriter(new FileWriter(filePath));
		//write each file name from ArrayList to file
		for(String fname : lof){
			fw.write(fname);
			fw.write("\n");
		}
		//Close streams
		fw.close();

		//release objects
		fw = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.files_list, menu);
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
