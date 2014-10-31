package edu.drake.QuoteBite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class Files_list extends Activity {
	private String listFiles;
	private ArrayList<String> ListofFileNames;
	private BufferedReader fr;
	private BufferedWriter fw;
	//gets path of file
	final private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/files.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("QuoteBite"); //app name
		getActionBar().setIcon(R.drawable.quoteformenu); //menu icon
		super.onCreate(savedInstanceState);
		//Arraylist to hold the file names that will appear on the page
		ListofFileNames = new ArrayList<String>();
		try {
			readInFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadActivity();
	}

	/* loadActivity
	 * Dynamically create the layout of page 
	 * after finding file names.
	 * Will be recalled after the user deletes a file. 
	 */
	private void loadActivity(){
		//make new linearLayout, and set parameters
		LinearLayout BaseLayout = new LinearLayout(this);
		BaseLayout.setOrientation(LinearLayout.VERTICAL);
		BaseLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		BaseLayout.setBackgroundResource(R.drawable.emptybackground);
		//make new scrollView to handle more files than the page length
		ScrollView sv = new ScrollView(this);
		Button myFilesTitle = new Button(this);
		myFilesTitle.setBackgroundColor(Color.TRANSPARENT);
		myFilesTitle.setTextSize(35);
		myFilesTitle.setText("MY FILES");
		RelativeLayout.LayoutParams lpLeftRule = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpLeftRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		BaseLayout.addView(myFilesTitle, lpLeftRule);
		sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		LinearLayout linLayout = new LinearLayout(this);
		//specifying vertical orientation
		linLayout.setOrientation(LinearLayout.VERTICAL);
		setContentView(BaseLayout);
		BaseLayout.addView(sv);
		sv.addView(linLayout);
		LayoutParams lpView = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		//loop through list of file names to dynamically create the file names and buttons
		for( int i=1; i<=ListofFileNames.size(); i++)
		{
			//create button, set id, and set the text
			final RelativeLayout horLayout = new RelativeLayout(this);
			RelativeLayout.LayoutParams lpRightRule = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lpRightRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			horLayout.setBackgroundResource(R.drawable.file_button_03);
			Button btn = new Button(this);
			ImageButton delete = new ImageButton(this);
			btn.setId(i);
			//width of button to take you to playback screen
			btn.setWidth(R.layout.activity_files_list - 50);
			btn.setGravity(Gravity.START);;
			delete.setId(i*1000);
			//delete icon
			delete.setImageResource(R.drawable.whitetrash);
			delete.setBackgroundColor(Color.TRANSPARENT);
			final int deleteid_ = delete.getId();
			btn.setText(ListofFileNames.get(i-1).toUpperCase());
			btn.setBackgroundColor(Color.TRANSPARENT);
			final String fileName = ListofFileNames.get(i-1);
			//add views to the scroll view
			horLayout.addView(btn);
			horLayout.addView(delete, lpRightRule);
			linLayout.addView(horLayout, lpView);
			delete = ((ImageButton)findViewById(deleteid_));
			//Set the delete on click listener to accomodate clicks of dynamic buttons
			delete.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view){
					//deleting file pop up
					AlertDialog.Builder alertDialog  = new AlertDialog.Builder(Files_list.this);
					alertDialog.setTitle("Delete File?");
					alertDialog.setMessage("Are you sure you want to delete this file?");
					alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					});
					//deletes file after confirmation
					alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath()
									+"/"+ListofFileNames.get(deleteid_/1000-1));
							File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
									+"/"+ListofFileNames.get(deleteid_/1000-1)+".3gp");
							myFile.delete();
							myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
									+"/"+ListofFileNames.get(deleteid_/1000-1)+".txt");
							myFile.delete();
							ListofFileNames.remove(deleteid_/1000-1);
							try {
								writeOutFiles(ListofFileNames);
							} catch (IOException e) {
								e.printStackTrace();
							}
							loadActivity();
						}
					});
					alertDialog.show();
				}
			});
			btn.setOnClickListener(new View.OnClickListener() {
				//call Playback page
				public void onClick(View view) {  
					Intent intent = new Intent(view.getContext(), Playback.class);
					//pass file name to activity
					intent.putExtra("fileName", Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/"+fileName);
					intent.putExtra("basicFileName",fileName);
					startActivity(intent);
				}
			});
		}
	}
	//Go to recording button
	public void goToRecording(View view){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	/* readInFiles
	 * Will read in file names from the specified .txt file
	 * and insert those files into a global ArrayList
	 * @throws IOException
	 */
	private void readInFiles() throws IOException {
		//create objects required for read
		fr = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(filePath))));
		String fline = null;
		//read all lines in file
		while((fline = fr.readLine()) != null){
			//add filenames to the ArrayList
			ListofFileNames.add(fline);	
		}
		//close all streams
		fr.close();
		//release objects
		fr = null;
	}

	/* writeOutFiles
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
		//close streams
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
		/* Handle action bar item clicks here. The action bar will
		 * automatically handle clicks on the Home/Up button, so long
		 * as you specify a parent activity in AndroidManifest.xml.
		 */
		int id = item.getItemId();
		if(id == R.id.mic1){
			goToRecording(item.getActionView());
		}
		return super.onOptionsItemSelected(item);
	}
}
