package com.mortaramultimedia.wordwolfappandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mortaramultimedia.wordwolfappandroid.activities.BoardActivity;
import com.mortaramultimedia.wordwolfappandroid.data.Model;


public class SettingsActivity extends Activity {

	public static final String TAG = "SettingsActivity";
	private int userNumRows = 3;
	private int userNumCols = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		Log.d(TAG, "I've made it to onCreate in SettingsActivity");
		init();
	}

	private void init()
	{
		Log.d(TAG, "init");
		createGridSizeSpinner();
	}

	private void createGridSizeSpinner()
	{
		Spinner spinner = (Spinner) findViewById(R.id.gridSizeSpinner);

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_gridsizes_array, android.R.layout.simple_spinner_item);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}

	public void handleNextButtonClick(View view)
	{
		Log.d(TAG, "handleNextButtonClick");

		getGridSizeSelection();

		Intent boardIntent = new Intent(this, BoardActivity.class);
		startActivity(boardIntent);
	}

	private void getGridSizeSelection()
	{
		Log.d(TAG, "getGridSizeSelection");

		Spinner spinner = (Spinner) findViewById(R.id.gridSizeSpinner);
		Object selectionObj = spinner.getItemAtPosition(spinner.getSelectedItemPosition());
		String selectionStr = selectionObj.toString();
		Log.d(TAG, "handleNextButtonClick: selection: " + selectionStr);

		int firstChar = Integer.parseInt( selectionStr.substring(0, 1) );
		int lastChar  = Integer.parseInt( selectionStr.substring( selectionStr.length() - 1, selectionStr.length() ) );

		this.userNumCols= firstChar;
		this.userNumRows = lastChar;

		Log.d(TAG, "handleNextButtonClick: user specified num cols: " + this.userNumCols);
		Log.d(TAG, "handleNextButtonClick: user specified num rows: " + this.userNumRows);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
