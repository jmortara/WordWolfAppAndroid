package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.data.Model;

public class DictionaryActivity extends Activity
{

	public static final String TAG = "DictionaryActivity";
	private ListView mListView = null;
	private EditText mSearchInput = null;
	private ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary);

		init();
	}

	private void init()
	{
		Log.d(TAG, "init");

		// refs
		mListView 		= (ListView) findViewById(R.id.dictListView);
		mSearchInput 	= (EditText) findViewById(R.id.searchInput);

		// set up an array adapter to adapt the global clientDictionary to the list view in this activity
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Model.getClientDictionary() );

		mListView.setAdapter(arrayAdapter);

		// add a listener for searching the list
		mSearchInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				DictionaryActivity.this.arrayAdapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dictionary, menu);
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
